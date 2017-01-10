/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Describe the parameters for a request to the PullString Web API.
 */
public class Request {

    /**
     * Constructor
     * @param key A valid API key.
     * @param type The build type to use.
     */
    public Request(String key, BuildType type) {
        mApiKey = key;
        mBuildType = type;
    }

    /**
     * Constructor, defaulting to a production build type.
     * @param key A valid API key.
     */
    public Request (String key) {
        mApiKey = key;
    }

    /**
     * Get the current API key.
     */
    public String getApiKey() {
        return mApiKey;
    }

    /**
     * Set a valid API key, which is required for all requests.
     */
    public void setApiKey(String apiKey) {
        this.mApiKey = apiKey;
    }

    /**
     * Get the identifier to the Web API for the current conversation. This can persist across
     * sessions and is required after a conversation is started.
     * @return The current conversation id.
     */
    public String getConversationId() {
        return mConversationId;
    }

    /**
     * Set a new identifier for an already existing conversation. Since ids can persist across
     * sessions, they can be reused to continue an earlier conversation.
     * @param conversationId The new conversation id.
     */
    public void setConversationId(String conversationId) {
        mConversationId = conversationId;
    }

    /**
     * Get the identifier to the Web API of the current conversation state. This can persist across
     * sessions.
     * @return The id for the current conversation state.
     */
    public String getParticipantId() {
        return mParticipantId;
    }

    /**
     * Set the conversation state id for a conversation. This can be used to restore the state
     * for a specific user within an already existing conversation.
     * @param participantId The new participant id
     */
    public void setParticipantId(String participantId) {
        this.mParticipantId = participantId;
    }

    /**
     * Get the current BuildType, which defaults to BuildType.PRODUCTION.
     * @return The current BuildType.
     */
    public BuildType getBuildType() {
        return mBuildType;
    }

    public void setBuildType(BuildType buildType) {
        this.mBuildType = buildType;
    }

    /**
     * Get the current ASR language, which defaults to "en-US".
     * @return The current ASR language.
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * Set the current ASR language.
     * @param language The new language code.
     */
    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    /**
     * Get the user's locale, which defaults to 'en-US'.
     * @return The current user locale.
     */
    public String getLocale() {
        return mLocale;
    }

    /**
     * Set the user's locale.
     * @param locale The new user locale.
     */
    public void setLocale(String locale) {
        this.mLocale = locale;
    }

    /**
     * Get the value, in seconds, representing the timezone offset in UTC. For example, PST would
     * be -28800.
     * @return The timezone offset, in seconds.
     */
    public int getTimezoneOffset() {
        return mTimezoneOffset;
    }

    /**
     * Set the timezone offset, in seconds. For example, PST would be -28800.
     * @param timezoneOffset The new offset, in seconds.
     */
    public void setTimezoneOffset(int timezoneOffset) {
        this.mTimezoneOffset = timezoneOffset;
    }

    /**
     * Check if the conversation should restart if a newer version of the project has been
     * published. The default value is true.
     * @return true of the conversation should restart if newer intelligence is available.
     */
    public boolean isRestartIfModified() {
        return mRestartIfModified;
    }

    /**
     * Set whether the conversation should restart if a newer version of the project has been
     * published.
     * @param restartIfModified Whether the conversation should restart if a newer version is
     *                          available.
     */
    public void setRestartIfModified(boolean restartIfModified) {
        this.mRestartIfModified = restartIfModified;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        this.mAccountId = accountId;
    }

    private String mApiKey;
    private String mConversationId;
    private String mParticipantId;
    private BuildType mBuildType = BuildType.PRODUCTION;
    private String mLanguage = "en-US";
    private String mLocale;
    private int mTimezoneOffset;
    private boolean mRestartIfModified = true;
    private String mAccountId;
}
