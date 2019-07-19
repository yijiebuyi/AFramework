package com.callme.platform.socket.engine;

import com.callme.platform.socket.client.Options;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：socket watcher的简单实现
 * 作者：huangyong
 * 创建时间：2019/1/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SimpleSocketWatcher extends SocketWatcher {
    private final int DEFAULT_OP_RECOUNT_COUNT = 4;
    /**
     * 从流中读数据或向流中写数据，重试次数
     */
    private int OP_RECOUNT_COUNT = DEFAULT_OP_RECOUNT_COUNT;
    /**
     * 重连次数
     */
    private int RECONNECT_COUNT = 0;

    /**
     * 当前的操作异常连接计数
     */
    private int currOpExCount = 0;
    /**
     * 当前的连接异常计数
     */
    private int currConnectExCount = 0;

    public SimpleSocketWatcher(Manager io) {
        super(io);

        Options options = null;
        if (io != null && (options = io.getOptions()) != null) {
            OP_RECOUNT_COUNT = options.opExceptionReconnectCount > 0 ?
                    options.opExceptionReconnectCount : DEFAULT_OP_RECOUNT_COUNT;

            RECONNECT_COUNT = options.reconnectionAttempts > 0 ? options.reconnectionAttempts : 0;
        }
    }

    /**
     * 小于重试连接次数都重新连接
     *
     * @param e
     */
    @Override
    public void onConnectEx(Exception e) {
        if (io == null || io.isShutdown()) {
            return;
        }

        if (currConnectExCount++ < RECONNECT_COUNT) {
            io.reconnect();
        }
    }

    /**
     * 大于设定的操作流重试次数，就开始重连
     *
     * @param e
     */
    @Override
    public void onReadEx(Exception e) {
        if (io == null || io.isShutdown() || !io.isReconnect()) {
            return;
        }

        if (++currOpExCount > OP_RECOUNT_COUNT) {
            currOpExCount = 0;
            io.reconnect();
        }
    }

    /**
     * 大于设定的操作流重试次数，就开始重连
     *
     * @param e
     */
    @Override
    public void onWriteEx(Exception e) {
        if (io == null || io.isShutdown() || !io.isReconnect()) {
            return;
        }

        if (++currOpExCount > OP_RECOUNT_COUNT) {
            currOpExCount = 0;
            io.reconnect();
        }
    }

    /**
     * 当socket连接的时候，重置相应的重连计数
     */
    @Override
    public void resetCurrCount() {
        currConnectExCount = 0;
        currOpExCount = 0;
    }
}
