package com.callme.platform.common;

import java.io.Serializable;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/2/20 18:13
 * Description: event bus传参基类
 * History:
 */
public class EventBusBean implements Serializable {

    public String msg;
    public int orderId;

    public EventBusBean(int orderId, String msg) {
        this.orderId = orderId;
        this.msg = msg;
    }
}
