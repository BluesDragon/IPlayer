package com.android.player.common.mediaplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.MediaPlayer.TrackInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.android.player.common.model.PlayItem;
import com.android.player.common.model.PlayItem.PlayType;
import com.android.player.common.util.LogUtil;

/**
 * MediaPlayer播放接口实现基类：<br>
 * 持有最基本的播放器（MediaPlayer）、播放状态（PlayStatus）、回调（mMediaPlayerListener）<br>
 * @author yeguolong
 */
@SuppressLint("NewApi")
public class DefaultMediaPlayer implements IMediaPlayer {

    private final Context mContext;
    private PlayItem mPlayItem;
    private MediaPlayer mMediaPlayer;
    private MediaPlayerListener mMediaPlayerListener;
    private PlayStatus mPlayStatus = PlayStatus.STOPED;
    private boolean isPrepared;

    private int mDuration;
    private int mCurrentPosition;
    private boolean isGCing;
    private boolean isLooping;
    private int mAudioStreamType = AudioManager.STREAM_MUSIC;

    /**
     * 播放视频的Uri
     */
    private Uri mPlayUri;

    private Timer mTimer;
    private TimerTask mTimerTask;

    public DefaultMediaPlayer(Context context) {
        this.mContext = context;
    }

    private void updatePlayStatus() {
        if (this.mMediaPlayerListener != null) {
            this.mMediaPlayerListener.onMediaInfoUpdate(this
                    .getCurrentPosition());
        }
    }

