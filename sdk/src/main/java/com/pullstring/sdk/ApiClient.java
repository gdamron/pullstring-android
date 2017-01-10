/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Encapsulate a REST request to the PullString Web API.
 */
class ApiClient extends HttpClient {
    ApiClient(String baseUrl, final ResponseListener listener) {
        super(baseUrl, listener);
    }

    /**
     * asynchronously POST JSON to the Web API
     *
     * @param endpoint The path of the Web API endpoint to hit
     * @param parameters The request's query string
     * @param headers Additional HTTPS headers
     * @param body JSONObject body
     */
    void post(String endpoint, HashMap<String, String> parameters, Map<String,String> headers, JSONObject body) {
        ApiRequest request = new ApiRequest(endpoint, parameters, headers, body);
        new ApiRequestTask().execute(request);
    }

    /**
     * asynchronously POST raw bytes (such as an audio file) to the Web API
     *
     * @param endpoint The path of the Web API endpoint to hit
     * @param parameters The request's query string
     * @param headers Additional HTTPS headers
     * @param body Request body as raw bytes.
     */
    void post(String endpoint, HashMap<String, String> parameters, Map<String,String> headers, byte[] body) {
        ApiRequest request = new ApiRequest(endpoint, parameters, headers, body);
        new ApiRequestTask().execute(request);
    }

    /**
     * POST JSON or bytes to the Web API depending on contents of request
     */
    private Response postSync(ApiRequest request) {
        if (request.json != null) {
            return postSync(request.endpoint, request.parameters, request.headers, request.json);
        } else if (request.bytes != null) {
            return postSync(request.endpoint, request.parameters, request.headers, request.bytes);
        }

        return getErrorResponse(Status.InternalError.BAD_REQUEST.getValue(), "Request body must not be null");
    }

    /**
     * POST JSON to the Web API.
     */
    private Response postSync(String endpoint, Map<String, String> parameters, Map<String,String> headers, JSONObject body) {
        try {
            // convert JSONObject to bytes and send
            String jsonString = body.toString();
            byte data[] = jsonString.getBytes(ENCODING);
            return postSync(endpoint, parameters, headers, data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return getErrorResponse(Status.InternalError.ENCODING.getValue(), e.getLocalizedMessage());
        }
    }

    /**
     * POST bytes to the Web API.
     */
    private Response postSync(String endpoint, final Map<String, String> parameters, Map<String, String> headers, byte[] body) {
        String urlStr = getUrl(endpoint, parameters);
        HttpsURLConnection connection = null;
        String errString = null;
        int statusCode = DEFAULT_SUCCESS_CODE;
        Response response = null;

        try {
            URL url = new URL(urlStr);
            connection = (HttpsURLConnection) url.openConnection();

            // add headers
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                connection.setRequestProperty(kv.getKey(), kv.getValue());
            }

            // write body to output stream
            connection.setDoOutput(true);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setFixedLengthStreamingMode(body.length);
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();

            // get the raw response and parse into Response object
            statusCode = connection.getResponseCode();
            response = parse(connection);
        } catch (IOException e) {
            if (connection != null) {
                try {
                    errString = connection.getResponseMessage();
                } catch (IOException e2) {
                    errString = e2.getLocalizedMessage();
                }
            }
        } catch (Exception e) {
            errString = e.getLocalizedMessage();
            statusCode = DEFAULT_ERROR_CODE;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            // If we've received an error, create a new Response with failure status
            if (errString != null) {
                response = getErrorResponse(statusCode, errString);
            }
        }

        return response;
    }

    /**
     * Execute requests to the Web API in the background
     */
    private class ApiRequestTask extends AsyncTask<ApiRequest, Void, Response> {
        @Override
        protected Response doInBackground(ApiRequest... requests) {
            return postSync(requests[0]);
        }

        @Override
        protected void onPostExecute(Response response) {
            mListener.onResponse(response);
        }
    }
}
