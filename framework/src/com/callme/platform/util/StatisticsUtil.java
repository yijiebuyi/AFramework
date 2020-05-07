package com.callme.platform.util;

import android.app.Activity;
import android.app.Application;

import com.baidu.mobstat.StatService;
import com.callme.platform.BuildConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：统计工具类
 * 作者：huangyong
 * 创建时间：2018/6/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class StatisticsUtil {

    /**
     * 初始化统计
     *
     * @param context
     */
    public static void init(Application context, String channel) {
        // 请在初始化时调用，参数为Application或Activity或Service
        UMConfigure.init(context, null, channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.setEncryptEnabled(true);
        StatService.start(context);
        StatService.setDebugOn(BuildConfig.DEBUG);
        StatService.setAppChannel(channel);
    }


    /**
     * 开始统计页面
     *
     * @param activity
     */
    public static void onResume(Activity activity) {
        StatService.onResume(activity);
        MobclickAgent.onResume(activity);
    }

    /**
     * 结束统计页面
     *
     * @param activity
     */
    public static void onPause(Activity activity) {
        StatService.onPause(activity);
        MobclickAgent.onPause(activity);
    }

}
