package com.lhy.jelly.ui.video;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhy.jelly.R;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.databinding.ActivityVideoPlayBinding;
import com.tencent.liteav.demo.common.utils.IntentUtils;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerGlobalConfig;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;
import com.tencent.rtmp.TXLiveConstants;

import lhy.library.utils.StatusBarUtil;

public class VideoPlayerActivity extends BaseActivity {

    public static final String TAG = VideoPlayerActivity.class.getName();
    private static final float sPlayerViewDisplayRatio = (float) 720 / 1280;   //当前界面播放器view展示的宽高比，用主流的16：9

    private ActivityVideoPlayBinding binding;
    private SuperPlayerView mSuperPlayerView;

    private boolean mIsManualPause = false;
    private RelativeLayout rlTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        initSuperVodGlobalSetting();
        initData();
    }

    private void initData() {
        String url = getIntent().getStringExtra("url");
        SuperPlayerModel model = new SuperPlayerModel();
        model.url = url;
        mSuperPlayerView.playWithModel(model);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
        // 开启悬浮窗播放
        prefs.enableFloatWindow = true;
        // 设置悬浮窗的初始位置和宽高
        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
        rect.x = 0;
        rect.y = 0;
        rect.width = 810;
        rect.height = 540;
        prefs.floatViewRect = rect;
        // 播放器默认缓存个数
        prefs.maxCacheItem = 5;
        // 设置播放器渲染模式
        prefs.enableHWAcceleration = true;
        prefs.renderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        //需要修改为自己的时移域名
//        prefs.playShiftDomain = "liteavapp.timeshift.qcloud.com";
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING
                || mSuperPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PAUSE) {
            Log.i(TAG, "onResume state :" + mSuperPlayerView.getPlayerState());
            if (!mSuperPlayerView.isShowingVipView() && !mIsManualPause) {
                mSuperPlayerView.onResume();
            }
            if (mSuperPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FLOAT) {
                mSuperPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
            }
        }
        if (mSuperPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN) {
            //隐藏虚拟按键，并且全屏
            View decorView = getWindow().getDecorView();
            if (decorView == null) return;
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                decorView.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
        mSuperPlayerView.setNeedToPause(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause state :" + mSuperPlayerView.getPlayerState());
        if (mSuperPlayerView.getPlayerMode() != SuperPlayerDef.PlayerMode.FLOAT) {
            // 有手动暂停
            if (mSuperPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PAUSE) {
                mIsManualPause = true;
            } else {
                mIsManualPause = false;
            }
            mSuperPlayerView.onPause();
            mSuperPlayerView.setNeedToPause(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuperPlayerView.release();
        if (mSuperPlayerView.getPlayerMode() != SuperPlayerDef.PlayerMode.FLOAT) {
            mSuperPlayerView.resetPlayer();
        }
    }

    private void initView() {
        rlTitle = binding.rlTitle;
        mSuperPlayerView = binding.playView;
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        mSuperPlayerView.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
            @Override
            public void onStartFullScreenPlay() {
                rlTitle.setVisibility(GONE);
            }

            @Override
            public void onStopFullScreenPlay() {
                rlTitle.setVisibility(VISIBLE);
            }

            @Override
            public void onClickFloatCloseBtn() {
                // 点击悬浮窗关闭按钮，那么结束整个播放
                mSuperPlayerView.resetPlayer();
                finish();
            }

            @Override
            public void onClickSmallReturnBtn() {
                // 点击小窗模式下返回按钮，开始悬浮播放
                showFloatWindow();
            }

            @Override
            public void onStartFloatWindowPlay() {
                // 开始悬浮播放后，直接返回到桌面，进行悬浮播放
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                IntentUtils.safeStartActivity(VideoPlayerActivity.this, intent);
            }

            @Override
            public void onPlaying() {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onError(int code) {

            }
        });
        adjustSuperPlayerViewAndMaskHeight();
    }


    /**
     * 以16：9 比例显示播放器view，优先保证宽度完全填充
     */
    private void adjustSuperPlayerViewAndMaskHeight() {
        final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = mSuperPlayerView.getLayoutParams();
        layoutParams.width = screenWidth;
        layoutParams.height = (int) (screenWidth * sPlayerViewDisplayRatio);
        mSuperPlayerView.setLayoutParams(layoutParams);
    }

    /**
     * 悬浮窗播放
     */
    private void showFloatWindow() {
        if (mSuperPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING) {
            mSuperPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.FLOAT);
        } else {
            mSuperPlayerView.resetPlayer();
            finish();
        }
    }

    @Override
    public void setStatusBar() {
//        StatusBarUtil.setColor(this,getResources().getColor(R.color.blue1));
        StatusBarUtil.setTransparentForImageView(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
