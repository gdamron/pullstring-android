package com.pullstring.sdk;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Introduction extends IntegrationTestBase {
    @Test
    public void introduction() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        textShouldEqual(new String[] {"Hello. What's your name?"}, response);
                        mConversation.sendText("janet");
                        break;
                    case 1:
                        textShouldEqual(new String[] {"Hello Janet"}, response);
                        String state = mConversation.getParticipantId();
                        mRequest.setParticipantId(state);
                        mRequest.setConversationId(null);
                        mConversation.start(PROJECT, mRequest);
                        break;
                    case 2:
                        textShouldEqual(new String[] {"Welcome back JANET"}, response);
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
