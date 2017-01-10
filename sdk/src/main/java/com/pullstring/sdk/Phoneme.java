/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describe a single phoneme for an audio response, e.g., to drive automatic lip sync.
 */
public class Phoneme {
    public Phoneme(JSONObject json) {
        if (json.has(Keys.Name))  {
            mName = json.optString(Keys.Name);
        }
        if (json.has(Keys.SecondsSinceStart)) {
            mSecondsSinceStart = json.optDouble(Keys.SecondsSinceStart, 0);
        }
    }

    /**
     * Get the phoneme's descriptive value.
     * @return The phoneme.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set the phoneme's descriptive value.
     * @param mName The phoneme.
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * Get the start time for the phoneme, relative to the beginning of a line of dialog.
     * @return The offset, in seconds.
     */
    public double getSecondsSinceStart() {
        return mSecondsSinceStart;
    }

    /**
     * Set the start time for the phoneme, relative to the beginning of a line of dialog.
     * @param mSecondsSinceStart The offset, in seconds.
     */
    public void setSecondsSinceStart(double mSecondsSinceStart) {
        this.mSecondsSinceStart = mSecondsSinceStart;
    }

    private String mName;
    private double mSecondsSinceStart;
}
