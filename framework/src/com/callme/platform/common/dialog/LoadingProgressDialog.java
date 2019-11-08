package com.callme.platform.common.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.widget.LoadingView;

/**
 * 加载进度条
 */
public class LoadingProgressDialog extends Dialog {
    private TextView mTvTip;
    private String mTxt;
    private LoadingView mProgressBar;
    private int mLoadingBg;
    private float mDimAmount = 0.3f;

    public LoadingProgressDialog(Context context) {
        super(context, R.style.LoadingDialog);
    }

    public LoadingProgressDialog(Context context, float dimAmount) {
        super(context, R.style.LoadingDialog);
        setDimAmount(dimAmount);
    }

    public LoadingProgressDialog(Context context, int loadBg) {
        super(context, R.style.LoadingDialog);
        mLoadingBg = loadBg;
    }

    public void setText(String txt) {
        mTxt = txt;
    }

    public void setDimAmount(float dimAmount) {
        if (dimAmount < 0 || dimAmount > 1) {
            return;
        }
        mDimAmount = dimAmount;
    }

    public void setText(int res) {
        if (res > 0) {
            mTxt = getContext().getString(res);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = mDimAmount;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);

        setContentView(R.layout.dialog_progress_loading);
        //使用MATCH_PARENT会出现状态栏顶部为黑色
        int screenH = getContext().getResources().getDisplayMetrics().heightPixels;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, screenH);
        mTvTip = (TextView) findViewById(R.id.progress_txt);
        mProgressBar = (LoadingView) findViewById(R.id.progress_bar);
        if (mTvTip != null && !TextUtils.isEmpty(mTxt)) {
            mTvTip.setText(mTxt);
        }

        if (mProgressBar != null && mLoadingBg != 0) {
            mProgressBar.setBackgroundResource(mLoadingBg);
        }
    }
}
