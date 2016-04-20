package com.android.player.common.controller;

import com.android.player.common.model.PlayItem;

public interface ControllerCallback {

    /**
     * 检测播放类型的回调
     * @param playItem
     * @return
     */
    public boolean onCheckPlayType(IPlayController iPlayController,
            PlayItem playItem);

}
