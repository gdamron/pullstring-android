/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Subclass of Entity to describe a single label.
 */
public class Label extends Entity<String> {

    public Label(String name, String value) {
        mType = EntityType.LABEL;
        mName = name;
        mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return mValue;
    }

    private String mValue;
}
