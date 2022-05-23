package com.lhy.jelly.ui.home;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lhy.jelly.R;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.constants.RouteConstants;
import com.lhy.jelly.databinding.ActivityMainBinding;
import com.lhy.jelly.service.MusicService2;
import com.lhy.jelly.ui.chat.ChatFragment;
import com.lhy.jelly.ui.mine.MineFragment;
import com.lhy.jelly.ui.music.MusicFragment;
import com.lhy.jelly.ui.video.VideoFragment;

import java.util.ArrayList;

import lhy.library.utils.StatusBarUtil;

@Route(path = RouteConstants.ROUTE_PATH_JELLY_MAIN_ACTIVITY)
public class MainActivity extends BaseActivity {

    public static final String[] TABS = {"music", "video", "chat", "mine"};

    private ActivityMainBinding binding;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initFragments(savedInstanceState);
        initView();
    }

    private void initFragments(Bundle savedInstanceState) {
        mFragments = new ArrayList<>();
        MusicFragment musicFragment = null;
        VideoFragment videoFragment = null;
        ChatFragment chatFragment = null;
        MineFragment meFragment = null;
        if (savedInstanceState != null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            musicFragment = (MusicFragment) supportFragmentManager.findFragmentByTag(TABS[0]);
            videoFragment = (VideoFragment) supportFragmentManager.findFragmentByTag(TABS[1]);
            chatFragment = (ChatFragment) supportFragmentManager.findFragmentByTag(TABS[2]);
            meFragment = (MineFragment) supportFragmentManager.findFragmentByTag(TABS[3]);
        }
        if (musicFragment == null) {
            musicFragment = MusicFragment.newInstance();
        }
        if (videoFragment == null) {
            videoFragment = VideoFragment.newInstance();
        }
        if (chatFragment == null) {
            chatFragment = ChatFragment.newInstance();
        }
        if (meFragment == null) {
            meFragment = MineFragment.newInstance();
        }
        mFragments.add(musicFragment);
        mFragments.add(videoFragment);
        mFragments.add(chatFragment);
        mFragments.add(meFragment);
    }

    private void navigationFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
            if (i == position) {
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragmentTransaction.add(R.id.fl_content, fragment, TABS[position]);
                }
            }
        }
        fragmentTransaction.commit();
    }

    private void initView() {
        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigationFragment(2);
            } else if (itemId == R.id.navigation_music) {
                navigationFragment(0);
            } else if (itemId == R.id.navigation_video) {
                navigationFragment(1);
            }
            if (itemId == R.id.navigation_mine) {
                navigationFragment(3);
            }
            return true;
        });
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this);
    }
}