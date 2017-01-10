/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * <!-- Add a javadoc description here -->
 */
public class IntroAsr extends IntegrationTestBase {
    @Test
    public void introAsr() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        textShouldEqual(new String[] {"Hello. What's your name?"}, response);
                        InputStream wav = getClass().getClassLoader().getResourceAsStream("res/raw/asr_test.wav");

                        try {
                            int readCount;
                            byte[] data = new byte[1024];
                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                            while ((readCount = wav.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, readCount);
                            }
                            buffer.flush();
                            mConversation.sendAudio(buffer.toByteArray());
                        } catch (IOException e) {
                            fail(e.getLocalizedMessage());
                        }

                        break;
                    case 1:
                        textShouldEqual(new String[] {"Hello Grant"}, response);
                        String state = mConversation.getParticipantId();
                        mRequest.setParticipantId(state);
                        mRequest.setConversationId(null);
                        mConversation.start(PROJECT, mRequest);
                        break;
                    case 2:
                        textShouldEqual(new String[] {"Welcome back GRANT"}, response);
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
