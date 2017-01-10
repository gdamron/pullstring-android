/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

class IntegrationTestBase {
    @Before
    public void setup() {
        mRequest = new Request(API_KEY);
        mConversation = new Conversation(new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                int step = mStep;
                mStep++;
                mListener.run(response, step);
            }
        });
        mSignal = new CountDownLatch(1);
    }

    void await() throws Exception {
        mSignal.await();
    }

    void finish() {
        mSignal.countDown();
    }

    void textShouldEqual(String[] expected, Response response) {
        int i = 0;
        ArrayList<Output> outputs = response.getOutputs();
        ArrayList<DialogOutput> dialogs = new ArrayList<>();

        for (Output output : outputs) {
            if (output.getType() == OutputType.DIALOG) {
                dialogs.add((DialogOutput)output);
            }
        }

        if (expected.length != dialogs.size())
        {
            fail("Number of expected responses is different than acutal responses.");
            return;
        }

        for (String text : expected)
        {
            String found = dialogs.get(i++).getText();
            if (!found.equals(text))
            {
                fail("Text does not match. Expected " + text + ", found");
                return;
            }
        }
    }

    void entityShouldEqual(Entity[] tests, Response response) {
        ArrayList<Entity> entities = response.getEntities();

        if (entities == null || entities.size() == 0)
        {
            fail("Response contains no entities.");
            return;
        }

        if (entities.size() != tests.length) {
            fail("Response contains unexpected number of entities.");
            return;
        }

        for (int i = 0; i < tests.length; i++)
        {
            Entity expected = tests[i];
            Entity entity = entities.get(i);
            EntityType type = expected.getType();

            if (!expected.getName().equals(entity.getName()))
            {
                fail("Entity's name is not the expected one");
                return;
            }
            else if (type != entity.getType())
            {
                fail("Entity is not the expected type");
                return;
            }

            if (expected.getValue() != null && entity.getValue() == null ||
                    expected.getValue() == null && entity.getValue() != null) {
                fail("Unexpected null value in Entity");
                return;
            } else if (type == EntityType.LIST) {
                ArrayList testList = ((List)expected).getValue();
                ArrayList entList = ((List)entity).getValue();

                if (testList.size() != entList.size()) {
                    fail("Expected Entity List length does not match.");
                    return;
                }

                for (int j = 0; j < testList.size(); j++) {
                    Class jType = testList.get(j).getClass();

                    if (!jType.equals(entList.get(j).getClass())) {
                        fail("Entity List value types do not match");
                        return;
                    }

                    Object testVal = testList.get(j);
                    Object entVal = entList.get(j);

                    if (jType.equals(String.class) && !(testVal).equals(entVal) ||
                            jType.equals(double.class) && (double)testVal != (double)entVal ||
                            jType.equals(boolean.class) && (boolean)testVal != (boolean)entVal)
                    {
                        fail("Entity is expected type but value does not match");
                        return;
                    }

                }
            } else if (!expected.getValue().equals(entity.getValue())) {
                fail("Entity is expected type but value does not match");
                return;
            }
        }
    }

    void behaviorShoudMatch(BehaviorOutput expected, Response response) {
        ArrayList<Output> outputs = response.getOutputs();
        ArrayList<BehaviorOutput> behaviors = new ArrayList<>();
        for (Output output : outputs) {
            if (output.getType() == OutputType.BEHAVIOR) {
                behaviors.add((BehaviorOutput)output);
            }
        }

        if (behaviors.size() == 0)
        {
            fail("No behaviors found in response.");
            return;
        }

        for (BehaviorOutput behavior : behaviors)
        {
            Map<String, ParameterValue> parameters = behavior.getParameters();
            if (behavior.getBehavior().equals(expected.getBehavior()))
            {
                Map<String, ParameterValue> expectedParameters = expected.getParameters();
                if (expectedParameters != null)
                {
                    for (Map.Entry<String, ParameterValue> kv : expectedParameters.entrySet())
                    {
                        if (!parameters.containsKey(kv.getKey()))
                        {
                            fail("Behavior parameters not found.");
                            return;
                        }

                        ParameterValue param = parameters.get(kv.getKey());
                        ParameterValue val = kv.getValue();

                        if (!val.getString().equals(param.getString()) ||
                                val.getBoolean() != param.getBoolean() ||
                                val.getDouble() != param.getDouble() ||
                                (val.getArray() != null && !val.getArray().equals(param.getArray())) ||
                                (val.getDictionary() != null && !val.getDictionary().equals(param.getDictionary())))
                        {
                            fail("Behavior parameters did not match.");
                            return;
                        }
                    }
                }
                // if you made it this far, you passed.
                return;
            }
        }

        fail("Matching behavior not found.");
    }

    void errorShouldMatch(String expected, Response response) {
        Status status = response.getStatus();
        if (status.isSuccess()) {
            fail("Request was successful but should have failed");
        }

        if (!status.getErrorMessage().startsWith(expected)) {
            fail("Received and unexpected error message: " + status.getErrorMessage());
        }
    }

    void shouldNotBeHere() {
        fail("The current test should have ended sooner");
    }

    TestListener mListener;
    Request mRequest;
    Conversation mConversation;
    private int mStep;
    private CountDownLatch mSignal;

    static String API_KEY = "36890c35-8ecd-4ac4-9538-6c75eb1ea6f6";
    static String PROJECT = "841cbd2c-e1bf-406b-9efe-a9025399aab4";

    abstract class TestListener {
        public abstract void run(Response response, int step);
    }
}
