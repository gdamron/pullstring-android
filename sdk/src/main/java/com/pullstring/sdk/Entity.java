/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Base class to describe a single entity, such as a label, counter, flag, or list.
 */
public abstract class Entity<T> {
    /**
     * Get a descriptive name of the entity.
     * @return The entity's name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the value of the entity, the type of which will vary depending on the subclass.
     *
     * <ul>
     *     <li>Counter: double</li>
     *     <li>Flag: boolean</li>
     *     <li>Label: String</li>
     *     <li>List: ArrayList&lt;Object&gt;</li>
     * </ul>
     *
     * @return The entity's value, the type varying depending on the subclass.
     */
    public T getValue() {
        return mValue;
    }

    /**
     * Get EntityType for this object, i.e.
     *
     * <ul>
     *     <li><code>EntityType.COUNTER</code></li>
     *     <li><code>EntityType.FLAG</code></li>
     *     <li><code>EntityType.LABEL</code></li>
     *     <li><code>EntityType.LIST</code></li>
     * </ul>
     * @return The EntityType for this object
     */
    public EntityType getType() {
        return mType;
    }

    protected EntityType mType;
    protected String mName;
    protected T mValue;
}
