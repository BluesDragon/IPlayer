package com.android.player.common.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.SurfaceHolder;

import com.android.player.common.mediaplayer.IMediaPlayer;
import com.android.player.common.mediaplayer.MediaPlayerListener;
import com.android.player.common.mediaplayer.PlayStatus;
import com.android.player.common.model.PlayItem;
import com.android.player.common.util.AudioFocusHelper;
import com.android.player.common.util.AudioFocusHelper.AudioFocusListener;
import com.android.player.common.util.MediaSessionHelper;
import com.android.player.common.util.MediaSessionHelper.MediaSessionListener;
import com.android.player.common.util.PlayerUtil;

/**
 * 播放控制实现基类（抽象类）：<br>
 * 1、持有最基本的播放器（IMediaPlayer）和音频焦点管理类（AudioFocusHelper）等。<br>
 * 2、播放控制实现类必须集成此类。<br>
 *
 * @author yeguolong
 */
public abstract class BasePlayController implements IPlayController {

    protected final Context mContext;// 上下文
    protected PlayItem mPlayItem;// 播放条目
    protected IMediaPlayer mIMediaPlayer;
    protected AudioFocusHelper mAudioFocusHelper;// 音频焦点工具类
    protected MediaSessionHelper mMediaSessionHelper;// 耳机线控工具类
    protected boolean isUserPause;
    protected ControllerCallback mControllerCallback;
    protected MediaPlayerListener mMediaPlayerListener;// MediaPlayer的回调接口，用户监听MediaPlayer相关回调事件
    private boolean isLooping;
    private AudioFocusListener mAudioFocusListener;
    private MediaSessionListener mMediaSessionListener;

    /**
     * 让播放器仅仅在短暂失去音频焦点并重新获得后才开始播放音乐。而不是任何时候重新获得焦点都开始播放。
     */
    private boolean mPausedByTransientLossOfFocus;

    public BasePlayController(Context context) {
        this.mContext = context;
    }

    /**
     * 监听音频焦点
     */
    protected void initAudioFocusListener() {
        this.mAudioFocusHelper = new AudioFocusHelper(this.mContext);
        this.mAudioFocusHelper.setAudioFocusListener(new AudioFocusListener() {

            @Override
            public void onAudioFocusLossTransientCanDuck() {
                log("initAudioFocusListener-->onAudioFocusLossTransientCanDuck");
                mPausedByTransientLossOfFocus = true;
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.onAudioFocusLossTransientCanDuck();
                }
            }

            @Override
            public void onAudioFocusLossTransient() {
                log("initAudioFocusListener-->onAudioFocusLossTransient");
                mPausedByTransientLossOfFocus = true;
                doPause(false);
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.onAudioFocusLossTransient();
                }
            }

            @Override
            public void onAudioFocusLoss() {
                log("initAudioFocusListener-->onAudioFocusLoss");
                mPausedByTransientLossOfFocus = false;
                doPause();
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.onAudioFocusLoss();
                }
            }

