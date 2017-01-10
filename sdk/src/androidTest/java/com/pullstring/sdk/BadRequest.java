/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * <!-- Add a javadoc description here -->
 */
public class BadRequest extends IntegrationTestBase {
    @Test
    public void badRequest() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        errorShouldMatch("Project name not set", response);
                        mConversation.start(PROJECT, null);
                        break;
                    case 1:
                        errorShouldMatch("Valid Request object missing", response);
                        finish();
                        break;
                    default:
                        shouldNotBeHere();
                }
            }
        };

        String conversationId = mConversation.getConversationId();
        String participantId = mConversation.getParticipantId();

        assertTrue(conversationId == null || conversationId.equals(""));
        assertTrue(participantId == null || participantId.equals(""));

        mConversation.start(null, null);
        await();
    }
}
