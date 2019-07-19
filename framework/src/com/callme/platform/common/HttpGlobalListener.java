package com.callme.platform.common;

import android.content.Context;

import java.util.Map;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：账户授权监听器，请求接口时如toke无效，token为空都会导致
 * 作者：huangyong
 * 创建时间：2018/5/18
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface HttpGlobalListener {

    /**
     * 账户授权监听器，请求接口时如toke无效，token为空都会导致
     *
     * @param code 授权失败码
     * @param msg  授权消息
     */
    public void onAccountAuthFail(int code, String msg);

    /**
     * 接口请求失败回调
     * @param context
     * @param url
     * @param erCode
     * @param exMsg
     * @param headers
     */
    public void requestFail(Context context, String url, int erCode, String exMsg, Map headers);
}
