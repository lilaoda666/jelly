package com.lhy.jelly.ui.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lhy.jelly.R;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.databinding.ActivityChatBinding;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar("聊天");
        initView();
    }

    private void initView() {
        getSupportFragmentManager().beginTransaction().add(R.id.fl_chat, ChatFragment.newInstance()).commit();
    }
}
