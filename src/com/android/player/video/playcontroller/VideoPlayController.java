package com.android.player.video.playcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.RelativeLayout.LayoutParams;

import com.android.player.common.controller.BasePlayController;
import com.android.player.common.mediaplayer.DefaultMediaPlayer;
import com.android.player.common.mediaplayer.IMediaPlayer;
import com.android.player.common.mediaplayer.MediaPlayerListener;
import com.android.player.common.mediaplayer.PlayStatus;
import com.android.player.common.model.PlayItem;
import com.android.player.common.util.LogUtil;
import com.android.player.video.VideoPlayerConfig;
import com.android.player.video.view.playerview.IPlayerView;
import com.android.player.video.view.playerview.IPlayerView.PlayerViewCallback;
import com.android.player.video.viewcontroller.IViewController;
import com.android.player.video.viewcontroller.ViewController;

/**
 * 视频播放控制器：持有IViewController
 * @author yeguolong
 */
public class VideoPlayController extends BasePlayController implements
        IVideoPlayController {

    private VideoPlayerConfig mVideoPlayerConfig;
    private IViewController mIViewController;// 界面布局控制接口
    private SurfaceHolder mSurfaceHolder;
    private boolean isActivityActive;// 播放控件是否显示（获取焦点）
    private boolean isSurfaceCreated;// SurfaceView是否显示
    private Callback mSurfaceHolderCallback;

    private int windowWidth;// 播放界面宽度，不一定是屏幕宽度
    private int windowHeight;// 播放界面高度，不一定是屏幕高度
    private int videoWidth;// 视频宽度
    private int videoHeight;// 视频高度

    private static final int SCAN_VIDEO_SIZE = 1;

    @SuppressLint("HandlerLeak")
    protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case SCAN_VIDEO_SIZE:
                VideoPlayController.this.scanVideoSize();
                break;
            default:
                break;
            }
        };
    };

    public VideoPlayController(Context context,
            VideoPlayerConfig videoPlayerConfig) {
        super(context);
        this.mVideoPlayerConfig = videoPlayerConfig;
        this.init();
    }

    private void init() {
        if (mVideoPlayerConfig != null
                && mVideoPlayerConfig.isRegisterAudioFocus()) {
            initAudioFocusListener();
        }
        if (mVideoPlayerConfig != null
                && mVideoPlayerConfig.isRegisterMediaSession()) {
            initMediaSessionListener();
        }
        this.initViewController();
    }

    private void initViewController() {
        this.mIViewController = new ViewController(this.mContext);
        this.initSurfaceHolder();
        this.mIViewController.getIPlayerView().setPlayerViewCallback(
                new PlayerViewCallback() {

                    @Override
                    public void onPlayerViewLayoutChange(boolean changed,
                            int left, int top, int right, int bottom) {
                        if (changed) {
                            VideoPlayController.this
                                    .log("onPlayerViewLayoutChange-->left:"
                                            + left + " top:" + top + " right:"
                                            + right + " bottom:" + bottom);
                            VideoPlayController.this.windowWidth = Math
                                    .abs(right - left);
                            VideoPlayController.this.windowHeight = Math
                                    .abs(bottom - top);
                            VideoPlayController.this.scanVideoSize();
                        }
                    }
                });
    }

    private void initSurfaceHolder() {
        if (this.mIViewController != null) {
            SurfaceHolder surfaceHolder = this.mIViewController
                    .getIPlayerView().getSurfaceView().getHolder();
            if (surfaceHolder != null) {
                surfaceHolder.addCallback(new Callback() {

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        VideoPlayController.this.log("surfaceCreated");
                        VideoPlayController.this.mSurfaceHolder = holder;
                        VideoPlayController.this.isSurfaceCreated = true;
                        VideoPlayController.this.checkPlay();
                        if (VideoPlayController.this.mSurfaceHolderCallback != null) {
                            VideoPlayController.this.mSurfaceHolderCallback
                                    .surfaceCreated(holder);
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder,
                            int format, int width, int height) {
                        VideoPlayController.this.log("surfaceChanged-->width:"
                                + width + " height:" + height);
                        VideoPlayController.this.windowWidth = width;
                        VideoPlayController.this.windowHeight = height;
                        if (VideoPlayController.this.mSurfaceHolderCallback != null) {
                            VideoPlayController.this.mSurfaceHolderCallback
                                    .surfaceChanged(holder, format, width,
                                            height);
                        }
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        VideoPlayController.this.log("surfaceDestroyed");
                        VideoPlayController.this.isSurfaceCreated = false;
                        VideoPlayController.this.mSurfaceHolder = null;
                        if (VideoPlayController.this.mPlayItem != null
                                && VideoPlayController.this.mIMediaPlayer != null) {
                            int currentPosition = VideoPlayController.this.mIMediaPlayer
                                    .getCurrentPosition();
                            VideoPlayController.this.mPlayItem
                                    .setStartPostion(currentPosition);
                            VideoPlayController.this
                                    .log("surfaceDestroyed-->currentPosition:"
                                            + currentPosition);
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                VideoPlayController.this.gcMediaPlayer();
                            };
                        }.start();
                        if (VideoPlayController.this.mSurfaceHolderCallback != null) {
                            VideoPlayController.this.mSurfaceHolderCallback
                                    .surfaceDestroyed(holder);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected IMediaPlayer initMediaPlayer() {
        if (mIMediaPlayer == null) {
            this.mIMediaPlayer = new DefaultMediaPlayer(this.mContext);
        }
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer
                    .setMediaPlayerListener(new MediaPlayerListener() {

                        @Override
                        public void onVideoSizeChanged(int width, int height) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onVideoSizeChanged(width, height);
                            }
                        }

                        @Override
                        public void onVideoRenderingStart(int extra) {
                            VideoPlayController.this.setCoverViewVisible(false);
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onVideoRenderingStart(extra);
                            }
                        }

                        @Override
                        public void onPrepared() {
                            if (VideoPlayController.this.mIMediaPlayer != null) {
                                VideoPlayController.this.videoWidth = VideoPlayController.this.mIMediaPlayer
                                        .getVideoWidth();
                                VideoPlayController.this.videoHeight = VideoPlayController.this.mIMediaPlayer
                                        .getVideoHeight();
                                VideoPlayController.this
                                        .log("onPrepared-->videoWidth:"
                                                + VideoPlayController.this.videoWidth
                                                + " videoHeight:"
                                                + VideoPlayController.this.videoHeight);
                                if (VideoPlayController.this.mSurfaceHolder != null) {
                                    VideoPlayController.this.mIMediaPlayer
                                            .setDisPlay(VideoPlayController.this.mSurfaceHolder);
                                }
                                int duration = VideoPlayController.this
                                        .getDuration();
                                VideoPlayController.this
                                        .log("onPrepared-->duration:"
                                                + duration);
                                VideoPlayController.this.doPlay();
                                if (VideoPlayController.this.mPlayItem != null) {
                                    VideoPlayController.this.mPlayItem
                                            .setDuration(duration);
                                    int startPostion = VideoPlayController.this.mPlayItem
                                            .getStartPostion();
                                    VideoPlayController.this
                                            .log("onPrepared-->startPostion:"
                                                    + startPostion);
                                    if (startPostion > 0) {
                                        VideoPlayController.this
                                                .doSeek(startPostion);
                                        VideoPlayController.this.mPlayItem
                                                .setStartPostion(0);
                                    }
                                }
                                if (!VideoPlayController.this.canPlay()) {
                                    VideoPlayController.this.doPause();
                                }
                            }
                            VideoPlayController.this.scanVideoSize();
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onPrepared();
                            }
                        }

                        @Override
                        public void onMediaInfoUpdate(int currentPosition) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onMediaInfoUpdate(currentPosition);
                            }
                        }

                        @Override
                        public boolean onInfo(int what, int extra) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onInfo(what, extra);
                            }
                            return false;
                        }

                        @Override
                        public boolean onError(int what, int extra) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onError(what, extra);
                            }
                            return false;
                        }

                        @Override
                        public void onCompletion() {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onCompletion();
                            }

                        }

                        @Override
                        public void onBufferingStart(int extra) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onBufferingStart(extra);
                            }
                        }

                        @Override
                        public void onBufferingEnd(int extra) {
                            if (VideoPlayController.this.mMediaPlayerListener != null) {
                                VideoPlayController.this.mMediaPlayerListener
                                        .onBufferingEnd(extra);
                            }
                        }

                        @Override
                        public void onPlay() {
                            if (mMediaPlayerListener != null) {
                                mMediaPlayerListener.onPlay();
                            }
                        }

                        @Override
                        public void onPause() {
                            if (mMediaPlayerListener != null) {
                                mMediaPlayerListener.onPause();
                            }
                        }

                        @Override
                        public void onStartPlay() {
                            if (mMediaPlayerListener != null) {
                                mMediaPlayerListener.onStartPlay();
                            }
                        }
                    });
        }
        return this.mIMediaPlayer;
    }

    /**
     * 扫描视频大小，重新设置视频横竖屏显示方式
     */
    private void scanVideoSize() {
        this.log("scanVideoSize");
        if (this.videoWidth == 0 || this.videoHeight == 0
                || this.windowWidth == 0 || this.windowHeight == 0) {
            return;
        }
        int width = this.windowWidth;
        int height = this.windowHeight;
        double videoAR = (double) this.videoWidth / this.videoHeight;
        if (this.videoWidth * this.windowHeight > this.windowWidth
                * this.videoHeight) {// 高度要调整
            height = (int) (this.windowWidth / videoAR);
        } else if (this.videoWidth * this.windowHeight < this.windowWidth
                * this.videoHeight) {// 宽度要调整
            width = (int) (this.windowHeight * videoAR);
        }

        if (this.mIViewController != null) {
            IPlayerView iPlayerView = this.mIViewController.getIPlayerView();
            if (iPlayerView != null) {
                SurfaceView surfaceView = iPlayerView.getSurfaceView();
                LayoutParams layoutParams = (LayoutParams) surfaceView
                        .getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                surfaceView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public void startPlay(PlayItem playItem) {
        this.mPlayItem = playItem;
        this.checkPlay();
    }

    private void checkPlay() {
        if (this.mPlayItem == null || !this.isSurfaceCreated) {
            return;
        }
        this.setCoverViewVisible(true);
        this.checkPlayType(this.mPlayItem);
        this.startPlay();
    }

    private void setCoverViewVisible(boolean visible) {
        if (VideoPlayController.this.mIViewController != null) {
            VideoPlayController.this.mIViewController.getIPlayerView()
                    .setCoverViewVisible(visible);
        }
    }

    private void startPlay() {
        if (this.mIMediaPlayer != null) {
            this.mIMediaPlayer.startPlay(this.mPlayItem);
        }
    }

    @Override
    public IViewController getViewController() {
        return this.mIViewController;
    }

    @Override
    public boolean canPlay() {
        return super.canPlay() && this.isActivityActive
                && this.isSurfaceCreated;
    }

    @Override
    public void onResume() {
        this.isActivityActive = true;
        this.requestMediaSessionFocus();
        if (this.getPlayStatus() == PlayStatus.PAUSED && this.canPlay()) {
            this.doPlay();
        }
    }

    @Override
    public void onPause() {
        this.isActivityActive = false;
        this.releaseMediaSessionFocus();
        if (this.getPlayStatus() == PlayStatus.PLAYING) {
            this.doPause();
        }
    }

    @Override
    protected void log(String text) {
        LogUtil.d("VideoPlayController-->" + text);
    }

    @Override
    public void addSurfaceHolderCallback(Callback callback) {
        this.mSurfaceHolderCallback = callback;
    }

}