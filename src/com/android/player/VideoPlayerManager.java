package com.android.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.player.common.util.LogUtil;
import com.android.player.video.VideoPlayerConfig;
import com.android.player.video.playcontroller.IVideoPlayController;
import com.android.player.video.playcontroller.VideoPlayController;

public class VideoPlayerManager {

    protected static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";// 声音改变
    protected static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";// 网络改变
    protected static final String NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";// 打电话
    protected static final String PHONE_STATE = "android.intent.action.PHONE_STATE";// 电话状态改变

    protected Context mContext;
    protected VideoPlayerConfig mVideoPlayerConfig;
    protected IVideoPlayController mIVideoPlayController;
    protected BroadcastReceiver mBroadcastReceiver;

    public VideoPlayerManager(Context context) {
        this.init(context, null);
    }

    public VideoPlayerManager(Context context,
            VideoPlayerConfig videoPlayerConfig) {
        this.init(context, videoPlayerConfig);
    }

    private void init(Context context, VideoPlayerConfig videoPlayerConfig) {
        this.mContext = context;
        this.mVideoPlayerConfig = videoPlayerConfig;
        if (mVideoPlayerConfig == null) {
            mVideoPlayerConfig = new VideoPlayerConfig();
        }
        this.initPlayController();
        this.registerBroadcastListener();
    }

    private void initPlayController() {
        this.mIVideoPlayController = new VideoPlayController(this.mContext,
                mVideoPlayerConfig);
    }

    /**
     * 当获得焦点时需要调用
     */
    public void onResume() {
        this.log("onResume");
        if (this.mIVideoPlayController != null) {
            this.mIVideoPlayController.onResume();
        }
    }

    /**
     * 当失去焦点时需要调用
     */
    public void onPause() {
        this.log("onPause");
        if (this.mIVideoPlayController != null) {
            this.mIVideoPlayController.onPause();
        }
    }

    /**
     * 获取视频播放配置器
     * @return
     */
    public VideoPlayerConfig getVideoPlayerConfig() {
        return mVideoPlayerConfig;
    }

    /**
     * 获取播放控制器
     * @return
     */
    public IVideoPlayController getVideoPlayController() {
        return this.mIVideoPlayController;
    }

    private void registerBroadcastListener() {
        if (this.mContext != null) {
            IntentFilter intentFilter = null;
            if (mVideoPlayerConfig != null
                    && mVideoPlayerConfig.isRegisterScreenStateReceiver()) {
                if (intentFilter == null) {
                    intentFilter = new IntentFilter();
                }
                intentFilter.addAction(Intent.ACTION_SCREEN_ON);// 开屏
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);// 锁屏
                intentFilter.addAction(Intent.ACTION_USER_PRESENT);// 解锁
            }
            if (mVideoPlayerConfig != null
                    && mVideoPlayerConfig.isRegisterPhoneStateReceiver()) {
                if (intentFilter == null) {
                    intentFilter = new IntentFilter();
                }
                intentFilter.addAction(NEW_OUTGOING_CALL);// 拨打电话
                intentFilter.addAction(PHONE_STATE);// 电话状态改变
            }
            if (intentFilter == null) {// 没有需要监听的广播，直接返回
                return;
            }
            this.log("registerBroadcastListener");
            this.mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    log("registerBroadcastListener-->action:" + action);
                    if (Intent.ACTION_SCREEN_ON.equals(action)) {// 开屏
                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {// 锁屏
                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {// 解锁
                    } else if (NEW_OUTGOING_CALL.equals(action)) {// 拨打电话
                    } else if (PHONE_STATE.equals(action)) {// 电话状态改变
                    }
                }
            };
            try {
                this.mContext.registerReceiver(this.mBroadcastReceiver,
                        intentFilter);
            } catch (Exception e) {
                LogUtil.e("VideoPlayerManager-->registerBroadcastListener-->Exception:" + e.toString());
            }
        }
    }

    private void unregisterBroadcastListener() {
        if (this.mContext != null && this.mBroadcastReceiver != null) {
            try {
                this.log("unregisterBroadcastListener");
                this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            } catch (Exception e) {
                LogUtil.e("VideoPlayerManager-->unregisterBroadcastListener-->Exception:" + e.toString());
            }
        }
    }

    public void release() {
        this.log("release");
        if (this.mIVideoPlayController != null) {
            this.mIVideoPlayController.release();
        }
        this.unregisterBroadcastListener();
    }

    protected void log(String text) {
        LogUtil.d("VideoPlayerManager-->" + text);
    }

}
