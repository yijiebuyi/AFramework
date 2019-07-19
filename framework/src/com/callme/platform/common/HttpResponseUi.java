package com.callme.platform.common;

import com.callme.platform.util.CmRequestListener;

import java.util.Map;

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

    public final static int FLAG_NONE = 0;

    /**
     * 显示http加载进度条(dialog形式)
     */
    public final static int FLAG_SHOW_PROGRESS_DIALOG = 1 << 1;

    /**
     * 显示http加载进度条(view形式)
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
     * http请求开始前
     *
     * @param handler http请求前会生成的唯一字符串，标识http的唯一
     */
    public void onPreStart(String handler);

    /**
     * http请求开始
     *
     * @param handler http请求运行时会生成的唯一字符串，标识http的唯一,（运行在线程池中的线程）
     */
    public void onStart(String handler);

    /**
     * http成功响应
     *
     * @param code 业务逻辑响应码
     */
    public void onSuccess(int code);

    /**
     * http错误响应
     *
     * @param errorCode http错误码 如：401 500
     * @param msg       如：401访问拒绝  500服务器内部错误
     */
    public void onError(int errorCode, String msg);

    /**
     * 设置http请求参数，只针对于加载失败后，点击页面重新加载请求
     *
     * @param url      请求url
     * @param method   请求方法
     * @param listener http请求回调
     */
    public void setRequestParams(Object comeFrom, String url, int method,
                                 CmRequestListener listener);

    /**
     * 请求头参数
     *
     * @param headers
     */
    public void setRequestHeaders(Map<String, String> headers);
}
