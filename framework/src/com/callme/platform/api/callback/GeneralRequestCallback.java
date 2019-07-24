package com.callme.platform.api.callback;

import com.callme.platform.api.request.LifeCycle;
import com.callme.platform.api.request.Request;
import com.callme.platform.util.CmRequestImpListener;

import retrofit2.Call;
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
public class GeneralRequestCallback extends BaseCallback {
    public <T> GeneralRequestCallback(Request request, CmRequestImpListener<T> listener, LifeCycle lifeCycle) {
        super(request, listener, lifeCycle);
    }

    public <T> GeneralRequestCallback(CmRequestImpListener<T> listener) {
        super(listener);
    }

    public <T> GeneralRequestCallback() {
        super();
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.code() == 200) {
            try {
                Object result = ParseUtil.parseData(response.body(), mRequestListener);
                if (mRequestListener != null) {
                    mRequestListener.onSuccess(result);
                }
            } catch (ClassCastException e) {
                String msg = mDefaultMsg;
                onFailureCallback(ErrorCode.CAST_EX, msg, false);
                CallRequestHelper.onFailure(call, ErrorCode.CAST_EX, e);
            } catch (Exception e) {
                String msg = mDefaultMsg;
                onFailureCallback(ErrorCode.HTTP_UNSPECIFIC, msg, false);
                CallRequestHelper.onFailure(call, ErrorCode.HTTP_UNSPECIFIC, e);
            } finally {
                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onSuccess(ErrorCode.SUCCESS);
                }
            }
        } else {
            boolean httpError = response == null;
            int code = httpError ? ErrorCode.HTTP_EX : response.code();
            String msg = mDefaultMsg;
            onFailureCallback(code, msg, httpError);
            CallRequestHelper.onFailure(call, response);
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
        CallRequestHelper.onFailure(call, ErrorCode.HTTP_EX, t);

        onLoadComplete();
    }

}
