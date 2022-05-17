package com.lhy.jelly.ui.video;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.databinding.ActivityVideoPlayBinding;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;

public class VideoPlayerActivity extends BaseActivity {

    private ActivityVideoPlayBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SuperPlayerView playView = binding.playView;

        String url = getIntent().getStringExtra("url");
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperPlayerModel model = new SuperPlayerModel();
//                model.url="/storage/emulated/0/Pictures/Screenshots/SVID_20220118_135850_1.mp4";
                model.url = url;
                playView.playWithModel(model);
            }
        });

    }
}
