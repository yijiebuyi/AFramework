package com.callme.platform.api.callback;

import android.text.TextUtils;
import android.util.Log;

import com.callme.platform.api.request.LifeCycle;
import com.callme.platform.api.request.Request;
import com.callme.platform.common.HttpResponseUi;
import com.callme.platform.util.CmRequestImpListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RequestCallback<T> implements Callback {
    public static final int CODE_INVALID = 501;
    /**
     * CODE未指定
     */
    public static final int CODE_UNSPECIFIED = -1000000;
    /**
     * CODE未知
     */
    public static final int CODE_UNKNOWN = -1000001;

    public static final int CODE_HTTP_EX = -1;
    public static final int CODE_CAST_EX = -2;

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAILED = 1;

    public static final int CODE_UPDATE = -10;

    private CmRequestImpListener mRequestImpListener;
    private HttpResponseUi mHttpResponseUi;

    private Request<T> mRequest;
    private LifeCycle mLifeCycle;
    private String mDefaultMsg = "数据请求失败！";

    public RequestCallback(Request request, CmRequestImpListener<T> listener, LifeCycle lifeCycle) {
        mRequest = request;
        mRequestImpListener = listener;
        mLifeCycle = lifeCycle;
    }

    public RequestCallback(CmRequestImpListener<T> listener) {
        mRequestImpListener = listener;
    }

    public void setHttpResponseUi(HttpResponseUi responseUi) {
        mHttpResponseUi = responseUi;
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.isSuccessful()) {
            try {
                ResultBean<T> result = (ResultBean<T>) response.body();
                int errorCode = getErrorCode(response);
                String errorMsg = getErrorMsg(response);
                if (mRequestImpListener != null) {
                    switch (errorCode) {
                        case CODE_SUCCESS:
                            mRequestImpListener.onSuccess(result);
                            break;
                        case CODE_FAILED:
                            mRequestImpListener.onFailure(errorCode, errorMsg);
                            break;
                        case CODE_UPDATE:
                            mRequestImpListener.onFailure(errorCode, errorMsg);
                            break;
                        case CODE_UNKNOWN:
                            mRequestImpListener.onFailure(errorCode, errorMsg);
                            break;
                        default:
                            mRequestImpListener.onFailure(errorCode, errorMsg);
                            break;
                    }
                }

                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onSuccess(errorCode);
                }
            } catch (ClassCastException e) {
                String msg = mDefaultMsg;
                if (mRequestImpListener != null) {
                    mRequestImpListener.onFailure(CODE_CAST_EX, msg);
                }
                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onError(CODE_CAST_EX, msg);
                }
            }
        } else {
            String msg = mDefaultMsg;
            int code = response.code();
            switch (code) {
                case CODE_INVALID:
                    //token 失效
                    break;
            }

            if (mRequestImpListener != null) {
                mRequestImpListener.onError(response.code(), msg);
            }
            if (mHttpResponseUi != null) {
                mHttpResponseUi.onError(response.code(), msg);
            }
        }

        if (mRequestImpListener != null) {
            mRequestImpListener.onLoadComplete();
        }

        if (mLifeCycle != null && mRequest != null) {
            mLifeCycle.removeRequestLifecycle(mRequest);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        String msg = mDefaultMsg;
        if (mRequestImpListener != null) {
            mRequestImpListener.onError(CODE_HTTP_EX, msg);
            //mRequestImpListener.onError(-1, "数据请求失败！");

            mRequestImpListener.onLoadComplete();
        }

        if (mLifeCycle != null) {
            mLifeCycle.removeRequestLifecycle(mRequest);
        }

        if (mHttpResponseUi != null) {
            mHttpResponseUi.onError(CODE_HTTP_EX, msg);
        }

        msg = t != null ? t.getLocalizedMessage() : "msg is null";
        if (TextUtils.isEmpty(msg)) {
            msg = mDefaultMsg;
        }
        Log.i("RequestCallback", msg);
    }

    /**
     * 获取错误码(版本间的兼容)
     * 3.0之前的接口，使用error字段
     * 3.0只有的接口，使用code字段
     *
     * @param response
     * @return
     */
    private int getErrorCode(Response response) {
        Object body = null;
        if (response == null || (body = response.body()) == null) {
            return CODE_UNKNOWN;
        }

        if (body instanceof ResultBean) {
            int error = ((ResultBean) body).error;
            if (error != CODE_UNSPECIFIED) {
                return error;
            } else if (TextUtils.equals("0", ((ResultBean) body).result)) {
                return 0;
            }

            return ((ResultBean) body).code;
        }

        return CODE_UNKNOWN;
    }

    /**
     * 获取错误码(版本间的兼容)
     * 3.0以前认证接口，使用result字段
     * 其他使用message
     *
     * @param response
     * @return
     */
    private String getErrorMsg(Response response) {
        Object body = null;
        if (response == null || (body = response.body()) == null) {
            return "";
        }

        if (body instanceof ResultBean) {
            String result = ((ResultBean) body).result;
            if (!TextUtils.isEmpty(result)) {
                return result;
            } else {
                return ((ResultBean) body).message;
            }
        }

        return "";
    }
}
