package com.android.player.common.model;

import java.io.Serializable;

/**
 * 播放视频/音频介质的载体类
 * @author yeguolong
 */
public class PlayItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum PlayType {
        ONLINE, // 在线
        LOCAL, // 本地
    }

    /**
     * 视频类型，默认是本地播放
     */
    private PlayType playType = PlayType.LOCAL;
    /**
     * 名称
     */
    private String displayName;
    /**
     * 播放地址
     */
    private String playUrl;
    /**
     * 起播时间
     */
    private int startPosition;
    /**
     * 时长
     */
    private long duration;
    /**
     * 视频宽度
     */
    private int videoWidth;
    /**
     * 视频高度
     */
    private int videoHeight;

    public PlayItem() {

    }

    public PlayItem(String playUrl) {
        setPlayUrl(playUrl);
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPlayUrl() {
        return this.playUrl;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setStartPostion(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPostion() {
        return this.startPosition;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public PlayType getPlayType() {
        return this.playType;
    }

    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

    public int getVideoWidth() {
        return this.videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return this.videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

}
