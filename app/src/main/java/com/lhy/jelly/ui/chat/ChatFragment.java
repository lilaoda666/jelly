package com.lhy.jelly.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lhy.jelly.R;
import com.lhy.jelly.base.BaseFragment;
import com.lhy.jelly.base.ImLiveData;
import com.lhy.jelly.databinding.FragmentChatBinding;

import java.util.List;

import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnAdvanceMsgListener;
import io.openim.android.sdk.listener.OnMsgSendCallback;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.OfflinePushInfo;
import io.openim.android.sdk.models.ReadReceiptInfo;
import lhy.library.utils.CommonUtils;

public class ChatFragment extends BaseFragment {


    private FragmentChatBinding binding;
    private RecyclerView rlvChat;
    private ChatMessageAdapter mChatMessageAdapter;

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);
        initView();
        initListener();
        return binding.getRoot();

    }

    private void initListener() {
        ImLiveData.getInstance().registerAdvanceMsgListener(new OnAdvanceMsgListener() {
            @Override
            public void onRecvNewMessage(Message msg) {
                mChatMessageAdapter.appendMessage(msg);
            }

            @Override
            public void onRecvC2CReadReceipt(List<ReadReceiptInfo> list) {

            }

            @Override
            public void onRecvGroupMessageReadReceipt(List<ReadReceiptInfo> list) {

            }

            @Override
            public void onRecvMessageRevoked(String msgId) {

            }
        });
    }

    private void initView() {
        rlvChat = binding.rlvChat;
        rlvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatMessageAdapter = new ChatMessageAdapter();
        rlvChat.setAdapter(mChatMessageAdapter);
        binding.send.setOnClickListener(v -> sendMsg());
    }

    private void sendMsg() {
        OpenIMClient.getInstance().messageManager.sendMessage(new OnMsgSendCallback() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(long progress) {

            }

            @Override
            public void onSuccess(Message s) {
                mChatMessageAdapter.appendMessage(s);
            }
        },OpenIMClient.getInstance().messageManager.createTextMessage(CommonUtils.getString(binding.input)),"3","",new OfflinePushInfo());
    }
}
