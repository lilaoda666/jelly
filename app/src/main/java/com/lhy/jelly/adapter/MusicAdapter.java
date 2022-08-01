package com.lhy.jelly.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;


import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lhy.jelly.R;
import com.lhy.jelly.bean.MusicBean;

import java.util.List;

import lhy.library.utils.DateUtils;


/**
 * Created by Liheyu on 2017/8/30.
 * Email:liheyu999@163.com
 */

public class MusicAdapter extends BaseQuickAdapter<MusicBean, BaseViewHolder> {

    public MusicAdapter() {
        super(R.layout.item_music);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicBean item) {
        helper.setText(R.id.text_music_artist,item.getArtist());
        helper.setText(R.id.text_music_name,item.getTitle());
        helper.setText(R.id.text_duration, DateUtils.stringForTime((int) item.getDuration()));
    }

}
