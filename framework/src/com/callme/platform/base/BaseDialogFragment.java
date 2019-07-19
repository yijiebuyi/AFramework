package com.callme.platform.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.callme.platform.R;
import com.callme.platform.util.OtherUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2017/10/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class BaseDialogFragment extends DialogFragment implements
        DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener,
        View.OnClickListener {

    protected Context mContext;
    protected boolean mDestroy = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDestroy = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDestroy = true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, R.style.CommonDialog);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside());

        View view = onCreateView();
        Window dialogWindow = dialog.getWindow();

        //set attributes
        dialogWindow.setContentView(view);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setLayout(getWindowWidth(), getWindowHeight());
        //set params
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = OtherUtils.clamp(getWindowDimAmount(), 0.0f, 1.0f);
        setWindowDimAmount(params);
        dialogWindow.setAttributes(params);

        onViewCreated(view);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                onFragmentShow(dialogInterface);
            }
        });

        dialog.setOnCancelListener(this);
        dialog.setOnDismissListener(this);
        return dialog;
    }

    protected void showProgress() {
        Activity ac = getActivity();
        if (ac instanceof BaseActivity) {
            ((BaseActivity) ac).showProgressDialog(null, true);
        }
    }

    protected void dismissProgress() {
        Activity ac = getActivity();
        if (ac instanceof BaseActivity) {
            ((BaseActivity) ac).closeProgressDialog();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    protected abstract View onCreateView();

    protected abstract void onFragmentShow(DialogInterface dialogInterface);

    protected void onViewCreated(View view) {

    }

    protected void setWindowDimAmount(WindowManager.LayoutParams params) {

    }

    protected float getWindowDimAmount() {
        return 0.5f;
    }

    protected int getWindowWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected int getWindowHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected boolean canceledOnTouchOutside() {
        return true;
    }
}
