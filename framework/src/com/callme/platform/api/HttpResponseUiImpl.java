package com.callme.platform.api;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.callme.platform.R;
import com.callme.platform.api.callback.ErrorCode;
import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.api.listenter.UiHandler;
import com.callme.platform.common.HttpGlobalListener;
import com.callme.platform.common.HttpResponseUi;
import com.callme.platform.util.ResourcesUtil;

import retrofit2.Call;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http回调响应相关联的默认ui实现(mvvm)
 * 作者：huangyong
 * 创建时间：2019/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HttpResponseUiImpl implements HttpResponseUi {

    private UiHandler mUiHandler;
    private RequestListener mListener;
    private Call mCall;

    public HttpResponseUiImpl() {
    }

    public HttpResponseUiImpl(UiHandler uiHandler, RequestListener listener) {
        mUiHandler = uiHandler;
        mListener = listener;
    }

    public HttpResponseUiImpl(Call call, RequestListener listener) {
        mListener = listener;
        mCall = call;
    }

    public HttpResponseUiImpl(UiHandler uiHandler, Call call, RequestListener listener) {
        mUiHandler = uiHandler;
        mListener = listener;
        mCall = call;
    }

    public void setCall(Call call) {
        mCall = call;
    }


    @Override
    @Deprecated
    public void setHttpResponseUi(int flag) {
        //do nothing
    }

    @Override
    public void onPreStart() {
        //do nothing
    }

    @Override
    public void onStart() {
        //do nothing
    }

    @Override
    public void onSuccess(int code) {
        closeProgressDialog();

        switch (code) {
            case ErrorCode.TOKEN_INVALID:
                String msg = ResourcesUtil.getString(R.string.login_auth_failure);
                loginAuthFailure(code, msg);
                break;
            case ErrorCode.APP_UPGRADE:

                break;
        }
    }

    @Override
    public void onFailure(int errorCode, String msg, boolean httpError) {
        closeProgressDialog();
        if (httpError) {
            onHttpError(errorCode, msg);
        }
    }

    @Override
    public void onCancelled() {
        closeProgressDialog();
    }

    private void closeProgressDialog() {
        if (mUiHandler != null && !cancelAutoCloseProgressDialog()) {
            mUiHandler.dismissProgressDialog();
        }
    }

    /**
     * 是否取消自动关闭对话框
     */
    private boolean cancelAutoCloseProgressDialog() {
        if (mListener != null) {
            return mListener.cancelAutoCloseProgressDialog();
        }

        return false;
    }

    private void onHttpError(int errorCode, String msg) {
        boolean showToast = true;
        if (mUiHandler != null) {
            //显示错误页面
            mUiHandler.showFailedView(mFailedViewClickLister);
        } else if (mListener != null) {
            showToast = mListener.onError(errorCode, msg);
        }

        if (ErrorCode.TOKEN_INVALID == errorCode) {
            //未授权，需要重新登录
            loginAuthFailure(errorCode, msg);
        } else {
            //弹出toast做默认处理
            if (showToast) {
                showToast(R.string.toast_no_net);
            }
        }
    }

    /**
     * 登录未授权
     */
    protected void loginAuthFailure(int errorCode, String msg) {
        showToast(R.string.login_auth_failure);
        HttpGlobalListener listener = HttpConfigure.getListener();
        if (listener != null) {
            listener.onAccountAuthFail(errorCode, msg);
        }
    }

    private void showToast(int resId) {
        try {
            Context appCxt = mUiHandler.getApplicationContext();
            Toast.makeText(appCxt, resId, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    private View.OnClickListener mFailedViewClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApiRequestManager.getInstance().enqueueRequest(mUiHandler, mCall.clone(),
                    mListener);
        }
    };

}
