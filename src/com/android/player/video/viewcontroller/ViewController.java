package com.android.player.video.viewcontroller;

import android.content.Context;

import com.android.player.video.view.playerview.IPlayerView;
import com.android.player.video.view.playerview.PlayerView;

/**
 * 界面布局控制实现类：<br>
 * 管理整个界面的各个界面接口：<br>
 * 1、IPlayerView<br>
 * @author yeguolong
 */
public class ViewController implements IViewController {

    private final Context mContext;

    private IPlayerView mIPlayerView;

    public ViewController(Context context) {
        this.mContext = context;
        this.init();
    }

    private void init() {
        this.initPlayerView();
    }

    private void initPlayerView() {
        this.mIPlayerView = new PlayerView(this.mContext);
    }

    @Override
    public IPlayerView getIPlayerView() {
        return this.mIPlayerView;
    }

}