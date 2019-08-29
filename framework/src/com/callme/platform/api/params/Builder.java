package com.callme.platform.api.params;

import okhttp3.RequestBody;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface Builder {
    public Builder add(String name, String value);

    public Builder add(String name, int value);

    public Builder add(String name, double value);

    public Builder add(String name, float value);

    public Builder add(String name, byte value);

    public Builder add(String name, boolean value);

    public Builder add(String name, short value);

    public Builder add(String name, long value);

    public Builder add(String name, char value);

    public RequestBody build();
}
