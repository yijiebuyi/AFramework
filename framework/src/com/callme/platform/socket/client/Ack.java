package com.callme.platform.socket.client;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：发送消息确认
 * 作者：huangyong
 * 创建时间：2018/11/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface Ack<T> {
    void call(T var);
}
