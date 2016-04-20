package com.android.player.video.view.playerview;

import android.view.SurfaceView;
import android.widget.RelativeLayout;

public interface IPlayerView {

    public interface PlayerViewCallback {
        public void onPlayerViewLayoutChange(boolean changed, int left,
                int top, int right, int bottom);
    }

    /**
     * 获取用于显示的SurfaceView
     * @return
     */
    public SurfaceView getSurfaceView();

    /**
     * 获取播放布局
     * @return
     */
    public RelativeLayout getPlayerView();

    /**
     * 设置覆盖浮层显示/隐藏
     * @param visible
     */
    public void setCoverViewVisible(boolean visible);

    /**
     * 设置播放布局的回调，主要是播放布局的重绘回调
     * @param playerViewCallback
     */
    public void setPlayerViewCallback(PlayerViewCallback playerViewCallback);
}
