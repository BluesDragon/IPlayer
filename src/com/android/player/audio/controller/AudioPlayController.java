package com.android.player.audio.controller;

import android.content.Context;

import com.android.player.audio.AudioPlayerConfig;
import com.android.player.common.controller.BasePlayController;
import com.android.player.common.mediaplayer.DefaultMediaPlayer;
import com.android.player.common.mediaplayer.IMediaPlayer;
import com.android.player.common.mediaplayer.MediaPlayerListener;
import com.android.player.common.model.PlayItem;
import com.android.player.common.util.LogUtil;

public class AudioPlayController extends BasePlayController {

    private AudioPlayerConfig mAudioPlayerConfig;

    public AudioPlayController(Context context,
            AudioPlayerConfig audioPlayerConfig) {
        super(context);
        this.mAudioPlayerConfig = audioPlayerConfig;
        init();
    }

    private void init() {
        if (mAudioPlayerConfig != null
                && mAudioPlayerConfig.isRegisterAudioFocus()) {
            initAudioFocusListener();
        }
        if (mAudioPlayerConfig != null
                && mAudioPlayerConfig.isRegisterMediaSession()) {
            initMediaSessionListener();
        }
    }

    @Override
    public void startPlay(PlayItem playItem) {
        this.mPlayItem = playItem;
        this.checkPlay();
    }

    private void checkPlay() {
        if (this.mPlayItem == null) {
            return;
        }
        this.checkPlayType(this.mPlayItem);
        this.startPlay();
    }

    private void startPlay() {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.startPlay(this.mPlayItem);
        }
    }

    @Override
    protected IMediaPlayer initMediaPlayer() {
        if (mIMediaPlayer == null) {
            this.mIMediaPlayer = new DefaultMediaPlayer(this.mContext);
        }
        this.mIMediaPlayer.setMediaPlayerListener(new MediaPlayerListener() {

            @Override
            public void onVideoSizeChanged(int width, int height) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoSizeChanged(width, height);
                }
            }

            @Override
            public void onVideoRenderingStart(int extra) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoRenderingStart(extra);
                }
            }

            @Override
            public void onPrepared() {
                if (mIMediaPlayer != null) {
                    int duration = getDuration();
                    log("onPrepared-->duration:" + duration);
                    doPlay();
                    if (mPlayItem != null) {
                        mPlayItem.setDuration(duration);
                        int startPostion = mPlayItem.getStartPostion();
                        log("onPrepared-->startPostion:" + startPostion);
                        if (startPostion > 0) {
                            doSeek(startPostion);
                            mPlayItem.setStartPostion(0);
                        }
                    }
                    if (!canPlay()) {
                        doPause();
                    }
                }
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPrepared();
                }
            }

            @Override
            public void onMediaInfoUpdate(int currentPosition) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onMediaInfoUpdate(currentPosition);
                }
            }

            @Override
            public boolean onInfo(int what, int extra) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onInfo(what, extra);
                }
                return false;
            }

            @Override
            public boolean onError(int what, int extra) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onError(what, extra);
                }
                return false;
            }

            @Override
            public void onCompletion() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onCompletion();
                }
            }

            @Override
            public void onBufferingStart(int extra) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onBufferingStart(extra);
                }
            }

            @Override
            public void onBufferingEnd(int extra) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onBufferingEnd(extra);
                }
            }

            @Override
            public void onPlay() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPlay();
                }
            }

            @Override
            public void onPause() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPause();
                }
            }

            @Override
            public void onStartPlay() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onStartPlay();
                }
            }
        });
        return null;
    }

    @Override
    protected void log(String text) {
        LogUtil.d("AudioPlayController-->" + text);
    }

}
