package com.callme.platform.common;

import java.io.Serializable;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * modify: hy
 * Author: 10496
 * Date: 2020/2/20 18:13
 * Description: event bus传参基类
 * History:
 */
public class EventBusBean implements Serializable {

    public int type;
    public Object data;

    public EventBusBean(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
