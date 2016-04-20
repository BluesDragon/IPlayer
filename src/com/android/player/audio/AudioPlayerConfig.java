package com.android.player.audio;

import com.android.player.common.util.LogUtil;

/**
 * 音频播放模块配置器
 * @author yeguolong
 */
public class AudioPlayerConfig {

    private boolean isRegisterAudioFocus;// 是否监听音频焦点
    private boolean isRegisterMediaSession;// 是否监听线控焦点

    /**
     * 是否监听音频焦点
     * @return
     */
    public boolean isRegisterAudioFocus() {
        log("isRegisterAudioFocus-->" + isRegisterAudioFocus);
        return isRegisterAudioFocus;
    }

    /**
     * 设置是否监听音频焦点
     * @param isRegisterAudioFocus
     */
    public void setRegisterAudioFocus(boolean isRegisterAudioFocus) {
        this.isRegisterAudioFocus = isRegisterAudioFocus;
    }

    /**
     * 是否监听线控焦点
     * @return
     */
    public boolean isRegisterMediaSession() {
        log("isRegisterMediaSession-->" + isRegisterMediaSession);
        return isRegisterMediaSession;
    }

    /**
     * 设置是否监听线控焦点
     * @param isRegisterMediaSession
     */
    public void setRegisterMediaSession(boolean isRegisterMediaSession) {
        this.isRegisterMediaSession = isRegisterMediaSession;
    }

    private void log(String text) {
        LogUtil.d("AudioPlayerConfig-->" + text);
    }

}
