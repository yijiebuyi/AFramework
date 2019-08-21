package com.callme.platform.util;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.util.permission.PermissionCallback;
import com.callme.platform.util.permission.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：通过window显示log， 仅用于测试
 * 作者：huangyong
 * 创建时间：2019/8/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WindowLog {
    public static Boolean mIsShown = false;
    private static Context mContext = null;
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private ListView mContentLv;
    private List<LogCacheUtil.LogBean> mMsgList = new ArrayList<LogCacheUtil.LogBean>();
    private GmsLogAdapter mGmsLogAdapter;
    private android.os.Handler mHandler;

    /**
     * 显示弹出框
     *
     * @param context
     */
    private void showPopupWindow(final Context context) {
        if (mIsShown) {
            return;
        }

        if (context == null) {
            return;
        }

        PermissionsHelper.getInstance().requestPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW,
                new PermissionCallback() {
                    @Override
                    public void onGranted(List<String> data) {
                        mIsShown = true;
                        mContext = context.getApplicationContext();
                        mWindowManager = (WindowManager) mContext
                                .getSystemService(Context.WINDOW_SERVICE);
                        mView = setUpView(context);
                        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                        // 设置flag
                        //int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
                        params.flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCHABLE;
                        //params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                        // 不设置这个弹出框的透明遮罩显示为黑色
                        params.format = PixelFormat.TRANSLUCENT;
                        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
                        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
                        // 不设置这个flag的话，home页的划屏会有问题
                        params.width = WindowManager.LayoutParams.MATCH_PARENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.gravity = Gravity.TOP | Gravity.LEFT;
                        mWindowManager.addView(mView, params);
                    }

                    @Override
                    public boolean onDenied(List<String> data) {
                        return false;
                    }
                });
    }

    private View setUpView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_log_window,
                null);
        mContentLv = view.findViewById(R.id.content_list);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        //hidePopupWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });
        return view;
    }

    /**
     * 隐藏弹出框
     */
    public void hidePopupWindow() {
        if (mIsShown && null != mView) {
            mWindowManager.removeView(mView);
            mIsShown = false;

            if (mMsgList != null) {
                mMsgList.clear();
                mMsgList = null;
            }

            if (mGmsLogAdapter != null) {
                mGmsLogAdapter.setData(null);
                mGmsLogAdapter = null;
            }
        }
    }

    public void updateMsgToWindow(final Context context, final LogCacheUtil.LogBean msgBean) {
        if (mHandler == null) {
            mHandler = new android.os.Handler(Looper.getMainLooper());
        }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    refreshMsgToWindow(context, msgBean);
                }
            });
        }

        private void refreshMsgToWindow (Context context, LogCacheUtil.LogBean msg){
            showPopupWindow(context);

            if (mMsgList == null) {
                mMsgList = new ArrayList<LogCacheUtil.LogBean>();
            }

            synchronized (mMsgList) {
                if (mMsgList.size() > 1000) {
                    mMsgList.remove(0);
                }
                mMsgList.add(msg);

                if (mGmsLogAdapter == null) {
                    mGmsLogAdapter = new GmsLogAdapter();
                }
                mGmsLogAdapter.setData(mMsgList);
                if (mContentLv != null) {
                    mContentLv.setAdapter(mGmsLogAdapter);
                    mContentLv.setSelection(mMsgList.size() - 1);
                }
            }
        }

        private class GmsLogAdapter extends BaseAdapter {
            List<LogCacheUtil.LogBean> mData;

            public void setData(List<LogCacheUtil.LogBean> data) {
                mData = data;
            }

            @Override
            public int getCount() {
                return mData != null ? mData.size() : 0;
            }

            @Override
            public Object getItem(int position) {
                return mData.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    convertView = new TextView(mContext);
                    holder = new Holder();
                    holder.tv = (TextView) convertView;
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                LogCacheUtil.LogBean log = mData.get(position);
                holder.tv.setText(log.msg);
                holder.tv.setTextColor(log.ex ? Color.RED : Color.BLACK);
                return convertView;
            }
        }

        private class Holder {
            TextView tv;
        }
    }
