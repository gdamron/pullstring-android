/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import java.util.ArrayList;

/**
 * Subclass of Entity to describe a single list.
 */
public class List extends Entity<ArrayList> {

    public List(String name, ArrayList value) {
        mType = EntityType.LIST;
        mName = name;
        mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Object> getValue() {
        return mValue;
    }

    private ArrayList<Object> mValue;
}
