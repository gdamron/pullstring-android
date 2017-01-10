/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * <!-- Add a javadoc description here -->
 */
class StreamingClient extends HttpClient {
    StreamingClient(String baseUrl, ResponseListener listener) {
        super(baseUrl, listener);
    }

    /**
     * Connect to the Web API asynchronously and pass the OutputStream to the listener
     */
    void open(String endpoint, Map<String,String> headers, OpenStreamListener listener) {
        mOpenListener = listener;
        ApiRequest request = new ApiRequest(endpoint, headers);
        new StreamRequestTask() {
            @Override
            protected Response run(ApiRequest request) {
                return openSync(request.endpoint, request.headers);
            }
        }.execute(request);
    }

    /**
     * Asynchronously close the connection to the Web API.
     */
    void close() {
        new StreamRequestTask() {
            @Override
            protected Response run(ApiRequest request) {
                return closeSync();
            }
        }.execute();
    }

    /**
     * Open connection with the Web API.
     * @return null on success or a Response object with Status set to failure.
     */
    private Response openSync(String endpoint, Map<String,String> headers) {
        String urlString = getUrl(endpoint, null);
        String errString = null;

        try {
            // open the connection
            URL url = new URL(urlString);
            mConnection = (HttpsURLConnection) url.openConnection();

            // set headers
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                mConnection.setRequestProperty(kv.getKey(), kv.getValue());
            }

            // configure the connection for chunked streaming
            mConnection.setDoOutput(true);
            mConnection.setRequestMethod(REQUEST_METHOD);
            mConnection.setChunkedStreamingMode(0);
            mOutputStream = new BufferedOutputStream(mConnection.getOutputStream());
        } catch (IOException e) {
            if (mConnection != null) {
                try {
                    errString = mConnection.getResponseMessage();
                } catch (IOException e2) {
                    errString = e2.getLocalizedMessage();
                }
            }
        } catch (Exception e) {
            // most likely a malformed url
            errString = e.getLocalizedMessage();
        }

        if (errString != null) {
            try {
                if (mOutputStream != null) {
                    mOutputStream.close();
                }
            } catch (IOException e) {
                // not much to do here since we really want the value already in errString
                e.printStackTrace();
            } finally {
                mOutputStream = null;
            }

            mConnection.disconnect();

            return getErrorResponse(Status.InternalError.BAD_REQUEST.getValue(), errString);
        }

        // if all went well, return null
        return null;
    }

    /**
     * Close the connection to the Web API.
     * @return A Response object. If there were errors, the object's Status property will contain
     * the error message and status code.
     */
    private Response closeSync() {
        int statusCode = DEFAULT_SUCCESS_CODE;
        Response response = null;
        String errString = null;

        try {
            // close the stream and disconnect the listener
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            mOpenListener = null;

            // use the superclass' parse method
            response = parse(mConnection);
        } catch (IOException e) {
            if (mConnection != null) {
                try {
                    errString = mConnection.getResponseMessage();
                } catch (IOException e2) {
                    errString = e2.getLocalizedMessage();
                }
            }
            statusCode = DEFAULT_ERROR_CODE;
        } catch (Exception e) {
            errString = e.getLocalizedMessage();
            statusCode = DEFAULT_ERROR_CODE;
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }

            if (errString != null) {
                response = getErrorResponse(statusCode, errString);
            }
        }

        return response;
    }

    /**
     * Open or close a stream in the background
     */
    private abstract class StreamRequestTask extends AsyncTask<ApiRequest, Void, Response> {
        @Override
        protected Response doInBackground(ApiRequest... params) {
            ApiRequest request = null;
            if (params != null && params.length > 0) {
                request = params[0];
            }
            return run(request);
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response != null && mListener != null) {
                mListener.onResponse(response);
                return;
            }

            if (mOutputStream != null && mOpenListener != null) {
                mOpenListener.onOpen(mOutputStream);
            }
        }

        protected abstract Response run(ApiRequest request);
    }

    private HttpsURLConnection mConnection;
    private OutputStream mOutputStream;
    private OpenStreamListener mOpenListener;
}
