package com.lhy.jelly.ui.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.bean.ApiResult;
import com.lhy.jelly.bean.User;
import com.lhy.jelly.constants.RouteConstants;
import com.lhy.jelly.databinding.ActivityLoginBinding;

import java.util.concurrent.TimeUnit;

import autodispose2.AutoDispose;
import autodispose2.ScopeProvider;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableConverter;
import lhy.library.http.RxObserver;
import lhy.library.utils.CommonUtils;
import lhy.library.utils.ToastUtils;


/**
 * Created by Liheyu on 2017/9/7.
 * Email:liheyu999@163.com
 */

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private EditText editAccount;
    private EditText editPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        editAccount = binding.editAccount;
        editPassword = binding.editPassword;
        editAccount.setText("13922239152");
        editPassword.setText("123456");
        binding.btnLogin.setOnClickListener(v -> {
            if (checkData()) {
                doLogin();
            }
        });
    }


    private boolean checkData() {
        if (TextUtils.isEmpty(CommonUtils.getString(editAccount)) || TextUtils.isEmpty(CommonUtils.getString(editPassword))) {
            ToastUtils.show("账号或密码不能为空");
            return false;
        }
        return true;
    }

    @SuppressLint("AutoDispose")
    public void doLogin() {
        User user = new User();
        user.setAccount(CommonUtils.getString(editAccount));
        user.setPassword(CommonUtils.getString(editPassword));
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .to(autoDispose())
                .subscribe(new RxObserver<Long>() {
                    @Override
                    public void onSuccess(Long value) {
                        gotoMain();
                    }
                });
    }


    private void gotoMain() {
        ARouter.getInstance().build(RouteConstants.ROUTE_PATH_JELLY_MAIN_ACTIVITY).navigation();
    }
}
