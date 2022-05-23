package com.lhy.jelly.ui.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhy.jelly.adapter.VideoAdapter;
import com.lhy.jelly.base.BaseFragment;
import com.lhy.jelly.bean.VideoBean;
import com.lhy.jelly.databinding.FragmentVideoBinding;
import com.lhy.jelly.utils.RxUtils;
import com.lhy.jelly.utils.VideoUtils;

import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions3.RxPermissions;


import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import lhy.library.http.RxObserver;
import lhy.library.utils.ToastUtils;

/**
 * Created by Liheyu on 2017/8/21.
 * Email:liheyu999@163.com
 */

public class VideoFragment extends BaseFragment {

    private FragmentVideoBinding binding;
    private VideoAdapter mVideoAdapter;
    private RecyclerView rlvVideo;
    private SmartRefreshLayout refreshLayout;

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater);
        initView();
        discoverBrowseableMediaApps(getContext());
        return binding.getRoot();
    }

    private void doRefresh() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .to(autoDispose())
                .subscribe(b -> {
                    if (b) {
                        scanVideo();
                    } else {
                        ToastUtils.show("无读写SD卡权限");
                    }
                });
    }

    private void scanVideo() {
        RxUtils.wrapRx(Observable.create((ObservableOnSubscribe<List<VideoBean>>) emitter -> {
            List<VideoBean> videoBeans = VideoUtils.getList(getContext().getApplicationContext());
            emitter.onNext(videoBeans);
        }))
                .to(autoDispose())
                .subscribe(new RxObserver<List<VideoBean>>() {
                    @Override
                    public void onSuccess(List<VideoBean> value) {
                        mVideoAdapter.setNewInstance(value);
                        refreshLayout.finishRefresh(true);
                    }

                    @Override
                    public void onFailure(String msg) {
                        refreshLayout.finishRefresh(false);
                    }
                });
    }


    private void initView() {
        rlvVideo = binding.rlvVideo;
        refreshLayout = binding.refreshLayout;
        rlvVideo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mVideoAdapter = new VideoAdapter();
        rlvVideo.setAdapter(mVideoAdapter);
        mVideoAdapter.setOnItemClickListener((adapter, view, position)
                -> {
            VideoBean item = mVideoAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("url", item.getPath());
            startActivity(intent);
        });
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
    private void discoverBrowseableMediaApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(MediaBrowserService.SERVICE_INTERFACE);
        List<ResolveInfo> services = packageManager.queryIntentServices(intent, 0);
        for (ResolveInfo resolveInfo : services) {
            if (resolveInfo.serviceInfo != null && resolveInfo.serviceInfo.applicationInfo != null) {

                ApplicationInfo applicationInfo = resolveInfo.serviceInfo.applicationInfo;
                String label = (String) packageManager.getApplicationLabel(applicationInfo);
                Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                String className = resolveInfo.serviceInfo.name;
                String packageName = resolveInfo.serviceInfo.packageName;
                Logger.d(packageName+"  "+className);
            }
        }

    }
}
