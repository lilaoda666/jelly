package com.lhy.jelly.ui.chat;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhy.jelly.databinding.AbsBaseChatBinding;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.sdk.models.Message;

public class ChatMessageAdapter extends RecyclerView.Adapter<AbsBaseChatViewHolder> {

    public static final String STATUS_PAYLOAD = "messageStatus";
    public static final String PROGRESS_PAYLOAD = "messageProgress";
    public static final String REVOKE_PAYLOAD = "messageRevoke";
    public static final String SIGNAL_PAYLOAD = "messageSignal";

    private List<Message> messageList = new ArrayList<>();


    @NonNull
    @Override
    public AbsBaseChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getViewHolderDefault(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsBaseChatViewHolder holder, int position) {
        Message data = messageList.get(position);
        Message lastMessage = null;
        if (position - 1 >= 0) {
            lastMessage = messageList.get(position - 1);
        }
        holder.bindData(data, lastMessage);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsBaseChatViewHolder holder, int position, @NonNull List<Object> payloads) {
      if(payloads.isEmpty()){
          super.onBindViewHolder(holder, position, payloads);
      }else {
          Message message = messageList.get(position);
          holder.bindData(message,payloads);
      }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        message.getContentType();
        return message.getContentType();
    }

    public AbsBaseChatViewHolder getViewHolderDefault(ViewGroup parent, int viewType) {
        AbsBaseChatBinding absBaseChatBinding = AbsBaseChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatTextMessageViewHolder(absBaseChatBinding, viewType);
    }

    public void appendMessages(List<Message> message) {
        removeSameMessage(message);
        int pos = messageList.size();
        messageList.addAll(message);
        notifyItemRangeInserted(pos, message.size());
    }

    private void removeSameMessage(List<Message> message){
        if (message == null || message.size() < 1){
            return;
        }
        for (Message bean : message) {
            int index = -1;
            for (int j = 0; j < messageList.size(); j++) {
                if (TextUtils.equals(bean.getClientMsgID(),messageList.get(j).getClientMsgID())) {
                    index = j;
                    break;
                }
            }
            if (index > -1) {
                messageList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public void appendMessage(Message message) {
        int pos = messageList.size();
        messageList.add(message);
        notifyItemInserted(pos);
    }

    public void clearMessageList() {
        int size = messageList.size();
        messageList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
