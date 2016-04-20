package com.android.player.common;

public interface PlayerListener {

    /**
     * 更新播放进度
     * @param duration
     *            总时长（毫秒）
     * @param currentPosition
     *            当前进度（毫秒）
     */
    public void onUpdateInfo(int duration, int currentPosition);

}
