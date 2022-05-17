package com.lhy.jelly.adapter;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lhy.jelly.R;
import com.lhy.jelly.bean.MusicBean;
import com.lhy.jelly.bean.VideoBean;

import java.io.File;

import lhy.library.utils.DateUtils;
import lhy.library.utils.FileUtils;


/**
 * Created by Liheyu on 2017/8/30.
 * Email:liheyu999@163.com
 */

public class VideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public VideoAdapter() {
        super(R.layout.item_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBean item) {
        helper.setText(R.id.text_duration, DateUtils.stringForTime((int) item.getDuration()));
        String title = item.getTitle();
        int i = title.indexOf(".");
        if (i > 0) {
            title = title.substring(0, i);
        }
        helper.setText(R.id.text_title, title);
        helper.setText(R.id.text_size, FileUtils.getFormatSize(item.getSize()));
        ImageView imageThumb = helper.getView(R.id.img_thumb);
//        Glide.with(getContext()).load(item.getThumbPath()).into(imageThumb);
        Glide.with(getContext()).load(Uri.fromFile(new File(item.getPath()))).into(imageThumb);
    }

}
