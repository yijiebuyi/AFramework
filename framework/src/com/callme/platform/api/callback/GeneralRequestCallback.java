package com.callme.platform.api.callback;

import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.callme.platform.api.request.LifeCycle;
import com.callme.platform.api.request.Request;
import com.callme.platform.common.HttpResponseUi;
import com.callme.platform.util.CmRequestImpListener;
import com.callme.platform.util.JsonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：非业务平台数据接口回调，返回的数据类型不是业务类型的的数据结构
 * 作者：huangyong
 * 创建时间：2019/5/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GeneralRequestCallback<T> implements Callback {
    private CmRequestImpListener mRequestImpListener;
    private HttpResponseUi mHttpResponseUi;
    private Request<T> mRequest;
    private LifeCycle mLifeCycle;
    private String mDefaultMsg = "获取数据失败，请检查您的网络连接！";

    public GeneralRequestCallback(Request request, CmRequestImpListener<T> listener) {
        mRequest = request;
        mRequestImpListener = listener;
    }

    public GeneralRequestCallback() {
        super();
    }

    @CallSuper
    public void setRequest(Request request) {
        mRequest = request;
    }

    @CallSuper
    public void setLifeCycle(LifeCycle lifeCycle) {
        mLifeCycle = lifeCycle;
    }

    @CallSuper
    public void setRequestListener(CmRequestImpListener listener) {
        mRequestImpListener = listener;
    }

    public void setHttpResponseUi(HttpResponseUi responseUi) {
        mHttpResponseUi = responseUi;
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.code() == 200) {
            try {
                Object result = parseData(response.body());
                if (mRequestImpListener != null) {
                    mRequestImpListener.onSuccess(result);
                }
            } catch (ClassCastException e) {
                String msg = mDefaultMsg;
                onFailureCallback(RequestCallback.CODE_CAST_EX, msg);
            } catch (Exception e) {
                String msg = mDefaultMsg;
                onFailureCallback(RequestCallback.CODE_UNSPECIFIED, msg);
            } finally {
                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onSuccess(RequestCallback.CODE_SUCCESS);
                }
            }
        } else {
            boolean httpError = response == null;
            int code = httpError ? RequestCallback.CODE_HTTP_EX : response.code();
            String msg = mDefaultMsg;
            onFailureCallback(code, msg);
        }

        onLoadComplete();
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (call.isCanceled()) {
            onCancelCallback();
        } else {
            String msg = mDefaultMsg;
            onFailureCallback(RequestCallback.CODE_HTTP_EX, msg);
        }

        onLoadComplete();
    }

    /**
     * 解析data数据
     *
     * @param data
     * @return
     */
    protected Object parseData(Object data) {
        if (data == null || TextUtils.isEmpty(data.toString())) {
            return null;
        }

        Type type = null;
        try {
            Type[] dataTypes = mRequestImpListener != null ? ((ParameterizedType) mRequestImpListener.getClass()
                    .getGenericSuperclass()).getActualTypeArguments() : null;
            type = dataTypes != null && dataTypes.length > 0 ? dataTypes[0] : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JsonUtils.isCanParsableJsonObject(type, data)) {
            data = JsonUtils.parseObject(data.toString(), type);
        }

        return data;
    }

    /**
     * 取消处理
     */
    protected void onCancelCallback() {
        if (mRequestImpListener != null) {
            mRequestImpListener.onCancelled();
        }
    }

    /**
     * 失败处理
     *
     * @param code
     * @param msg
     */
    protected void onFailureCallback(int code, String msg) {
        if (mRequestImpListener != null) {
            mRequestImpListener.onFailure(code, msg);
        }

        if (mHttpResponseUi != null) {
            mRequestImpListener.onFailure(code, msg);
        }
    }


    /**
     * 加载完成
     */
    protected void onLoadComplete() {
        if (mRequestImpListener != null) {
            mRequestImpListener.onLoadComplete();
        }
        if (mLifeCycle != null && mRequest != null) {
            mLifeCycle.removeRequestLifecycle(mRequest);
        }
    }
}
