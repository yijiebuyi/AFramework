package com.callme.platform.util.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.callme.platform.R;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：AlertDialog.Builder继承封装
 * 作者：zyl
 * 创建时间：on 2018/6/15.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlertDialogBuilder extends AlertDialog.Builder {
    private DialogCallback mDialogCallback;

    public AlertDialogBuilder(@NonNull Context context) {
        super(context);
        setPositiveText(R.string.setting);
        setNegativeText(R.string.cancel);
    }

    public void setDialogListener(DialogCallback dialogCallback) {
        this.mDialogCallback = dialogCallback;
    }

    public void setNegativeText(String s) {
        setNegativeButton(s, null);
    }

    public void setNegativeText(int id) {
        setNegativeButton(id, null);
    }

    public void setPositiveText(String s) {
        setPositiveButton(s, null);
    }

    public void setPositiveText(int id) {
        setPositiveButton(id, null);
    }

    @Override
    public AlertDialog.Builder setNegativeButton(int textId, final DialogInterface.OnClickListener
            listener) {
        return super.setNegativeButton(textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClick(dialog, which);
                }

                onNegative();
            }
        });
    }

    @Override
    public AlertDialog.Builder setNegativeButton(CharSequence text, final DialogInterface
            .OnClickListener listener) {
        return super.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClick(dialog, which);
                }

                onNegative();
            }
        });
    }

    @Override
    public AlertDialog.Builder setPositiveButton(int textId, final DialogInterface.OnClickListener
            listener) {
        return super.setPositiveButton(textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClick(dialog, which);
                }

                onPositive();
            }
        });
    }

    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, final DialogInterface
            .OnClickListener listener) {
        return super.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onClick(dialog, which);
                }

                onPositive();
            }
        });
    }

    private void onNegative() {
        if (mDialogCallback != null) {
            mDialogCallback.onNegative();
        }
    }

    private void onPositive() {
        if (mDialogCallback != null) {
            mDialogCallback.onPositive();
        }
    }
}
