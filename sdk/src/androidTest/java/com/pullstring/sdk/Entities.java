/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <!-- Add a javadoc description here -->
 */
public class Entities extends IntegrationTestBase {
    @Test
    public void entities() throws Exception {
        final Label label = new Label("NAME", "jill");
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        textShouldEqual(new String[] {"Hello. What's your name?"}, response);
                        mConversation.sendText("jack");
                        break;
                    case 1:
                        textShouldEqual(new String[] {"Hello Jack"}, response);
                        mConversation.getEntities(new ArrayList<>(Arrays.asList("NAME")));
                        break;
                    case 2:
                        Label expected = new Label("NAME", "jack");
                        entityShouldEqual(new Entity[] {expected}, response);
                        mConversation.setEntities(new ArrayList<Entity>(Arrays.asList(label)));
                        break;
                    case 3:
                        entityShouldEqual(new Entity[] {label}, response);
                        mConversation.sendText("test web service");
                        break;
                    case 4:
                        textShouldEqual(new String[] {"Web Service Result = INPUT STRING / 42 / 0 / red, green, blue, and purple"},response);
                        mConversation.getEntities(new ArrayList<>(Arrays.asList("Number2", "Flag2", "List2")));
                        break;
                    case 5:
                        Counter number2 = new Counter("Number2", 42);
                        Flag flag2 = new Flag("Flag2", false);
                        List list2 = new List("List2", new ArrayList<>(Arrays.asList("red", "green", "blue", "purple")));
                        entityShouldEqual(new Entity[] {flag2, list2, number2}, response);
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
