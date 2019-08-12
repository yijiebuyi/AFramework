package com.callme.platform.util.excutor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
class DefaultTaskExecutor extends AbsTaskExecutor {
    private final Object mLock = new Object();
    private ExecutorService mDiskIO;

    @Nullable
    private volatile Handler mMainHandler;

    @Override
    public void executeOnDiskIO(Runnable runnable) {
        if (mDiskIO == null) {
            mDiskIO = Executors.newFixedThreadPool(2);
        }
        mDiskIO.execute(runnable);
    }

    @Override
    public void postToMainThread(@NonNull Runnable runnable, long delay) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }

        if (delay > 0) {
            mMainHandler.postDelayed(runnable, delay);
        } else {
            mMainHandler.post(runnable);
        }
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public void cancelAll() {
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }
    }
}
