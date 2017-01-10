/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdktestbed;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.pullstring.sdk.Conversation;

class SpeechRecorder {
    SpeechRecorder(Conversation conversation) {
        mConversation = conversation;
        // audio should be 16-bit mono linear PCM at 16 kHz sample rate
        mRecorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE * BYTES_PER_SAMPLE
        );
    }

    void start() {
        mIsRecording = true;
        // process audio on a background thread
        mAudioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                short samples[] = new short[BUFFER_SIZE];
                while (mIsRecording) {
                    mRecorder.read(samples, 0, BUFFER_SIZE);

                    if (samples.length == 0) continue;

                    mConversation.addAudio(samples);
                }
            }
        }, "SpeechRecorder Thread");

        mConversation.startAudio();
        mRecorder.startRecording();
        mAudioThread.start();
    }

    void stop() {
        mRecorder.stop();
        mIsRecording = false;
        mAudioThread = null;
        mConversation.endAudio();
    }

    private AudioRecord mRecorder;
    private Conversation mConversation;
    private boolean mIsRecording;
    private Thread mAudioThread;

    private static final int SAMPLE_RATE = 16000;
    private static final int BUFFER_SIZE = 512;
    private static final int BYTES_PER_SAMPLE = 2;
}
