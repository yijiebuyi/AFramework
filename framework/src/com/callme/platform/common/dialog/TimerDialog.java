package com.callme.platform.common.dialog;

import android.app.Activity;
import android.content.Context;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2018/12/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TimerDialog {
    private Context mContext;
    private long mLastTime;
    private int mInterval;
    private OnTimerListener mListener;
    private String mTitle;
    private String mMsg;
    private int mCancelTip;
    private int mConfirmTip;
    private static final int DEFAULT_INTERVAL = 5000;

    public TimerDialog(Context context) {
        mContext = context;
        mInterval = DEFAULT_INTERVAL;
        mLastTime = System.currentTimeMillis();
    }

    public void setInterval(int interval) {
        mInterval = interval;
    }

    public void start() {
        mLastTime = System.currentTimeMillis();
    }

    public void refreshTime() {
        long time = System.currentTimeMillis();
        if (time - mLastTime < mInterval) {
            showConfirmDialog(mListener);
        } else {
            if (mListener != null) {
                mListener.onDo();
            }
        }
        mLastTime = time;
    }

    public void refreshTime(OnTimerListener listener) {
        long time = System.currentTimeMillis();
        if (time - mLastTime < mInterval) {
            showConfirmDialog(listener);
        } else {
            if (listener != null) {
                listener.onDo();
            }
        }
    }

    private void showConfirmDialog(final OnTimerListener listener) {
        if (!(mContext instanceof Activity)) {
            return;
        }
        Activity activity = ((Activity) mContext);
        if (activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        
        final ThemeDDialog dialog = new ThemeDDialog(mContext, mMsg, mTitle);
        dialog.setNegativeButton(mCancelTip, new ThemeDDialog.DialogOnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton(mConfirmTip, new ThemeDDialog.DialogOnClickListener() {
            @Override
            public void onClick() {
                if (listener != null) {
                    listener.onDo();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public TimerDialog setMsg(int msg) {
        return setMsg(mContext.getString(msg));
    }

    public TimerDialog setTitle(int title) {
        return setTitle(mContext.getString(title));
    }

    public TimerDialog setMsg(String msg) {
        mMsg = msg;
        return this;
    }

    public TimerDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public TimerDialog setCancelTip(int resId) {
        mCancelTip = resId;
        return this;
    }

    public TimerDialog setConfirmTip(int resId) {
        mConfirmTip = resId;
        return this;
    }

    public void setOnTimerListener(OnTimerListener listener) {
        mListener = listener;
    }

    public interface OnTimerListener {
        void onDo();
    }
}
