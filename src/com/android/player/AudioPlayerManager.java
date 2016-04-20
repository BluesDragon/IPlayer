package com.android.player;

import android.content.Context;

import com.android.player.audio.AudioPlayerConfig;
import com.android.player.audio.controller.AudioPlayController;
import com.android.player.common.controller.IPlayController;
import com.android.player.common.util.LogUtil;

/**
 * 音频播放管理者，负责管理：<br>
 * 1、IPlayController（播放控制器）<br>
 * 2、AudioPlayerConfig（音频播放配置信息类）<br>
 * @author yeguolong
 */
public class AudioPlayerManager {

    protected Context mContext;
    protected IPlayController mIPlayController;
    protected AudioPlayerConfig mAudioPlayerConfig;

    public AudioPlayerManager(Context context) {
        this.init(context, null, null);
    }

    public AudioPlayerManager(Context context,
            AudioPlayerConfig audioPlayerConfig) {
        this.init(context, audioPlayerConfig, null);
    }

    public AudioPlayerManager(Context context,
            AudioPlayerConfig audioPlayerConfig, IPlayController iPlayController) {
        this.init(context, audioPlayerConfig, iPlayController);
    }

    private void init(Context context, AudioPlayerConfig audioPlayerConfig,
            IPlayController iPlayController) {
        this.mContext = context;
        this.mAudioPlayerConfig = audioPlayerConfig;
        if (mAudioPlayerConfig == null) {
            mAudioPlayerConfig = new AudioPlayerConfig();
        }
        if (iPlayController == null) {
            this.mIPlayController = new AudioPlayController(mContext,
                    mAudioPlayerConfig);
        }
    }

    /**
     * 获取播放控制器
     * @return
     */
    public IPlayController getPlayController() {
        return this.mIPlayController;
    }

    /**
     * 获取音频播放配置器
     * @return
     */
    public AudioPlayerConfig getAudioPlayerConfig() {
        return mAudioPlayerConfig;
    }

    /**
     * 释放播放器
     */
    public void release() {
        this.log("release");
        if (this.mIPlayController != null) {
            this.mIPlayController.release();
        }
    }

    protected void log(String text) {
        LogUtil.d("VideoPlayerManager-->" + text);
    }

}
