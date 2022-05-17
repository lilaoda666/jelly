package com.lhy.jelly.base;

import com.lhy.jelly.BuildConfig;

import lhy.library.base.LhyApplication;

public class BaseApplication extends LhyApplication {


    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
