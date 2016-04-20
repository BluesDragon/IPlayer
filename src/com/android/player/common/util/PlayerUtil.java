package com.android.player.common.util;

import android.text.TextUtils;

import com.android.player.common.model.PlayItem.PlayType;

public class PlayerUtil {

    /**
     * 根据URL检查播放类型，在线/本地
     * @param playUrl
     * @return
     */
    public static PlayType getPlayType(String playUrl) {
        PlayType playType = PlayType.LOCAL;
        if (!TextUtils.isEmpty(playUrl)
                && (playUrl.startsWith("http") || playUrl.startsWith("rtsp"))) {
            playType = PlayType.ONLINE;
        } else {
            playType = PlayType.LOCAL;
        }
        return playType;
    }

}
