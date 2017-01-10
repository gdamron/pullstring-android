/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Base class for outputs that are of type dialog or behavior
 */
public abstract class Output {
    /**
     * Get a unique identifier for this Output.
     * @return The unique identifier for this Output.
     */
    public String getGuid() {
        return mGuid;
    }

    /**
     * The OutputType for this Output, i.e.:
     *
     * <ul>
     *     <li>OutputType.BEHAVIOR</li>
     *     <li>OutputType.DIALOG</li>
     * </ul>
     * @return The OutputType for this Output
     */
    public OutputType getType() { return mType; }

    protected String mGuid;
    protected OutputType mType;
}
