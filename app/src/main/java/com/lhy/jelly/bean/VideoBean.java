package com.lhy.jelly.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lihy on 2018/4/20 14:59
 * E-Mail ：liheyu999@163.com
 */
public class VideoBean {

    private String path;
    private String title;
    //xxx.mp4
    private String displayName;
    private long duration;
    private String thumbPath;
    private long size;

    public VideoBean() {
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
