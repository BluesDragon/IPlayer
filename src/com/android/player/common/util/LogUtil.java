package com.android.player.common.util;

import android.util.Log;

/**
 * Log日志工具类，所有日志必须通过这个类输出
 * @author yeguolong
 */
public class LogUtil {

    private static String TAG = "iplayer";

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void i(String log) {
        Log.i(TAG, log);
    }

    public static void d(String log) {
        Log.d(TAG, log);
    }

    public static void e(String log) {
        Log.e(TAG, log);
    }

}
