package com.callme.platform.api.listenter;


import org.json.JSONObject;

public abstract class RequestListener<T> {
    /**
     * http请求前
     */
    public void onPreStart() {
    }

    /**
     * 是否取消自动关闭对话框
     *
     * @return
     */
    public boolean cancelAutoCloseProgressDialog() {
        return false;
    }

    /**
     * http请求取消回调
     */
    public void onCancelled() {

    }

    /**
     * http成功回调,逻辑正常
     *
     * @param response
     */
    public void onResponse(JSONObject response) {

    }

    /**
     * http成功回调,逻辑正常
     *
     * @param response
     */
    public abstract void onSuccess(T response);

    /**
     * http成功回调,逻辑错误
     *
     * @param exceptionCode
     * @param response
     */
    public abstract void onFailure(int exceptionCode, String response);

    /**
     * http回调失败
     *
     * @param errorCode
     * @param response
     * @return 默认返回true，是否需要弹出默认的toast提示
     * @see com.callme.platform.api.RetrofitHttpResponseUi#onFailure(int, String, boolean)
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
