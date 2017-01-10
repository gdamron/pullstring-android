# Android SDK for the PullString Web API

## Overview

This repository provides a package to access the PullString Web API.

The PullString Web API lets you add text or audio conversational capabilities to your apps, based upon content that you write in the PullString Author environment and publish to the PullString Platform.

## Quickstart

The primary classes for interacting with the PullString SDK are the Conversation, Request, and Response objects. Below is a simple example showing how to start a conversation with the PullString Web API. It will print the initial content under the default Activity for your PullString Author Project. We've provided the API key and Project ID for the example **Rock, Paper, Scissors** chatbot, but of course, you can change `MY_API_KEY` and `MY_PROJECT_ID` to user our own Project ID and API key. You can find these values in the settings for you project in you account on **[pullstring.com](http://pullstring.com)**

```java
import com.pullstring.sdk.*;

...

Conversation mConversation;

final String MY_API_KEY = "9fd2a189-3d57-4c02-8a55-5f0159bff2cf";
final String MY_PROJECT_ID = "e50b56df-95b7-4fa1-9061-83a7a9bea372";

...
// create a conversation in your activity's onStart() method or
// somewhere convenient
mConversation = new Conversation(new ResponseListener() {
    @Override
    public void onResponse(Response response) {
        ArrayList<Output> outputs = response.getOutputs();
        for (Output output : outputs) {
            if (output.getType() == OutputType.DIALOG) {
                DialogOutput dialog = (DialogOutput)output;
                Log.d(TAG, dialog.getText());
            }
        }
    }
});

// create a request with your API key and start the conversaiton
Request request = new Request(MY_API_KEY);
mConversation.start(MY_PROJECT_ID, request);

...
// > Do you want to play Rock, Paper, Scissors?
mConversation.sendText("yes!");

```
Run the **app** configuration for another example demonstrating how to use the SDK to hold a conversation. This test app once again connects to the **Rock, Paper, Scissors** bot, but it shows how to use both text and speech input.

## Importing

To use the SDK in your own project, run *Build > Make Project* or *Build > Make Module 'sdk'*. The resulting .aar can be found in `sdk/build/outputs/aar/`

## Documentation

Documentation for this SDK can be found in [PullStringAndroidSDKRef.md](https://github.com/pullstring/pullstring-android/blob/master/PullStringAndroidSDKRef.md). In addition, the PullString Web API specification can be found at:

> http://docs.pullstring.com/docs/api

For more information about the PullString Platform, refer to:

> http://pullstring.com

## Integration Tests

To execute the SDK's test suite, right-click on the `com.pullstring.com (androidTest)` package and select *"Run Tests in 'com.pullst...'"* or run `./gradlew connectedAndroidTest` from the command line.  When running them from the command line, the test results can be found at `sdk/build/reports/androidTests/connected/index.html`.