            @Override
            public void onAudioFocusGain() {
                log("initAudioFocusListener-->onAudioFocusGain");
                if (canPlay() && mPausedByTransientLossOfFocus) {
                    mPausedByTransientLossOfFocus = false;
                    doPlay();
                }
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.onAudioFocusGain();
                }
            }
        });
    }

    /**
     * 监听耳机线控焦点
     */
    protected void initMediaSessionListener() {
        log("initMediaSessionListener-->" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 5.0以上才能用，否则无效
            return;
        }
        this.mMediaSessionHelper = new MediaSessionHelper(this.mContext);
        this.mMediaSessionHelper.initMediaButtonReceiver();
        this.mMediaSessionHelper
                .setMediaSessionCallback(new MediaSessionListener() {

                    @Override
                    public void onSkipToPrevious() {
                        log("initMediaSessionListener-->onSkipToPrevious");
                        if (mMediaSessionListener != null) {
                            mMediaSessionListener.onSkipToPrevious();
                        }
                    }

                    @Override
                    public void onSkipToNext() {
                        log("initMediaSessionListener-->onSkipToNext");
                        if (mMediaSessionListener != null) {
                            mMediaSessionListener.onSkipToNext();
                        }
                    }

                    @Override
                    public void onPlay() {
                        log("initMediaSessionListener-->onPlay");
                        togglePlay();
                        if (mMediaSessionListener != null) {
                            mMediaSessionListener.onPlay();
                        }
                    }

                    @Override
                    public void onPause() {
                        log("initMediaSessionListener-->onPause");
                        togglePlay();
                        if (mMediaSessionListener != null) {
                            mMediaSessionListener.onPause();
                        }
                    }

                    @Override
                    public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                        if (mMediaSessionListener != null) {
                            return mMediaSessionListener
                                    .onMediaButtonEvent(mediaButtonIntent);
                        }
                        return false;
                    }
                });
    }

    @Override
    public void startPlay(String url) {
        startPlay(new PlayItem(url));
    }

    /**
     * 用户切换播放/暂停
     */
    private void togglePlay() {
        if (this.getPlayStatus() == PlayStatus.PLAYING) {
            this.isUserPause = true;
            this.doPause();
        } else if (this.getPlayStatus() == PlayStatus.PAUSED) {
            this.isUserPause = false;
            this.doPlay();
        }
    }

    @Override
    public boolean canPlay() {
        return !this.isUserPause;
    }

    /**
     * 初始化播放器：抽象接口。<br>
     * 这里放在子类去初始化，本基类中不做处理。
     *
     * @return
     */
    protected abstract IMediaPlayer initMediaPlayer();

    protected void checkPlayType(PlayItem playItem) {
        if (mControllerCallback != null
                && mControllerCallback.onCheckPlayType(this, playItem)) {
            setLooping(isLooping);
            return;
        }
        this.initMediaPlayer();
        setLooping(isLooping);
        if (playItem != null) {
            playItem.setPlayType(PlayerUtil.getPlayType(playItem.getPlayUrl()));
        }
    }

    @Override
    public boolean doPlay() {
        if (this.requestAudioFocus()) {
            if (this.mIMediaPlayer != null) {
                this.mIMediaPlayer.doPlay();
                return true;
            }
        }
        return false;
    }

    @Override
    public void doPause() {
        doPause(true);
    }

    @Override
    public void doPause(boolean releaseAudioFocus) {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.doPause();
        }
        if (releaseAudioFocus) {
            this.releaseAudioFocus();
        }
    }

    @Override
    public void doSeek(int targetPosition) {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.doSeek(targetPosition);
        }
    }

    @Override
    public int getCurrentPosition() {
        int position = 0;
        if (this.mIMediaPlayer != null) {
            position = this.mIMediaPlayer.getCurrentPosition();
        }
        return position;
    }

    @Override
    public PlayItem getPlayItem() {
        return this.mPlayItem;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        if (this.mIMediaPlayer != null) {
            duration = this.mIMediaPlayer.getDuration();
        }
        return duration;
    }

    @Override
    public void setMediaPlayerListener(MediaPlayerListener mediaPlayerListener) {
        this.mMediaPlayerListener = mediaPlayerListener;
    }

    @Override
    public void setIMediaPlayer(IMediaPlayer iMediaPlayer) {
        this.mIMediaPlayer = iMediaPlayer;
        this.initMediaPlayer();
    }

    @Override
    public void setUserPause(boolean isUserPause) {
        this.isUserPause = isUserPause;
    }

    @Override
    public void doStop() {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.doStop();
        }
    }

    @Override
    public void gcMediaPlayer() {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.gcMediaPlayer();
        }
    }

    @Override
    public IMediaPlayer getIMediaPlayer() {
        return this.mIMediaPlayer;
    }

    @Override
    public PlayStatus getPlayStatus() {
        if (this.mIMediaPlayer != null) {
            return this.mIMediaPlayer.getPlayStatus();
        }
        return PlayStatus.STOPED;
    }

    @Override
    public boolean requestAudioFocus() {
        boolean result = true;
        if (this.mAudioFocusHelper != null) {
            result = this.mAudioFocusHelper.requestFocus();
            log("requestAudioFocus-->result:" + result);
        }
        return result;
    }

    @Override
    public boolean releaseAudioFocus() {
        if (this.mAudioFocusHelper != null) {
            return this.mAudioFocusHelper.abandonFocus();
        }
        return false;
    }

    @Override
    public void requestMediaSessionFocus() {
        if (this.mMediaSessionHelper != null) {
            this.mMediaSessionHelper.registerMediaSession();
        }
    }

    @Override
    public void releaseMediaSessionFocus() {
        if (this.mMediaSessionHelper != null) {
            this.mMediaSessionHelper.unRegisterMediaSession();
        }
    }

    private void releaseMediaSession() {
        this.releaseMediaSessionFocus();
        if (mMediaSessionHelper != null) {
            mMediaSessionHelper.release();
        }
    }

    @Override
    public int getCurrentVolume() {
        if (this.mAudioFocusHelper != null) {
            return this.mAudioFocusHelper.getCurrentVolume();
        }
        return 0;
    }

    @Override
    public int getMaxVolume() {
        if (this.mAudioFocusHelper != null) {
            return this.mAudioFocusHelper.getMaxVolume();
        }
        return 0;
    }

    @Override
    public void setVolume(int value) {
        if (this.mAudioFocusHelper != null) {
            this.mAudioFocusHelper.setVolume(value);
        }
    }

    @Override
    public void setLooping(boolean looping) {
        this.isLooping = looping;
        if (mIMediaPlayer != null) {
            mIMediaPlayer.setLooping(looping);
        }
    }

    @Override
    public void setControllerCallback(ControllerCallback controllerCallback) {
        mControllerCallback = controllerCallback;
    }

    @Override
    public void setAudioFocusListener(AudioFocusListener audioFocusListener) {
        this.mAudioFocusListener = audioFocusListener;
    }

    @Override
    public void setMediaSessionListener(
            MediaSessionListener mediaSessionListener) {
        this.mMediaSessionListener = mediaSessionListener;
    }

    @Override
    public void setAudioStreamType(int audioStreamType) {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.setAudioStreamType(audioStreamType);
        }
    }

    @Override
    public void setDisPlay(SurfaceHolder surfaceHolder) {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.setDisPlay(surfaceHolder);
        }
    }

    protected abstract void log(String text);

    @Override
    public void release() {
        this.log("release");
        this.gcMediaPlayer();
        this.releaseAudioFocus();
        this.releaseMediaSession();
        this.mPlayItem = null;
    }

    @Override
    public void setWakeMode(int mode) {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.setWakeMode(mode);
        }
    }

}
