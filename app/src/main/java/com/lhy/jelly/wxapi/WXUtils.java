package com.lhy.jelly.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import androidx.navigation.PopUpToBuilder;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import lhy.library.utils.ToastUtils;

public class WXUtils {

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String WECHAT_APP_ID = "wx88888888";

    // IWXAPI 是第三方 app 和微信通信的 openApi 接口
    private static IWXAPI mWXApi;

   public static void initWeChat(Context context) {
        mWXApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);

        //建议动态监听微信启动广播进行注册到微信
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该 app 注册到微信
                mWXApi.registerApp(WECHAT_APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    public static void longinWx() {
        if (!mWXApi.isWXAppInstalled()) {
            ToastUtils.show("未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWXApi.sendReq(req);

    }

    public static void handleIntent(Intent intent, IWXAPIEventHandler handler){
       mWXApi.handleIntent(intent,handler);
    }
}
