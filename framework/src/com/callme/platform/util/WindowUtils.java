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

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.util.permission.PermissionCallback;
import com.callme.platform.util.permission.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

/**
 * 弹窗辅助类
 *
 * @ClassName WindowUtils
 */
public class WindowUtils {
    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;
    private List<GmsLogBean> mMsgList = new ArrayList<GmsLogBean>();
    private ListView mContentLv;
    private TextView mContent;
    private GmsLogAdapter mGmsLogAdapter;
    private static WindowUtils mInstance;

    private android.os.Handler mHandler;
    private ExecutorService mExecutorService;

    private WindowUtils() {
        mExecutorService = Executors.newSingleThreadExecutor();
        mHandler = new android.os.Handler(Looper.getMainLooper());
    }

    public synchronized static WindowUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WindowUtils();
        }

        return mInstance;
    }

    /**
     * 显示弹出框
     *
     * @param context
     */
    private void showPopupWindow(final Context context) {
        if (isShown) {
            LogUtil.i(LOG_TAG, "return cause already shown");
            return;
        }

        if (context == null) {
            return;
        }

        PermissionsHelper.getInstance().requestPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW,
                new PermissionCallback() {
                    @Override
                    public void onGranted(List<String> data) {
                        isShown = true;
                        LogUtil.i(LOG_TAG, "showPopupWindow");
                        mContext = context.getApplicationContext();
                        mWindowManager = (WindowManager) mContext
                                .getSystemService(Context.WINDOW_SERVICE);
                        mView = setUpView(context);
                        final LayoutParams params = new LayoutParams();
                        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
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
                        params.width = LayoutParams.MATCH_PARENT;
                        params.height = LayoutParams.WRAP_CONTENT;
                        params.gravity = Gravity.TOP | Gravity.LEFT;
                        mWindowManager.addView(mView, params);
                        LogUtil.i(LOG_TAG, "add view");
                    }

                    @Override
                    public boolean onDenied(List<String> data) {
                        return false;
                    }
                });
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
        //@since 3.1 生产环境也打印日志到本地，进行分析
        /*if (release) {
            return;
        }*/

        final GmsLogBean msgBean = new GmsLogBean();
        msgBean.ex = false;
        if (TextUtils.isEmpty(label)) {
            msgBean.msg = msg;
        } else {
            msgBean.msg = label + ":" + msg;
        }

        //updateMsgToWindow(context, msgBean);
        saveMsgToLocalCache(false, msgBean);
    }

    /**
     * 更新日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param ex
     */
    public synchronized void updateMsg(Context context, boolean release, Exception ex) {
        updateMsg(context, release, null, ex);
    }

    /**
     * 更新日志, 并保持到本地
     *
     * @param context
     * @param release 生产环境
     * @param ex
     */
    public synchronized void updateMsg(Context context, boolean release, String label, Exception ex) {
        if (release) {
            return;
        }

        final GmsLogBean msgBean = new GmsLogBean();
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

    private void saveMsgToLocalCache(boolean ex, GmsLogBean msgBean) {
        //Logger.d("GMS", ex ? "==error==" + msgBean.msg : msgBean.msg);
        FileLogHelper.getInstance().write(ex ? "==error==" + msgBean.msg : msgBean.msg);
    }

    private void updateMsgToWindow(final Context context, final GmsLogBean msgBean) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshMsgToWindow(context, msgBean);
            }
        });
    }

    private void refreshMsgToWindow(Context context, GmsLogBean msg) {
        showPopupWindow(context);

        if (mMsgList == null) {
            mMsgList = new ArrayList<GmsLogBean>();
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

    /**
     * 隐藏弹出框
     */
    public void hidePopupWindow() {
        LogUtil.i(LOG_TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            LogUtil.i(LOG_TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;

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

    private View setUpView(final Context context) {
        LogUtil.i(LOG_TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.layout_log_window,
                null);
        mContentLv = view.findViewById(R.id.content_list);
        view.setOnKeyListener(new OnKeyListener() {
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

    public static class GmsLogBean {
        public boolean ex;
        public String msg;
    }

    private class GmsLogAdapter extends BaseAdapter {
        List<GmsLogBean> mData;

        public void setData(List<GmsLogBean> data) {
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
                convertView = new TextView(WindowUtils.mContext);
                holder = new Holder();
                holder.tv = (TextView) convertView;
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            GmsLogBean log = mData.get(position);
            holder.tv.setText(log.msg);
            holder.tv.setTextColor(log.ex ? Color.RED : Color.BLACK);
            return convertView;
        }
    }

    private class Holder {
        TextView tv;
    }
}
