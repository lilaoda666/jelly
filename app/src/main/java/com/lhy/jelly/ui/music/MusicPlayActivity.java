package com.lhy.jelly.ui.music;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhy.jelly.R;
import com.lhy.jelly.base.BaseActivity;
import com.lhy.jelly.service.MusicService;

import java.util.List;

public class MusicPlayActivity extends BaseActivity {

    private MediaBrowserCompat.ConnectionCallback mConnectionCallbacks;
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat.Callback mCallback;
    private ImageView mPlayPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initData();
        initView();
    }

    private void initView() {
        // Grab the view for the play/pause button
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        // Attach a listener to the button
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly
                int pbState = MediaControllerCompat.getMediaController(MusicPlayActivity.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(MusicPlayActivity.this).getTransportControls().pause();
                } else {
                    MediaControllerCompat.getMediaController(MusicPlayActivity.this).getTransportControls().play();
                }
            }
        });
    }

    private void initData() {
        mConnectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
            @Override
            public void onConnected() {
                if(mMediaBrowser.isConnected()){
                    String root = mMediaBrowser.getRoot();
                    mMediaBrowser.unsubscribe(root);
                    mMediaBrowser.subscribe(MusicService.MY_MEDIA_ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
                        @Override
                        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                            super.onChildrenLoaded(parentId, children);
                        }
                    });
                }
                // Get the token for the MediaSession
                MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

                // Create a MediaControllerCompat
                MediaControllerCompat mediaController =
                        null;
                try {
                    mediaController = new MediaControllerCompat(MusicPlayActivity.this, token);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                // Save the controller
                MediaControllerCompat.setMediaController(MusicPlayActivity.this, mediaController);
                // Finish building the UI
                buildTransportControls();
            }

            @Override
            public void onConnectionSuspended() {
                super.onConnectionSuspended();
            }

            @Override
            public void onConnectionFailed() {
                super.onConnectionFailed();
            }
        };
        ComponentName serviceComponent = new ComponentName(this, MusicService.class);
        mMediaBrowser = new MediaBrowserCompat(this, serviceComponent, mConnectionCallbacks, null); // optional Bu
    }

    private void buildTransportControls() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();

        // Register a Callback to stay in sync
        mCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                //播放音乐改变
            }

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                //根据状态更新UI
            }

        };
        mediaController.registerCallback(mCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(mCallback);
        }
        mMediaBrowser.disconnect();

    }
}
