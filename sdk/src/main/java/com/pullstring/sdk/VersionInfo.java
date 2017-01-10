/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

/**
 * Encapsulates version information for PullString's Web API.
 */
public class VersionInfo {
    /**
     * The public-facing endpoint of the PullString Web API.
     */
    public final static String API_BASE_URL = "https://conversation.pullstring.ai/v1/";

    /**
     * Check if the SDK currently supports a feature.
     * @param feature A FeatureName value
     * @return true if the feature is supported.
     */
    public static boolean hasFeature(FeatureName feature) {
        switch (feature) {
            case STREAMING_ASR:
                return true;
            default:
                return false;
        }
    }
}
