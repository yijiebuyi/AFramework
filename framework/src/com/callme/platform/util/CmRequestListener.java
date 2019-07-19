package com.callme.platform.util;

import org.json.JSONObject;

/**
 * Cm 监听回调
 *
 * @param <T>
 */
public abstract class CmRequestListener<T> {

    /**
     * http请求前
     *
     * @param handlerId
     */
    public void onPreStart(String handlerId) {
    }

    /**
     * http请求开始回调
     *
     * @param handlerId
     */
    public void onStart(String handlerId) {
    }

    /**
     * http请求取消回调
     */
    public void onCancelled() {
    }

    /**
     * http请求超时回调
     */
    public void onLoginTimeout() {
    }

    /**
     * http重新发送请求回调
     */
    public void onReSendReq() {
    }

    /**
     * http 加载中回调
     *
     * @param total
     * @param current
     * @param isUploading
     */
    public void onLoading(long total, long current, boolean isUploading) {
    }

    /**
     * http成功回调,逻辑正常
     *
     * @param response
     */
    public abstract void onResponse(JSONObject response);

    /**
     * http成功回调,逻辑错误
     *
     * @param exceptionCode
     * @param response
     */
    public abstract void onFailure(int exceptionCode, String response);

    /**
     * 数据异常,处理目前后台数据返回的异常数据
     * @param exceptionCode
     * @param srcResponse
     */
    public void onDataException(int exceptionCode, String srcResponse) {

    }

    /**
     * http回调失败
     *
     * @param errorCode
     * @param response
     * @return 默认返回true，是否需要弹出默认的toast提示
     * @see com.callme.platform.common.SimpleHttpResponseUi#onError(int, String)
     */
    public boolean onError(int errorCode, String response) {
        return true;
    }

    /**
     * http加载完成回调，不管失败与否
     */
    public void onLoadComplete() {

    }


}
