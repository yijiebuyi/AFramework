package com.callme.platform.util;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/10/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import android.content.Context;
import android.text.TextUtils;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：日志缓存处理工具类
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LogCacheUtil {
    private static LogCacheUtil mInstance;

    private LogCacheUtil() {

    }

    public static LogCacheUtil getInstance() {
        if (mInstance == null) {
            synchronized (LogCacheUtil.class) {
                if (mInstance == null) {
                    mInstance = new LogCacheUtil();
                }
            }
        }

        return mInstance;
    }

    /**
     * 更新日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param msg
     */
    public synchronized void updateMsg(Context context, boolean release, String msg) {
        updateMsg(context, release, null, msg);
    }

    /**
     * 更新日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param msg
     */
    public synchronized void updateMsg(Context context, boolean release, String label, String msg) {
        updateMsg(context, release, false, label, msg);
    }

    /**
     * 更新日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param msg
     */
    public synchronized void updateMsg(Context context, boolean release, boolean ex, String label, String msg) {
        //@since 3.1 生产环境也打印日志到本地，进行分析
        /*if (release) {
            return;
        }*/

        final LogBean msgBean = new LogBean();
        msgBean.ex = ex;
        if (TextUtils.isEmpty(label)) {
            msgBean.msg = msg;
        } else {
            msgBean.msg = label + ":" + msg;
        }

        //updateMsgToWindow(context, msgBean);
        saveMsgToLocalCache(false, msgBean);
    }

    /**
     * 更新异常日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param ex
     */
    public synchronized void updateMsg(Context context, boolean release, Exception ex) {
        updateMsg(context, release, null, ex);
    }

    /**
     * 更新异常日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param ex
     */
    public synchronized void updateMsg(Context context, boolean release, String label, Exception ex) {
        if (release) {
            return;
        }

        final LogBean msgBean = new LogBean();
        msgBean.ex = true;
        if (TextUtils.isEmpty(label)) {
            msgBean.msg = getMsg(ex);
        } else {
            msgBean.msg = label + ":" + getMsg(ex);
        }

        //updateMsgToWindow(context, msgBean);
        saveMsgToLocalCache(true, msgBean);
    }

    public static String getMsg(Exception e) {
        return e != null ? e.getLocalizedMessage() : "";
    }

    private void saveMsgToLocalCache(boolean ex, LogBean msgBean) {
        //Logger.d("GMS", ex ? "==error==" + msgBean.msg : msgBean.msg);
        FileLogHelper.getInstance().write(ex ? "==error==" + msgBean.msg : msgBean.msg);
    }

    public static class LogBean {
        public boolean ex;
        public String msg;
    }
}
