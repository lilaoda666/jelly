package com.lhy.jelly.data;

import com.lhy.jelly.bean.ApiResult;
import com.lhy.jelly.bean.LoginParam;
import com.lhy.jelly.bean.User;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/user/login")
    Observable<ApiResult<User>> login(@Body LoginParam param);

    @POST("/user/add")
    Observable<ApiResult<User>> register(@Body LoginParam param);

}
