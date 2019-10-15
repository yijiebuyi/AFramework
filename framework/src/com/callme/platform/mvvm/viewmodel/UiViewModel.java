package com.callme.platform.mvvm.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.callme.platform.api.listenter.UiHandler;
import com.callme.platform.mvvm.envet.SingleLiveEvent;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：封装实现HttpResponseUi，处理loading,failedview的显示逻辑
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UiViewModel extends AndroidViewModel implements UiHandler {
    public UiViewModel(@NonNull Application application) {
        super(application);
    }

    private SingleLiveEvent<Boolean> mShowDialogEvent;
    private SingleLiveEvent<Void> mDismissDialogEvent;
    private SingleLiveEvent<View.OnClickListener> mFailedEvent;

    public SingleLiveEvent<Boolean> getShowDialogEvent() {
        if (mShowDialogEvent == null) {
            mShowDialogEvent = new SingleLiveEvent<>();
        }

        return mShowDialogEvent;
    }

    public SingleLiveEvent<Void> getDismissDialogEvent() {
        if (mDismissDialogEvent == null) {
            mDismissDialogEvent = new SingleLiveEvent<>();
        }

        return mDismissDialogEvent;
    }

    public SingleLiveEvent<View.OnClickListener> getFailedEvent() {
        if (mFailedEvent == null) {
            mFailedEvent = new SingleLiveEvent<>();
        }

        return mFailedEvent;
    }

    @Override
    public void showProgressDialog(boolean cancelable) {
        getShowDialogEvent().postValue(cancelable);
    }

    @Override
    public void dismissProgressDialog() {
        getDismissDialogEvent().postValue(null);
    }

    @Override
    public void showFailedView(View.OnClickListener listener) {
        getFailedEvent().postValue(listener);
    }

    @Override
    public Context getApplicationContext() {
        return getApplication();
    }
}
