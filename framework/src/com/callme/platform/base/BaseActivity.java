package com.callme.platform.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callme.platform.R;
import com.callme.platform.common.activity.NoNetworkGuideActivity;
import com.callme.platform.common.dialog.LoadingProgressDialog;
import com.callme.platform.common.dialog.ThemeDDialog;
import com.callme.platform.common.dialog.ThemeDDialog.DialogOnClickListener;
import com.callme.platform.util.AppCompatUtil;
import com.callme.platform.util.CmActivityManager;
import com.callme.platform.util.LogUtil;
import com.callme.platform.util.StatisticsUtil;
import com.callme.platform.util.ToastUtil;
import com.gyf.barlibrary.ImmersionBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：所有activity基类
 * 作者：mikeyou
 * 创建时间：2017-10-6
 *
 * 修改人：黄勇
 * 修改描述：沉浸式状态栏，布局文件，butterknife注解
 *         注：位于库(framework)中BaseActivity的子类不能使用@Bindview注解(对应R2的资源)，butterknife的bug
 * 修改日期
 */

public abstract class BaseActivity extends FragmentActivity {
    //root layout
    protected LinearLayout mBaseLayout;
    protected FrameLayout mParentContent;
    protected FrameLayout mBaseContent;
    private LinearLayout mProgressBar;
    protected LinearLayout mFailedView;
    private TextView mRefreshTv;

    /**
     * network status notify view
     */
    private View mNetNotifyView;

    /**
     * titleBar layout
     */
    protected View mHeadLayout;
    protected FrameLayout mRightView;
    protected FrameLayout mLeftView;
    protected ImageView mLeftIv;
    protected TextView mLeftTv;
    protected ImageView mRightIv;
    protected TextView mRightTv;
    protected TextView mMsgTv;
    protected ImageView mMiddleIv;
    protected TextView mTvTitle;
    protected View mLeftDot;
    protected View mRightDot;
    protected View mHeader;

    private LoadingProgressDialog mLoadingProgressDialog;

    protected boolean mIsCancelable = false;
    private boolean mShowNetDefault = true;
    private boolean mNetNotConnect = false;
    /**
     * 沉浸式状态栏
     */
    protected ImmersionBar mImmersionBar;
    /**
     * 注解
     */
    protected Unbinder mUnbinder;
    protected Context mContext;
    protected Context mAppContext;

