package com.android.player.video.playcontroller;

import android.view.SurfaceHolder.Callback;

import com.android.player.common.controller.IPlayController;
import com.android.player.video.viewcontroller.IViewController;

public interface IVideoPlayController extends IPlayController {

    /**
     * 获取IViewController
     * @return
     */
    public IViewController getViewController();

    /**
     * 当获得焦点
     */
    public void onResume();

    /**
     * 当失去焦点
     */
    public void onPause();

    /**
     * 添加SurfaceHolder的Callback回调接口监听
     * @param callback
     */
    public void addSurfaceHolderCallback(Callback callback);

}
