package com.callme.platform.util.excutor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;

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
public class TaskExecutor extends AbsTaskExecutor {
    private static volatile TaskExecutor sInstance;

    @NonNull
    private AbsTaskExecutor mDelegate;

    @NonNull
    private AbsTaskExecutor mDefaultTaskExecutor;

    @NonNull
    private static final Executor sMainThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            getInstance().postToMainThread(command);
        }
    };

    @NonNull
    private static final Executor sIOThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            getInstance().executeOnDiskIO(command);
        }
    };

    private TaskExecutor() {
        mDefaultTaskExecutor = new DefaultTaskExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    /**
     * 返回task executor的单例.
     *
     * @return The singleton
     */
    @NonNull
    public static TaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (TaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new TaskExecutor();
            }
        }
        return sInstance;
    }

    /**
     * 设置代理Exccutor
     *
     * @param taskExecutor
     */
    public void setDelegate(@Nullable AbsTaskExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    public void executeOnDiskIO(Runnable runnable) {
        mDelegate.executeOnDiskIO(runnable);
    }

    @Override
    public void postToMainThread(@NonNull Runnable runnable, long delay) {
        mDelegate.postToMainThread(runnable, delay);
    }

    @NonNull
    public static Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    @NonNull
    public static Executor getIOThreadExecutor() {
        return sIOThreadExecutor;
    }

    @Override
    public boolean isMainThread() {
        return mDelegate.isMainThread();
    }

    @Override
    public void cancelAll() {
        mDelegate.cancelAll();
    }
}
