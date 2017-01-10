/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Subclass of Entity to describe a single flag.
 */
public class Flag extends Entity<Boolean> {

    public Flag(String name, boolean value) {
        mType = EntityType.FLAG;
        mName = name;
        mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getValue() {
        return mValue;
    }

    private boolean mValue;
}
