package com.android.player.video;

import com.android.player.common.util.LogUtil;

/**
 * 视频播放模块配置器
 * @author yeguolong
 */
public class VideoPlayerConfig {

    private boolean isRegisterAudioFocus;// 是否监听音频焦点
    private boolean isRegisterMediaSession;// 是否监听线控焦点
    private boolean isRegisterScreenStateReceiver;// 是否监听并处理锁屏广播
    private boolean isRegisterPhoneStateReceiver;// 是否监听并处理电话广播

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

    /**
     * 是否监听并处理锁屏广播
     * @return
     */
    public boolean isRegisterScreenStateReceiver() {
        log("isRegisterScreenStateReceiver-->" + isRegisterScreenStateReceiver);
        return isRegisterScreenStateReceiver;
    }

    /**
     * 设置是否监听并处理锁屏广播
     * @param isRegisterScreenStateReceiver
     */
    public void setRegisterScreenStateReceiver(
            boolean isRegisterScreenStateReceiver) {
        this.isRegisterScreenStateReceiver = isRegisterScreenStateReceiver;
    }

    /**
     * 是否监听并处理电话广播
     * @return
     */
    public boolean isRegisterPhoneStateReceiver() {
        log("isRegisterPhoneStateReceiver-->" + isRegisterPhoneStateReceiver);
        return isRegisterPhoneStateReceiver;
    }

    /**
     * 是否监听并处理电话广播
     * @param isRegisterPhoneStateReceiver
     */
    public void setRegisterPhoneStateReceiver(
            boolean isRegisterPhoneStateReceiver) {
        this.isRegisterPhoneStateReceiver = isRegisterPhoneStateReceiver;
    }

    private void log(String text) {
        LogUtil.d("VideoPlayerConfig-->" + text);
    }
}
