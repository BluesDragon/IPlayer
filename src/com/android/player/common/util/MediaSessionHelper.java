package com.android.player.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.PlaybackState;
import android.os.SystemClock;

/**
 * @author yeguolong
 */
@SuppressLint("NewApi")
public class MediaSessionHelper {

    private MediaSession mediaSession;
    private MediaSessionListener mMediaSessionCallback;
    private String mMediaSessionTag = "MediaSessionManager";
    private final Context context;

    public MediaSessionHelper(Context context) {
        this.context = context;
    }

    public MediaSessionHelper(Context context,
            MediaSessionListener mediaSessionCallback) {
        this.context = context;
        this.mMediaSessionCallback = mediaSessionCallback;
    }

    public void initMediaButtonReceiver() {
        this.mediaSession = new MediaSession(this.context,
                this.mMediaSessionTag);
        this.mediaSession.setCallback(new Callback() {
            @Override
            public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
                if (MediaSessionHelper.this.mMediaSessionCallback != null) {
                    MediaSessionHelper.this.mMediaSessionCallback
                            .onMediaButtonEvent(mediaButtonIntent);
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }

            @Override
            public void onPause() {
                super.onPause();
                if (MediaSessionHelper.this.mMediaSessionCallback != null) {
                    MediaSessionHelper.this.mMediaSessionCallback.onPause();
                }
                MediaSessionHelper.this
                        .changeMediaSessionState(PlaybackState.STATE_PAUSED);
            }

            @Override
            public void onPlay() {
                super.onPlay();
                if (MediaSessionHelper.this.mMediaSessionCallback != null) {
                    MediaSessionHelper.this.mMediaSessionCallback.onPlay();
                }
                MediaSessionHelper.this
                        .changeMediaSessionState(PlaybackState.STATE_PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                if (MediaSessionHelper.this.mMediaSessionCallback != null) {
                    MediaSessionHelper.this.mMediaSessionCallback
                            .onSkipToPrevious();
                }
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                if (MediaSessionHelper.this.mMediaSessionCallback != null) {
                    MediaSessionHelper.this.mMediaSessionCallback
                            .onSkipToNext();
                }
            }
        });
        this.mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        this.changeMediaSessionState(PlaybackState.STATE_STOPPED);
    }

    public void registerMediaSession() {
        if (this.mediaSession != null) {
            this.mediaSession.setActive(true);
        }
    }

    public void unRegisterMediaSession() {
        if (this.mediaSession != null) {
            this.mediaSession.setActive(false);
        }
    }

    public void release() {
        if (this.mediaSession != null) {
            this.mediaSession.release();
            this.mediaSession = null;
        }
    }

    /**
     * @param playbackState
     *            PlaybackState.STATE_PLAYING | PlaybackState.STATE_PAUSED
     */
    public void changeMediaSessionState(int playbackState) {
        if (this.mediaSession != null) {
            PlaybackState state = new PlaybackState.Builder()
                    .setActions(
                            PlaybackState.ACTION_PLAY
                                    | PlaybackState.ACTION_PAUSE
                                    | PlaybackState.ACTION_PLAY_PAUSE
                                    | PlaybackState.ACTION_SKIP_TO_NEXT
                                    | PlaybackState.ACTION_SKIP_TO_PREVIOUS
                                    | PlaybackState.ACTION_STOP)
                    .setState(playbackState,
                            PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                            SystemClock.elapsedRealtime()).build();
            this.mediaSession.setPlaybackState(state);
        }
    }

    public void setMediaSessionTag(String tag) {
        this.mMediaSessionTag = tag;
    }

    public void setMediaSessionCallback(
            MediaSessionListener mediaSessionCallback) {
        this.mMediaSessionCallback = mediaSessionCallback;
    }

    public MediaSessionListener getMediaSessionCallback() {
        return this.mMediaSessionCallback;
    }

    public interface MediaSessionListener {
        public boolean onMediaButtonEvent(final Intent mediaButtonIntent);

        public void onPause();

        public void onPlay();

        public void onSkipToPrevious();

        public void onSkipToNext();
    }
}
