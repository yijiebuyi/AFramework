package com.callme.platform.api.params;

import org.json.JSONArray;
import org.json.JSONException;

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
public class JsonArrayBuilder extends ParamsBody<String> implements Builder {

    JSONArray mArray;

    public JsonArrayBuilder() {
        mArray = new JSONArray();
    }

    @Override
    public Builder add(String name, String value) {
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, int value) {
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, double value) {
        try {
            mArray.put(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Builder add(String name, float value) {
        try {
            mArray.put(value);
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
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, short value) {
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, long value) {
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, char value) {
        mArray.put(value);
        return this;
    }

    @Override
    public Builder add(String name, ParamsBody param) {
        mArray.put(param);
        return this;
    }

    @Override
    public RequestBody build() {
        return create();
    }

    @Override
    protected String mediaType() {
        return MEDIA_TYPE_JSON;
    }

    @Override
    protected String body() {
        return mArray.toString();
    }

    @Override
    public String toString() {
        if (mArray != null) {
            return mArray.toString();
        }

        return super.toString();
    }
}
