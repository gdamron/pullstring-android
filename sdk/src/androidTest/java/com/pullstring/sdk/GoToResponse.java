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
public class GoToResponse extends IntegrationTestBase {
    @Test
    public void goToResponse() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        mConversation.goTo("d6701507-61a9-47d9-8300-2e9c6b08dfcd");
                        break;
                    case 1:
                        textShouldEqual(new String[] {"Hello "}, response);
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
