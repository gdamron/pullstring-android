/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Define the interface for receiving responses from the PullString Web API.
 */
public abstract class ResponseListener {
    /**
     * The method for receiving response from the PullString Web API.
     * @param response A PullString Response object from the Web API.
     */
    public abstract void onResponse(Response response);
}
