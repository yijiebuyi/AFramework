package com.callme.platform.api;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.callme.platform.R;
import com.callme.platform.api.callback.ErrorCode;
import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.base.BaseActivity;
import com.callme.platform.base.BaseFragment;
import com.callme.platform.common.HttpGlobalListener;
import com.callme.platform.common.HttpResponseUi;

import retrofit2.Call;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/8/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RetrofitHttpResponseUi implements HttpResponseUi {
    /**
     * 当前请求自什么页面，是谁调用
     * 如果mComeFrom是Activity，或者Fragment，这是将会有对话框显示，失败占位view有关
     */
    private Object mComeFrom;
    /**
     * 是否已经显示加载进度框
     */
    private boolean mBeShownProgress = false;

    /**
     *
     */
    private int mResponseUiFlag = FLAG_NONE;

    private Call mCall;
    private RequestListener mListener;


    /**
     * @param flag
     */
    public void setHttpResponseUi(int flag) {
        mResponseUiFlag = flag;
    }

    public RetrofitHttpResponseUi(Object comeFrom, Call call, RequestListener listener) {
        mComeFrom = comeFrom;
        mCall = call;
        mListener = listener;
    }

    @Override
    public void onPreStart() {
        //显示加载进度条
        boolean showProgressDialog = (mResponseUiFlag & FLAG_SHOW_PROGRESS_DIALOG) != 0;
        boolean showProgressView = (mResponseUiFlag & FLAG_SHOW_PROGRESS_VIEW) != 0;
        boolean cancelable = (mResponseUiFlag & FLAG_PROGRESS_DIALOG_NONCANCELABLE) == 0;
        mBeShownProgress = showProgressDialog || showProgressView;

        if (mComeFrom != null && mBeShownProgress) {
            if (mComeFrom instanceof BaseActivity) {
                ((BaseActivity) mComeFrom).showProgressDialog(cancelable);
            } else if (mComeFrom instanceof BaseFragment) {
                ((BaseFragment) mComeFrom).showProgressDialog(cancelable);
            }
        }
    }

    @Override
    public void onStart() {
        //do nothing
    }

    @Override
    public void onSuccess(int code) {
        if ((mResponseUiFlag & FLAG_PROGRESS_MANUAL_CLOSE) == 0) {
            //关闭所有的对话框
            closeLoadingProgress();
        } else {
            //加载进度框关闭改为手动
        }

        switch (code) {
            case ErrorCode.TOKEN_INVALID:
                String msg = "登录失效,请重新登录!";
                if (mComeFrom instanceof Context) {
                    Context appCxt = ((Context) mComeFrom).getApplicationContext();
                    msg = appCxt.getString(R.string.login_auth_failure);
                }
                loginAuthFailure(code, msg);
                break;
            case ErrorCode.APP_UPGRADE:
                break;
        }
        if (code != 0) {
            sendErrorLog(code, "");
        }
    }

    private void onHttpError(int errorCode, String msg) {
        boolean canShowFailureView = (mResponseUiFlag & FLAG_SHOW_LOAD_FAIL_VIEW) != 0;
        boolean showToast = true;
        if (mComeFrom != null && canShowFailureView) {
            //显示错误页面
            if (mComeFrom instanceof BaseActivity) {
                ((BaseActivity) mComeFrom).showFailedView(mFailedViewClickLister);
                showToast = false;
            } else if (mComeFrom instanceof BaseFragment) {
                ((BaseFragment) mComeFrom).showFailedView(mFailedViewClickLister);
                showToast = false;
            }
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

    @Override
    public void onFailure(int errorCode, String msg, boolean httpError) {
        closeLoadingProgress();
        if (httpError) {
            onHttpError(errorCode, msg);
        }
        sendErrorLog(errorCode, msg);
    }

    @Override
    public void onCancelled() {
        closeLoadingProgress();
    }

    private View.OnClickListener mFailedViewClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mComeFrom instanceof Context) {
                ApiRequestManager.getInstance().enqueueRequest((Context) mComeFrom, mCall.clone(),
                        mListener);
            }
        }
    };

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

    /**
     * 关闭所有的加载进度条
     */
    private void closeLoadingProgress() {
        if (mComeFrom instanceof BaseActivity) {
            ((BaseActivity) mComeFrom).closeProgressDialog();
        } else if (mComeFrom instanceof BaseFragment) {
            ((BaseFragment) mComeFrom).closeProgressDialog();
        }
    }

    private void showToast(int resId) {
        try {
            if ((mComeFrom instanceof Activity) && (((Activity) mComeFrom).isFinishing()
                    || ((Activity) mComeFrom).isDestroyed())) {
                return;
            }
            if (mComeFrom instanceof Context) {
                Context appCxt = ((Context) mComeFrom).getApplicationContext();
                Toast.makeText(appCxt, resId, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private void showToast(String msg) {
        try {
            if ((mComeFrom instanceof Activity) && (((Activity) mComeFrom).isFinishing()
                    || ((Activity) mComeFrom).isDestroyed())) {
                return;
            }
            if (mComeFrom instanceof Context) {
                Context appCxt = ((Context) mComeFrom).getApplicationContext();
                Toast.makeText(appCxt, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 上传日志
     *
     * @param errorCode
     * @param msg
     */
    private void sendErrorLog(int errorCode, String msg) {
        HttpGlobalListener listener = HttpConfigure.getListener();
        if (listener != null) {
            try {
                if (mComeFrom instanceof Context) {
                    listener.requestFail((Context) mComeFrom, mCall.request().url().toString(),
                            errorCode, msg, null);
                } else if (mComeFrom instanceof BaseFragment) {
                    listener.requestFail(((BaseFragment) mComeFrom).getContext(), mCall.request()
                            .url().toString(), errorCode, msg, null);
                }
            } catch (Exception e) {
            }
        }
    }
}
