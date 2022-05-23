package com.lhy.jelly.service;

import android.service.media.MediaBrowserService;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.lhy.jelly.bean.MusicBean;

public class MusicServiceConnection {

    private MutableLiveData<Boolean> isConnected  = new MutableLiveData<>();
    private MutableLiveData<MusicBean> test  = new MutableLiveData<>();

}
