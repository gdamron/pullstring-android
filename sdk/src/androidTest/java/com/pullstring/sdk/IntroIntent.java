/*
 * Copyright (c) 2017 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IntroIntent extends IntegrationTestBase {
    @Test
    public void introIntent() throws Exception {
        mListener = new TestListener() {
            @Override
            public void run(Response response, int step) {
                switch (step) {
                    case 0:
                        textShouldEqual(new String[] {"Welcome to the LUIS test. What do you like?"}, response);
                        Label colorLabel = new Label("Luis Color", "Green");
                        ArrayList<Entity> entities = new ArrayList<>();
                        entities.add(colorLabel);
                        mConversation.sendIntent("Favorite Color", entities);
                        break;
                    case 1:
                        textShouldEqual(new String[] {"Green is a cool color", "Tell me another color"}, response);
                        finish();
                        break;
                    default:
                        shouldNotBeHere();
                }
            }
        };

        String intentProject = "176a87fb-4d3c-fde5-4b3c-54f18c2d99a4";
        mConversation.start(intentProject, mRequest);
        await();
    }
}
