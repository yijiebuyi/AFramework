package com.callme.platform.api.params;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.ByteString;

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
public abstract class ParamsBody<T> {
    public static final String MEDIA_TYPE_JSON = "application/json";

    protected RequestBody create() {
        T bodyData = body();
        String mediaType = mediaType();
        RequestBody body = null;
        if (bodyData instanceof String) {
            body = RequestBody.create(MediaType.parse(mediaType), (String) bodyData);
        } else if (bodyData instanceof File) {
            body = RequestBody.create(MediaType.parse(mediaType), (File) bodyData);
        } else if (bodyData instanceof ByteString) {
            body = RequestBody.create(MediaType.parse(mediaType), (ByteString) bodyData);
        } else if (bodyData instanceof byte[]) {
            body = RequestBody.create(MediaType.parse(mediaType), (byte[]) bodyData);
        }

        return body;
    }

    protected abstract String mediaType();

    protected abstract T body();

    public static Builder jsonBuilder() {
        return new JsonBuilder();
    }

    public static Builder jsonArrayBuilder() {
        return new JsonArrayBuilder();
    }
}
