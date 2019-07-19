package com.callme.platform.socket.engine;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：socket watcher
 * 作者：huangyong
 * 创建时间：2019/1/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class SocketWatcher {
    protected Manager io;

    public SocketWatcher(Manager io) {
        this.io = io;
    }

    /**
     * 连接异常
     * @param e
     */
    public abstract void onConnectEx(Exception e);

    /**
     * 从流读数据异常
     * @param e
     */
    public abstract void onReadEx(Exception e);

    /**
     * 向流写数据异常
     * @param e
     */
    public abstract void onWriteEx(Exception e);

    /**
     * 重置计数
     */
    public void resetCurrCount() {

    }
}
