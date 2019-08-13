package com.callme.platform.api.callback;

import android.support.annotation.CallSuper;

import com.callme.platform.api.request.LifeCycle;
import com.callme.platform.api.request.Request;
import com.callme.platform.common.HttpResponseUi;
import com.callme.platform.api.listenter.RequestListener;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2019/5/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class BaseCallback implements retrofit2.Callback {
    protected RequestListener mRequestListener;
    protected HttpResponseUi mHttpResponseUi;

    protected Request mRequest;
    protected LifeCycle mLifeCycle;
    protected String mDefaultMsg = "获取数据失败，请检查您的网络连接！";

    public <T> BaseCallback(Request request, RequestListener<T> listener, LifeCycle lifeCycle) {
        mRequest = request;
        mRequestListener = listener;
        mLifeCycle = lifeCycle;
    }

    public <T> BaseCallback(RequestListener<T> listener) {
        mRequestListener = listener;
    }

    public <T> BaseCallback() {

    }

    @CallSuper
    public void setHttpResponseUi(HttpResponseUi responseUi) {
        mHttpResponseUi = responseUi;
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
    public void setRequestListener(RequestListener listener) {
        mRequestListener = listener;
    }

    @CallSuper
    public void set(Request request, LifeCycle lifeCycle, HttpResponseUi responseUi, RequestListener listener) {
        mRequest = request;
        mRequestListener = listener;
        mHttpResponseUi = responseUi;
        mLifeCycle = lifeCycle;
    }


    /**
     * 取消处理
     */
    protected void onCancelCallback() {
        if (mHttpResponseUi != null) {
            mHttpResponseUi.onCancelled();
        }
        if (mRequestListener != null) {
            mRequestListener.onCancelled();
        }
    }


    /**
     * 失败处理
     *
     * @param code
     * @param msg
     * @param httpError 是否是http请求错误
     */
    protected void onFailureCallback(int code, String msg, boolean httpError) {
        if (mRequestListener != null) {
            mRequestListener.onFailure(code, msg);
        }

        if (mHttpResponseUi != null) {
            mHttpResponseUi.onFailure(code, msg, httpError);
        }
    }

    /**
     * 加载完成
     */
    protected void onLoadComplete() {
        if (mRequestListener != null) {
            mRequestListener.onLoadComplete();
        }
        if (mLifeCycle != null && mRequest != null) {
            mLifeCycle.removeRequestLifecycle(mRequest);
        }
    }
}
