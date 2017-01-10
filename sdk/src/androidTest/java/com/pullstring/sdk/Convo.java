/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

/**
 * <!-- Add a javadoc description here -->
 */
public class Convo extends IntegrationTestBase {
    @Test
    public void conversation() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.sendActivity("wizard");
                        break;
                    case 1:
                        mConversation.sendText("wizard");
                        break;
                    case 2:
                        textShouldEqual(new String[] { "Talk to the dwarf" }, response);
                        mConversation.sendText("dwarf");
                        break;
                    case 3:
                        textShouldEqual(new String[] { "Here's my axe" }, response);
                        mConversation.sendText("dwarf");
                        break;
                    case 4:
                        textShouldEqual(new String[] { "You already have my axe" }, response);
                        mConversation.sendText("wizard");
                        break;
                    case 5:
                        textShouldEqual(new String[] { "Here's my spell" }, response);
                        mConversation.sendText("wizard");
                        break;
                    case 6:
                        textShouldEqual(new String[] { "You already have my spell" }, response);
                        finish();
                        break;
                    default:
                        shouldNotBeHere();
                        break;
                }
            }
        };

        mConversation.start(PROJECT, mRequest);
        await();
    }
}
