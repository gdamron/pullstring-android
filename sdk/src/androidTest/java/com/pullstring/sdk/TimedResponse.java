/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * <!-- Add a javadoc description here -->
 */
public class TimedResponse extends IntegrationTestBase {
    @Test
    public void goToResponse() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.sendActivity("fafa5f56-d6f1-4381-aec8-ce37a68e465f");
                        break;
                    case 1:
                        mTimerVal = response.getTimedResponseInterval();
                        Assert.assertTrue(mTimerVal > 0);
                        textShouldEqual(new String[] {"Say something"}, response);
                        mConversation.checkForTimedResponse();
                        break;
                    case 2:
                        ArrayList<Output> outputs = response.getOutputs();
                        Assert.assertTrue(
                                "Should not have received output when checking for timed response",
                                (outputs == null || outputs.size() == 0)
                        );
                        try {
                            Thread.sleep((int)(mTimerVal * 1000));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mConversation.checkForTimedResponse();
                        break;
                    case 3:
                        textShouldEqual(new String[] {"I'm waiting"}, response);
                        mConversation.sendText("hit the fallback");
                        break;
                    case 4:
                        textShouldEqual(new String[] {"That was something", "Yes it was"}, response);
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
    private double mTimerVal;
}
