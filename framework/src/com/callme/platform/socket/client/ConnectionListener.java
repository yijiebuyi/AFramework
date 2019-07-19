package com.callme.platform.socket.client;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：连接监听器
 * 作者：huangyong
 * 创建时间：2018/12/11
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface ConnectionListener {
    /**
     * 连接上
     */
    public void onConnected();

    /**
     * 断开连接
     */
    public void onDisconnect();

    /**
     * 断开连接
     */
    public void disconnected();

    /**
     * 连接错误
     */
    public void onConnectError(Exception e);

    /**
     * 连接超时
     */
    public void onConnectTimeOut(Exception e);
}
