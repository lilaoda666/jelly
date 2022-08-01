package com.lhy.jelly.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.lhy.jelly.base.BaseFragment;
import com.lhy.jelly.databinding.FragmentMineBinding;
import com.lhy.jelly.utils.RxUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import lhy.library.http.AbsObserver;

/**
 * Created by Liheyu on 2017/8/21.
 * Email:liheyu999@163.com
 */

public class MineFragment extends BaseFragment {


    private FragmentMineBinding binding;
    private boolean isRefreshIng;
    private TabLayout tabLayout;
    private SmartRefreshLayout refreshView;
    private ImageView imgHeadBg;
    private AppBarLayout appBar;

    public static MineFragment newInstance() {

        Bundle args = new Bundle();

        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater);
        initView();
        initScrollListener();
        return binding.getRoot();
    }

    private void initView() {
        tabLayout = binding.tabLayout;
        refreshView = binding.refreshView;
        imgHeadBg = binding.imgHeadBg;
        appBar = binding.appBar;

        TabLayout.Tab tab = tabLayout.newTab();
        tabLayout.addTab(tab);
        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);
        tab.setText("点赞");
        tab2.setText("收藏");
        refreshView.setOnRefreshListener(refreshLayout -> doRefresh());
        refreshView.setEnableRefresh(true);
    }

    private void doRefresh() {
        RxUtils.wrapRx(Observable.timer(1, TimeUnit.SECONDS))
                .to(autoDispose())
                .subscribe(new AbsObserver<Long>() {
                    @Override
                    public void onSuccess(Long value) {
                        refreshView.finishRefresh(true);
                    }

                    @Override
                    public void onFailure(String msg) {
                        refreshView.finishRefresh(false);
                    }
                });
    }

    private void initScrollListener() {
        int maxHeight = SmartUtil.dp2px(370F);
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int totalScrollRange = appBarLayout.getTotalScrollRange();
            float alpha = verticalOffset * 1F / totalScrollRange;
            binding.flTitle.setAlpha(alpha);
            //当刷新时会意外回调一次verticalOffset = 0
            if (isRefreshIng) return;
            imgHeadBg.setTranslationY(verticalOffset * 0.8f);
        });
        refreshView.setOnMultiListener(new SimpleMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                isRefreshIng = offset != 0;
                float fl = offset * 0.8F;
                imgHeadBg.setTranslationY(offset);
                float scale = 1 + (fl / maxHeight);
                imgHeadBg.setScaleX(scale);
                imgHeadBg.setScaleY(scale);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
