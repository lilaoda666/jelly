package com.lhy.jelly.ui.chat;

import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lhy.jelly.databinding.AbsBaseChatBinding;
import com.lhy.jelly.databinding.ChatMessageTextViewHolderBinding;

import io.openim.android.sdk.models.Message;

public class ChatTextMessageViewHolder extends AbsBaseChatViewHolder {

    private ChatMessageTextViewHolderBinding textBinding;

    public ChatTextMessageViewHolder(@NonNull AbsBaseChatBinding binding, int viewType) {
        super(binding, viewType);
    }

    @Override
    public void addContainer() {
        textBinding = ChatMessageTextViewHolderBinding.inflate(LayoutInflater.from(parent.getContext()),
                getContainer(), true);
    }

    @Override
    public void bindData(Message message, Message lastMessage) {
        super.bindData(message, lastMessage);
        TextView messageText = textBinding.messageText;
        messageText.setText(message.getContent());
    }

}
