/*
 * Copyright (c) 2017 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * The Action to take for a conversation when new content is published
 *
 * <ul>
 *     <li>IfModifiedAction.RESTART</li>
 *     <li>IfModifiedAction.UPDATE</li>
 *     <li>IfModifiedAction.NOTHING</li>
 * </ul>
 */
public enum IfModifiedAction {
    RESTART {
        @Override
        public String toString() { return "restart"; }
    },
    UPDATE {
        @Override
        public String toString() { return "update"; }
    },
    NOTHING {
        @Override
        public String toString() { return "nothing"; }
    }
}
