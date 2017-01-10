/*
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdktestbed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    ChatMessageAdapter() {
        mMessages = new ArrayList<>();
    }

    void addMessage(String message, boolean isBot) {
        ChatMessage chat = new ChatMessage(message, isBot);
        mMessages.add(chat);
        notifyItemInserted(mMessages.size() - 1);
    }

    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v =(LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);

        // set text
        TextView textView = (TextView) holder.mChatView.findViewById(R.id.botTextView);
        textView.setText(message.message);

        // only show badge for either bot or user
        TextView hiddenBadge = (TextView) holder.mChatView.findViewById(message.isBot ? R.id.youName : R.id.botName);
        hiddenBadge.setVisibility(View.GONE);

        TextView shownBadge = (TextView) holder.mChatView.findViewById(message.isBot ? R.id.botName : R.id.youName);
        shownBadge.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mChatView;
        ViewHolder(LinearLayout v) {
            super(v);
            mChatView = v;
        }
    }

    private static class ChatMessage {
        boolean isBot;
        String message;

        ChatMessage(String text, boolean isBot) {
            this.isBot = isBot;
            message = text;
        }
    }

    private ArrayList<ChatMessage> mMessages;
}
