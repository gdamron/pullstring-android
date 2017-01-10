/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * The asset build type to request for the Web API.
 *
 * <ul>
 *     <li>BuildType.PRODUCTION</li>
 *     <li>BuildType.STAGING</li>
 *     <li>BuildType.SANDBOX</li>
 * </ul>
 */
public enum BuildType {
    PRODUCTION {
        @Override
        public String toString() {
            return "production";
        }
    },
    STAGING {
        @Override
        public String toString() {
            return "staging";
        }
    },
    SANDBOX {
        @Override
        public String toString() {
            return "sandbox";
        }
    }
}
