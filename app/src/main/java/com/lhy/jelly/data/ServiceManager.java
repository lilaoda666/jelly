package com.lhy.jelly.data;


import com.lhy.jelly.constants.Config;

import lhy.library.http.LhyManager;

public class ServiceManager {

    private static ApiService mApiService;

    private ServiceManager() {
    }

    public static ApiService getApiService() {
        if (mApiService == null) {
            mApiService = LhyManager.getInstance().createService(Config.API_HOST, ApiService.class);
        }
        return mApiService;
    }
}