    private void startTimer() {
        stopTimer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                updatePlayStatus();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void startPlay(PlayItem playItem) {
        this.mPlayItem = playItem;
        String url = this.mPlayItem == null ? null : this.mPlayItem
                .getPlayUrl();
        this.log("startPlay-->url:" + url);
        if (this.mContext == null || TextUtils.isEmpty(url)) {
            return;
        }
        this.startPlay();
    }

    @Override
    public void startPlay(Uri uri) {
        this.mPlayUri = uri;
        this.log("startPlay-->uri:" + uri);
        if (this.mPlayUri == null) {
            return;
        }
        this.startPlay();
    }

    private void startPlay() {
        this.mCurrentPosition = 0;
        this.mDuration = 0;
        this.isPrepared = false;
        this.gcMediaPlayer();
        if (mMediaPlayerListener != null) {
            mMediaPlayerListener.onStartPlay();
        }
        this.initMediaPlayer();
        this.doPrepare();
    }

    /**
     * 开始请求播放数据
     * @param url
     */
    private void doPrepare() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                log("doPrepare");
                isPrepared = false;
                mPlayStatus = PlayStatus.PREPARRING;
                try {
                    if (mMediaPlayer != null) {
                        if (mPlayUri != null) {
                            mMediaPlayer.setDataSource(mContext, mPlayUri);
                        } else {
                            String url = mPlayItem.getPlayUrl();
                            if (mPlayItem != null
                                    && mPlayItem.getPlayType() == PlayType.LOCAL) {
                                mMediaPlayer.setDataSource(url);
                            } else {
                                mMediaPlayer.setDataSource(mContext,
                                        Uri.parse(url));
                            }
                        }
                        mMediaPlayer.prepareAsync();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("setDataSource-->error:" + e.toString());
                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onError(1, -2147483648);
                    }
                }
            }
        }.start();
    }

    private void initMediaPlayer() {
        this.log("initMediaPlayer");
        if (this.mMediaPlayer != null) {
            return;
        }
        this.mMediaPlayer = new MediaPlayer();
        setLooping(isLooping);
        this.mMediaPlayer.setAudioStreamType(mAudioStreamType);
        setWakeMode(PowerManager.PARTIAL_WAKE_LOCK);
        this.mMediaPlayer.setOnInfoListener(new OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:// 缓冲开始
                    mPlayStatus = PlayStatus.BUFFERRING;
                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onBufferingStart(extra);
                    }
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:// 缓冲结束
                    if (mPlayStatus == PlayStatus.PAUSED) {// 判断有的rom在缓冲的时候，pause失效的问题。
                        doPause();
                    } else if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mPlayStatus = PlayStatus.PLAYING;
                    }
                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onBufferingEnd(extra);
                    }
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:// 起播
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mPlayStatus = PlayStatus.PLAYING;
                    }
                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onVideoRenderingStart(extra);
                    }
                    break;
                }
                if (mMediaPlayerListener != null) {
                    return mMediaPlayerListener.onInfo(what, extra);
                }
                return false;
            }
        });
        this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                log("onCompletion");
                if (mPlayStatus != PlayStatus.COMPLETED) {
                    mPlayStatus = PlayStatus.COMPLETED;
                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onCompletion();
                    }
                }
            }
        });
        this.mMediaPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                log("onError-->what:" + what + " extra:" + extra);
                if (mMediaPlayerListener != null) {
                    return mMediaPlayerListener.onError(what, extra);
                }
                return false;
            }
        });
        this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mPlayStatus = PlayStatus.PREPARED;
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPrepared();
                }
            }
        });
        this.mMediaPlayer
                .setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {

                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width,
                            int height) {
                        if (mMediaPlayerListener != null) {
                            mMediaPlayerListener.onVideoSizeChanged(width,
                                    height);
                        }
                    }
                });
    }

    @Override
    public PlayStatus getPlayStatus() {
        return this.mPlayStatus;
    }

    @Override
    public void setDisPlay(SurfaceHolder surfaceHolder) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setDisplay(surfaceHolder);
        }
    }

    @Override
    public void doPlay() {
        this.log("doPlay");
        if (this.mMediaPlayer != null
                && this.isPrepared()
                && (this.mPlayStatus == PlayStatus.PREPARED || this.mPlayStatus == PlayStatus.PAUSED)) {
            try {
                this.mMediaPlayer.start();
                this.mPlayStatus = PlayStatus.PLAYING;
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPlay();
                }
            } catch (IllegalStateException e) {
                this.log("doPlay-->IllegalStateException:" + e.toString());
                e.printStackTrace();
            }
        }
        this.startTimer();
    }

    @Override
    public void doSeek(int targetMsecPosition) {
        if (targetMsecPosition >= this.getDuration()) {
            return;
        }
        if (this.mMediaPlayer != null) {
            this.log("doSeek-->targetMsecPosition:" + targetMsecPosition);
            try {
                this.mMediaPlayer.seekTo(targetMsecPosition);
            } catch (IllegalStateException e) {
                this.log("doSeek-->IllegalStateException:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean doPause() {
        this.stopTimer();
        this.log("doPause");
        boolean state = false;
        if (this.mPlayStatus == PlayStatus.PLAYING && this.mMediaPlayer != null) {
            try {
                this.mMediaPlayer.pause();
                state = true;
                this.mPlayStatus = PlayStatus.PAUSED;
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onPause();
                }
            } catch (IllegalStateException e) {
                this.log("doPause-->IllegalStateException:" + e.toString());
                e.printStackTrace();
            }
        }
        return state;
    }

    @Override
    public void doStop() {
        this.stopTimer();
        this.isPrepared = false;
        if (this.mPlayStatus != PlayStatus.STOPED && this.mMediaPlayer != null) {
            this.log("doStop");
            try {
                if (this.isPrepared) {
                    this.mMediaPlayer.stop();
                }
                this.mPlayStatus = PlayStatus.STOPED;
            } catch (IllegalStateException e) {
                this.log("doStop-->IllegalStateException:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void gcMediaPlayer() {
        this.stopTimer();
        this.isPrepared = false;
        if (this.isGCing) {
            return;
        }
        this.isGCing = true;
        if (this.mMediaPlayer != null) {
            this.log("gcMediaPlayer");
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.release();
                this.mPlayStatus = PlayStatus.STOPED;
            } catch (Exception e) {
                this.log("gcMediaPlayer-->Exception:" + e.toString());
                e.printStackTrace();
            }
            this.mMediaPlayer = null;
        }
        this.isGCing = false;
    }

    @Override
    public void setMediaPlayerListener(MediaPlayerListener mediaPlayerListener) {
        this.mMediaPlayerListener = mediaPlayerListener;
    }

    @Override
    public int getCurrentPosition() {
        if (this.mMediaPlayer != null
                && (mPlayStatus == PlayStatus.PLAYING
                        || mPlayStatus == PlayStatus.PAUSED
                        || mPlayStatus == PlayStatus.PREPARED
                        || mPlayStatus == PlayStatus.COMPLETED || mPlayStatus == PlayStatus.BUFFERRING)) {
            try {
                this.mCurrentPosition = this.mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.mCurrentPosition;
    }

    @Override
    public int getDuration() {
        if (this.mDuration == 0) {
            if (this.mMediaPlayer != null
                    && (mPlayStatus == PlayStatus.PLAYING
                            || mPlayStatus == PlayStatus.PAUSED
                            || mPlayStatus == PlayStatus.PREPARED
                            || mPlayStatus == PlayStatus.COMPLETED || mPlayStatus == PlayStatus.BUFFERRING)) {
                return this.mDuration = this.mMediaPlayer.getDuration();
            }
        }
        return this.mDuration;
    }

    @Override
    public boolean isPrepared() {
        return this.isPrepared;
    }

    @Override
    public Object getMediaPlayer() {
        return this.mMediaPlayer;
    }

    @Override
    public int getVideoWidth() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    private void log(String log) {
        LogUtil.d("DefaultMediaPlayer-->" + log);
    }

    @Override
    public List<AudioTrackInfo> getTrackInfo() {
        if (this.mMediaPlayer != null) {
            TrackInfo[] trackInfo = this.mMediaPlayer.getTrackInfo();
            List<AudioTrackInfo> trackInfoList = new ArrayList<AudioTrackInfo>();
            for (int i = 0; i < trackInfo.length; i++) {
                TrackInfo info = trackInfo[i];
                if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    AudioTrackInfo audioTrackInfo = new AudioTrackInfo();
                    audioTrackInfo.setTrackId(i);
                    audioTrackInfo.setTrackType(info.getTrackType());
                    audioTrackInfo.setLanguage(info.getLanguage());
                    trackInfoList.add(audioTrackInfo);
                }
            }
            return trackInfoList;
        }
        return null;
    }

    @Override
    public void setTrackInfo(int index) {
        if (this.mMediaPlayer != null) {
            try {
                this.mMediaPlayer.selectTrack(index);
            } catch (Exception e) {
                this.log("setTrackInfo-->Exception:" + e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getSelectedTrack() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer
                    .getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        }
        return 0;
    }

    @Override
    public void setLooping(boolean looping) {
        this.isLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    @Override
    public void setAudioStreamType(int audioStreamType) {
        this.mAudioStreamType = audioStreamType;
        if (mMediaPlayer != null) {
            mMediaPlayer.setAudioStreamType(audioStreamType);
        }
    }

    @Override
    public void setWakeMode(int mode) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setWakeMode(mContext, mode);
        }
    }
}
