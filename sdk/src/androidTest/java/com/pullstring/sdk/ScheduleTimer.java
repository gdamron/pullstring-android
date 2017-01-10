/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Assert;
import org.junit.Test;

/**
 * <!-- Add a javadoc description here -->
 */
public class ScheduleTimer extends IntegrationTestBase {
    @Test
    public void scheduleTimer() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.sendActivity("timer");
                        break;
                    case 1:
                        mTimerVal = response.getTimedResponseInterval();
                        Assert.assertTrue(mTimerVal > 0);
                        textShouldEqual(new String[] {"Starting timer"}, response);
                        mConversation.sendText("intervening input");
                        break;
                    case 2:
                        textShouldEqual(new String[] {"Ignored"}, response);
                        try {
                            Thread.sleep((int) (mTimerVal * 1000));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mConversation.checkForTimedResponse();
                        break;
                    case 3:
                        textShouldEqual(new String[] {"Timer fired"}, response);
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
