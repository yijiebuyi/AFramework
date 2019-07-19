package com.callme.platform.socket.engine;


/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：数据包
 * 作者：
 * 创建时间：
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class Packet<T> extends Transport{
    public int id;
    public T data;

    public Packet(T data) {
        this.data = data;
    }

    public Packet(int id, T data) {
        this.id = id;
        this.data = data;
    }
}
