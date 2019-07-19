package com.callme.platform.api.request;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：请求生命周期，若当前http请求由某个activity发出，可使用fragment来绑定到Activity上，
 * 从而监听activity的生命周期，当Activity的onDestroy被调用时，fragment的onDestroy同时被调用，此时可以取消首页非后台
 * 网络请求
 * 作者：huangyong
 * 创建时间：2018/8/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface RequestLifecycle {
    /**
     * Callback for when {@link android.app.Fragment#onStart()}}
     * or {@link android.app.Activity#onStart()} is called.
     */
    void onStart();

    /**
     * Callback for when {@link android.app.Fragment#onStop()}} o
     * r {@link android.app.Activity#onStop()}} is called.
     */
    void onStop();

    /**
     * Callback for when {@link android.app.Fragment#onDestroy()}}
     * or {@link android.app.Activity#onDestroy()} is
     * called.
     */
    void onDestroy();
}
