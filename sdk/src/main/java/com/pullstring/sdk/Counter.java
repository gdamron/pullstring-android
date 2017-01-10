/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Subclass of Entity to describe a single counter.
 */
public class Counter extends Entity<Double> {

    public Counter(String name, double value) {
        mType = EntityType.COUNTER;
        mName = name;
        mValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getValue() {
        return mValue;
    }

    private double mValue;
}
