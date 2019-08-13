package com.callme.platform.common;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http回调响应相关联的默认ui
 * 作者：huangyong
 * 创建时间：2018/5/17
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface HttpResponseUi {
    /**
     * http响应，ui对应的flag
     */
    public final static String RESPONSE_UI_FLAG = "response_ui_flag";


    /**
     * none
     */
    public final static int FLAG_NONE = 0;

    /**
     * 显示http加载进度条(dialog形式)
     */
    public final static int FLAG_SHOW_PROGRESS_DIALOG = 1 << 1;

    /**
     * 显示http加载进度条(view形式)
     * @deprecated
     */
    public final static int FLAG_SHOW_PROGRESS_VIEW = 1 << 2;

    /**
     * 加载进度条不可取消
     */
    public final static int FLAG_PROGRESS_DIALOG_NONCANCELABLE = 1 << 3;

    /**
     * 是否默认显示http加载(http请示失败，非业务逻辑)失败的占位view
     */
    public final static int FLAG_SHOW_LOAD_FAIL_VIEW = 1 << 4;

    /**
     * http响应成功后，默认不关闭加载对话，该为手动控制
     */
    public final static int FLAG_PROGRESS_MANUAL_CLOSE = 1 << 5;

    /**
     * http加载失败后能后重试
     */
    //public final static int FLAG_LOAD_FAIL_RETRYABLE = 1 << 6;

    /**
     * @param flag
     * @see #FLAG_NONE
     * @see #FLAG_PROGRESS_DIALOG_NONCANCELABLE
     * @see #FLAG_PROGRESS_MANUAL_CLOSE
     * @see #FLAG_SHOW_LOAD_FAIL_VIEW
     * @see #FLAG_SHOW_PROGRESS_DIALOG
     * @see #FLAG_SHOW_PROGRESS_VIEW
     */
    public void setHttpResponseUi(int flag);

    /**
     * http请求开始前
     */
    public void onPreStart();

    /**
     * http请求开始
     */
    public void onStart();

    /**
     * http成功响应
     *
     * @param code 业务逻辑响应码
     */
    public void onSuccess(int code);

    /**
     * 处理错误
     *
     * @param errorCode
     * @param msg
     * @param httpError 如：401(访问拒绝) 500(服务器内部错误)
     */
    public void onFailure(int errorCode, String msg, boolean httpError);

    /**
     * 接口被取消
     */
    public void onCancelled();
}
