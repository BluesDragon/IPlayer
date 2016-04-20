package com.android.player.common.mediaplayer;

import java.util.List;

import android.net.Uri;
import android.view.SurfaceHolder;

import com.android.player.common.model.PlayItem;

/**
 * MediaPlayer播放控制接口，仅供IPlayControler的实现类持有
 * @author yeguolong
 */
public interface IMediaPlayer {

    /**
     * 开启一次播放
     * @param url
     *            播放地址
     */
    public void startPlay(PlayItem playItem);

    /**
     * 开启一次播放
     * @param uri
     *            播放uri
     */
    public void startPlay(Uri uri);

    /**
     * 播放
     */
    public void doPlay();

    /**
     * 暂停
     * @return 暂停成功或失败
     */
    public boolean doPause();

    /**
     * 停止
     */
    public void doStop();

    /**
     * seek快进
     */
    public void doSeek(int targetPosition);

    /**
     * 销毁播放器
     */
    public void gcMediaPlayer();

    /**
     * 设置播放回调接口
     */
    public void setMediaPlayerListener(MediaPlayerListener mediaPlayerListener);

    /**
     * 设置视频资源的显示器
     */
    public void setDisPlay(SurfaceHolder surfaceHolder);

    /**
     * 获取播放状态
     */
    public PlayStatus getPlayStatus();

    /**
     * 获取当前播放进度
     * @return
     */
    public int getCurrentPosition();

    /**
     * 获取播放时长
     * @return
     */
    public int getDuration();

    /**
     * 是否已经准备好，可以播放了
     * @return
     */
    public boolean isPrepared();

    /**
     * 获取视频宽度
     * @return
     */
    public int getVideoWidth();

    /**
     * 获取视频高度
     * @return
     */
    public int getVideoHeight();

    /**
     * 获取MediaPlayer实例：<br>
     * 1、根据使用者配置的IMediaPlayer的实现类来返回；<br>
     * 2、不配置的话，使用默认的DefaultMediaPlayer，会返回原生MediaPlayer，可以根据需要强转。<br>
     * @return
     */
    public Object getMediaPlayer();

    /**
     * 获取音轨等信息
     * @return
     */
    public List<AudioTrackInfo> getTrackInfo();

    /**
     * 选择音轨
     * @param index
     */
    public void setTrackInfo(int index);

    /**
     * 获取当前选择的音轨
     * @return
     */
    public int getSelectedTrack();

    /**
     * 设置是否循环播放
     * @param looping
     */
    public void setLooping(boolean looping);

    /**
     * 设置音频流类型
     * @param audioStreamType
     */
    public void setAudioStreamType(int audioStreamType);

    /**
     * 设置唤醒模式
     * @param mode
     */
    public void setWakeMode(int mode);

}
