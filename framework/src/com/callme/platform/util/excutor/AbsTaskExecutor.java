package com.callme.platform.util.excutor;

import android.support.annotation.NonNull;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2019/8/12
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class AbsTaskExecutor {

    /**
     * IO 线程执行任务
     *
     * @param runnable
     */
    public abstract void executeOnDiskIO(@NonNull Runnable runnable);

    /**
     * post task到主线程执行
     *
     * @param runnable runnable
     */
    public void postToMainThread(@NonNull Runnable runnable) {
        postToMainThread(runnable, 0);
    }

    /**
     * post task到主线程执行
     *
     * @param runnable runnable
     * @param delay    延时执行时间,单位ms
     */
    public abstract void postToMainThread(@NonNull Runnable runnable, long delay);

    /**
     * 主线程执行task
     * <p>
     * 如果当前线程是主线程，立刻执行
     *
     * @param runnable runnable
     */
    public void executeOnMainThread(@NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMainThread(runnable);
        }
    }

    /**
     * 当前线程是否是主线程
     */
    public abstract boolean isMainThread();

    public abstract void cancelAll();
}
