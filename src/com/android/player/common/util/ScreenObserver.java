package com.android.player.common.util;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

/**
 * 屏幕状态观察者
 * @author yeguolong
 */
public class ScreenObserver {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;
    private static Method mReflectScreenState;

    public ScreenObserver(Context context) {
        mContext = context;
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOn();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOff();
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                if (mScreenStateListener != null) {
                    mScreenStateListener.onUserPresent();
                }
            }
        }
    }

    /**
     * 请求screen状态更新
     */
    public void setScreenStateListener(ScreenStateListener listener) {
        mScreenStateListener = listener;
    }

    /**
     * 第一次请求screen状态
     */
    private void firstGetScreenState() {
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Activity.POWER_SERVICE);
        if (isScreenOn(manager)) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 启动screen状态广播接收器
     */
    public void registerScreenBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mScreenReceiver = new ScreenBroadcastReceiver();
        try {
            mReflectScreenState = PowerManager.class.getMethod("isScreenOn",
                    new Class[] {});
        } catch (Exception e) {
        }
        if (mContext != null) {
            try {
                mContext.registerReceiver(mScreenReceiver, filter);
            } catch (Exception e) {
            }
        }
        firstGetScreenState();
    }

    public void unregisterScreenBroadcastReceiver() {
        mReflectScreenState = null;
        if (mContext != null) {
            try {
                mContext.unregisterReceiver(mScreenReceiver);
            } catch (Exception e) {
            }
        }
    }

    /**
     * screen是否打开状态
     */
    public boolean isScreenOn(PowerManager pm) {
        boolean screenState;
        try {
            screenState = (Boolean) mReflectScreenState.invoke(pm);
        } catch (Exception e) {
            screenState = false;
        }
        return screenState;
    }

    /**
     * screen是否锁住
     * @param c
     * @return
     */
    public final static boolean isScreenLocked(Context c) {
        android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c
                .getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    public void release() {
        unregisterScreenBroadcastReceiver();
        mContext = null;
    }

    public interface ScreenStateListener {
        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();
    }

}
