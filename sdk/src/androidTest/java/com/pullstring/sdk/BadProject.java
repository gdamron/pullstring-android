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
public class BadProject extends IntegrationTestBase {
    @Test
    public void badProject() throws Exception {
        final String crap = "crapcrapcrap";
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        errorShouldMatch("Invalid project for API key", response);
                        mRequest.setApiKey(crap);
                        mConversation.start(PROJECT, mRequest);
                        break;
                    case 1:
                        errorShouldMatch("Invalid Authorization Bearer Token", response);
                        finish();
                        break;
                    default:
                        shouldNotBeHere();
                }
            }
        };

        mConversation.start(crap, mRequest);
        await();
    }
}
