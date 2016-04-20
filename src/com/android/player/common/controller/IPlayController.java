package com.android.player.common.controller;

import android.view.SurfaceHolder;

import com.android.player.common.mediaplayer.IMediaPlayer;
import com.android.player.common.mediaplayer.MediaPlayerListener;
import com.android.player.common.mediaplayer.PlayStatus;
import com.android.player.common.model.PlayItem;
import com.android.player.common.util.AudioFocusHelper.AudioFocusListener;
import com.android.player.common.util.MediaSessionHelper.MediaSessionListener;

/**
 * 播放控制接口：仅供PlayerManager或外部使用者调用
 * @author yeguolong
 */
public interface IPlayController {

    /**
     * 开启一次播放：PlayItem播放
     * @param url
     *            播放地址
     */
    public void startPlay(String url);

    /**
     * 开启一次播放：PlayItem播放
     * @param playItem
     */
    public void startPlay(PlayItem playItem);

    /**
     * 播放
     */
    public boolean doPlay();

    /**
     * 暂停：如果是外部暂停，希望在下次返回播放界面时保持暂停状态的话，则需要调用setUserPause(true);
     */
    public void doPause();

    /**
     * 暂停：如果是外部暂停，希望在下次返回播放界面时保持暂停状态的话，则需要调用setUserPause(true);
     * @param releaseAudioFocus
     *            是否需要释放音频焦点
     */
    public void doPause(boolean releaseAudioFocus);

    /**
     * 设置UserPause
     * @param isUserPause
     *            是否是用户暂停：如果是，则下次返回播放界面，会保持暂停状态
     */
    public void setUserPause(boolean isUserPause);

    /**
     * 停止
     */
    public void doStop();

    /**
     * seek快进
     * @param targetPosition
     *            毫秒值
     */
    public void doSeek(int targetPosition);

    /**
     * 销毁播放器
     */
    public void gcMediaPlayer();

    /**
     * 获取播放状态
     */
    public PlayStatus getPlayStatus();

    /**
     * 释放播放器
     */
    public void release();

    /**
     * 请求音频焦点
     */
    public boolean requestAudioFocus();

    /**
     * 释放音频焦点
     */
    public boolean releaseAudioFocus();

    /**
     * 获取当前播放进度
     * @return
     */
    public int getCurrentPosition();

    /**
     * 获取总时长
     * @return
     */
    public int getDuration();

    /**
     * 获取PlayItem
     * @return
     */
    public PlayItem getPlayItem();

    /**
     * 判断是否可以播放
     * @return
     */
    public boolean canPlay();

    /**
     * 获取IMediaPlayer接口
     * @return
     */
    public IMediaPlayer getIMediaPlayer();

    /**
     * 注册耳机线控焦点
     */
    public void requestMediaSessionFocus();

    /**
     * 释放耳机线控焦点
     */
    public void releaseMediaSessionFocus();

    /**
     * 设置音量
     * @param value
     */
    public void setVolume(int value);

    /**
     * 获取当前音量
     * @return
     */
    public int getCurrentVolume();

    /**
     * 获取音量最大值
     * @return
     */
    public int getMaxVolume();

    /**
     * 设置IMediaPlayer接口监听回调
     * @param mediaPlayerListener
     */
    public void setMediaPlayerListener(MediaPlayerListener mediaPlayerListener);

    /**
     * 设置播放引擎
     * @param iMediaPlayer
     */
    public void setIMediaPlayer(IMediaPlayer iMediaPlayer);

    /**
     * 设置控制器的回调接口
     * @param controllerCallback
     */
    public void setControllerCallback(ControllerCallback controllerCallback);

    /**
     * 设置是否循环播放
     * @param looping
     */
    public void setLooping(boolean looping);

    /**
     * 设置音频焦点监听回调
     * @param audioFocusListener
     */
    public void setAudioFocusListener(AudioFocusListener audioFocusListener);

    /**
     * 设置线控按键的回调监听
     * @param mediaSessionListener
     */
    public void setMediaSessionListener(
            MediaSessionListener mediaSessionListener);

    /**
     * 设置音频流类型
     * @param audioStreamType
     */
    public void setAudioStreamType(int audioStreamType);

    /**
     * 设置视频资源的显示器
     */
    public void setDisPlay(SurfaceHolder surfaceHolder);

    /**
     * 设置唤醒模式
     * @param mode
     */
    public void setWakeMode(int mode);
}
