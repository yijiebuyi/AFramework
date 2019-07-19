package com.callme.platform.util;


import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class CmRequestImpListener<T> extends CmRequestListener<T> {
    private Type[] mTypes;

    public CmRequestImpListener() {
        mTypes = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    public void onResponse(JSONObject response) {
        if (response != null) {
            String str = response.toString();
            T t = JsonUtils.parseObject(str, mTypes[0]);
            if (t != null) {
                onSuccess(t);
            }
        } else {
            onFailure(-1, "数据加载失败!");
        }
    }

    public abstract void onSuccess(T response);

}
