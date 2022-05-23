package com.lhy.jelly.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.lhy.jelly.BuildConfig;
import com.lhy.jelly.R;
import com.lhy.jelly.bean.MusicBean;
import com.lhy.jelly.utils.MusicUtils;
import com.orhanobut.logger.Logger;
import com.tencent.rtmp.TXVodPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicService extends MediaBrowserServiceCompat {

    public static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + "music";
    public static final int NOTIFY_ID = 999;

    public static final String TAG = "MusicService";

    public static final String MY_MEDIA_ROOT_ID = "media_root_id";
    public static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat mPlaybackState;
    private MediaSessionCompat.Token mSessionToken;

    private RemoteViews mRemoteViews;
    private int mSeekPos;
    private Notification mNotification;
    private Notification.Builder mNotifyBuild;
    private NotificationManager mNotificationManager;

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
    private NotificationCompat.Builder builder;


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

    //    private void initNotify() {
//        initRemoteViews();
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyBuild = new Notification.Builder(this)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContent(mRemoteViews)
//                .setSound(null)
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        mNotification = mNotifyBuild.build();
//    }

//    private void initRemoteViews() {
//        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify_play);
//        PendingIntent playIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicService2.MusicBroadCastReceiver.PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicService2.MusicBroadCastReceiver.CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent playNextIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicService2.MusicBroadCastReceiver.PLAY_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent playPreIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(MusicService2.MusicBroadCastReceiver.PLAY_PRE), PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.img_play, playIntent);
//        mRemoteViews.setOnClickPendingIntent(R.id.img_play_next, playNextIntent);
//        mRemoteViews.setOnClickPendingIntent(R.id.img_play_prev, playPreIntent);
//    }


    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new TXVodPlayer(getApplicationContext());

        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, launchIntentForPackage, 0);
        mMediaSession = new MediaSessionCompat(getApplicationContext(), TAG);
        mMediaSession.setSessionActivity(pendingIntent);
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_FAST_FORWARD | PlaybackStateCompat.ACTION_PREPARE)
                .build();
        mMediaSession.setPlaybackState(mPlaybackState);
        MediaBrowserCompat.MediaItem mediaItem = getMediaList().get(0);
        Uri mediaUri = mediaItem.getDescription().getMediaUri();
//        mMediaSession.setMetadata();
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start the service

//                    service.start();
//                    // Set the session active  (and update metadata and state)
//                    mMediaSession.setActive(true);
//                    // start the player (custom call)
//                    mPlayer.start();
//                    // Register BECOME_NOISY BroadcastReceiver
//                    registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
//                    // Put the service in the foreground, post notification
//                    service.startForeground(myPlayerNotification);
                    MusicService.start(getApplicationContext());
                    mMediaSession.setActive(true);
                    registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                    startForeground(NOTIFY_ID, builder.build());

                }

            }

            @Override
            public void onStop() {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                // Abandon audio focus
                am.abandonAudioFocus(afChangeListener);
                unregisterReceiver(myNoisyAudioStreamReceiver);
                // Start the service
//                service.stop(self);
//                // Set the session inactive  (and update metadata and state)
//                mMediaSession.setActive(false);
//                // stop the player (custom call)
//                player.stop();
//                // Take the service out of the foreground
//                service.stopForeground(false);

                stopSelf();
                mMediaSession.setActive(false);
                mPlayer.stopPlay(true);
                stopForeground(true);

            }
        });
        mSessionToken = mMediaSession.getSessionToken();
        setSessionToken(mSessionToken);

        MediaControllerCompat controller = mMediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder
                // Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.girl1)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_play_pause_black, "暂停",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_STOP)));

// Display the notification and place the service in the foreground
        startForeground(NOTIFY_ID, builder.build());
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
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    private boolean allowBrowsing(String clientPackageName, int clientUid) {
        return true;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Logger.d(Thread.currentThread().getName());//main
        result.detach();
        result.sendResult(getMediaList());

    }

    private List<MediaBrowserCompat.MediaItem> getMediaList() {
        List<MusicBean> musicList = MusicUtils.getMusicList(getApplicationContext());
        List<MediaBrowserCompat.MediaItem> list = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            MusicBean musicBean = musicList.get(i);
            MediaDescriptionCompat build = new MediaDescriptionCompat.Builder()
                    .setMediaId(String.valueOf(musicBean.getId()))
                    .setTitle(musicBean.getTitle())
                    .setMediaUri(Uri.fromFile(new File(musicBean.getUrl())))
                    .setSubtitle("")
                    .build();
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(build, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            list.add(mediaItem);
        }
        return list;
    }

    private void discoverBrowseableMediaApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(MediaBrowserService.SERVICE_INTERFACE);
        List<ResolveInfo> services = packageManager.queryIntentServices(intent, 0);
        for (ResolveInfo resolveInfo : services) {
            if (resolveInfo.serviceInfo != null && resolveInfo.serviceInfo.applicationInfo != null) {

                ApplicationInfo applicationInfo = resolveInfo.serviceInfo.applicationInfo;
                String label = (String) packageManager.getApplicationLabel(applicationInfo);
                Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                String className = resolveInfo.serviceInfo.name;
                String packageName = resolveInfo.serviceInfo.packageName;
                Logger.d(packageName+"  "+className);
            }
        }

    }
}
