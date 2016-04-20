package com.android.player.video.view.playerview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PlayerView implements IPlayerView {

    private RelativeLayout mPlayerView;// 这个播放界面的layout
    private SurfaceView mSurfaceView;// 播放显示的SurfaceView
    private RelativeLayout mCoverView;
    private PlayerViewCallback mPlayerViewCallback;

    public PlayerView(Context context) {
        this.initView(context);
    }

    /**
     * 初始化各个View布局
     * @param context
     */
    private void initView(Context context) {
        this.mPlayerView = new RelativeLayout(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (PlayerView.this.mPlayerViewCallback != null) {
                    PlayerView.this.mPlayerViewCallback
                            .onPlayerViewLayoutChange(changed, l, t, r, b);
                }
            }
        };
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.mPlayerView.setKeepScreenOn(true);
        this.mPlayerView.setClipChildren(false);
        this.mPlayerView.setBackgroundColor(Color.BLACK);
        this.mPlayerView.setLayoutParams(params);
        this.mPlayerView.setGravity(Gravity.CENTER);

        this.initSurfaceView(context);
        this.initCoverView(context);
    }

    private void initSurfaceView(Context context) {
        this.mSurfaceView = new SurfaceView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.mSurfaceView.setLayoutParams(params);
        if (this.mPlayerView != null) {
            this.mPlayerView.addView(this.mSurfaceView);
        }
    }

    private void initCoverView(Context context) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.mCoverView = new RelativeLayout(context);
        this.mCoverView.setBackgroundColor(Color.BLACK);
        this.mCoverView.setLayoutParams(params);
        if (this.mPlayerView != null) {
            this.mPlayerView.addView(this.mCoverView);
        }
    }

    @Override
    public SurfaceView getSurfaceView() {
        return this.mSurfaceView;
    }

    @Override
    public RelativeLayout getPlayerView() {
        return this.mPlayerView;
    }

    @Override
    public void setCoverViewVisible(boolean visible) {
        if (this.mCoverView != null) {
            this.mCoverView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setPlayerViewCallback(PlayerViewCallback playerViewCallback) {
        this.mPlayerViewCallback = playerViewCallback;
    }

}
