package com.android.player.common.mediaplayer;

public interface MediaPlayerListener {

    /**
     * 播放信息回调
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    public boolean onInfo(int what, int extra);

    /**
     * 播放完成
     * @param mp
     */
    public void onCompletion();

    /**
     * 播放错误
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    public boolean onError(int what, int extra);

    /**
     * 请求完成，准备播放
     * @param mp
     */
    public void onPrepared();

    /**
     * 当视频大小发生改变
     * @param mp
     * @param width
     * @param height
     */
    public void onVideoSizeChanged(int width, int height);

    /**
     * 缓冲开始
     * @param mp
     * @param extra
     */
    public void onBufferingStart(int extra);

    /**
     * 缓冲结束
     * @param mp
     * @param extra
     */
    public void onBufferingEnd(int extra);

    /**
     * 播放开始，视频第一帧
     * @param mp
     * @param extra
     */
    public void onVideoRenderingStart(int extra);

    /**
     * 定时更新播放信息回调（主线程）
     * @param currentPosition
     */
    public void onMediaInfoUpdate(int currentPosition);

    /**
     * 当开始播放的回调
     */
    public void onPlay();

    /**
     * 当暂停播放的回调
     */
    public void onPause();

    /**
     * 开始请求播放
     */
    public void onStartPlay();

}
