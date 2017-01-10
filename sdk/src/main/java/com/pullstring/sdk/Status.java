/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Describe the status and any errors from a Web API response.
 */
public class Status {
    public Status() {
        mSuccess = true;
        mStatusCode = 200;
        mErrorMessage = "";
    }

    /**
     * Get a descriptive message.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * Set a descriptive message.
     */
    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    /**
     * Check whether status is success or failure.
     */
    public boolean isSuccess() {
        return mSuccess;
    }

    /**
     * Set whether status is success or failure.
     */
    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    /**
     * Get the server or internal status code. If an internal (non-server) error occurs, the
     * possible codes are:
     *
     * <ul>
     *     <li>Encoding Error: -1</li>
     *     <li>Parsing Error: -2</li>
     *     <li>Malformed Request: -3</li>
     * </ul>
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * Set the internal or server status code. @see Status.InternalError for possible internal
     * status codes.
     */
    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }

    private boolean mSuccess;
    private int mStatusCode;
    private String mErrorMessage;

    /**
     * Describe internal (non-server) error states.
     *
     * <ul>
     *     <li>InternalError.ENCODING = -1</li>
     *     <li>InternalError.PARSING = -2</li>
     *     <li>InternalError.BAD_REQUEST = -3</li>
     *
     * </ul>
     */
    public enum InternalError {
        ENCODING(-1),
        PARSING(-2),
        BAD_REQUEST(-3);

        private final int mCode;
        InternalError(int code) { mCode = code; }
        public int getValue() { return mCode; }
    }
}
