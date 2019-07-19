package com.callme.platform.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
public class HomeKeyListenerHelper {
    private Context context;
    private BroadcastReceiver receiver;

    public HomeKeyListenerHelper(Context ctx) {
        context = ctx;
    }

    public void registerHomeKeyListener(HomeKeyListener l) {
        if (null != context) {
            registerListener(l);
        }
    }

    private void registerListener(HomeKeyListener l) {
        receiver = new HomeKeyEventBroadcastReceiver(l);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(receiver, intentFilter);
    }

    public void unregisterHomeKeyListener() {
        if (null != context && null != receiver) {
            context.unregisterReceiver(receiver);
        }
    }

    public interface HomeKeyListener {
        public void onHomeKeyShortPressed();
        public void onHomeKeyLongPressed();
    }
}
