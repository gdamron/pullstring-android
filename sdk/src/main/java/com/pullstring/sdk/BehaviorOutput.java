/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Subclass of Output the represents a behavior response.
 */
public class BehaviorOutput extends Output {

    public BehaviorOutput(JSONObject json) {
        mGuid = json.optString(Keys.Guid);
        mBehavior = json.optString(Keys.Behavior);

        JSONObject params = json.optJSONObject(Keys.Parameters);
        if (params != null && params.length() > 0) {
            mParameters = ParameterValue.getParameterValueDict(params);
        }
    }

    public BehaviorOutput() {
        mGuid = "";
        mBehavior = "";
        mParameters = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputType  getType() {
        return mType;
    }

    /**
     * Get a dictionary with any parameters defined for the behavior.
     * @return A Map&lt;String, ParameterValue&gt; of parameters
     */
    public HashMap<String, ParameterValue> getParameters() {
        return mParameters;
    }

    public void setParameters(HashMap<String, ParameterValue> parameters) {
        mParameters = parameters;
    }

    /**
     * Get the name of the behavior.
     * @return A String representing the name of the behavior.
     */
    public String getBehavior() {
        return mBehavior;
    }

    /**
     * Set the name of the behavior
     * @param behavior A String representing the name of the behavior.
     */
    public void setBehavior(String behavior) {
        mBehavior = behavior;
    }

    private String mBehavior;
    private HashMap<String, ParameterValue> mParameters;
    private final OutputType mType = OutputType.BEHAVIOR;
}
