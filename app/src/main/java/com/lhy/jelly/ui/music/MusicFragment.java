package com.lhy.jelly.ui.music;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lhy.jelly.R;
import com.lhy.jelly.adapter.MusicAdapter;
import com.lhy.jelly.base.BaseFragment;
import com.lhy.jelly.bean.MusicBean;
import com.lhy.jelly.databinding.FragmentMusicBinding;
import com.lhy.jelly.utils.MusicUtils;
import com.lhy.jelly.utils.RxUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions3.RxPermissions;


import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import lhy.library.http.RxObserver;
import lhy.library.utils.ToastUtils;

/**
 * Created by Liheyu on 2017/8/21.
 * Email:liheyu999@163.com
 */

public class MusicFragment extends BaseFragment {

    private FragmentMusicBinding binding;
    private RecyclerView rlvMusic;
    private SmartRefreshLayout refreshLayout;

    private MusicAdapter mMusicAdapter;

    public static MusicFragment newInstance() {
        Bundle args = new Bundle();
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater);
        initView();
        return binding.getRoot();
    }


    private void doRefresh() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .to(autoDispose())
                .subscribe(b -> {
                    if (b) {
                        scanMusic();
                    } else {
                        ToastUtils.show("无读写SD卡权限");
                    }
                });
    }

    private void scanMusic() {
        RxUtils.wrapRx(Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> {
            List<MusicBean> mp3Infos = MusicUtils.getMusicList(getContext().getApplicationContext());
            emitter.onNext(mp3Infos);
        }))
                .to(autoDispose())
                .subscribe(new RxObserver<List<MusicBean>>() {
                    @Override
                    public void onSuccess(List<MusicBean> value) {
                        mMusicAdapter.setNewInstance(value);
                        refreshLayout.finishRefresh(true);
                    }

                    @Override
                    public void onFailure(String msg) {
                        refreshLayout.finishRefresh(false);
                    }
                });
    }


    private void initView() {
        TextView textTitle = binding.getRoot().findViewById(R.id.text_title);
        textTitle.setText("音乐");
        rlvMusic = binding.rlvMusic;
        refreshLayout = binding.refreshLayout;
        rlvMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        mMusicAdapter = new MusicAdapter();
        rlvMusic.setAdapter(mMusicAdapter);
        mMusicAdapter.setOnItemClickListener((adapter, view, position)
                -> ToastUtils.show(String.valueOf(position)));
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableOverScrollDrag(false);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener((v) -> doRefresh());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
