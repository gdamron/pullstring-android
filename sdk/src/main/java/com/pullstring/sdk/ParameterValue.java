/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An arbitrary value passed to an output or entity within a Response from the Web API.
 */
public class ParameterValue {
    public ParameterValue(Object param) {
        updateParamValues(param);
    }

    /**
     * Get the value safely cast to a String.
     * @return The value as a String or an empty String if original value was not a String.
     */
    public String getString() {
        return mString;
    }

    /**
     * Get the value safely case to a double
     * @return The value as a double or 0.0 if the original value was not numeric.
     */
    public double getDouble() {
        return mDouble;
    }

    /**
     * Get the value safely cast to a boolean.
     * @return The value as a boolean or false if the original value was not a boolean.
     */
    public boolean getBoolean() {
        return mBool;
    }

    /**
     * Get the value safely case to an ArrayList.
     * @return The value as an ArrayList or an empty ArrayList if the original value was not an
     * array.
     */
    public ArrayList<ParameterValue> getArray() {
        return mArray;
    }

    /**
     * Get the value safely case to a dictionary.
     * @return The value as a HashMap or and empty HashMap if the original value was not a
     * dictionary
     */
    public HashMap<String, ParameterValue> getDictionary() {
        return mDictionary;
    }

    /**
     * Get the original value.
     * @return The original value as an Object.
     */
    public Object getRawValue() {
        return mObject;
    }

    /**
     * Set a new raw value.
     * @param param A String, double, boolean, ArrayList, JSONArray, HashMap, or JSONObject.
     */
    public void setValue(Object param) {
        updateParamValues(param);
    }

    /**
     * Convert a JSONObject into a HashMap
     */
    static HashMap<String, ParameterValue> getParameterValueDict(JSONObject json) {
        Iterator<String> keys = json.keys();
        HashMap<String, ParameterValue> dict = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            Object p = json.opt(key);
            if (p != null) {
                ParameterValue param = new ParameterValue(p);
                dict.put(key, param);
            }
        }
        return dict;
    }

    /**
     * Cast to various types, falling back to defaults as necessary.
     */
    private void updateParamValues(Object param) {

        mObject = param;
        mString = (param instanceof String) ? (String)param : "";
        mDouble = (param instanceof Double) ? (Double)param : 0.0;
        mBool = (param instanceof Boolean) ? (Boolean)param : false;
        mArray = null;
        mDictionary = null;

        if (param instanceof JSONArray) {
            JSONArray arr = (JSONArray)param;
            mArray = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                Object p = arr.opt(i);
                if (p != null) {
                    ParameterValue parameter = new ParameterValue(p);
                    mArray.add(parameter);
                }
            }
        }

        if (param instanceof ArrayList) {
            ArrayList<?> list = (ArrayList<?>)param;
            mArray = new ArrayList<>();
            for (Object p : list) {
                if (p instanceof ParameterValue) {
                    mArray.add((ParameterValue) p);
                }
            }
        }

        if (param instanceof JSONObject) {
            mDictionary = getParameterValueDict((JSONObject)param);
        }

        if (param instanceof HashMap) {
            HashMap<?,?> map = (HashMap<?,?>)param;
            mDictionary = new HashMap<>();
            for(Map.Entry<?,?> kv : map.entrySet()) {
                if (kv.getKey() instanceof String && kv.getValue() instanceof ParameterValue) {
                    String key = (String)kv.getKey();
                    ParameterValue val = (ParameterValue)kv.getValue();
                    mDictionary.put(key, val);
                }
            }
        }

        // prefer an empty ArrayList or HashMap over null
        if (mArray == null) {
            mArray = new ArrayList<>();
        }

        if (mDictionary == null) {
            mDictionary = new HashMap<>();
        }
    }

    private String mString;
    private double mDouble;
    private boolean mBool;
    private ArrayList<ParameterValue> mArray;
    private HashMap<String, ParameterValue> mDictionary;
    private Object mObject;
}
