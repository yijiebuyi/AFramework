package com.callme.platform.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/12/18
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HomeKeyEventBroadcastReceiver extends BroadcastReceiver {
    private static final String SYSTEM_EVENT_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "homekey";
    private static final String SYSTEM_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    private HomeKeyListenerHelper.HomeKeyListener listener;

    public HomeKeyEventBroadcastReceiver(HomeKeyListenerHelper.HomeKeyListener l) {
        listener = l;
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) {
            return;
        }
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_EVENT_REASON);
            if (null != reason) {
                if (reason.equals(SYSTEM_HOME_KEY)) {
                    //Home key short pressed.
                    if (null != listener) {
                        listener.onHomeKeyShortPressed();
                    }
                } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    //Home key long pressed.
                    if (null != listener) {
                        listener.onHomeKeyLongPressed();
                    }
                } else if (reason.equals(SYSTEM_DIALOG_REASON_ASSIST)) {
                    //Home key long pressed.
                    if (null != listener) {
                        listener.onHomeKeyLongPressed();
                    }
                }
            }
        }
    }
}
