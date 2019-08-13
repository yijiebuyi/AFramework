package com.callme.platform.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.callme.platform.api.callback.FileRequestCallback;
import com.callme.platform.api.callback.GeneralRequestCallback;
import com.callme.platform.api.callback.RequestCallback;
import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.api.request.Request;
import com.callme.platform.common.HttpResponseUi;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http请求管理
 * 作者：huangyong
 * 创建时间：2018/8/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApiRequestManager {
    /**
     * The singleton instance of RequestManagerRetriever.
     */
    private static final ApiRequestManager INSTANCE = new ApiRequestManager();
    /**
     * Main thread handler to handle cleaning up pending fragment maps.
     */
    private final Handler mHandler;

    public static ApiRequestManager getInstance() {
        return INSTANCE;
    }

    private ApiRequestManager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 异步请求
     *
     * @param context
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequest(Context context, final Call call, RequestListener<T> listener) {
        enqueueRequest(context, HttpResponseUi.FLAG_NONE, call, listener);
    }

    /**
     * 异步请求
     *
     * @param context
     * @param responseFlag {@link HttpResponseUi#FLAG_NONE}{@link HttpResponseUi#FLAG_SHOW_PROGRESS_DIALOG}
     *                     {@link HttpResponseUi#FLAG_SHOW_PROGRESS_VIEW}{@link HttpResponseUi#FLAG_PROGRESS_DIALOG_NONCANCELABLE}
     *                     {@link HttpResponseUi#FLAG_SHOW_LOAD_FAIL_VIEW}{@link HttpResponseUi#FLAG_PROGRESS_MANUAL_CLOSE}
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequest(Context context, int responseFlag, final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }
        RetrofitHttpResponseUi ui = new RetrofitHttpResponseUi(context, call, listener);
        ui.setHttpResponseUi(responseFlag);
        ui.onPreStart();


        Request request = new Request(context, mHandler, call, listener);
        request.enqueue(ui);
    }

    /**
     * 异步请求(后台运行)
     *
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequestInBackground(final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        RequestCallback callback = new RequestCallback(listener);
        call.enqueue(callback);
    }

    /**
     * 同步请求
     *
     * @param call
     * @param listener
     * @param <T>
     */
    public static <T> void executeRequest(final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        RequestCallback callback = new RequestCallback(listener);
        try {
            Response response = call.execute();
            callback.onResponse(null, response);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    /**
     * 下载文件
     *
     * @param call
     * @param filePath 保存的文件路径
     * @param listener
     */
    public void download(final Call call, final String filePath, final RequestListener<String> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        FileRequestCallback callback = new FileRequestCallback(mHandler, filePath, listener);
        call.enqueue(callback);
    }

    /**
     * 异步请求
     * 非业务的接口请求
     *
     * @param context
     * @param responseFlag {@link HttpResponseUi#FLAG_NONE}{@link HttpResponseUi#FLAG_SHOW_PROGRESS_DIALOG}
     *                     {@link HttpResponseUi#FLAG_SHOW_PROGRESS_VIEW}{@link HttpResponseUi#FLAG_PROGRESS_DIALOG_NONCANCELABLE}
     *                     {@link HttpResponseUi#FLAG_SHOW_LOAD_FAIL_VIEW}{@link HttpResponseUi#FLAG_PROGRESS_MANUAL_CLOSE}
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueGeneralRequest(Context context, int responseFlag, final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        RetrofitHttpResponseUi ui = new RetrofitHttpResponseUi(context, call, listener);
        ui.setHttpResponseUi(responseFlag);
        ui.onPreStart();

        Request request = new Request(context, mHandler, call);
        GeneralRequestCallback callback = new GeneralRequestCallback();
        request.enqueue(ui, listener, callback);
    }
}
