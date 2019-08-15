package com.callme.platform.api.request;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：兼容form表单提交数据为null时，retrofit，okhttp报错
 * 作者：huangyong
 * 创建时间：2019/8/15
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RequestParamMap extends HashMap<String, String> {

    public static RequestParamMap newInstance() {
        return new RequestParamMap();
    }

    @Override
    public String put(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Field map contained null key.");
        }
        if (value == null) {
            value = "";
        }

        return super.put(key, value);
    }
}
