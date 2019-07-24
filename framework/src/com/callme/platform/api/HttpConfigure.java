package com.callme.platform.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.callme.platform.common.HttpGlobalListener;
import com.callme.platform.util.PkgUtil;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：Http配置初始化
 * 作者：huangyong
 * 创建时间：2019/4/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HttpConfigure {
    private static Context mAppContext;
    private static HttpGlobalListener mListener;

    /**
     * 初始化Http Header常量
     *
     * @param context
     * @param apiVersion    接口版本号
     * @param channelId     渠道号
     * @param clientSubType 客户端子类型 {@link com.callme.platform.api.HttpHeader#CLIENT_SUB_TYPE}
     */
    public static void init(@NonNull Context context, String apiVersion, String channelId, String clientSubType) {
        mAppContext = context.getApplicationContext();
        //初始化 Http header
        HttpHeader.init(context, apiVersion, channelId, clientSubType);
    }

    /**
     * 设置全局监听器
     *
     * @param listener
     */
    public static void setHttpGlobalListener(HttpGlobalListener listener) {
        mListener = listener;
    }

    public static HttpGlobalListener getListener() {
        return mListener;
    }
    public static Context getAppContext() {
        return mAppContext;
    }

}
