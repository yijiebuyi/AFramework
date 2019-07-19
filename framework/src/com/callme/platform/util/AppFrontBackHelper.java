package com.callme.platform.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：app前后台切换监听
 * 作者：huangyong
 * 创建时间：2018/12/14
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AppFrontBackHelper implements HomeKeyListenerHelper.HomeKeyListener {
    private final int MSG_BACK_APP = 100;
    private final int BACK_APP_TIME_OUT = 2 * 60 * 1000;

    private OnAppStatusListener mOnAppStatusListener;
    private static AppFrontBackHelper mInstance;
    private HomeKeyListenerHelper mHomeKeyListenerHelper;
    private Context mAppContext;
    private Handler mHandler;

    private boolean mHomeKeyPressBackGround = false;

    public static synchronized AppFrontBackHelper getInstance() {
        if (mInstance == null) {
            mInstance = new AppFrontBackHelper();
        }

        return mInstance;
    }

    /**
     * 注册状态监听，仅在Application中使用
     *
     * @param application
     */
    public void register(Application application) {
        register(application, null);
    }

    /**
     * 注册状态监听，仅在Application中使用
     *
     * @param application
     * @param listener
     */
    public void register(Application application, OnAppStatusListener listener) {
        mOnAppStatusListener = listener;
        mHomeKeyListenerHelper = new HomeKeyListenerHelper(application);
        mHomeKeyListenerHelper.registerHomeKeyListener(this);
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    /**
     * 解注
     *
     * @param application
     */
    public void unRegister(Application application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        if (mHomeKeyListenerHelper != null) {
            mHomeKeyListenerHelper.unregisterHomeKeyListener();
        }
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks =
            new Application.ActivityLifecycleCallbacks() {
                //打开的Activity数量统计
                private int activityStartCount = 0;

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    activityStartCount++;
                    mHomeKeyPressBackGround = false;
                    //数值从0变到1说明是从后台切到前台
                    if (activityStartCount == 1) {
                        //从后台切到前台
                        if (mOnAppStatusListener != null) {
                            mOnAppStatusListener.onForeground();
                        }

                        releaseHandler();
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    activityStartCount--;
                    //数值从1到0说明是从前台切到后台
                    if (activityStartCount == 0) {
                        //从前台切到后台
                        if (mOnAppStatusListener != null) {
                            mOnAppStatusListener.onBackstage();
                        }

                        initHandler();
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            };

    private void initHandler() {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    onHandleMessage(msg);
                }
            };
        }

        mHandler.sendEmptyMessageDelayed(MSG_BACK_APP, BACK_APP_TIME_OUT);
    }

    private void releaseHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private void onHandleMessage(Message msg) {
        if (msg != null && msg.what == MSG_BACK_APP) {
            if (mOnAppStatusListener != null) {
                mOnAppStatusListener.onBackAppTip();
            }
            mHandler.sendEmptyMessageDelayed(MSG_BACK_APP, BACK_APP_TIME_OUT);
        }
    }

    /**
     * 当按下home键盘是，同时也会触发onActivityStopped方法被调用
     * {@link android.app.Application.ActivityLifecycleCallbacks#onActivityStopped(Activity)}
     */
    @Override
    public void onHomeKeyShortPressed() {
        if (mOnAppStatusListener != null && !mHomeKeyPressBackGround) {
            mHomeKeyPressBackGround = true;
            mOnAppStatusListener.onHomeKeyPressed();
        }
    }

    @Override
    public void onHomeKeyLongPressed() {
        //TODO
        //if (mOnAppStatusListener != null) {
        //    mOnAppStatusListener.onHomeKeyPressed();
        //}
    }

    public interface OnAppStatusListener {
        /**
         * 回到app前台
         */
        void onForeground();

        /**
         * app进入后台运行
         */
        void onBackstage();

        /**
         * 回到app提示：APP后台运行时，每间隔2分钟，推送1次
         */
        void onBackAppTip();

        /**
         * app按下home键盘
         */
        void onHomeKeyPressed();
    }
}
