package com.lhy.jelly.ui.login;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.bean.LoginParam;
import com.lhy.jelly.bean.User;
import com.lhy.jelly.constants.RouteConstants;
import com.lhy.jelly.data.ApiService;
import com.lhy.jelly.data.ServiceManager;
import com.lhy.jelly.databinding.ActivityLoginBinding;
import com.lhy.jelly.utils.RxUtils;
import com.lhy.jelly.utils.TextClickSpan;
import com.lhy.jelly.wxapi.WXUtils;
import com.orhanobut.logger.Logger;

import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import lhy.library.http.LhyManager;
import lhy.library.http.HttpObserver;
import lhy.library.utils.CommonUtils;
import lhy.library.utils.ToastUtils;


/**
 * Created by Liheyu on 2017/9/7.
 * Email:liheyu999@163.com
 */

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        WXUtils.initWeChat(this.getApplicationContext());
    }

    private void initView() {
        binding.btnLogin.setOnClickListener(v -> {
            doLogin();
        });
        SpannableString spannableString = new SpannableString("我已阅读并同意《用户协议》他《隐私协议》");
        spannableString.setSpan(new TextClickSpan() {
            @Override
            public void clickText(@NonNull View widget) {
                clickUserProtocol();
            }
        }, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new TextClickSpan() {
            @Override
            public void clickText(@NonNull View widget) {
                clickPrivateProtocol();
            }
        }, 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textPrivate.setText(spannableString);
        binding.textPrivate.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textPrivate.setHighlightColor(getResources().getColor(android.R.color.transparent));
        binding.imgLoginWechat.setOnClickListener(v -> WXUtils.longinWx());
    }

    private void clickPrivateProtocol() {
        ToastUtils.show("clickPrivateProtocol");
    }

    private void clickUserProtocol() {
        ToastUtils.show("clickUserProtocol");
    }


    private boolean checkData() {
        if (TextUtils.isEmpty(CommonUtils.getString(binding.textPhone))) {
            ToastUtils.show("账号或密码不能为空");
            return false;
        }
        return true;
    }

    public void doLogin() {
//        loginIm(null);
        LoginParam loginParam = new LoginParam();
        loginParam.setPassword("123456");
        loginParam.setPhone("13922239153");
        RxUtils.wrapHttp(ServiceManager.getApiService().login(loginParam))
                .to(autoDispose())
                .subscribe(new HttpObserver<User>() {
                    @Override
                    public void onSuccess(User value) {
                        loginIm(value);
                    }
                });
    }

    private void loginIm(User value) {
        OpenIMClient.getInstance().login(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Logger.d("onError " + error);
            }

            @Override
            public void onSuccess(String data) {
                gotoMain();
            }
        }, String.valueOf(value.getId()), value.getToken());
    }

    private void gotoMain() {
        ARouter.getInstance().build(RouteConstants.ROUTE_PATH_ACTIVITY_MAIN).navigation();
    }

    public void doRegister(){
        LoginParam loginParam = new LoginParam();
        loginParam.setPassword("123456");
        loginParam.setPhone("13922239153");
        RxUtils.wrapHttp(ServiceManager.getApiService().register(loginParam))
                .to(autoDispose())
                .subscribe(new HttpObserver<User>() {
                    @Override
                    public void onSuccess(User value) {
                        loginIm(value);
                    }
                });
    }

}
