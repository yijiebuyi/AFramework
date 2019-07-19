package com.callme.platform.util.permission;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：Dialog封装,自定义dialog需子类重写onCreateDialog(FragmentActivity activity)方法
 * 作者：zyl
 * 创建时间：on 2018/6/15.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SimpleDialog extends DialogFragment {
    protected DialogCallback mDialogListener;
    protected String mDialogTitle;
    protected String mDialogMessage;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = onCreateDialog(getActivity());
        if (dialog != null) {
            return dialog;
        }

        return super.onCreateDialog(savedInstanceState);
    }

    public Dialog onCreateDialog(FragmentActivity activity) {
        if (activity == null) {
            return null;
        }

        AlertDialogBuilder builder = new AlertDialogBuilder(activity);
        builder.setTitle(mDialogTitle);
        builder.setMessage(mDialogMessage);
        builder.setDialogListener(mDialogListener);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (transaction == null) {
            return -1;
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(this, tag);
        transaction.addToBackStack(null);
        return transaction.commitAllowingStateLoss();
    }

    public void show(FragmentManager fragmentManager, String tag) {
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(this, tag);
            ft.commitNowAllowingStateLoss();
        }
    }

    public void show(FragmentActivity activity, String tag) {
        if (activity != null) {
            FragmentManager fm = activity.getSupportFragmentManager();
            if (fm != null) {
                show(fm, tag);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDialogListener != null) {
            mDialogListener.onDismiss();
        }
    }

    public void setDialogListener(DialogCallback l) {
        this.mDialogListener = l;
    }

    public void setDialogTitle(String title) {
        this.mDialogTitle = title;
    }

    public void setDialogMessage(String message) {
        this.mDialogMessage = message;
    }
}
