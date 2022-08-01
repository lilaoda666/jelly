package com.lhy.jelly.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhy.jelly.base.BaseFragment;
import com.lhy.jelly.databinding.FragmentChatBinding;
import com.lhy.jelly.databinding.FragmentChatListBinding;

public class ChatListFragment extends BaseFragment {

    private FragmentChatListBinding binding;

    public static ChatListFragment newInstance() {
        Bundle args = new Bundle();
        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater);
        binding.btnTest.setOnClickListener(v -> startActivity(new Intent(getContext(), ChatActivity.class)));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
