package lhy.library.utils;


import android.widget.Toast;

import androidx.annotation.StringRes;

import lhy.library.base.LhyApplication;


public class ToastUtils {

    private ToastUtils() {
    }

    public static void show(String content) {
        Toast toast = Toast.makeText(LhyApplication.getContext(), content, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.TOP,0,120);
        toast.show();
    }

    public static void show(@StringRes int resId) {
        Toast.makeText(LhyApplication.getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showNetError() {
        Toast.makeText(LhyApplication.getContext(), "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
    }

    public static void showNoMore() {
        Toast.makeText(LhyApplication.getContext(), "没有更多数据了...", Toast.LENGTH_SHORT).show();
    }
}
