package com.callme.platform.api.request;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.callme.platform.api.Util;
import com.callme.platform.api.callback.BaseCallback;
import com.callme.platform.api.callback.ErrorCode;
import com.callme.platform.api.callback.RequestCallback;
import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.common.HttpResponseUi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class Request implements RequestLifecycle, Handler.Callback {
    private static final String TAG = "Request";
    private static final String FRAGMENT_TAG = "com.hywalk.callme.manager";

    private static final int ID_REMOVE_FRAGMENT = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT = 2;

    private Handler mHandler;
    private LifeCycle mLifeCycle;
    private Context mContext;

    /**
     * retrofit call
     */
    private Call mCall;
    /**
     * http callback
     */
    private RequestListener mListener;

    /**
     * Pending adds for RequestManagerFragments.
     */
    final Map<FragmentManager, RequestFragment> mPendingRequestFragments =
            new HashMap<FragmentManager, RequestFragment>();

    /**
     * Pending adds for SupportRequestManagerFragments.
     */
    final Map<android.support.v4.app.FragmentManager, SupportRequestFragment> mPendingSupportRequestFragments =
            new HashMap<android.support.v4.app.FragmentManager, SupportRequestFragment>();

    /**
     * constructor
     *
     * @param context
     * @param handler
     * @param call
     * @param listener
     */
    public <T> Request(Context context, Handler handler, final Call call, RequestListener<T> listener) {
        mHandler = handler;

        mCall = call;
        mListener = listener;

        mContext = context;
        get(context);
    }

    /**
     * constructor
     *
     * @param context
     * @param handler
     * @param call
     */
    public <T> Request(Context context, Handler handler, final Call call) {
        mHandler = handler;
        mCall = call;
        get(context);
    }

    public void get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                get((Activity) context);
            } else if (context instanceof ContextWrapper) {
                get(((ContextWrapper) context).getBaseContext());
            }
        }
    }

    public void get(FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            get(activity.getApplicationContext());
        } else {
            //assertNotDestroyed(activity);
            android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
            LifeCycle rq = getSupportRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    public void get(Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
        }
        if (Util.isOnBackgroundThread()) {
            get(fragment.getActivity().getApplicationContext());
        } else {
            android.support.v4.app.FragmentManager fm = fragment.getChildFragmentManager();
            LifeCycle rq = getSupportRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void get(Activity activity) {
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            get(activity.getApplicationContext());
        } else {
            //assertNotDestroyed(activity);
            FragmentManager fm = activity.getFragmentManager();
            LifeCycle rq = getRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    /*@TargetApi(17)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }*/

    @TargetApi(17) //Build.VERSION_CODES.JELLY_BEAN_MR1
    public void get(android.app.Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
        }
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < 17) {
            get(fragment.getActivity().getApplicationContext());
        } else {
            FragmentManager fm = fragment.getChildFragmentManager();
            LifeCycle rq = getRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    @TargetApi(17) //Build.VERSION_CODES.JELLY_BEAN_MR1
    public LifeCycle getRequestManagerFragment(final FragmentManager fm) {
        RequestFragment current = (RequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = mPendingRequestFragments.get(fm);
            if (current == null) {
                current = new RequestFragment();
                mPendingRequestFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                mHandler.obtainMessage(ID_REMOVE_FRAGMENT, fm).sendToTarget();
            }
        }
        mLifeCycle = current;

        return current;
    }

    public LifeCycle getSupportRequestManagerFragment(final android.support.v4.app.FragmentManager fm) {
        SupportRequestFragment current = (SupportRequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = mPendingSupportRequestFragments.get(fm);
            if (current == null) {
                current = new SupportRequestFragment();
                mPendingSupportRequestFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                mHandler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT, fm).sendToTarget();
            }
        }

        mLifeCycle = current;
        return current;
    }

    @Override
    public boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case ID_REMOVE_FRAGMENT:
                FragmentManager fm = (FragmentManager) message.obj;
                key = fm;
                removed = mPendingRequestFragments.remove(fm);
                break;
            case ID_REMOVE_SUPPORT_FRAGMENT:
                android.support.v4.app.FragmentManager supportFm = (android.support.v4.app.FragmentManager) message.obj;
                key = supportFm;
                removed = mPendingSupportRequestFragments.remove(supportFm);
                break;
            default:
                handled = false;
        }
        if (handled && removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (mCall != null) {
            mCall.cancel();
            Log.i(TAG, "===============call cancel===========");
        }

        //Log.i(TAG, "call:" + mCall.isCanceled());
    }

    public void enqueue() {
        if (unavailable()) {
            return;
        }
        if (mCall != null) {
            mCall.enqueue(new RequestCallback(this, mListener, mLifeCycle));
        }
    }

    public void enqueue(HttpResponseUi responseUi) {
        if (unavailable()) {
            return;
        }
        if (mCall != null) {
            RequestCallback callback = new RequestCallback(this, mListener, mLifeCycle);
            callback.setHttpResponseUi(responseUi);
            mCall.enqueue(callback);
        }
    }

    /**
     * 自定义请求回调，三方的数据格式
     *
     * @param responseUi
     * @param listener
     * @param callback
     */
    public void enqueue(HttpResponseUi responseUi, RequestListener listener, BaseCallback callback) {
        if (unavailable()) {
            return;
        }
        if (mCall != null) {
            callback.setHttpResponseUi(responseUi);
            callback.setRequest(this);
            callback.setLifeCycle(mLifeCycle);
            callback.setRequestListener(listener);
            mCall.enqueue(callback);
        }
    }

    private boolean unavailable() {
        boolean unavailable = RequestOther.unavailable2();
        if (unavailable) {
            String s = RequestOther.sMsg2;
            if (TextUtils.isEmpty(s)) {
                s = "权限不够，无法访问!";
            }
            if (mListener != null) {
                mListener.onFailure(ErrorCode.HTTP_UNKNOWN, s);
            } else if (mContext != null) {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        }

        return unavailable;
    }

}
