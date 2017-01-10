/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Define the set of outputs that can be returned in a response.
 *
 * <ul>
 *     <li>OutputType.DIALOG</li>
 *     <li>OutputType.BEHAVIOR</li>
 * </ul>
 */
public enum OutputType {
    DIALOG {
        @Override
        public String toString() {
            return "dialog";
        }
    },
    BEHAVIOR {
        @Override
        public String toString() {
            return "behavior";
        }
    }
}
