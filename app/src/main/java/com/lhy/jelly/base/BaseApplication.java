package com.lhy.jelly.base;

import android.content.Context;

import com.lhy.jelly.BuildConfig;
import com.lhy.jelly.R;
import com.lhy.jelly.ui.home.MainActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;


import lhy.library.base.LhyApplication;

public class BaseApplication extends LhyApplication {


    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImLiveData.getInstance().init();
    }

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.colorPrimary, R.color.colorPrimary);//全局设置主题颜色
            MaterialHeader materialHeader = new MaterialHeader(context);
            materialHeader.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark);
            return materialHeader;
            //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }
}
