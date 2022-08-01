package com.lhy.jelly.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.lhy.jelly.BuildConfig;
import com.lhy.jelly.R;
import com.lhy.jelly.bean.MusicBean;
import com.lhy.jelly.utils.MusicUtils;
import com.orhanobut.logger.Logger;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXVodPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicService extends MediaBrowserServiceCompat implements ITXVodPlayListener {

    public static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + "music";
    public static final int NOTIFY_ID = 999;

    public static final String TAG = "MusicService";

    public static final String MY_MEDIA_ROOT_ID = "media_root_id";
    public static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mMediaSession;
    private MediaSessionCompat.Token mSessionToken;

    private int mSeekPos;

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };
    private BroadcastReceiver myNoisyAudioStreamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private TXVodPlayer mPlayer;
    private AudioManager mAudioManager;
    private MediaNotificationManager mMediaNotificationManager;
    private List<MediaMetadataCompat> mCurrentPlaylistItems = Collections.emptyList();
    private int mCurrentMediaItemIndex = 0;
    private PlaybackStateCompat mPlaybackState;
    private NotificationManager mNotificationManager;

    public static void start(Context context) {
        Intent service = new Intent(context, MusicService.class);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        initMediaSession();
        mMediaNotificationManager = new MediaNotificationManager(this);
    }


    private void notifyTEST() {
//        mMediaNotificationManager = new MediaNotificationManager(this);
//        MediaControllerCompat controller = mMediaSession.getController();
//        mCurrentDescription = mediaList.get(mCurrentPosition).getDescription();
//        mNextAction = new NotificationCompat.Action(R.drawable.ic_play_next, "下一首",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
//        mPrevAction = new NotificationCompat.Action(R.drawable.ic_play_prev, "上一首",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
//        mPlayAction = new NotificationCompat.Action(R.drawable.ic_play_black, "播放",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY));
//        mPauseAction = new NotificationCompat.Action(R.drawable.ic_play_pause_black, "暂停",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE));
//        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
//        builder
//                .setContentTitle(mCurrentDescription.getTitle())
//                .setContentText(mCurrentDescription.getSubtitle())
//                .setSubText(mCurrentDescription.getDescription())
//                .setLargeIcon(mCurrentDescription.getIconBitmap())
//                .setContentIntent(controller.getSessionActivity())
//                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                        PlaybackStateCompat.ACTION_STOP))
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSmallIcon(R.drawable.girl1)
//                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .addAction(mPrevAction)
//                .addAction(mPlayAction)
//                .addAction(mPauseAction)
//                .addAction(mNextAction)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mMediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0)
//                        .setShowCancelButton(true)
//                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                                PlaybackStateCompat.ACTION_STOP)));
//        startForeground(NOTIFY_ID, builder.build());
    }

    private void initMediaSession() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, launchIntentForPackage, 0);
        mMediaSession = new MediaSessionCompat(getApplicationContext(), TAG);
        mMediaSession.setSessionActivity(pendingIntent);
        //必须定义后才能在session的回调里收到
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build();
        mSessionToken = mMediaSession.getSessionToken();
        mMediaSession.setPlaybackState(mPlaybackState);
        mMediaSession.setCallback(new JellyMediaSessionCallback());
        setSessionToken(mSessionToken);
    }

    private void initData() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mPlayer = new TXVodPlayer(getApplicationContext());
        mPlayer.setVodListener(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //任务被移除，保存播放信息
//        saveRecentSongToStorage()
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaNotificationManager.onDestroy();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Logger.d(Thread.currentThread().getName());//main
        result.detach();
        Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> emitter.onNext(MusicUtils.getMusicList(getApplicationContext()))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicItems -> {
                    List<MediaBrowserCompat.MediaItem> mediaList = getMediaList(musicItems);
                    List<MediaMetadataCompat> mediaDataList = getMediaDataList(musicItems);
                    result.sendResult(mediaList);
                    mCurrentPlaylistItems = mediaDataList;
                });
    }

    private List<MediaBrowserCompat.MediaItem> getMediaList(List<MusicBean> musicList) {
        List<MediaBrowserCompat.MediaItem> list = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            MusicBean musicBean = musicList.get(i);
            MediaDescriptionCompat build = new MediaDescriptionCompat.Builder()
                    .setMediaId(String.valueOf(musicBean.getId()))
                    .setTitle(musicBean.getTitle())
                    .setMediaId(musicBean.getUrl())
//                    .setMediaUri(Uri.fromFile(new File(musicBean.getUrl())))
                    .setSubtitle("")
                    .build();
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(build, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            list.add(mediaItem);
        }
        return list;
    }

    private List<MediaMetadataCompat> getMediaDataList(List<MusicBean> musicList) {
        List<MediaMetadataCompat> list = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            MusicBean musicBean = musicList.get(i);
            MediaMetadataCompat build = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(musicBean.getUrl()))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, musicBean.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicBean.getArtist())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicBean.getDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, musicBean.getUrl())
