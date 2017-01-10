/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Encapsulate some shared functionality for HTTP requests in a base class
 */
class HttpClient {
    HttpClient(String baseUrl, final ResponseListener listener) {
        mBaseUrl = baseUrl;
        mListener = listener;
    }

    /**
     * Convert a dictionary of parameters into a query string
     */
    String getUrl(String endpoint, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder(mBaseUrl);
        if (!mBaseUrl.endsWith("/") && !endpoint.startsWith("/")) {
            sb.append('/');
        }
        sb.append(endpoint);

        if (parameters == null) {
            return sb.toString();
        }

        sb.append('?');

        Set<String> keySet = parameters.keySet();
        int keyCount = keySet.size();

        for (Map.Entry<String, String> kv : parameters.entrySet()) {
            --keyCount;
            sb.append(kv.getKey());
            sb.append('=');
            sb.append(kv.getValue());

            if (keyCount > 0) {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    /**
     * Read the InputStream from the connection and parse it into a Response object.
     */
    static Response parse(HttpsURLConnection connection) {
        Response response;
        try {
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            String jsonStr = read(stream);
            String endpoint = connection.getHeaderField(Keys.LocationHeader);

            JSONObject json = new JSONObject(jsonStr);
            json.putOpt(Keys.EndpointHeader, endpoint);

            response = new Response(json);
        } catch (IOException e) {
            try {
                InputStream errStream = new BufferedInputStream(connection.getErrorStream());
                String errString = read(errStream);
                JSONObject errJson = new JSONObject(errString).getJSONObject(Keys.Error);
                response = getErrorResponse(errJson.getInt(Keys.Status), errJson.getString(Keys.Message));
            } catch (Exception e2) {
                response = getErrorResponse(Status.InternalError.PARSING.getValue(), e2.getLocalizedMessage());
            }
        } catch (JSONException e) {
            response = getErrorResponse(Status.InternalError.PARSING.getValue(), e.getLocalizedMessage());
        }

        return response;
    }

    /**
     * Populate a Status object with error code and message
     */
    static Response getErrorResponse(int code, String message) {
        Response response = new Response();
        Status status = new Status();
        status.setSuccess(false);
        status.setStatusCode(code);
        status.setErrorMessage(message);
        response.setStatus(status);

        return response;
    }

    /**
     * Convert and InputStream into a String
     */
    static private String read(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, ENCODING);
        char[] buffer = new char[1024];
        int bytesRead;
        StringBuilder builder = new StringBuilder();

        while((bytesRead = reader.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, bytesRead));
        }

        return builder.toString();
    }

    /**
     * Encapsulate a Web API request that can be used whether sending JSON, and audio file, or an
     * audio stream
     */
    class ApiRequest {
        /**
         * Request with bytes
         */
        ApiRequest(String endpoint, Map<String, String> parameters, Map<String,String> headers, byte[] body) {
            this.endpoint = endpoint;
            this.parameters = parameters;
            this.headers = headers;
            this.bytes = body;
            this.json = null;
        }

        /**
         * Request with json
         */
        ApiRequest(String endpoint, Map<String, String> parameters, Map<String,String> headers, JSONObject body) {
            this.endpoint = endpoint;
            this.parameters = parameters;
            this.headers = headers;
            this.bytes = null;
            this.json = body;
        }

        /**
         * Request with no body or parameters (i.e., for opening a stream)
         */
        ApiRequest(String endpoint, Map<String,String> headers) {
            this.endpoint = endpoint;
            this.parameters = null;
            this.headers = headers;
            this.bytes = null;
            this.json = null;
        }

        String endpoint;
        Map<String, String> parameters;
        Map<String, String> headers;
        byte[] bytes;
        JSONObject json;
    }

    private String mBaseUrl;
    protected ResponseListener mListener;

    static final String ENCODING = "UTF-8";
    static final String REQUEST_METHOD = "POST";
    static final int DEFAULT_ERROR_CODE = 501;
    static final int DEFAULT_SUCCESS_CODE = 200;
}
