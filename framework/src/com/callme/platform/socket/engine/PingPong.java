package com.callme.platform.socket.engine;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.callme.platform.socket.client.Ack;
import com.callme.platform.socket.client.Options;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：默认心跳
 * 作者：huangyong
 * 创建时间：2019/2/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PingPong {
    /**
     * 心跳消息
     */
    private final static int MSG_HEARTBEAT = 1;
    /**
     * 心跳消息超时
     */
    private final static int MSG_HEARTBEAT_TIMEOUT = 2;
    /**
     * 心跳间隔
     */
    private final static int HEARTBEAT_INTERVAL = 60 * 1000;
    /**
     * 心跳超时间隔
     */
    private final static int HEARTBEAT_TIMEOUT_INTERVAL = 45 * 1000;

    private Manager manager;
    private HandlerThread handlerThread;
    private volatile Looper heartbeatLooper;
    private volatile Handler heartbeatHandler;
    private int serial = Integer.MAX_VALUE;

    public PingPong(Manager manager) {
        this.manager = manager;
        this.handlerThread = new HandlerThread("heartbeat", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        this.heartbeatLooper = handlerThread.getLooper();
        this.heartbeatHandler = new HeartbeatHandler(heartbeatLooper);
    }

    /**
     * 自动发送心跳包
     */
    public void sendHeartbeat() {
        Options options = manager.getOptions();
        if (options == null || options.heartbeatData == null) {
            return;
        }

        manager.send(serial--, options.heartbeatData, new Ack<Packet>() {
            @Override
            public void call(Packet pkt) {
                Log.i(Manager.TAG, "heartbeat callback");
                if (heartbeatHandler != null) {
                    heartbeatHandler.removeMessages(MSG_HEARTBEAT_TIMEOUT);
                }
            }
        });
        if (heartbeatHandler != null) {
            heartbeatHandler.sendEmptyMessageDelayed(MSG_HEARTBEAT, HEARTBEAT_INTERVAL);
            heartbeatHandler.sendEmptyMessageDelayed(MSG_HEARTBEAT_TIMEOUT, HEARTBEAT_TIMEOUT_INTERVAL);
        }
    }

    public void release() {
        if (heartbeatHandler != null) {
            heartbeatHandler.removeCallbacksAndMessages(null);
            heartbeatHandler = null;
        }
    }

    /**
     * 处理心跳的handler
     */
    private final class HeartbeatHandler extends Handler {
        public HeartbeatHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleHeartbeat(msg);
        }
    }

    /**
     * 心跳 handler
     *
     * @param msg
     */
    private void onHandleHeartbeat(Message msg) {
        if (heartbeatHandler == null) {
            return;
        }

        switch (msg.what) {
            case MSG_HEARTBEAT:
                //heartbeatHandler.removeMessages(MSG_HEARTBEAT_TIMEOUT);
                sendHeartbeat();
                break;
            case MSG_HEARTBEAT_TIMEOUT:
                heartbeatHandler.removeMessages(MSG_HEARTBEAT);
                heartbeatHandler.removeMessages(MSG_HEARTBEAT_TIMEOUT);
                manager.reconnect();
                break;
        }
    }

}