//                    .putString(
//                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
//                          )
//                    .putString(
//                            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
//                            getAlbumArtUri(albumArtResName))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicBean.getTitle())
                    .build();
            list.add(build);
        }
        return list;
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    public class JellyMediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onSkipToNext() {
            mSeekPos = 0;
            if (mCurrentPlaylistItems.size() - 1 > mCurrentMediaItemIndex) {
                mCurrentMediaItemIndex++;
                MediaDescriptionCompat description = mCurrentPlaylistItems.get(mCurrentMediaItemIndex).getDescription();
                mPlayer.startPlay(description.getMediaId());
                Notification notification = mMediaNotificationManager.getNotification(description, mPlaybackState, mSessionToken);
                mNotificationManager.notify(NOTIFY_ID, notification);
            }

        }

        @Override
        public void onSkipToPrevious() {
            mSeekPos = 0;
            if (mCurrentMediaItemIndex > 0) {
                mCurrentMediaItemIndex--;
                MediaDescriptionCompat description = mCurrentPlaylistItems.get(mCurrentMediaItemIndex).getDescription();
                mPlayer.startPlay(description.getMediaId());
                Notification notification = mMediaNotificationManager.getNotification(description, mPlaybackState, mSessionToken);
                mNotificationManager.notify(NOTIFY_ID, notification);
            }

        }

        @Override
        public void onPlay() {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                mSeekPos = (int) mPlayer.getCurrentPlaybackTime();
            } else {
                int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    MusicService.start(getApplicationContext());
                    mMediaSession.setActive(true);
                    registerReceiver(myNoisyAudioStreamReceiver, intentFilter);

                    mPlayer.setStartTime(mSeekPos);
                    if (mCurrentPlaylistItems.size() - 1 < mCurrentMediaItemIndex) {
                        return;
                    }
                    MediaDescriptionCompat description = mCurrentPlaylistItems.get(mCurrentMediaItemIndex).getDescription();
                    mPlayer.startPlay(description.getMediaId());
                    Notification notification = mMediaNotificationManager.getNotification(description, mPlaybackState, mSessionToken);
                    startForeground(NOTIFY_ID, notification);
                }
            }
            MediaDescriptionCompat description = mCurrentPlaylistItems.get(mCurrentMediaItemIndex).getDescription();
            Notification notification = mMediaNotificationManager.getNotification(description, mPlaybackState, mSessionToken);
            mNotificationManager.notify(NOTIFY_ID, notification);

        }

        @Override
        public void onStop() {
            mAudioManager.abandonAudioFocus(afChangeListener);
            unregisterReceiver(myNoisyAudioStreamReceiver);
            stopSelf();
            mMediaSession.setActive(false);
            mPlayer.stopPlay(true);
            stopForeground(true);

        }
    }

}
