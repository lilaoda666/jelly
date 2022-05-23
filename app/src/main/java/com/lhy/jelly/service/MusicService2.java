package com.lhy.jelly.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lhy.jelly.BuildConfig;
import com.lhy.jelly.R;
import com.lhy.jelly.bean.MusicBean;
import com.orhanobut.logger.Logger;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class MusicService2 extends Service implements ITXVodPlayListener {

    public static final String TAG = "MusicService";

    public static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + "music";
    public static final int NOTIFY_ID = 999;

    private NotificationManager mNotificationManager;
    private TXVodPlayer mVodPlayer;
    private SuperPlayerDef.PlayerState mCurrentPlayState;
    private MusicBroadCastReceiver mMusicReceiver;
    private RemoteViews mRemoteViews;
    private int mSeekPos;
    private Notification mNotification;
    private Notification.Builder mNotifyBuild;

    public static void start(Context context) {
        Intent service = new Intent(context, MusicService2.class);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotify();
        showForegroundForO();
        Logger.d(TAG + "onCreate");
        EventBus.getDefault().register(this);
        initBroadReceiver();
        initData();
    }

    private void initNotify() {
        initRemoteViews();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyBuild = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(mRemoteViews)
                .setSound(null)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mNotification = mNotifyBuild.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG + "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mMusicReceiver);
    }


    private void initBroadReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicBroadCastReceiver.CLOSE);
        filter.addAction(MusicBroadCastReceiver.PLAY);
        filter.addAction(MusicBroadCastReceiver.PLAY_NEXT);
        filter.addAction(MusicBroadCastReceiver.PLAY_PRE);
        mMusicReceiver = new MusicBroadCastReceiver();
        registerReceiver(mMusicReceiver, filter);
    }

    private void showForegroundForO() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "一起听", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mNotifyBuild.setChannelId(CHANNEL_ID);
            startForeground(NOTIFY_ID, mNotifyBuild.build());
            stopForegroundForO();
        }
    }


    private void initData() {
        mVodPlayer = new TXVodPlayer(getApplicationContext());
        mVodPlayer.setVodListener(this);
    }

    private void initRemoteViews() {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify_play);
        PendingIntent playIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicBroadCastReceiver.PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicBroadCastReceiver.CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playNextIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicBroadCastReceiver.PLAY_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPreIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicBroadCastReceiver.PLAY_PRE), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.img_play, playIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.img_play_next, playNextIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.img_play_prev, playPreIntent);
    }


    private void updateNotify() {
        updateRemoteViews();
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    private void updateRemoteViews() {
        if (mMusicList != null && mMusicList.size() > mCurrentPlayPosition) {
            MusicBean musicBean = mMusicList.get(mCurrentPlayPosition);
            mRemoteViews.setTextViewText(R.id.text_music_name, musicBean.getTitle());
            mRemoteViews.setTextViewText(R.id.text_name, musicBean.getArtist());
        }
    }

    private void stopForegroundForO() {
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> stopForeground(true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playMessage(List<MusicBean> musicList) {
        beginPlay(musicList);
    }

    List<MusicBean> mMusicList = null;
    int mCurrentPlayPosition = 0;

    public void beginPlay(List<MusicBean> list) {
        if (list.size() == 0) return;
        mMusicList = list;
        mCurrentPlayPosition = 0;
        if(mVodPlayer.isPlaying()){
            mVodPlayer.stopPlay(true);
        }
        mVodPlayer.startPlay(mMusicList.get(mCurrentPlayPosition).getUrl());
        updateNotify();
    }

    public void seek(int position) {
        if (mVodPlayer != null) {
            mVodPlayer.seek(position);
            if (!mVodPlayer.isPlaying()) {
                mVodPlayer.resume();
            }
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle bundle) {
        switch (event) {
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED:

                break;
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:

                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                playNextMusic();
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    private final class MusicBroadCastReceiver extends BroadcastReceiver {

        public static final String PLAY_PRE = "com.lhy.jelly.play_pre";
        public static final String PLAY_NEXT = "com.lhy.jelly.play_next";
        public static final String PLAY = "com.lhy.jelly.play";
        public static final String CLOSE = "com.lhy.jelly.close";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case CLOSE:
                    Logger.d("onReceive " + CLOSE);
                    break;
                case PLAY:
                    Logger.d("onReceive " + PLAY);
                    playOrPauseMusic();
                    break;
                case PLAY_NEXT:
                    Logger.d("onReceive " + PLAY_NEXT);
                    playNextMusic();
                    break;
                case PLAY_PRE:
                    Logger.d("onReceive " + PLAY_PRE);
                    playPreMusic();
                    break;
            }
        }

    }

    private void playOrPauseMusic() {
        if (mVodPlayer != null) {
            if (mVodPlayer.isPlaying()) {
                mVodPlayer.pause();
                mSeekPos = (int) mVodPlayer.getCurrentPlaybackTime();
            } else {
                mVodPlayer.setStartTime(mSeekPos);
                mVodPlayer.startPlay(mMusicList.get(mCurrentPlayPosition).getUrl());
            }
        }
    }

    private void playNextMusic() {
        if (mVodPlayer != null) {
            mSeekPos = 0;
            if (mMusicList.size() - 1 > mCurrentPlayPosition) {
                mCurrentPlayPosition++;
                mVodPlayer.startPlay(mMusicList.get(mCurrentPlayPosition).getUrl());
                updateNotify();
            } else {
                stopPlay();
            }
        }
    }

    private void stopPlay() {
        if (mVodPlayer != null) {
            mVodPlayer.stopPlay(false);
        }
    }

    private void playPreMusic() {
        if (mVodPlayer != null) {
            mSeekPos = 0;
            if (mMusicList.size() - 1 >= mCurrentPlayPosition && mCurrentPlayPosition > 0) {
                mCurrentPlayPosition--;
                mVodPlayer.startPlay(mMusicList.get(mCurrentPlayPosition).getUrl());
                updateNotify();
            }
        }
    }


}
