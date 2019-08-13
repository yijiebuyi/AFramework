package com.callme.platform.api.callback;

import android.text.TextUtils;

import com.callme.platform.BuildConfig;
import com.callme.platform.api.request.LifeCycle;
import com.callme.platform.api.request.Request;
import com.callme.platform.api.listenter.RequestListener;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：业务平台数据接口回调
 * @see ResultBean
 * 作者：huangyong
 * 创建时间：2018/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RequestCallback extends BaseCallback {
    private final static String DATA_EX_MSG = "获取数据失败，请重试！";

    public <T> RequestCallback(Request request, RequestListener<T> listener, LifeCycle lifeCycle) {
        super(request, listener, lifeCycle);
    }

    public <T> RequestCallback(RequestListener<T> listener) {
        super(listener);
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.code() == 200) {
            try {
                ResultBean result = (ResultBean) response.body();
                int errorCode = ParseUtil.getErrorCode(response);
                String errorMsg = ParseUtil.getErrorMsg(response);
                if (mRequestListener != null) {
                    switch (errorCode) {
                        case ErrorCode.SUCCESS:
                            mRequestListener.onSuccess(result);
                            break;
                        case ErrorCode.FAILURE:
                        case ErrorCode.APP_UPGRADE:
                        case ErrorCode.HTTP_UNKNOWN:
                        case ErrorCode.HTTP_UNSPECIFIC:
                        default:
                            if (TextUtils.isEmpty(errorMsg)) {
                                errorMsg = mDefaultMsg;
                            } else if (errorMsg.contains("exception") || errorMsg.contains("Exception")) {
                                if (!BuildConfig.DEBUG) {
                                    errorMsg = mDefaultMsg;
                                }
                            }
                            onFailureCallback(errorCode, errorMsg, false);
                            CallRequestLogHelper.onFailure(call, response);
                            break;
                    }
                }

                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onSuccess(errorCode);
                }
            } catch (ClassCastException e) {
                String msg = mDefaultMsg;
                onFailureCallback(ErrorCode.CAST_EX, msg, false);
                CallRequestLogHelper.onFailure(call, ErrorCode.CAST_EX, e);
            } catch (Exception e) {
                String msg = DATA_EX_MSG;
                onFailureCallback(ErrorCode.HTTP_UNSPECIFIC, msg, false);
                CallRequestLogHelper.onFailure(call, ErrorCode.HTTP_UNSPECIFIC, e);
            }
        } else {
            boolean httpError = response == null;
            int code = httpError ? ErrorCode.HTTP_EX : response.code() ;
            String msg = mDefaultMsg;
            onFailureCallback(code, msg, httpError);
            CallRequestLogHelper.onFailure(call, response);
        }

        onLoadComplete();
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (call.isCanceled()) {
            onCancelCallback();
        } else {
            String msg = mDefaultMsg;
            onFailureCallback(ErrorCode.HTTP_EX, msg, true);
        }
        CallRequestLogHelper.onFailure(call, ErrorCode.HTTP_EX, t);

        onLoadComplete();
    }

}
