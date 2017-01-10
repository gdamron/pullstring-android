/*
 * Encapsulate a conversation thread for PullString's Web API.
 *
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * The Conversation class can be used to interface with the PullString API.
 * </p><p>
 * To begin a conversation, call the start() method, providing a PullString project ID and a
 * Request object specifying you API key.
 * </p><p>
 * The Web API returns a Response object that can contain zero or more outputs, such as lines of
 * dialog or behaviors. This Response object is passed to the onResponse callback as its sole
 * parameter.
 * </p><p>
 * Example:
 * </p><pre><code>
 * // create a Request with a valid API key
 * Request request = new Request(MY_API_KEY);
 *
 * // create a Conversation with ResponseListener
 * Conversation conversation = new Conversation(new ResponseListener() {
 *    {@literal @Override}
 *     public void onResponse(Response response) {
 *         ArrayList<Output> outputs = response.getOutputs();
 *         for (Output output : outputs) {
 *             if (output.getType() == OutputType.DIALOG) {
 *                 DialogOutput dialog = (DialogOutput) output;
 *                 Log.d(TAG, dialog.getText());
 *             }
 *         }
 *     }
 * });
 *
 * // start conversation
 * conversation.start(MY_PROJECT, request);
 * </code></pre>
 */
public class Conversation {
    /**
     * Creates a Conversation
     * @param listener ResponseListener object that will receive all responses from the Web API.
     */
    public Conversation(ResponseListener listener) {
        mListener = listener;
        mApiClient = new ApiClient(VersionInfo.API_BASE_URL, new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                // Conversation ID and Participant ID can change at any time, so keep the current
                if (response != null && mRequest != null) {
                    mRequest.setConversationId(response.getConversationId());
                    mRequest.setParticipantId(response.getParticipantId());
                }

                if (mListener != null) {
                    mListener.onResponse(response);
                }
            }
        });

        mStreamingClient = new StreamingClient(VersionInfo.API_BASE_URL, listener);
    }

    /**
     * <p>
     * Start a new conversation with the Web API and receive a response via the ResponseListener
     * passed into the constructor.
     * </p><p>
     * Example:
     * </p><pre><code>
     * Request request = new Request(MY_API_KEY);
     * conversation.start(MY_PROJECT, request);
     * </code></pre>
     * @param project The PullString project ID.
     * @param request A Request object with a valid API key set.
     */
    public void start(String project, Request request) {
        if (project == null || project.isEmpty()) {
            exitWithFailure("Project name not set");
            return;
        }

        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("project", project);
        body.put("time_zone_offset", request.getTimezoneOffset());

        postJson(body);
    }

    /**
     * Send user input text to the Web API and receive a response via the ResponseListener passed
     * into the constructor.
     * @param text User input text.
     */
    public void sendText(String text) { sendText(text, null); }

    /**
     * Send user input text to the Web API and receive a response via the ResponseListener passed
     * into the constructor.
     * @param text User input text.
     * @param request A Request object with a valid API key set.
     */
    public void sendText(String text, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("text", text);

        postJson(body);
    }

    /**
     * Send an activity name or ID to the Web API and receive a response via the ResponseListener
     * passed into the constructor.
     * @param activity The activity name or ID.
     */
    public void sendActivity(String activity) { sendActivity(activity, null); }

    /**
     * Send an activity name or ID to the Web API and receive a response via the ResponseListener
     * passed into the constructor.
     * @param activity The activity name or ID.
     * @param request A Request object with a valid API key set.
     */
    public void sendActivity(String activity, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("activity", activity);

        postJson(body);
    }

    /**
     * Send an event to the Web API and receive a response via the ResponseListener passed into
     * the constructor.
     * @param eventName The event name.
     * @param parameters Any accompanying parameters.
     */
    public void sendEvent(String eventName, HashMap<String, Object> parameters) {
        sendEvent(eventName, parameters, null);
    }

    /**
     * Send an event to the Web API and receive a response via the ResponseListener
     * passed into the constructor.
     * @param eventName The event name.
     * @param parameters Any accompanying parameters.
     * @param request A Request object with a valid API key set.
     */
    public void sendEvent(String eventName, HashMap<String, Object> parameters, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();

        try {
            JSONObject paramJson = null;
            if (parameters != null) {
                paramJson = new JSONObject();
                for (Map.Entry<String, Object> kv : parameters.entrySet()) {
                    paramJson.put(kv.getKey(), kv.getValue());
                }
            }

            JSONObject event = new JSONObject();
            event.put("name", eventName);
            event.put("parameters", paramJson);
            body.put("event", event);
        } catch (JSONException e) {
            exitWithFailure(e.getLocalizedMessage());
        }

        postJson(body);
    }

    /**
     * Jump the conversation directly to a response
     * @param responseId The UUID of the response to jump to.
     */
    public void goTo(String responseId) { goTo(responseId, null); }

    /**
     * Jump the conversation directly to a response
     * @param responseId The UUI of the response to jump to.
     * @param request A Request object with a valid API key set.
     */
    public void goTo(String responseId, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("goto", responseId);

        postJson(body);
    }

    /**
     * Call the Web API to see if there is a time-based response to process. You only need to call
     * this if the previous response returned a value for the timedResponseInterval > 0. In this
     * case, set a timer for that value (in seconds) and then call this method. If there is no time-
     * based response, the ResponseListener object passed into the constructor will receive an empty
     * Response object.
     */
    public void checkForTimedResponse() { checkForTimedResponse(null); }

    /**
     * Call the Web API to see if there is a time-based response to process. You only need to call
     * this if the previous response returned a value for the timedResponseInterval >= 0. In this
     * case, set a timer for that value (in seconds) and then call this method. If there is no time-
     * based response, the ResponseListener object passed into the constructor will receive an empty
     * Response object.
     * @param request A Request object with a valid API key.
     */
    public void checkForTimedResponse(Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        postJson(null);
    }

    /**
     * Request the values of the specified entities (i.e.: labels, flags, and lists) from the Web
     * API.
     * @param names An array of entity names
     */
    public void getEntities(ArrayList<String> names) { getEntities(names, null); }

    /**
     * Request the values of the specified entities (i.e.: labels, flags, and lists) from the Web
     * API.
     * @param names An array of entity names
     * @param request A Request object with a valid API key set.
     */
    public void getEntities(ArrayList<String> names, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        JSONArray arr = new JSONArray(names);
        HashMap<String, Object> body = new HashMap<>();
        body.put("get_entities", arr);

        postJson(body);
    }

    /**
     * Change the values of the specified entities (i.e.: labels, flags, and lists) from the Web API.
     * @param entities An array specifying the entities to set (with their new values).
     */
    public void setEntities(ArrayList<Entity> entities) { setEntities(entities, null); }

    /**
     * Change the values of the specified entities (i.e.: labels, flags, and lists) from the Web API.
     * @param entities An array specifying the entities to set (with their new values).
     * @param request A Request object with a valid API key set.
     */
    public void setEntities(ArrayList<Entity> entities, Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        JSONObject entDict = new JSONObject();

        try {
            for (Entity e : entities) {
                entDict.putOpt(e.getName(), e.getValue());
            }
        } catch (JSONException e) {
            exitWithFailure(e.getLocalizedMessage());
            return;
        }

        HashMap<String, Object> body = new HashMap<>();
        body.put("set_entities", entDict);

        postJson(body);
    }

    /**
     * Get the current conversation ID. Conversation IDs can persist across sessions, if desired.
     * @return The current conversation ID.
     */
    public String getConversationId() {
        if (mRequest != null) {
            return mRequest.getConversationId();
        }
        return null;
    }

    /**
     * The current participant ID, which identifies the current state for clients. This can persist
     * across sessions, if desired.
     * @return The current participant ID.
     */
    public String getParticipantId() {
        if (mRequest != null) {
            return mRequest.getParticipantId();
        }
        return null;
    }

    /**
     * Initiate a progressive (chunked) streaming of audio data.
     */
    public void startAudio() {
        startAudio(null);
    }

    /**
     * Initiate a progressive (chunked) streaming of audio data, where supported.
     * @param request A Request object with a valid API key set.
     */
    public void startAudio(Request request) {
        if (!ensureRequestExists(request)) {
            return;
        }

        if (mSpeech == null) {
            mSpeech = new Speech();
        }

        HashMap<String, String> headers = getHeaders(mRequest, true);
        // open a stream asynchronously
        mStreamingClient.open(getEndpoint(), headers, new OpenStreamListener() {
            @Override
            public void onOpen(OutputStream os) {
                mAudioStream = os;
                mSpeech.start();
            }
        });
    }

    /**
     * Stream a chunk of raw audio samples. You must call StartAudio() first. The format of
     * the audio must be mono linear PCM audio data at a sample rate of 16000 samples per second.
     * @param samples Raw audio samples.
     */
    public void addAudio(short[] samples) {
        if (mSpeech != null) {
            mSpeech.streamAudio(samples, mAudioStream);
        }
    }

    /**
     * Signal that all audio has been provided via AddAudio() calls. This will complete the audio
     * request and return the Web API response via the ResponseListener object passed into the
     * constructor.
     */
    public void endAudio() {
        mSpeech.stop();
        mStreamingClient.close();
    }

    /**
     * Send an entire audio sample of the user speaking to the Web API. Audio must be, mono 16-bit
     * linear PCM at a sample rate of 16000 samples per second.
     * @param audio Mono 16-bit linear PCM audio clip at 16k Hz sample rate.
     */
    public void sendAudio(byte[] audio) {
        sendAudio(audio, AsrAudioFormat.WAV_16K);
    }

    /**
     * Send an entire audio file of the user speaking to the Web API. Audio must be, mono 16-bit
     * linear PCM at a sample rate of 16000 samples per second.
     * @param audio Mono 16-bit linear PCM audio clip at 16k Hz sample rate.
     * @param format The audio sample's format [Currently, only AsrAudioFormat.WAV_16K is
     * supported].
     */
    public void sendAudio(byte[] audio, AsrAudioFormat format) {
        sendAudio(audio, format, null);
    }

    /**
     * Send an entire audio file of the user speaking to the Web API. Audio must be, mono 16-bit
     * linear PCM at a sample rate of 16000 samples per second.
     * @param audio Mono 16-bit linear PCM audio clip at 16k Hz sample rate.
     * @param format The audio sample's format [Currently, only AsrAudioFormat.WAV_16K is
     * supported].
     * @param request A Request object with a valid API key set.
     */
    public void sendAudio(byte[] audio, AsrAudioFormat format, Request request) {
        if (format == AsrAudioFormat.RAW_PCM_16K) {
            exitWithFailure("Unsupported format sent to sendAudio.");
            return;
        }

        if (!ensureRequestExists(request)) {
            return;
        }

        HashMap<String, String> headers = getHeaders(mRequest, true);
        HashMap<String, String> query = getQuery(mRequest);

        StringBuilder sb = new StringBuilder();
        byte[] data = Speech.getWavData(audio, sb);
        String err = sb.toString();

        if (data != null && err.isEmpty()) {
            mApiClient.post(getEndpoint(), query, headers, data);
        } else {
            exitWithFailure(err);
        }
    }

    /**
     * /conversation for a new conversation. /conversation/<conversation id> for a continuing one.
     */
    private String getEndpoint() {
        StringBuilder sb = new StringBuilder(Keys.ConversationEndpoint);

        String conversationId = getConversationId();
        if (conversationId != null) {
            sb.append('/');
            sb.append(conversationId);
        }

        return sb.toString();
    }

    /**
     * Send an asynchronous request to the Web API
     */
    private void postJson(HashMap<String, Object> payload) {
        HashMap<String, String> headers = getHeaders(mRequest, false);
        HashMap<String, String> query = getQuery(mRequest);
        JSONObject json = getBody(mRequest, payload);

        mApiClient.post(getEndpoint(), query, headers, json);
    }

    /**
     * Add Authorization, Accept, and Content-Type headers
     */
    private HashMap<String, String> getHeaders(Request request, boolean isAudio) {
        if (request == null) return null;
        HashMap<String,String> headers = new HashMap<>();

        headers.put("Authorization", "Bearer " + request.getApiKey());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", isAudio ? "audio/l16; rate=16000" : "application/json");

        return headers;
    }

    /**
     * Add asr_language and account (if present) to query string.
     */
    private HashMap<String, String> getQuery(Request request) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("asr_language", request.getLanguage());

        String account = request.getAccountId();
        if (account != null && !account.isEmpty()) {
            parameters.put("account", account);
        }

        return parameters;
    }

    /**
     * Add build_type, participant, restart_if_modified, and any custom parameters to body if
     * present and not the default values.
     */
    private JSONObject getBody(Request request, HashMap<String, Object> parameters) {

        JSONObject body = new JSONObject();

        try {
            BuildType buildType = request.getBuildType();
            // production build type is default, so only add non-default value
            if (buildType != BuildType.PRODUCTION) {
                body.put("build_type", buildType.toString());
            }

            String participant = request.getParticipantId();
            if (participant != null && !participant.isEmpty()) {
                body.put("participant", participant);
            }

            // default value for restart_if_modified is true, so only add if false
            if (!request.isRestartIfModified()) {
                body.put("restart_if_modified", false);
            }

            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    body.put(key, parameters.get(key));
                }
            }
        } catch (JSONException e) {
            Log.e(Keys.TAG, e.toString());
            body = null;
        }

        return body;
    }

    /**
     * Keep internal request up to date.
     */
    private boolean ensureRequestExists(Request request) {
        // always make a user supplied request the internal request
        if (request != null) {
            mRequest = request;
        }

        // this should only really happen if null is passed to start()
        if (mRequest == null) {
            exitWithFailure("Valid Request object missing");
        }

        return mRequest != null;
    }

    /**
     * Populates a Status object with the provided failure message and calls the ResponseListener.
     */
    private void exitWithFailure(String message) {
        Response response = new Response();
        Status status = new Status();
        status.setSuccess(false);
        status.setErrorMessage(message);
        status.setStatusCode(Status.InternalError.BAD_REQUEST.getValue());
        response.setStatus(status);

        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    private Request mRequest;
    private ApiClient mApiClient;
    private StreamingClient mStreamingClient;
    private OutputStream mAudioStream;
    private ResponseListener mListener;
    private Speech mSpeech;
}
