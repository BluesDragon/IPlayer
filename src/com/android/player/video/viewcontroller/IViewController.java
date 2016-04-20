package com.android.player.video.viewcontroller;

import com.android.player.video.view.playerview.IPlayerView;

/**
 * 界面布局控制接口，仅供IVideoPlayControler的实现类持有
 * @author yeguolong
 */
public interface IViewController {

    /**
     * 获取播放界面布局
     * @return
     */
    public IPlayerView getIPlayerView();

}
