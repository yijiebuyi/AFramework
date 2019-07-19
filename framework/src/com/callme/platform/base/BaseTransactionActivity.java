package com.callme.platform.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.callme.platform.R;
import com.callme.platform.common.dialog.LoadingProgressDialog;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：半透明背景activity
 * <p>
 * <p>
 * 作者：zhanghui 2018/10/6
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class BaseTransactionActivity extends AppCompatActivity {

    private FrameLayout mContainer;
    private Unbinder mBinder;
    private LoadingProgressDialog mLoadingProgressDialog;

    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (supportFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_base_transaction);
        } else {
            setContentView(R.layout.activity_base_transaction);
            setStatusBarStyle();
        }
        mContainer = findViewById(R.id.container);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outClickClose()) {
                    finish();
                }
            }
        });
        addIntoContent(getContentView());
        if (needRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        onContentAdded();
    }

    protected void setBackground(int redId) {
        mContainer.setBackgroundResource(redId);
    }

    protected abstract View getContentView();

    protected abstract void onContentAdded();

    /**
     * 添加view到容器中
     *
     * @param view
     */
    private void addIntoContent(View view) {
        if (view != null) {
            mContainer.removeAllViews();
            mContainer.addView(view);
            mBinder = ButterKnife.bind(this);
        } else {
            try {
                throw new Exception("content view can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }

        if (needRegisterEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }

        super.onDestroy();
    }

    /**
     * 是否支持全屏
     *
     * @return
     */
    protected boolean supportFullScreen() {
        return true;
    }

    /**
     * 设置状态栏背景
     */
    protected void setStatusBarStyle() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarView(R.id.status_bar_view)
                .barColorInt(getStatusBarColor())
                .navigationBarColor(R.color.black)
                .init();
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
     * 设置状态栏背景色资源id
     *
     * @return
     */
    protected int getStatusBarColorResId() {
        return R.color.black_title_bg;
    }

    protected boolean needRegisterEventBus() {
        return false;
    }

    public void showProgressDialog(boolean cancelable) {
        if (mLoadingProgressDialog == null) {
            mLoadingProgressDialog = new LoadingProgressDialog(this);
        }
        mLoadingProgressDialog.setCancelable(cancelable);
        if (!isDestroyed() && !isFinishing() && !mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.show();
        }
    }

    public void closeProgressDialog() {
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.dismiss();
        }
    }

    protected boolean outClickClose() {
        return true;
    }
}
