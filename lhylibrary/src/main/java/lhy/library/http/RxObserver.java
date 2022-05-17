package lhy.library.http;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;


import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lhy.library.base.ActivityUtils;
import lhy.library.base.LhyApplication;
import lhy.library.http.exception.ApiException;
import lhy.library.widget.LoadingDialog;
import retrofit2.HttpException;


public abstract class RxObserver<T> implements Observer<T> {

    private static final String TAG = "RxObserver.class";

    private Activity mActivity;
    private LoadingDialog mLoadingDialog;
    private Disposable mDisposable;
    private boolean isUserCancel;
    private boolean isShowToast = true;
    private boolean isShowDialog = false;

    public RxObserver() {
    }

    public RxObserver(boolean showDialog) {
        this.isShowDialog = showDialog;
    }

    public RxObserver(boolean showDialog, boolean isShowToast) {
        this.isShowToast = isShowToast;
        this.isShowDialog = showDialog;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.mDisposable = d;
        showDialog();
    }

    @Override
    public void onNext(T value) {
        hideDialog();
        onSuccess(value);
    }

    @Override
    public void onError(final Throwable e) {
        e.printStackTrace();
        hideDialog();

        String errorMsg;
        if (e instanceof SocketTimeoutException) {
            errorMsg = "服务器响应超时，请稍候再试";
        } else if (e instanceof ConnectException) {
            errorMsg = "网络连接错误，请检查网络";
        } else if (e instanceof JsonParseException) {
            errorMsg = "数据解析失败";
        } else if (e instanceof SQLiteException) {
            errorMsg = "数据操作失败";
        } else if (e instanceof HttpException) {
            errorMsg = "连接失败：" + e.getMessage();
        } else if (e instanceof IOException) {
            errorMsg = "请求失败: " + e.getMessage();
        } else if (e instanceof ApiException) {
            errorMsg = e.getMessage();
        } else {
            errorMsg = !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "连接失败，请稍候再试";
        }

        //如果用户主动取消 则不提示任何信息
        if (isShowToast) {
            if (!isUserCancel) {
                Looper looper = Looper.myLooper();
                if (looper != null && looper.getThread().getId() == Looper.getMainLooper().getThread().getId()) {
                    Toast.makeText(LhyApplication.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            } else {
                isUserCancel = false;
            }
        }
        onFailure(e);
        onFailure(errorMsg);
    }

    @Override
    public void onComplete() {
        hideDialog();
    }

    private void showDialog() {
        if (!isShowDialog) {
            return;
        }
        mActivity = ActivityUtils.getCurrentActivity();
        if (mActivity == null) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mActivity);
            mLoadingDialog.show();
            mLoadingDialog.setOnCancelListener(dialog -> {
                Log.i(TAG, "user cancel");
                isUserCancel = true;
                mDisposable.dispose();
                mActivity = null;
                mLoadingDialog = null;
            });
        }
        mLoadingDialog.show();
    }

    private void hideDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        mActivity = null;
    }

    public abstract void onSuccess(T value);

    public void onFailure(Throwable e) {
    }

    public void onFailure(String msg) {
    }
}
