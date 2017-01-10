/*
 * Copyright (c) 2017 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * <!-- Add a javadoc description here -->
 */
public class VersionAndFeatures {
    @Test
    public void versionAndFeatures() throws Exception {
        assertTrue("Base URL is not correct", VersionInfo.API_BASE_URL.equals("https://conversation.pullstring.ai/v1/"));
        assertTrue("Streaming Asr should be enabled", VersionInfo.hasFeature(FeatureName.STREAMING_ASR));
    }
}
