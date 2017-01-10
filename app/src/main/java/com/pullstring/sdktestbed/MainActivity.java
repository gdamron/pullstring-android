/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdktestbed;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pullstring.sdk.Conversation;
import com.pullstring.sdk.DialogOutput;
import com.pullstring.sdk.Output;
import com.pullstring.sdk.OutputType;
import com.pullstring.sdk.Request;
import com.pullstring.sdk.Response;
import com.pullstring.sdk.ResponseListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configure recycler view and adapter
        mChatRecycler = (RecyclerView) findViewById(R.id.chatRecyclerView);
        mChatRecycler.setHasFixedSize(true);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatMessageAdapter();
        mChatRecycler.setAdapter(mAdapter);

        // configure text input
        mTextInput = (EditText) findViewById(R.id.user_input);
        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId != EditorInfo.IME_ACTION_GO) { return false; }

                cancelTimer();

                // send text input to web api and update view
                String text = mTextInput.getText().toString();
                mTextInput.setText("");
                mAdapter.addMessage(text, false);
                mChatRecycler.scrollToPosition(mAdapter.getItemCount() - 1);
                mConversation.sendText(text);

                return true;
            }
        });

        // configure speech input, hold button to talk
        mRecordButton = (ImageButton) findViewById(R.id.record_button);
        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mSpeechRecorder.start();
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mSpeechRecorder.stop();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // instantiate a conversation and response listener
        mConversation = new Conversation(new ResponseListener() {
            @Override
            public void onResponse(Response response) {
                double delayTime = 0.0;

                // add any speech input to the view
                String asr = response.getAsrHypothesis();
                if (asr != null && !asr.isEmpty()) {
                    mAdapter.addMessage(asr, false);
                    mChatRecycler.scrollToPosition(mAdapter.getItemCount() - 1);
                }

                // add any bot responses to the view
                ArrayList<Output> outputs = response.getOutputs();
                for (Output output : outputs) {
                    if (output.getType() == OutputType.DIALOG) {
                        DialogOutput dialog = (DialogOutput) output;
                        mAdapter.addMessage(dialog.getText(), true);
                        mChatRecycler.scrollToPosition(mAdapter.getItemCount() - 1);

                        delayTime += dialog.getDuration();
                    }
                }

                // set timer to check for more input, if necessary
                double interval = response.getTimedResponseInterval();
                if (interval > 0) {
                    delayTime += interval;
                    long delayMillis = (long) (delayTime * 1000);

                    cancelTimer();

                    mNoResponseTimer = new Timer();
                    mTimerElapsed = new ConversationTimerTask();
                    mNoResponseTimer.schedule(mTimerElapsed, delayMillis);
                }
            }
        });

        // get mic permissions or create recorder
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 0);
        } else {
            mSpeechRecorder = new SpeechRecorder(mConversation);
        }

        // start new conversation
        Request request = new Request(API_KEY);
        mConversation.start(PROJECT, request);
    }

    private void cancelTimer() {
        if (mTimerElapsed != null) {
            mTimerElapsed.cancel();
        }

        if (mNoResponseTimer != null) {
            mNoResponseTimer.cancel();
        }
    }

    // ping Web API for more output when timer elapses
    private class ConversationTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mConversation != null) {
                mConversation.checkForTimedResponse();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                // disable speech button is permission not granted or instantiate recorder
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mRecordButton.setEnabled(false);
                } else {
                    mSpeechRecorder = new SpeechRecorder(mConversation);
                }
                break;
            default:
                break;
        }
    }

    private Conversation mConversation;
    private RecyclerView mChatRecycler;
    private EditText mTextInput;
    private ImageButton mRecordButton;
    private ChatMessageAdapter mAdapter;
    private Timer mNoResponseTimer;
    private TimerTask mTimerElapsed;
    private SpeechRecorder mSpeechRecorder = null;

    // API key and project ID for the Rock, Paper, Scissors bot
    private static final String API_KEY = "9fd2a189-3d57-4c02-8a55-5f0159bff2cf";
    private static final String PROJECT = "e50b56df-95b7-4fa1-9061-83a7a9bea372";
}
