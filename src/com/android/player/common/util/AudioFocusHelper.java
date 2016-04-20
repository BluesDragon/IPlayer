package com.android.player.common.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * 音频焦点工具类
 * @author yeguolong
 */
public class AudioFocusHelper {

    private final Context mContext;
    private AudioManager mAudioManager;
    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private AudioFocusListener mAudioFocusListener;// 音频焦点监听

    /**
     * 音频焦点工具类
     */
    public AudioFocusHelper(Context context) {
        this.mContext = context;
        this.initAudioManager();
    }

    /**
     * 初始化AudioManager
     */
    private void initAudioManager() {
        this.mAudioManager = (AudioManager) this.mContext
                .getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        this.initOnAudioFocusChangeListener();
    }

    /**
     * 初始化音频焦点变化的监听
     */
    private void initOnAudioFocusChangeListener() {
        if (this.mOnAudioFocusChangeListener == null) {
            this.mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:// 你已经得到了音频焦点。
                        if (AudioFocusHelper.this.mAudioFocusListener != null) {
                            AudioFocusHelper.this.mAudioFocusListener
                                    .onAudioFocusGain();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:// 你已经失去了音频焦点很长时间了。你必须停止所有的音频播放。因为你应该不希望长时间等待焦点返回，这将是你尽可能清除你的资源的一个好地方。例如，你应该释放MediaPlayer。
                        if (AudioFocusHelper.this.mAudioFocusListener != null) {
                            AudioFocusHelper.this.mAudioFocusListener
                                    .onAudioFocusLoss();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:// 你暂时失去了音频焦点，但很快会重新得到焦点。你必须停止所有的音频播放，但是你可以保持你的资源，因为你可能很快会重新获得焦点。
                        if (AudioFocusHelper.this.mAudioFocusListener != null) {
                            AudioFocusHelper.this.mAudioFocusListener
                                    .onAudioFocusLossTransient();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:// 你暂时失去了音频焦点，但你可以小声地继续播放音频（低音量）而不是完全扼杀音频。
                        if (AudioFocusHelper.this.mAudioFocusListener != null) {
                            AudioFocusHelper.this.mAudioFocusListener
                                    .onAudioFocusLossTransientCanDuck();
                        }
                        break;
                    default:
                        break;
                    }
                }
            };
        }
    }

    /**
     * 请求音频焦点
     * @return
     */
    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == this.mAudioManager
                .requestAudioFocus(this.mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * 释放音频焦点
     * @return
     */
    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == this.mAudioManager
                .abandonAudioFocus(this.mOnAudioFocusChangeListener);
    }

    /**
     * 设置音频焦点监听回调
     * @param audioFocusListener
     */
    public void setAudioFocusListener(AudioFocusListener audioFocusListener) {
        this.mAudioFocusListener = audioFocusListener;
    }

    /**
     * Like FLAG_SHOW_UI, but only dialog warnings and confirmations, no
     * sliders.
     * 带有可以显示系统音量保护提示的标识，拷贝于AudioManager内部标识，原因：原值为hide类型。
     */
    public static final int FLAG_SHOW_UI_WARNINGS = 1 << 10;

    /**
     * 设置系统声音
     * @param value
     */
    public void setVolume(int value) {
        if ((value >= 0) && (value <= this.getMaxVolume())) {
            this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    value, FLAG_SHOW_UI_WARNINGS);
        }
    }

    /**
     * 获取系统声音最大值
     * @return
     */
    public int getMaxVolume() {
        return this.mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 获取系统当前声音
     * @return
     */
    public int getCurrentVolume() {
        return this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 音频焦点监听
     * @author yeguolong
     */
    public interface AudioFocusListener {

        /**
         * 你已经得到了音频焦点。
         */
        public void onAudioFocusGain();

        /**
         * 你已经失去了音频焦点很长时间了。你必须停止所有的音频播放。因为你应该不希望长时间等待焦点返回，这将是你尽可能清除你的资源的一个好地方。例如
         * ，你应该释放MediaPlayer。
         */
        public void onAudioFocusLoss();

        /**
         * 你暂时失去了音频焦点，但很快会重新得到焦点。你必须停止所有的音频播放，但是你可以保持你的资源，因为你可能很快会重新获得焦点。
         */
        public void onAudioFocusLossTransient();

        /**
         * 你暂时失去了音频焦点，但你可以小声地继续播放音频（低音量）而不是完全扼杀音频。
         */
        public void onAudioFocusLossTransientCanDuck();
    }

}
