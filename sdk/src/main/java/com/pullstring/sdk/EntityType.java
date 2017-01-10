/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Defines the list of entity types.
 *
 * <ul>
 *     <li>EntityType.COUNTER</li>
 *     <li>EntityType.FLAG</li>
 *     <li>EntityType.LABEL</li>
 *     <li>EntityType.LIST</li>
 * </ul>
 */
public enum EntityType {
    LABEL {
        @Override
        public String toString() {
            return "label";
        }
    },
    COUNTER {
        @Override
        public String toString() {
            return "counter";
        }
    },
    FLAG {
        @Override
        public String toString() {
            return "flag";
        }
    },
    LIST {
        @Override
        public String toString() {
            return "list";
        }
    }
}
