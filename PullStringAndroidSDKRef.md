# PullString SDK Class Reference

The PullString Web API lets you add text or audio conversational capabilities to your apps, based upon content that you write in the PullString Author environment and publish to the PullString Platform.

## Packages

| Name | Description |
| ---- | ---- |
| [`com.pullstring.sdk`](#namespace_pullstring)| Main [PullString](#namespace_pullstring) namespace |

### Summary

 Name                        | Description
-----------------------------|---------------------------------------------
`class `[`Conversation`](#conversation) | The [Conversation](#conversation) class can be used to interface with the [PullString](#namespace_pullstring) API.
`class `[`ResponseListener`](#responselistener) | Define the interface for receiving [Responses](#response) from the [PullString](#namespace_pullstring) Web API.
`class `[`Request`](#request) | Describe the parameters for a request to the [PullString](#namespace_pullstring) Web API.
`class `[`Response`](#response) | Presents the output of a request to the [PullString](#namespace_pullstring) Web API.
`class `[`Entity`](#entity) | Base class to describe a single entity, such as a label, counter, flag, or list.
`class `[`Label`](#label) | Subclass of [Entity](#entity) to describe a single [Label](#label).
`class `[`Counter`](#counter) | Subclass of [Entity](#entity) to describe a single counter.
`class `[`Flag`](#flag) | Subclass of [Entity](#entity) to describe a single [Flag](#flag).
`class `[`List`](#list) | Subclass of [Entity](#entity) to describe a single [List](#list).
`class `[`Output`](#output) | Base class for outputs that are of type dialog or behavior.
`class `[`DialogOutput`](#dialogoutput) | Subclass of [Output](#output) that represents a dialog response
`class `[`BehaviorOutput`](#behavioroutput) | Subclass of [Output](#output) that represents a behavior response
`class `[`ParameterValue`](#parametervalue) | An arbitrary value passed to an output or entity within a [Response](#response) from the Web API.
`class `[`Status`](#status) | Describe the status and any errors from a Web API response.
`class `[`Phoneme`](#phoneme) | Describe a single phoneme for an audio response, e.g., to drive automatic lip sync.
`class `[`VersionInfo`](#versioninfo) | Encapsulates version information for [PullString](#namespace_pullstring)'s Web API.
`enum `[`BuildType`](#ebuildtype) | The asset build type to request for Web API requests.
`enum` [`IfModifiedAction`](#eifmodifiedaction) | The Action to take for a conversation when new content is published
`enum `[`EntityType`](#eentitytype) | Define the list of entity types
`enum `[`OutputType`](#eoutputtype) | Define the set of outputs that can be returned in a response.
`enum `[`FeatureName`](#efeaturename) | Define features to check if they are supported.
`enum `[`AsrAudioFormat`](#easraudioformat) | Define Possible Speech Recognition formats.

<a name="namespace_pullstring"></a>

## Package com.pullstring.sdk
Main PullString SDK package

<a name="conversation"></a>

## class Conversation

The [Conversation](#conversation) class can be used to interface with the [PullString](#namespace_pullstring) API.

To begin a conversation, call the [Begin()](#conversation+begin) method, providing a [PullString](#namespace_pullstring) project ID and a [Request](#request) object specifying your API key.

The Web API returns a [Response](#response) object that can contain zero or more outputs, such as lines of dialog or behaviors. This [Response](#response) object is passed to the OnResponseReceived callback as its sole parameter.

#### Sample Code

```java

// The API Key and Project ID can be found by logging in to pullstring.com and
// navigating to Account > Web API keys (platform.pullstring.com/accounts/keys/)
static final String MY_API_KEY = '...';
static final String MY_PROJECT_ID = '...';

@Override
protected void onStart() {
    super.onStart();
    // Create a Conversation and a ResponseListener
    mConversation = new Conversation(new ResponseListener() {
        @Override
        public void onResponse(Response response) {
            // response.getOutputs() can contain dialog as well as any behaviors.
            ArrayList<Output> outputs = response.getOutputs();
            for (Output output : outputs) {
                if (output.getType() == OutputType.DIALOG) {
                    DialogOutput dialog = (DialogOutput) output;
                    // Often, we're most concerned with the dialog response text, but
                    // dialog responses can contain audio and video uris as well as
                    // the line's duration.
                    Log.d(TAG, dialog.getText());
                }

                // All custom behaviors defined in PullString Author are returned
                // by the Web API when they occur. Others, such as setting a label,
                // are internal to the bot and will not appear in responses.
                if (output.getType() == OutputType.BEHAVIOR) {
                    BehaviorOutput behavior (BehaviorOutput) output;
                    Log.d(TAG, behvior.getBehavior());
                }
            }

            // if timed response interval is set, set a timer to check the Web API
            // for more output in the specified number of seconds.
            double interval = response.getTimedResponseInterval();
            if (interval > 0) {
                // convert timed response interval to milliseconds
                long delayMillis = (long) (interval * 1000);

                // cancel the timer if it is running already.
                if (mTimerElapsed != null) {
                    mTimerElapsed.cancel();
                }

                if (mNoResponseTimer != null) {
                    mNoResponseTimer.cancel();
                }

                // start the timer
                mNoResponseTimer = new Timer();
                mTimerElapsed = new TimerTask() {
                    // When the timout expires, ping the Web API.
                    @Override
                    public void run() {
                        mConversation.checkForTimedResponse();
                    }
                };
                mNoResponseTimer.schedule(mTimerElapsed, delayMillis);
            }
        }
    });

    // To start a conversation, pass a valid Project ID and a request containing
    // at least a valid API Key. You can also set the request's conversationId
    // and participantId to a stored values to continue a previous conversation.
    Request request = new Request(API_KEY);
    mConversation.start(MY_PROJECT_ID, request);
}

...

// At some point, send some text to the bot.
void helloWorld()
{
    // stop the timer if it is running
    if (mTimerElapsed != null) {
        mTimerElapsed.cancel();
    }

    if (mNoResponseTimer != null) {
        mNoResponseTimer.cancel();
    }

    conversation.sendText("Hello, world!");
}

...

private Conversation mConversation;
private TimerTask mTimerElapsed;
private Timer mNoResponseTimer;

```

### Summary

* [Conversation(ResponseListener listener)](#conversation+constructor)
    * [.getConversationId()](#conversation+ConversationId)
    * [.getParticipantId()](#conversation+ParticipantId)
    * [.start(projectName, request)](#conversation+Start)
    * [.sendText(text)](#conversation+SendText)
    * [.sendText(text, request)](#conversation+SendText)
    * [.sendActivity(activity)](#conversation+SendActivity)
    * [.sendActivity(activity, request)](#conversation+SendActivity)
    * [.sendEvent(event, parameters)](#conversation+SendEvent)
    * [.sendEvent(event, parameters, request)](#conversation+SendEvent)
    * [.startAudio()](#conversation+StartAudio)
    * [.startAudio(request)](#conversation+StartAudio)
    * [.addAudio(buffer)](#conversation+AddAudio)
    * [.endAudio()](#conversation+EndAudio)
    * [.sendAudio(audio)](#conversation+SendAudio)
    * [.sendAudio(audio, format)](#conversation+SendAudio)
    * [.sendAudio(audio, format, request)](#conversation+SendAudio)
    * [.goTo(responseId)](#conversation+GoTo)
    * [.goTo(responseId, request)](#conversation+GoTo)
    * [.checkForTimedResponse()](#conversation+CheckForTimedResponse)
    * [.checkForTimedResponse(request)](#conversation+CheckForTimedResponse)
    * [.getEntities(entities)](#conversation+GetEntities)
    * [.getEntities(entities, request)](#conversation+GetEntities)
    * [.setEntities(entities)](#conversation+SetEntities)
    * [.setEntities(entities, request)](#conversation+SetEntities)

### Methods

<a name="conversation+ConversationId"></a>
#### `public String getConversationId()`

Get the current conversation ID. Cconversation IDs can persist across sessions, if desired.

<a name="conversation+ParticipantId"></a>
#### `public String getParticipantId()`

Get the current participant ID, which identifies the current state for clients. This can persist accross sessions, if desired.

<a name="conversation+constructor"></a>
#### `public Conversation(`[`ResponseListener`](#responselistener)` listener)`

Create a conversation, passing a [ResponseLisener](#responselistener) object to receive responses from the Web API.

```java
Conversation conversation = new Conversation(new ResposneListener() {
    @Override
    public void onResponse(Response response) {
        // handle response...
    }
});
```

**Parameters**
* `listener` The [ResponseLisener](#responselistener) that will receive responses from the Web API.

<a name="conversation+Start"></a>
#### `public void start(string project,`[`Request`](#request)` request)`

Start a new conversation with the Web API and receive a response via the OnResponseReceived event.

```java
Request request = new Request (MY_API_KEY);
conversation.start(MY_PROJECT, request);
```

**Parameters**
* `project` The PullSring project ID.
* `request` A [Request](#request) object with a valid ApiKey value set

<a name="conversation+SendText"></a>
#### `public void sendText(String text)`
#### `public void sendText(String text,`[`Request`](#request)` request)`

Send user input text to the Web API and receive a response via the OnResponseReceived event.

**Parameters**
* `text` User input text.
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+SendActivity"></a>
#### `public void sendActivity(String activity)`
#### `public void sendActivity(String activity,`[`Request`](#request)` request)`

Send an activity name or ID to the Web API and receive a response via the OnResponseReceived event.

**Parameters**
* `activity` The activity name or ID.
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+SendEvent"></a>
#### `public void sendEvent(String eventName, HashMap<String, Object> parameters)`
#### `public void sendEvent(String eventName, HashMap<String, Object> parameters,`[`Request`](#request)` request)`

Send an event to the Web API and receive a response via the OnResponseReceived event.

**Parameters**
* `eventName` The event name.
* `parameters` Any accompanying paramters.
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+GoTo"></a>
#### `public void goTo(String responseId)`
#### `public void goTo(String responseId,`[`Request`](#request)` request)`

Jump the conversation directly to a response.

**Parameters**
* `responseId` The UUID of the response to jump to.
* `request` [Optional] A request object with at least apiKey and conversationId set.


<a name="conversation+CheckForTimedResponse"></a>
#### `public void checkForTimedResponse()`
#### `public void checkForTimedResponse(`[`Request`](#request)` request)`

Call the Web API to see if there is a time-based response to process. You nly need to call this if the previous response returned a value for the timedResponseInterval >= 0. In this case, set a timer for that value (in seconds) and then call this method. If there is no time-based response, OnResponseReceived will be passed an empty [Response](#response) object.

**Parameters**
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+GetEntities"></a>
#### `public void getEntities(ArrayList<String> names)`
#### `public void getEntities(ArrayList<String> names,`[`Request`](#request)` request)`

[Request](#request) the values of the specified entities (i.e.: labels, counters, flags, and lists) from the Web API.

**Parameters**
* `names` An array of entity names.
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+SetEntities"></a>
#### `public void setEntities(ArrayList<`[`Entity`](#entity)`> entities)`
#### `public void setEntities(ArrayList<`[`Entity`](#entity)`> entities,`[`Request`](#request)` request)`

Change the value of the specified entities (i.e.: labels, counters, flags, and lists) via the Web API.

**Parameters**
* `entities` An array specifying the entities to set (with their new values).
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+SendAudio"></a>
#### `public void sendAudio(byte[] audio)`
#### `public void sendAudio(byte[] audio, `[`AsrAudioFormat`](#easraudioformat)` format)`
#### `public void sendAudio(byte[] audio, `[`AsrAudioFormat`](#easraudioformat)` format, `[`Request`](#request)` request)`

Send an entire audio sample of the user speaking to the Web API. Audio must be, mono 16-bit linear PCM at a sample rate of 16000 samples per second.

**Parameters**
* `clip` Mono 16-bit Wave file data at 16k Hz.
* `fomrat` [Optional] The audio sample's format [Currently, only AsrAudioFormat.WAV_16K is suported].
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+StartAudio"></a>
#### `public void startAudio(`[`Request`](#request)`)`
#### `public void startAudio(`[`Request`](#request)` request)`

Initiate a progressive (chunked) streaming of audio data, where supported.

If not supported, such as in the Web Player, this will batch up all audio and send it all at once when [EndAudio()](#conversation+EndAudio) is called.

**Parameters**
* `request` [Optional] A request object with at least apiKey and conversationId set.

<a name="conversation+AddAudio"></a>
#### `public void addAudio(short[] audio)`

Add a chunk of raw audio samples. You must call [StartAudio()](#conversation+StartAudio) first. The format of the audio must be mono linear PCM audio data at a sample rate of 16000 samples per second.

**Parameters**
* `audio`

<a name="conversation+EndAudio"></a>
#### `public void endAudio()`

Signal that all audio has been provided via [AddAudio()](#conversation+AddAudio) calls. This will complete the audio request and return the Web API response via the OnResponseReceived event.

<a name="responselistener"></a>

## abstract class ResponseListener

Define the interface for receiving responses from the PullString Web API.

### Summary

* [ResponseListener](#responselistener)
    * [.onResponse()](#responselistener+onResponse)

### Methods

<a name="responselistener+onResponse"></a>
#### `public abstract void onResponse(Response response)`

The method for receiving response from the PullString Web API.

```java
ResponseListener listener = new ResponseListener() {
    @Override
    public void onResponse(Response response) {
        // handle response...ß
    }
};
```

**Parameters**
* `response` A PullString Response object from the Web API.

<a name="request"></a>
# class Request

Describe the parameters for a request to the [PullString](#namespace_pullstring) Web API.


### Summary
* [Request](#request)
    * [.getApiKey()](#request+ApiKey)
    * [.setApiKey(apiKey)](#request+ApiKey)
    * [.getConversationId()](#request+ConversationId)
    * [.setConversationId(conversationId)](#request+ConversationId)
    * [.getParticipantId()](#request+ParticipantId)
    * [.setParticipantId(participantId)](#request+ParticipantId)
    * [.getBuildType()](#request+BuildType)
    * [.setBuildType(buildType)](#request+BuildType)
    * [.getLanguage()](#request+Language)
    * [.setLanguage(language)](#request+Language)
    * [.getLocale()](#request+Locale)
    * [.setLocale(locale)](#request+Locale)
    * [.getTimezoneOffset()](#request+TimezoneOffset)
    * [.setTimezoneOffset(timezoneOffset)](#request+TimezoneOffset)
    * [.getIfModifiedAction()](#request+IfModifiedAction)
    * [.setIfModifiedAction(ifModifiedAction)](#request+IfModifiedAction)
    * [.isRestartIfModified()](#request+RestartIfModified) *[deprecated]*
    * [.setRestartIfModified(restartIfModified)](#request+RestartIfModified) *[deprecated]*

### Methods

<a name="request+ApiKey"></a>
#### `public String getApiKey()`
#### `public void setApiKey(String apiKey)`

Get/set your API key, required for all requests.

<a name="request+participantId"></a>
#### `public String getParticipantId()`
#### `public void setParticipantId(String participantId)`

Get/set a String that identifies state to the Web API and can persist across sessions.

<a name="request+BuildType"></a>
#### `public String getBuildType()`
#### `public void setBuildType(`[`BuildType`](#ebuildtype)` buildType)`

Get/set the current build type. Default is [BuildType.PRODUCTION](#ebuildtype).

<a name="request+ConversationId"></a>
#### `public String getConversationId()`
#### `public void setConversationId(String conversationId)`

Get set String that identifies an ongoing conversation to the Web API and can persist across sessions. It is required after a conversation is started.

<a name="request+Language"></a>
#### `public String getLanguage()`
#### `public void setLanguage(String language)`

Get/set the ASR language. Default is 'en-US'.

<a name="request+Locale"></a>
#### `public String getLocale()`
#### `public void setLocale(String locale)`

Get/set user locale. Default is 'en-US'.

<a name="request+TimezoneOffset"></a>
#### `public int getTimezoneOffset()`
#### `public void setTimezoneOffset(int timezoneOffset)`

Get/set the value, in seconds, representing the offset in UTC. For example, PST would be -28800.

<a name="request+IfModifiedAction"></a>
#### `public IfModifiedAction getIfModifiedAction()`
#### `public void setIfModifiedAction(IfModifiedAction ifModifiedAction)`

Get/set the Action to take for a conversation when new content is published. Default value is [IfModifiedAction.NOTHING](#eifmodifiedaction+Nothing).

<a name="request+RestartIfModified"></a>
#### `public boolean isRestartIfModified()`
#### `public void setRestartIfModified(boolean restartifModified)`

Get/set whether to restart this conversation when a newer version of the project has been published. Default value is true.

<a name="ebuildtype"></a>
# enum BuildType

The asset build type to request for Web API requests.

* [BuildType.PRODUCTION](#ebuildtype+Production) (default)
* [BuildType.STAGING](#ebuildtype+Staging)
* [BuildType.SANDBOX](#ebuildtypeSandbox)

<a name="eifmodifiedaction"></a>
# class EIfModifiedAction

The Action to take for a conversation when new content is published.

* [EIfModifiedAction.RESTART](#eifmodifiedaction+Restart)
    * The conversation will restart when new content is published.
* [EIfModifiedAction.UPDATE](#eifmodifiedaction+Update)
    * The conversation will restart when new content is published while maintaining current state.
* [EIfModifiedAction.NOTHIN](#eifmodifiedaction+Nothing) (default)
    * The conversation will not restart when new content is published.

<a name="response"></a>
# class Response

Presents the output of a request to the [PullString](#namespace_pull_string) Web API.

### Summary
* [Response(JSONObject json)](#response+new)
    * [.getStatus()](#response+Status)
    * [.getConversationId()](#response+ConversationId)
    * [.getParticipantId()](#response+ParticipantId)
    * [.getETag()](#response+ETag)
    * [.getOutputs()](#response+Outputs)
    * [.getEntities()](#response+Entities)
    * [.getLastModified()](#response+LastModified)
    * [.getTimedResponseInterval()](#response+TimedResponseInterval)
    * [.getAsrHypothesis()](#response+AsrHypothesis)
    * [.getEndpoint()](#response+Endpoint)

### Mehods

<a name="response+new"></a>
#### `public  Response(JSONObject json)`

Constructs a `Response` object from JSON.

<a name="response+Status"></a>
#### `public `[`Status`](#status)` getStatus()`

Represents status and any errors returned by Web API

<a name="response+conversationId"></a>
#### `public String getConversationId()`

Identifies an ongoing conversation to the Web API and can persist across sessions. It is required after a conversation is started.

<a name="response+ParticipantId"></a>
#### `public String getParticipantId()`

Identifies state to the Web API and can persist across sessions.

<a name="response+ETag"></a>
#### `public String getETag()`

Unique identifier for a version of the content.

<a name="response+Outputs"></a>
#### `public ArrayList<`[`Output`](#output)`> getOutputs()`

Dialog or behaviors returned from the Web API.

<a name="response+Entities"></a>
#### `public ArrayList<`[`Entity`](#entity)`> getEntities()`

Counters, flags, etc for the conversation.

<a name="response+LastModified"></a>
#### `public Date getLastModified()`

Time of content modification.

<a name="response+TimedResponseInterval"></a>
#### `public double getTimedResponseInterval()`

Indicates that there may be another response to process in the specified number of seconds. Set a timer and call checkForTimedResponse() from a conversation to retrieve it.

<a name="response+AsrHypothesis"></a>
#### `public String getAsrHypothesis()`

The recognized speech, if audio has been submitted.

<a name="response+Endpoint"></a>
#### `public String getEndpoint()`

The public endpoint for the current conversation.

<a name="dialogoutput"></a>
# class DialogOutput

```
class DialogOutput
  extends com.pullstring.sdk.Output
```

Subclass of [Output](#output) that represents a dialog response

### Summary

* [DialogOutput(JSONObject json)](#dialogoutput+new)
    * [.getType()](#dialogoutput+Type)
    * [.getText()](#dialogoutput+Text)
    * [.setText(text)](#dialogoutput+Text)
    * [.getAudioUri()](#dialogoutput+AudioUri)
    * [.setAudioUri(audioUri)](#dialogoutput+AudioUri)
    * [.getVideoUri()](#dialogoutput+VideoUri)
    * [.setVideoUri(videoUri)](#dialogoutput+VideoUri)
    * [.getCharacter()](#dialogoutput+Character)
    * [.setCharacter(character)](#dialogoutput+Character)
    * [.getDuration()](#dialogoutput+Duration)
    * [.setDuration(duration)](#dialogoutput+Duration)
    * [.getUserData()](#dialogoutput+UserData)
    * [.setUserData(userData)](#dialogoutput+UserData)
    * [.getPhonemes()](#dialogoutput+Phonemes)
    * [.setPhonemes(phonemes)](#dialogoutput+Phonemes)

### Methods

<a name="dialogoutput+new"></a>
#### `public  DialogOutput(Dictionary< string, object > json)`

Constructs a `DialogOutput` object from JSON.

<a name="dialogoutput+Type"></a>
#### `public `[`OutputType`](#eoutputtype)` getType()`

[OutputType.DIALOG](#eoutputtype+Dialog) (read only)

<a name="dialogoutput+Text"></a>
#### `public String getText()`
#### `public void setText(String text)`

Get/set character's text response.

<a name="dialogoutput+AudioUri"></a>
#### `public String getAudioUri()`
#### `public void setAudioUri(String audioUri)`

Get/set location of recorded audio, if available.

<a name="dialogoutput+VideoUri"></a>
#### `public String getVideoUri()`
#### `public void setVideoUri(videoUri)`

Get/set location of recorded video, if available.

<a name="dialogoutput+Character"></a>
#### `public String getCharacter()`
#### `public void setCharacter(String character)`

Get/set the speaking character.

<a name="dialogoutput+Duration"></a>
#### `public double getDuration()`
#### `public void setDuration(String duration)`

Get/set duration of spoken line in seconds.

<a name="dialogoutput+UserData"></a>
#### `public String getUserData()`
#### `public void setUserData(String userData)`

Get/set optional arbitrary string data that was associated with the dialog line within PullString Author.

<a name="dialogoutput+Phonemes"></a>
#### `public ArrayList<`[`Phoneme`](#phoneme)`> getPhonemes()`
#### `public void setPhonemes(ArrayList<`[`Phoneme`](#phoneme)`> phonemes)`

Get/set Optional arbitrary string data that was associated with the dialog line within PullString Author.


<a name="behavioroutput"></a>
# class BehaviorOutput

```
class BehaviorOutput
  extends com.pullstring.sdk.Output
```

Subclass of [Output](#output) that represents a behavior response

### Summary

* [BehaviorOutput(JSONObject json)](#behavioroutput+new)
    * [.getType()](#behavioroutput+Type)
    * [.getBehavior()](#behavioroutput+Behavior)
    * [.setBehavior(behavior)](#behavioroutput+Behavior)
    * [.getParameters()](#behavioroutput+ParameterValue)
    * [.setParameters(parameters)](#behavioroutput+ParameterValue)


### Methods

<a name="behavioroutput+new"></a>
#### `public  BehaviorOutput(Dictionary< string, object > json)`

Construct a `BehaviorOutput` object from JSON.

<a name="behavioroutput+Type"></a>
#### `public [OutputType](#eoutputtype) getType()`

[OutputType.BEHAVIOR](#eoutputtype.Behavior) (read only)

<a name="behavioroutput+Behavior"></a>
#### `public String getBehavior()`
#### `public void setBehavior(String behavior)`

Get/set the name of the behavior.

<a name="behavioroutput+ParameterValue"></a>
#### `public HashMap<String, `[`ParameterValue`](#parametervalue)`> getParameters()`
#### `public void getParameters(HashMap<String, `[`ParameterValue`](#parametervalue)`> parameters)`

Get/set a dictionary with any parameters defined for the behavior.

<a name="output"></a>
# class Output

Base class for outputs that are of type dialog or behavior.


### Summary

* [Output](#output)
    * [getType()](#output+Type)
    * [getGuid()](#output+Guid)

### Methods

<a name="output+Type"></a>
#### `public String getType()`

An [OutputType](#e_output_type), i.e.

* [OutputType.DIALOG](#eoutputtype.Dialog)
* [OutputType.BEHAVIOR](#eoutputtype.Behavior)

<a name="behavioroutput+Guid"></a>
#### `public String getGuid()`

Unique identifier for this [Output](#output).

<a name="eoutputtype"></a>
# enum OutputType

Define the set of outputs that can be returned in a response.

* [OutputType.DIALOG](#eoutputtype.Dialog)
* [OutputType.BEHAVIOR](#eoutputtype.Behavior)

<a name="parametervalue"></a>
# class ParameterValue

An arbitrary value passed to an output or entity within a Response from the Web API.

### Summary

* [ParameterValue(Object param)](#parametervalue+new)
    * [.getString()](#parametervalue+String)
    * [.getDouble()](#parametervalue+Double)
    * [.getBoolean()](#parametervalue+Boolean)
    * [.getArray()](#parametervalue+Array)
    * [.getDictionary()](#parametervalue+Dictionary)
    * [.getRawValue()](#parametervalue+Value)
    * [.setValue(param)](#parametervalue+Set)

### Methods

<a name="parametervalue+new"></a>
#### `public ParameterValue(Object param)`

Create a ParameterValue from an arbitrary `Object`

<a name="parametervalue+String"></a>
#### `public String getString()`

Get the value safely cast to a String.

<a name="parametervalue+Double"></a>
#### `public double getDouble()`

Get the value safely cast to a double.

<a name="parametervalue+Boolean"></a>
#### `public boolean getBoolean()`

Get the value safely cast to a boolean.

<a name="parametervalue+Array"></a>
#### `public ArrayList<ParameterValue> getArray()`

Get the value safely cast to a ArrayList.

<a name="parametervalue+Dictionary"></a>
#### `public HashMap<String, ParameterValue> getDictionary()`

Get the value safely cast to a HashMap.

<a name="parametervalue+Value"></a>
#### `public Object getRawValue()`

Get the original value as an `Object`.

<a name="#parametervalue+Set"></a>
#### `public void setValue(Object param)`

Set a new raw value -- String, double, boolean, ArrayList, JSONArray, HashMap, or JSONObject are supported.

<a name="counter"></a>
# class Counter

```
class Counter
  extends com.pullstring.sdk.Entity<Double>
```

Subclass of [Entity](#entity) to describe a single counter.

### Summary

* [Counter(name, value)](#counter+new)
    * [.getType()](#counter+Type)
    * [.getValue())](#counter+DoubleValue)

### Methods

<a name="counter+new"></a>
#### `public  Counter(String name, double value)`

Constructor

<a name="counter+Type"></a>
#### `public String getType()`

[EntityType.COUNTER](#eentitytype+Counter)

<a name="counter+DoubleValue"></a>
#### `public double getValue()`

The counter's underlying numeric value.

<a name="flag"></a>
# class Flag

```
class Flag
  extends com.pullstring.sdk.Entity<Boolean>
```

Subclass of [Entity](#entity) to describe a single [Flag](#flag).


### Summary

* [Flag(name, value)](#flag+new)
    * [.getType()](#flag+Type)
    * [.getValue())](#flag+BoolValue)

### Methods

<a name="flag+new"></a>
#### `public  Flag(String name, boolean value)`

Constructor

<a name="flag+Type"></a>
#### `public EntityType getType()`

[EntityType.FLAG](#eentitytype+Flag)

<a name="flag+BoolValue"></a>
#### `public boolean getValue()`

The flag's underlying boolean value.

<a name="label"></a>
# class Label

```
class Label
  extends public com.pullstring.sdk.Entity<String>
```

Subclass of [Entity](#entity) to describe a single [Label](#label).

### Summary

* [Label(name, value)](#label+new)
    * [.getType()](#label+Type)
    * [.getValue())](#label+StringValue)

### Methods

<a name="label+new"></a>
#### `public  Label(String name, String value)`

Constructor

<a name="label+Type"></a>
#### `public EntityType getType()`

[EntityType.LABEL](#eentitytype+Label)

<a name="label+StringValue"></a>
#### `public String getValue()`

The label's underlying String value.

<a name="list"></a>
# class List

```
class List
  extends public com.pullstring.sdk.Entity<ArrayList>
```

Subclass of [Entity](#entity) to describe a single [List](#list).


### Summary

* [List(name, value)](#list+new)
    * [.getType()](#list+Type)
    * [.getValue())](#list+ArrayValue)

### Mehods

<a name="list+new"></a>
#### `public  List(String name, ArrayList value)`

Constructor

<a name="list+Type"></a>
#### `public EntityType getType()`

[EntityType.LIST](#eentitytype+List)

<a name="list+ArrayValue"></a>
#### `public ArrayList getValue()`

The list's underlying ArrayList value.


<a name="entity"></a>
# abstract class Entity<T>

Base class to describe a single entity, such as a label, counter, flag, or list.

### Summary

* [Entity](#entity)
    * [.getType()](#entity+Type)
    * [.getName()](#entity+Name)
    * [.getValue()](#entity+Value)

### Methods

<a name="entity+Type"></a>
#### `public EntityType getType()`

[EEntityType](#eentitytype) for this object, i.e.

* [EEntityType.Label](#eentitytype+Label)
* [EEntityType.Counter](#eentitytype+Counter)
* [EEntityType.Flag](#eentitytype+Flag)
* [EEntityType.List](#eentitytype+List)

<a name="entity+Name"></a>
#### `public String getName()`

Descriptive name of entity.

<a name="entity+Value"></a>
#### `public T getValue()`

The value of the entity, the type of which will vary depending on the subclass.

* [Label](#label): string
* [Counter](#counter): double
* [Flag](#flag): bool
* [List](#list): object[]


<a name="eentitytype"></a>
# enum EntityType

Define the list of entity types

* [EEntityType.LABEL](#eentitytype+Label)
* [EEntityType.COUNTER](#eentitytype+Counter)
* [EEntityType.FLAG](#eentitytype+Flag)
* [EEntityType.LIST](#eentitytype+List)

<a name="phoneme"></a>
# class Phoneme

Describe a single phoneme for an audio response, e.g., to drive automatic lip sync.

### Summary

* [Phonome(JSONObject json)](#phoneme+new)
    * [.getName()](#phoneme+Name)
    * [.setName(name)](#phoneme+Name)
    * [.getSecondsSinceStart()](#phoneme+SecondsSinceStart)
    * [.setSecondsSinceStart(secondsSinceStart)](#phoneme+SecondsSinceStart)

### Methods

<a name="phoneme+new"></a>
#### `public  Phoneme(Dictionary< string, object > json)`

Construct a phoneme from JSON.

<a name="phoneme+Name"></a>
#### `public String getName()`
#### `public void setName(String name)`

Get/set the phoneme's descriptive value.

<a name="phoneme+SecondsSinceStart"></a>
#### `public double getSecondsSinceStart()`
#### `public void setSecondsSinceStart(double secondsSinceStart)`

Get/set the start time for the phoneme, relative to the beginning of a line of dialog.


<a name="status"></a>
# class Status

Describe the status and any errors from a Web API response.

### Summary

* [Status](#status)
    * [.getSuccess()](#status+Success)
    * [.setSuccess(success)](#status+Success)
    * [.getStatusCode()](#status+StatusCode)
    * [.setStatusCode(statusCode)](#status+StatusCode)
    * [.getErrorMessage()](#status+ErrorMessage)
    * [.setErrorMessage(errorMessage)](#status+ErrorMessage)

### Properties

<a name="status+Sucess"></a>
#### `public boolean isSuccess()`
#### `public void setSuccess(boolean success)`

Get/set whether status is success or failure.

<a name="status+StatusCode"></a>
#### `public int getStatusCode()`
#### `public void setStatusCode(int statusCode)`

Get/set the server or internal status code.  If an internal (non-server) error occurs, the possible codes are:

* Encoding Error: -1
* Parsing Error: -2
* Bad Request: -3

<a name="status+ErrorMessage"></a>
#### `public String getErrorMessage()`
#### `public void setErrorMessage(String errorMessage)`

Get/set a descriptive message.

<a name="versioninfo"></a>
# class VersionInfo

Encapsulates version information for [PullString](#namespace_pullstring)'s Web API.

### Summary

| Name | Type | Description
| ---- | ---- |------------ |
| [`API_BASE_URL`](#versioninfo+ApiBaseUrl) | `string` | The public-facing endpoint of the [PullString](#namespace_pullstring) Web API.

* [VersionInfo](#versioninfo)
    * [hasFeature](#versioninfo+HasFeature)

### Properties

<a name="versioninfo+ApiBaseUrl"></a>
#### `public static String API_BASE_URL`

The public-facing endpoint of the [PullString](#namespace_pullstring) Web API.

### Methods

<a name="versioninfo+HasFeature"></a>
#### `public static boolean hasFeature(FeatureName feature)`

Check if the SDK currently supports a feature.

**Parameters**
`feature` A [FeatureName](#featurename) value

**Returns** `true` if the feature is supported.

<a name=#featurename></a>
# enum FeatureName

Define feature to check if they are supported.

* FeatureName.STREAMING_ASR
