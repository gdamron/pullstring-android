/*
 * Copyright (c) 2017 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

/**
 * <!-- Add a javadoc description here -->
 */
public class BadAudio extends IntegrationTestBase {
    @Test
    public void badAudio() throws Exception {
        // not a wav
        final byte[] badHeader1 = {'R','U','F','F'};
        // too many channels
        final byte[] badHeader2 = {'R','I','F','F',0, 0, 0, 0,'W','A','V','E','f','m','t',' ',0,0,0,0,1,0,2,0,-128,62,0,0,0,0,0,0,0,0,16,0};
        // wrong sample rate
        final byte[] badHeader3 = {'R','I','F','F',0, 0, 0, 0,'W','A','V','E','f','m','t',' ',0,0,0,0,1,0,1,0,-128,-69,0,0,0,0,0,0,0,0,16,0};
        // too many bits per sample
        final byte[] badHeader4 = {'R','I','F','F',0, 0, 0, 0,'W','A','V','E','f','m','t',' ',0,0,0,0,1,0,1,0,-128,62,0,0,0,0,0,0,0,0,4,0};
        // no data
        final byte[] badHeader5 = {'R','I','F','F',0, 0, 0, 0,'W','A','V','E','f','m','t',' ',0,0,0,0,1,0,1,0,-128,62,0,0,0,0,0,0,0,0,16,0};

        mListener = new IntegrationTestBase.TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.sendAudio(badHeader1, AsrAudioFormat.RAW_PCM_16K);
                        break;
                    case 1:
                        errorShouldMatch("Unsupported format sent to sendAudio.", response);
                        mConversation.sendAudio(badHeader1);
                        break;
                    case 2:
                        errorShouldMatch("Data is not a WAV file", response);
                        mConversation.sendAudio(badHeader2);
                        break;
                    case 3:
                        errorShouldMatch("WAV data is not mono 16-bit data at 16k sample rate", response);
                        mConversation.sendAudio(badHeader3);
                        break;
                    case 4:
                        errorShouldMatch("WAV data is not mono 16-bit data at 16k sample rate", response);
                        mConversation.sendAudio(badHeader4);
                        break;
                    case 5:
                        errorShouldMatch("WAV data is not mono 16-bit data at 16k sample rate", response);
                        mConversation.sendAudio(badHeader5);
                        break;
                    case 6:
                        errorShouldMatch("Cannot find data segment in WAV file", response);
                        finish();
                        break;
                    default:
                        shouldNotBeHere();
                }
            }
        };

        mConversation.start(PROJECT, mRequest);
        await();
    }
}
