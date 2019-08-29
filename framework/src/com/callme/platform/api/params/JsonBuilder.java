package com.callme.platform.api.params;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
public class JsonBuilder extends ParamsBody<String> implements Builder {

    private JSONObject params;

    public JsonBuilder() {
        params = new JSONObject();
    }

    @Override
    public Builder add(String name, String value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        if (value == null) {
            value = "";
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, int value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, double value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, float value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, byte value) {
        return null;
    }

    @Override
    public Builder add(String name, boolean value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, short value) {
        return null;
    }

    @Override
    public Builder add(String name, long value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, char value) {
        if (TextUtils.isEmpty(name)) {
            return this;
        }
        try {
            params.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public RequestBody build() {
        return create();
    }

    @Override
    protected String mediaType() {
        return "application/json";
    }

    @Override
    protected String body() {
        return params.toString();
    }
}
