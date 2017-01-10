/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Presents the output of a request to the PullString Web API.
 */
public class Response {
    public Response() {
        // default constructor, handy for constructing error responses
    }

    public Response(JSONObject json) {
        // status defaults to success
        mStatus = new Status();
        JSONObject error = json.optJSONObject(Keys.Error);
        if (error != null) {
            mStatus.setSuccess(false);
            mStatus.setErrorMessage(error.optString(Keys.Message, "Unknown Error"));
            mStatus.setStatusCode(error.optInt(Keys.Status, 500));
        }

        // top-level JSON properties
        mConversationId = json.optString(Keys.ConversationId);
        mParticipantId = json.optString(Keys.ParticipantId);
        mETag = json.optString(Keys.ETag);
        mAsrHypothesis = json.optString(Keys.AsrHypothesis);
        mEndpoint = json.optString(Keys.EndpointHeader);
        mTimedResponseInterval = json.optDouble(Keys.TimedResponseInterval, 0);

        // last modified
        String interval = json.optString(Keys.LastModified);
        if (!interval.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            try {
                mLastModified = dateFormat.parse(interval);
            } catch (ParseException e) {
                e.printStackTrace();
                mLastModified = null;
            }
        }

        // outputs
        mOutputs = new ArrayList<>();
        JSONArray outputs = json.optJSONArray(Keys.Outputs);
        if (outputs != null) {
            for (int i = 0; i < outputs.length(); i++) {
                JSONObject output = outputs.optJSONObject(i);
                if (output == null || !output.has(Keys.OutputType)) {
                    continue;
                }

                String type = output.optString(Keys.OutputType);

                if (type.equals(OutputType.BEHAVIOR.toString())) {
                    mOutputs.add(new BehaviorOutput(output));
                } else if (type.equals(OutputType.DIALOG.toString())) {
                    mOutputs.add(new DialogOutput(output));
                }
            }
        }

        // entities -- convert from JSON dictionary to an array
        mEntities = new ArrayList<>();
        JSONObject entities = json.optJSONObject(Keys.Entities);
        if (entities != null) {
            Iterator<String> keys = entities.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object entity = entities.opt(key);

                if (entity == null) {
                    continue;
                }

                if (entity instanceof String) {
                    mEntities.add(new Label(key, (String)entity));
                } else if (entity instanceof JSONArray) {
                    JSONArray arr = (JSONArray) entity;
                    ArrayList<Object> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        Object item = arr.opt(i);
                        if (item != null) list.add(item);
                    }
                    mEntities.add(new List(key, list));
                } else if (entity instanceof Boolean) {
                    mEntities.add(new Flag(key, (boolean)entity));
                } else if (entity instanceof Number) {
                    mEntities.add(new Counter(key, ((Number)entity).doubleValue()));
                }
            }
        }
    }

    /**
     * Get the Status object containing any errors returned by the Web API.
     * @return A Status object reflecting either success or failure of the last request.
     */
    public Status getStatus() {
        return mStatus;
    }

    /**
     * Set the success of failure Status of the last request.
     * @param mStatus The new Status object
     */
    void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    /**
     * Get the identifier to the Web API for an ongoing conversation. This can persist across
     * sessions and is required after a conversation is started.
     * @return The current conversation id.
     */
    public String getConversationId() {
        return mConversationId;
    }

    /**
     * Get the identifier to the Web API for the current state. This can persist across sessions.
     * @return The current conversation state id.
     */
    public String getParticipantId() {
        return mParticipantId;
    }

    /**
     * Get the unique identifier for a version of the content.
     * @return A unique id.
     */
    public String getETag() {
        return mETag;
    }

    /**
     * Get the dialog or behaviors returned from the Web API.
     * @return An ArrayList of Output objects. Can be a mix of dialog and behaviors.
     */
    public ArrayList<Output> getOutputs() {
        return mOutputs;
    }

    /**
     * Get the counters, flags, labels, lists, etc returned from the Web API.
     * @return An ArrayList of Entity objects. Can be a mix of subclasses.
     */
    public ArrayList<Entity> getEntities() {
        return mEntities;
    }

    /**
     * Get the time content was last modified.
     * @return A Date representing when the content was last modified.
     */
    public Date getLastModified() {
        return mLastModified;
    }

    /**
     * Get indication if there may be another response to process in the specified number of
     * seconds. If the value is greater than 0, set a timer and call checkForTimedResponse() from a
     * conversation to retrieve it.
     * @return The amount of time, in seconds, after which the next response will be available.
     */
    public double getTimedResponseInterval() {
        return mTimedResponseInterval;
    }

    /**
     * Get the hypothesis for recognized speech, if audio has been submitted.
     * @return The Asr hypothesis or an empty String.
     */
    public String getAsrHypothesis() {
        return mAsrHypothesis;
    }

    /**
     * Get the public endpoint for the current conversation.
     * @return The current conversation endpoint.
     */
    public String getEndpoint() {
        return mEndpoint;
    }

    private Status mStatus;
    private String mConversationId;
    private String mParticipantId;
    private String mETag;
    private ArrayList<Output> mOutputs;
    private ArrayList<Entity> mEntities;
    private Date mLastModified;
    private double mTimedResponseInterval;
    private String mAsrHypothesis;
    private String mEndpoint;

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy kk:mm:ss Z";
}
