package com.lhy.jelly.utils;


import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.load.engine.Resource;
import com.lhy.jelly.bean.ApiResult;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lhy.library.http.HttpObserver;
import lhy.library.http.exception.ApiException;

/**
 * Created by Lihy on 2018/6/28 14:55
 * E-Mail ：liheyu999@163.com
 */
public class RxUtils {

    public static <T> Observable<T> wrapHttp(Observable<ApiResult<T>> observable) {
        return observable.map(tApiResult -> {
            if (tApiResult.getStatus() != 1) {
                throw new ApiException(tApiResult.getMessage());
            }
            return tApiResult.getData();
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> wrapRx(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    public static <T> void wrapLiveData(Observable<T> observable, MutableLiveData<Resource<T>> liveData) {
//        observable.subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe(d -> liveData.setValue(Resource.loading()))
//                .subscribe(new HttpObserver<T>() {
//                    @Override
//                    public void onSuccess(T value) {
//                        if(liveData.hasActiveObservers())
//                        liveData.setValue(Resource.success(value));
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        liveData.setValue(Resource.error(msg));
//                    }
//                });
//    }
}
