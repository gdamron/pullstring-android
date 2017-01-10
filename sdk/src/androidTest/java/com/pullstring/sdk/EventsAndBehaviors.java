/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;
import java.util.HashMap;

/**
 * <!-- Add a javadoc description here -->
 */
public class EventsAndBehaviors extends IntegrationTestBase {
    @Test
    public void entities() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.sendEvent("simple_event", null);
                        break;
                    case 1:
                        BehaviorOutput expected = new BehaviorOutput();
                        expected.setBehavior("simple_action");
                        behaviorShoudMatch(expected, response);

                        HashMap<String, Object> parameters = new HashMap<>();
                        parameters.put("name", "green");
                        mConversation.sendEvent("event_with_param", parameters);
                        break;
                    case 2:
                        BehaviorOutput expected2 = new BehaviorOutput();
                        expected2.setBehavior("action_with_param");
                        HashMap<String, ParameterValue> expectedParams = new HashMap<>();
                        expectedParams.put("name", new ParameterValue("Green"));
                        expected2.setParameters(expectedParams);

                        textShouldEqual(new String[] {"Green Event Called"}, response);
                        behaviorShoudMatch(expected2, response);

                        HashMap<String, Object> parameters2 = new HashMap<>();
                        parameters2.put("name", "red");
                        mConversation.sendEvent("event_with_param", parameters2);
                        break;
                    case 3:
                        BehaviorOutput expected3 = new BehaviorOutput();
                        expected3.setBehavior("action_with_param");
                        HashMap<String, ParameterValue> expectedParams2 = new HashMap<>();
                        expectedParams2.put("name", new ParameterValue("Red"));
                        expected3.setParameters(expectedParams2);

                        textShouldEqual(new String[] {"Red Event Called"}, response);
                        behaviorShoudMatch(expected3, response);

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
