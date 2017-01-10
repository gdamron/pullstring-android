/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import java.io.OutputStream;

/**
 * Defines the interface for waiting for the StreamingClient's stream to connect and
 * become available.
 */
abstract class OpenStreamListener {
    abstract void onOpen(OutputStream os);
}