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

/**
 * Subclass of Output representing a dialog response.
 */
public class DialogOutput extends Output {
    public DialogOutput(JSONObject json) {
        mGuid = json.optString(Keys.Guid);
        mText = json.optString(Keys.Text);
        mAudioUri = json.optString(Keys.Uri);
        mVideoUri = json.optString(Keys.VideoFile);
        mCharacter = json.optString(Keys.Character);
        mDuration = json.optDouble(Keys.Duration, 0);
        mUserData = json.optString(Keys.UserData);

        JSONArray phonemeJson = json.optJSONArray(Keys.Phonemes);
        if (phonemeJson != null && phonemeJson.length() > 0) {
            mPhonemes = new ArrayList<>();
            for (int i = 0; i < phonemeJson.length(); i++) {
                JSONObject p = phonemeJson.optJSONObject(i);
                if (p != null) {
                    Phoneme phoneme = new Phoneme(p);
                    mPhonemes.add(phoneme);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @return OutputType.DIALOG
     */
    @Override
    public OutputType  getType() {
        return mType;
    }

    /**
     * Get the character's text response.
     * @return The response String.
     */
    public String getText() {
        return mText;
    }

    /**
     * Set the character's text response.
     * @param text The response String.
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * Get the location of recorded audio, if available.
     * @return The audio path String.
     */
    public String getAudioUri() {
        return mAudioUri;
    }

    /**
     * Set the location of recorded audio.
     * @param audioUri The audio path String.
     */
    public void setAudioUri(String audioUri) {
        mAudioUri = audioUri;
    }

    /**
     * Get the location of recorded video, if available.
     * @return The video path String.
     */
    public String getVideoUri() {
        return mVideoUri;
    }

    /**
     * Set the location of recorded video.
     * @param videoUri The video path String.
     */
    public void setVideoUri(String videoUri) {
        mVideoUri = videoUri;
    }

    /**
     * Get the speaking character's name.
     * @return The character's name.
     */
    public String getCharacter() {
        return mCharacter;
    }

    /**
     * Set the speaking character's name.
     * @param character The character's name.
     */
    public void setCharacter(String character) {
        mCharacter = character;
    }

    /**
     * Get the duration of the spoken line, in seconds.
     * @return The duration, in seconds.
     */
    public double getDuration() {
        return mDuration;
    }

    /**
     * Set the duration of the spoken line, in seconds.
     * @param duration The duration, in seconds.
     */
    public void setDuration(double duration) {
        mDuration = duration;
    }

    /**
     * Get optional arbitrary String data that was associated with the dialog line within
     * PullString Author.
     * @return The user data String.
     */
    public String getUserData() {
        return mUserData;
    }

    /**
     * Set arbitrary String data.
     * @param userData The user data String.
     */
    public void setUserData(String userData) {
        mUserData = userData;
    }

    /**
     * Get spoken mouth shapes for the line.
     * @return An ArrayList of Phonemes.
     */
    public ArrayList<Phoneme> getPhonemes() {
        return mPhonemes;
    }

    /**
     * Set spoken mouth shapes for the line.
     * @param phonemes An ArrayList of Phonemes.
     */
    public void setPhonemes(ArrayList<Phoneme> phonemes) {
        mPhonemes = phonemes;
    }

    private String mText;
    private String mAudioUri;
    private String mVideoUri;
    private String mCharacter;
    private double mDuration;
    private String mUserData;
    private ArrayList<Phoneme> mPhonemes;
    private final OutputType mType = OutputType.DIALOG;
}
