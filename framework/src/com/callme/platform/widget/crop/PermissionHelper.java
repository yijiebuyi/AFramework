package com.callme.platform.widget.crop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.callme.platform.R;

import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：权限帮助类
 * 作者：huangyong
 * 创建时间：2018/3/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PermissionHelper {

    /**
     * @param permissions 如果应用之前请求过此权限但用户拒绝了请求，shouldShowRequestPermissionRationale()此方法将返回true
     * @return 如果应用之前请求过此权限但用户拒绝了请求，返回拒绝后的权限
     */
    @TargetApi(23)
    public static String shouldRationale(Context context, String[] permissions) {
        if (!(context instanceof Activity) || Build.VERSION.SDK_INT < 23) {
            return null;
        }

        boolean rationale = false;
        for (String permission : permissions) {
            rationale = ((Activity) context).shouldShowRequestPermissionRationale(permission);
            if (rationale) {
                return permission;
            }
        }

        return null;
    }

    /**
     * @param permission 如果应用之前请求过此权限但用户拒绝了请求，shouldShowRequestPermissionRationale()此方法将返回true
     * @return 如果应用之前请求过此权限但用户拒绝了请求，返回拒绝后的权限
     */
    @TargetApi(23)
    public static String shouldRationale(Context context, String permission) {
        if (!(context instanceof Activity) || Build.VERSION.SDK_INT < 23) {
            return null;
        }

        boolean flag = ((Activity) context).shouldShowRequestPermissionRationale(permission);

        return flag ? permission : null;
    }

    /**
     * 是否具有某个权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否都具有某个权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasGranted(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        for (String permission : permissions) {
            if (!hasGranted(context, permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 当权限被拒绝后，显示提示框
     *
     * @param context
     * @param permission
     * @param requestCode
     * @param finishCurrActivity
     */
    public static void showPermissionTipDialog(final Context context, final String permission,
                                               final int requestCode, final boolean finishCurrActivity) {
        List<String> perStr = PermissionUtil.transformText(context, permission);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String titlePrefix = perStr != null && !perStr.isEmpty() ? perStr.get(0) : "";
        String title = titlePrefix + context.getString(R.string.permission_title_permission_rationale);
        builder.setTitle(title);

        builder.setPositiveButton(R.string.permission_resume, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission},
                            requestCode);
                }
                dialog.dismiss();
                if (finishCurrActivity) {
                    ((Activity) context).finish();
                }
            }
        });

        builder.setNegativeButton(R.string.permission_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (finishCurrActivity) {
                    ((Activity) context).finish();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}
