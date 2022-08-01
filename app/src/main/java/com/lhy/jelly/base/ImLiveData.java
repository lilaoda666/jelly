package com.lhy.jelly.base;

import com.lhy.jelly.constants.Config;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnAdvanceMsgListener;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnConnListener;
import io.openim.android.sdk.listener.OnFriendshipListener;
import io.openim.android.sdk.listener.OnMsgSendCallback;
import io.openim.android.sdk.models.BlacklistInfo;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.OfflinePushInfo;
import io.openim.android.sdk.models.ReadReceiptInfo;
import io.openim.android.sdk.models.UserInfo;

public class ImLiveData {

    private static ImLiveData instance = null;

    private final ArrayList<OnAdvanceMsgListener> mAdvanceMsgListeners = new ArrayList<>();
    private final ArrayList<OnFriendshipListener> mFriendshipListeners = new ArrayList<>();

    private ImLiveData() {
    }

    public static ImLiveData getInstance() {
        if (instance == null) {
            synchronized (ImLiveData.class) {
                if (instance == null) {
                    instance = new ImLiveData();
                }
            }
        }
        return instance;
    }

    public void init() {
        initIm();
        initMsgListener();
        initFriendshipManager();
    }

    public void registerAdvanceMsgListener(OnAdvanceMsgListener listener) {
        synchronized (mAdvanceMsgListeners) {
            mAdvanceMsgListeners.add(listener);
        }
    }

    public void unRegisterAdvanceMsgListener(OnAdvanceMsgListener listener) {
        synchronized (mAdvanceMsgListeners) {
            mAdvanceMsgListeners.remove(listener);
        }
    }

    public void registerFriendshipListener(OnFriendshipListener listener) {
        synchronized (mAdvanceMsgListeners) {
            mFriendshipListeners.add(listener);
        }
    }

    public void unRegisterFriendshipListener(OnFriendshipListener listener) {
        synchronized (mAdvanceMsgListeners) {
            mFriendshipListeners.remove(listener);
        }
    }

    private void initFriendshipManager() {
        OpenIMClient.getInstance().friendshipManager.setOnFriendshipListener(new OnFriendshipListener() {
            @Override
            public void onBlacklistAdded(BlacklistInfo u) {
                // 拉入黑名单
            }

            @Override
            public void onBlacklistDeleted(BlacklistInfo u) {
                // 从黑名单删除
            }

            @Override
            public void onFriendApplicationAccepted(FriendApplicationInfo u) {
                // 发出或收到的好友申请已同意
            }

            @Override
            public void onFriendApplicationAdded(FriendApplicationInfo u) {
                // 发出或收到的好友申请被添加
            }

            @Override
            public void onFriendApplicationDeleted(FriendApplicationInfo u) {
                // 发出或收到的好友申请被删除
            }

            @Override
            public void onFriendApplicationRejected(FriendApplicationInfo u) {
                // 发出或收到的好友申请被拒绝
            }

            @Override
            public void onFriendInfoChanged(FriendInfo u) {
                // 朋友的资料发生变化
            }

            @Override
            public void onFriendAdded(FriendInfo u) {
                // 好友被添加
            }

            @Override
            public void onFriendDeleted(FriendInfo u) {
                // 好友被删除
            }
        });
    }

    private void initMsgListener() {
        OpenIMClient.getInstance().messageManager.setAdvancedMsgListener(new OnAdvanceMsgListener() {
            @Override
            public void onRecvNewMessage(Message msg) {
                // 收到新消息，界面添加新消息
            }

            @Override
            public void onRecvC2CReadReceipt(List<ReadReceiptInfo> list) {
                // 消息被阅读回执，将消息标记为已读
            }

            @Override
            public void onRecvGroupMessageReadReceipt(List<ReadReceiptInfo> list) {

            }

            @Override
            public void onRecvMessageRevoked(String msgId) {
                // 消息成功撤回，从界面移除消息
            }
        });
    }

    private void initIm() {
        String storageDir = BaseApplication.getContext().getCacheDir().getAbsolutePath();
        String objectStorage = "cos"; // 图片上传服务器如 腾讯cos
        boolean result = OpenIMClient.getInstance().initSDK(Config.IM_API_URL, Config.IM_WS_URL, storageDir, Config.IM_LOG_LEVEL, objectStorage,
                new OnConnListener() {
                    @Override
                    public void onConnectFailed(long code, String error) {
                        // 连接服务器失败，可以提示用户当前网络连接不可用
                        Logger.d("onConnectFailed");
                    }

                    @Override
                    public void onConnectSuccess() {
                        // 已经成功连接到服务器
                        Logger.d("onConnectSuccess");
                    }

                    @Override
                    public void onConnecting() {
                        // 正在连接到服务器，适合在 UI 上展示“正在连接”状态。
                        Logger.d("onConnecting");
                    }

                    @Override
                    public void onKickedOffline() {
                        // 当前用户被踢下线，此时可以 UI 提示用户“您已经在其他端登录了当前账号，是否重新登录？”
                        Logger.d("onKickedOffline");
                    }

                    @Override
                    public void onUserTokenExpired() {
                        // 登录票据已经过期，请使用新签发的 UserSig 进行登录。
                        Logger.d("onUserTokenExpired");
                    }
                });
        Logger.d("初始化" + result);
    }

    public void sendMessage(String text,String recvId){
        Message textMessage = OpenIMClient.getInstance().messageManager.createTextMessage(text);
        OpenIMClient.getInstance().messageManager.sendMessage(new OnMsgSendCallback() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(long progress) {

            }

            @Override
            public void onSuccess(Message s) {

            }
        }, textMessage,recvId,null,new OfflinePushInfo());
    }

    private void getSelfUserInfo(){
        OpenIMClient.getInstance().userInfoManager.getSelfUserInfo(new OnBase<UserInfo>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(UserInfo data) {
                // 返回当前登录用户的资料
            }
        });
    }

}