    protected Bundle mSavedInstanceState;
    private boolean mNetChangeReceiverFlag = false;
    /**
     * 是否已销毁
     */
    private boolean mIsDestroyed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatUtil.isTranslucentOrFloating(this)) {
            AppCompatUtil.fixOrientation(this);
        }
        super.onCreate(savedInstanceState);
        mIsDestroyed = false;
        CmActivityManager.getInstance().addActivity(this);
        mContext = this;
        mSavedInstanceState = savedInstanceState;
        mAppContext = this.getApplicationContext();

        boolean supportFullScr = supportFullScreen();
        if (supportFullScr) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.base_activity);

        //1.初始化view
        initView();
        //2.设置状态栏样式
        if (!supportFullScr) {
            setStatusBarStyle();
        }
        //3.设置是否屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //4.添加view到content容器中，子类实现
        addIntoContent(getContentView());
        //5.初始化view，设置onclick监听器
        //解决继承自BaseActivity且属于当前库(framework)的子类butterknife不能使用Bindview的注解，onclick的注解
        initSubView();
        //6.register eventbus
        if (needRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        //7.view已添加到container
        onContentAdded();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        BugtagsUtil.onPause(this, BuildConfig.type);
        StatisticsUtil.onPause(this);
        MobclickAgent.onPause(this);
        unregisterNetReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        BugtagsUtil.onResume(this, BuildConfig.type);
        StatisticsUtil.onResume(this);
        MobclickAgent.onResume(this);
        registerNetReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StatisticsUtil.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;

        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        if (needRegisterEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        closeProgressDialog();
        CmActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mBaseLayout = (LinearLayout) findViewById(R.id.base_layout);
        mParentContent = (FrameLayout) findViewById(R.id.parent_content);
        mBaseContent = (FrameLayout) findViewById(R.id.base_content);

        mHeadLayout = findViewById(R.id.head);
        mLeftView = (FrameLayout) findViewById(R.id.left_view);
        mRightView = (FrameLayout) findViewById(R.id.right_view);
        //left
        mLeftIv = (ImageView) findViewById(R.id.left_image);
        mLeftTv = (TextView) findViewById(R.id.left_text);
        //middle
        mTvTitle = (TextView) findViewById(R.id.middle_title);
        mMiddleIv = (ImageView) findViewById(R.id.middle_image);
        //right
        mRightIv = (ImageView) findViewById(R.id.right_image);
        mRightTv = (TextView) findViewById(R.id.right_text);
        mMsgTv = (TextView) findViewById(R.id.select_top);

        mHeader = findViewById(R.id.header);

        mLeftDot = findViewById(R.id.circle_left);
        mRightDot = findViewById(R.id.circle_right);

        mRefreshTv = (TextView) findViewById(R.id.failed_reflesh);
        mProgressBar = (LinearLayout) findViewById(R.id.base_progress);
        mProgressBar.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mFailedView = (LinearLayout) findViewById(R.id.base_failed);
        mFailedView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //默认处理title左上view的点击事件（back）
        mLeftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置内容背景颜色
        if (needSetBackground()) {
            mParentContent.setBackgroundResource(R.color.common_grey_bg_color);
        }
    }


    /**
     * 添加view到容器中
     *
     * @param view
     */
    private void addIntoContent(View view) {
        if (view != null) {
            if (!attachMergeLayout()) {
                mBaseContent.removeAllViews();
                mBaseContent.addView(view);
            }
            if (!delayBind()) {
                mUnbinder = ButterKnife.bind(this);
            }

        } else {
            try {
                throw new Exception("content view can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean delayBind() {
        return false;
    }

    /**
     * @return view
     */
    protected abstract View getContentView();

    /**
     * 加载布局
     *
     * @param resource
     * @return
     */
    protected View inflate(@LayoutRes int resource) {
        return LayoutInflater.from(this).inflate(resource, null);
    }

    /**
     * 加载布局
     *
     * @param resource
     * @return
     */
    protected View inflate(@LayoutRes int resource, @Nullable ViewGroup root) {
        return LayoutInflater.from(this).inflate(resource, root);
    }

    /**
     * 初始化 subView，一般只用于在framework中BaseActivity子类，为了解决
     * 继承自BaseActivity且属于当前库(framework)的子类butterknife不能使用Bindview的注解，onclick的注解
     */
    protected void initSubView() {

    }

    /**
     * @return return true 添加的layout以merge标签作为根布局, false layout不以merge标签作为根布局
     */
    protected boolean attachMergeLayout() {
        return false;
    }

    /**
     * 添加view完成回调，用于初始化数据
     */
    protected abstract void onContentAdded();

    /**
     * 设置添加view的容器背景色
     *
     * @param color
     */
    protected void setContentBackground(int color) {
        if (color > 0) {
            mBaseContent.setBackgroundColor(color);
        }
    }

    /**
     * 是否需要显示顶部栏
     *
     * @param isNeed
     */
    protected final void needHeader(boolean isNeed) {
        if (isNeed) {
            mHeadLayout.setVisibility(View.VISIBLE);
        } else {
            mHeadLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示对话框进度
     *
     * @param cancelable
     * @deprecated
     */
    public final void showProgress(boolean cancelable) {
        mIsCancelable = cancelable;
        mFailedView.setVisibility(View.GONE);
        mProgressBar.getBackground().setAlpha(100);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 显示对话框进度
     *
     * @param handlerId
     * @param cancelable
     * @deprecated
     */
    public final void showProgress(String handlerId, boolean cancelable) {
        mIsCancelable = cancelable;
        mFailedView.setVisibility(View.GONE);
        mProgressBar.getBackground().setAlpha(100);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭对话框进度
     */
    public final void closeProgress() {
        mIsCancelable = false;
        mProgressBar.setVisibility(View.GONE);
        mFailedView.setVisibility(View.GONE);
    }

    /**
     * 显示加载失败view
     *
     * @param listener
     */
    public final void showFailedView(OnClickListener listener) {
        mIsCancelable = false;
        mProgressBar.setVisibility(View.GONE);
        mFailedView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mRefreshTv.setOnClickListener(listener);
        }
    }

    /**
     * 设置页面title
     *
     * @param res
     */
    @Override
    public void setTitle(int res) {
        if (res > 0) {
            setTitle(getResources().getText(res));
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
        }

        setActTitle(res);
    }

    /**
     * 设置页面title
     *
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
            mTvTitle.setVisibility(View.VISIBLE);
            mMiddleIv.setVisibility(View.INVISIBLE);
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
        }

        setActTitle(title);
    }

    /**
     * 设置Activity的页面title
     *
     * @param res
     */
    protected final void setActTitle(int res) {
        super.setTitle(res);
    }

    /**
     * 设置Activity的页面title
     *
     * @param title
     */
    protected void setActTitle(CharSequence title) {
        super.setTitle(title);
    }

    /**
     * 设置页面title的图标
     *
     * @param drawable
     */
    public final void setImageTitle(int drawable) {
        if (drawable > 0 && getResources().getDrawable(drawable) != null) {
            mMiddleIv.setImageResource(drawable);
            mMiddleIv.setVisibility(View.VISIBLE);
            mTvTitle.setVisibility(View.INVISIBLE);
        } else {
            mMiddleIv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置title 左边view 是否可以显示
     *
     * @param visibility
     */
    public final void setLeftVisibility(int visibility) {
        mLeftView.setVisibility(visibility);
    }

    /**
     * 设置title 右边view 是否可以显示
     *
     * @param visibility
     */
    public final void setRightVisibility(int visibility) {
        mRightView.setVisibility(visibility);
    }

    /**
     * 设置title栏右边的文字
     *
     * @param res
     */
    public final void setRightTxt(int res) {
        if (res > 0) {
            setRightTxt(getResources().getString(res));
        } else {
            mRightTv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置title栏右边的文字
     *
     * @param txt
     */
    public final void setRightTxt(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            mRightTv.setText(txt);
            mRightTv.setVisibility(View.VISIBLE);
        } else {
            mRightTv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置title布局右边文本是否可以点击
     *
     * @param enable
     */
    public final void setRightTxtEnable(boolean enable) {
        if (mRightTv != null) {
            mRightTv.setEnabled(enable);
        }
    }

    /**
     * 设置title布局左边对应的图标
     *
     * @param resId
     */
    public final void setLeftDrawable(int resId) {
        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setImageResource(resId);
        mLeftIv.setVisibility(View.VISIBLE);
    }

    /**
     * 设置title栏左边的文字
     *
     * @param resId
     */
    public final void setLeftText(int resId) {
        if (resId != 0) {
            setLeftText(getResources().getString(resId));
        }
    }

    /**
     * 设置title栏左边的文字
     *
     * @param txt
     */
    public final void setLeftText(String txt) {
        if (TextUtils.isEmpty(txt)) {
            mLeftIv.setVisibility(View.GONE);
            mLeftTv.setText(txt);
            mLeftTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置title布局右边对应的图标
     *
     * @param resId
     */
    public final void setRightDrawable(int resId) {
        mRightIv.setImageResource(resId);
        mRightIv.setVisibility(View.VISIBLE);
        mRightTv.setVisibility(View.GONE);
    }

    public final void setMsgText(int count) {
        if (count > 0 && count < 100) {
            mMsgTv.setText(count + "");
            mMsgTv.setVisibility(View.VISIBLE);
        } else if (count > 99) {
            mMsgTv.setText("···");
            mMsgTv.setVisibility(View.VISIBLE);
        } else {
            mMsgTv.setVisibility(View.GONE);
        }
    }

    /**
     * 显示右边红点
     *
     * @param show
     */
    protected final void showRightDot(boolean show) {
        mRightDot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示左边红点
     *
     * @param show
     */
    protected final void showLeftDot(boolean show) {
        mLeftDot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示对话框形式的加载提示
     *
     * @param cancelable 是否可以取消请求
     */
    public final void showProgressDialog(boolean cancelable) {
        mIsCancelable = cancelable;
        if (mLoadingProgressDialog == null) {
            mLoadingProgressDialog = new LoadingProgressDialog(this);
        }
        if (mLoadingProgressDialog.isShowing()) {
            return;
        }
        mLoadingProgressDialog.setCancelable(mIsCancelable);
        mLoadingProgressDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                mIsCancelable = false;
            }
        });
        mLoadingProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mIsCancelable = false;
            }
        });
        if (!isDestroyed() && !isFinishing() && !mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.show();
        }
    }

    /**
     * 显示对话框形式的加载提示
     *
     * @param handlerId  数据访问的handler
     * @param cancelable 是否可以取消请求
     * @deprecated
     */
    public final void showProgressDialog(final String handlerId, boolean cancelable) {
        mIsCancelable = cancelable;
        if (mLoadingProgressDialog == null) {
            mLoadingProgressDialog = new LoadingProgressDialog(this);
        }
        if (mLoadingProgressDialog.isShowing()) {
            return;
        }
        mLoadingProgressDialog.setCancelable(mIsCancelable);
        mLoadingProgressDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                //cancelSingleRequest(handlerId);
                mIsCancelable = false;
            }
        });
        mLoadingProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                //cancelSingleRequest(handlerId);
                mIsCancelable = false;

            }
        });
        if (!isDestroyed() && !isFinishing() && !mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.show();
        }
    }

    /**
     * 取消加载对话框
     */
    public final void closeProgressDialog() {
        mIsCancelable = false;
        if (!isDestroyed() && !isFinishing() && mLoadingProgressDialog != null
                && mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.dismiss();
        }
    }

    /**
     * 显示通用dialog消息
     *
     * @param msg
     * @param titleRes
     */
    public final void showMsg(String msg, int titleRes) {
        if (this.isFinishing()) {
            return;
        }
        final ThemeDDialog dialog = new ThemeDDialog(this, msg, titleRes);
        dialog.setNegativeButton(R.string.common_sure, new DialogOnClickListener() {

            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * 显示通用dialog消息
     *
     * @param msg
     * @param title
     */
    public final void showMsg(int msg, int title) {
        if (this.isFinishing()) {
            return;
        }
        final ThemeDDialog dialog = new ThemeDDialog(this, msg, title);
        dialog.setNegativeButton(R.string.common_sure, new DialogOnClickListener() {

            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * 设置可取消的消息对话框
     *
     * @param msg
     * @param title
     */
    public final void showCancelableMsg(String msg, int title) {
        if (this.isFinishing()) {
            return;
        }
        final ThemeDDialog dialog = new ThemeDDialog(this, msg, title);
        dialog.setCancellable(false);
        dialog.setNegativeButton(R.string.common_sure, new DialogOnClickListener() {

            @Override
            public void onClick() {
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }

    /**
     * 设置可取消的消息对话框
     *
     * @param msg
     * @param title
     * @param button
     */
    public final void showCancelableMsg(String msg, int title, int button) {
        if (this.isFinishing()) {
            return;
        }
        final ThemeDDialog dialog = new ThemeDDialog(this, msg, title);
        dialog.setCancellable(false);
        dialog.setNegativeButton(button, new DialogOnClickListener() {

            @Override
            public void onClick() {
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }

    /**
     * 设置跟布局的背景颜色
     *
     * @param color
     */
    public final void setBaseBg(int color) {
        if (mBaseLayout != null) {
            mBaseLayout.setBackgroundColor(color);
        }
    }

    /**
     * 添加网络布局
     */
    public final void notifyNetworkInfo() {
        View child = mBaseLayout.getChildAt(1);
        if (mBaseLayout != null && (child == null || child != mNetNotifyView)) {
            mNetNotifyView = new HeaderNotifyView(this).getView();
            mBaseLayout.addView(mNetNotifyView, 1);
            mNetNotifyView.setClickable(true);
            mNetNotifyView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(BaseActivity.this, NoNetworkGuideActivity.class);
                    startActivity(intent);

                }
            });
        }
    }

    /**
     * 移除网络布局
     */
    public final void removeNetworkInfo() {
        View child = mBaseLayout.getChildAt(1);
        if (mBaseLayout != null && child != null && child == mNetNotifyView) {
            mBaseLayout.removeView(mNetNotifyView);
        }
    }

    /**
     * 获取顶部的高度
     *
     * @return
     */
    protected final int getHeadHeight() {
        return mHeadLayout.getHeight();
    }

    /**
     * 设置是否按默认显示网络状况 现在有两种 true 为默认 false 为不默认
     *
     * @param flag
     */
    public void setShowNetDefault(boolean flag) {
        mShowNetDefault = flag;
    }

    /**
     * 注册网络变化监听器
     */
    private void registerNetReceiver() {
        if (!mNetChangeReceiverFlag && needRegisterNetChange()) {
            mNetChangeReceiverFlag = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetReceiver, filter);
        }
    }

    /**
     * 释放网络变化监听器
     */
    private void unregisterNetReceiver() {
        if (mNetChangeReceiverFlag && needRegisterNetChange()) {
            mNetChangeReceiverFlag = false;
            unregisterReceiver(mNetReceiver);
        }
    }

    /**
     * 权限配置在app模块中的manifest
     */
    @SuppressLint("MissingPermission")
    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (mNetNotConnect) {
                        mNetNotConnect = false;
                        int type = info.getType();
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            ToastUtil.showCustomViewToast(BaseActivity.this, 0,
                                    R.string.toast_net_wifi, Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showCustomViewToast(BaseActivity.this, 0,
                                    R.string.toast_net_2g, Toast.LENGTH_SHORT);
                        }
                    }
                    removeNetworkInfo();
                } else {
                    mNetNotConnect = true;
                    if (mShowNetDefault) {
                        ToastUtil.showCustomViewToast(BaseActivity.this, 0,
                                R.string.toast_no_net, Toast.LENGTH_SHORT);
                    } else {
                        notifyNetworkInfo();
                    }
                }
            } catch (Exception e) {
                LogUtil.w("BaseActivity", "Missing permissions required by " +
                        "ConnectivityManager.getActiveNetworkInfo: android.permission.ACCESS_NETWORK_STATE");
            }
        }
    };

    /**
     * 是否支持全屏
     *
     * @return
     */
    protected boolean supportFullScreen() {
        return false;
    }

    protected boolean needRegisterNetChange() {
        return true;
    }

    /**
     * 设置状态栏背景
     */
    protected void setStatusBarStyle() {
        if (!isImmersionBarEnabled()) {
            return;
        }

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarView(R.id.status_bar_view)
                .barColorInt(getStatusBarColor())
                .navigationBarColor(R.color.black)
                .init();
    }


    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 设置状态栏背景色资源id
     *
     * @return
     */
    protected int getStatusBarColorResId() {
        return R.color.black_title_bg;
    }

    /**
     * 设置状态栏背景色
     *
     * @return
     */
    protected int getStatusBarColor() {
        if (Build.VERSION.SDK_INT > 22) {
            return getColor(getStatusBarColorResId());
        } else {
            return getResources().getColor(getStatusBarColorResId());
        }
    }

    /**
     * 设置状态栏字体为黑色，为了解决白色状态栏的问题
     */
    protected void setStatusBarBlackFont() {
        if (mImmersionBar != null) {
            mImmersionBar.statusBarDarkFont(true, 0.2f).init();
        }
    }

    /**
     * 是否需要注册eventBus
     *
     * @return 默认不需要注册eventBus
     */
    protected boolean needRegisterEventBus() {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param stringId
     * @see #showToast(String msg)
     */
    public void showToast(int stringId) {
        showToast(getString(stringId));
    }

    /**
     * 统一调用，统一非空判断
     *
     * @param msg
     */
    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showCustomViewToast(getApplication(), msg);
        }
    }

    /**
     * 是否需要设置背景颜色
     *
     * @return
     */
    protected boolean needSetBackground() {
        return true;
    }


    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (AppCompatUtil.isTranslucentOrFloating(this)) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (BaseApplication.getInstance() != null) {
            BaseApplication.getInstance().onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        }
        return mIsDestroyed;
    }
}
