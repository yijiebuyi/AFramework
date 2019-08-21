package com.callme.platform.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

public class PhoneUtil {

    public static void sendMessage(Context context, String num, String content) {
        Uri uri = Uri.parse("smsto:" + num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param number
     */
    public static void callPhone(Context context, String number) {
        callPhone(context, number, false);
    }

    /**
     * @param context
     * @param number
     * @param isDoubleCard 是否双卡
     */
    public static void callPhone(Context context, String number, boolean isDoubleCard) {
        callPhone(context, number, 9876, isDoubleCard);
    }

    public static void callPhone(Context context, String phoneNumber, int requestCode) {
        callPhone(context, phoneNumber, requestCode, false);
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     * @param requestCode
     * @param isDoubleCard 是否双卡
     */
    public static void callPhone(Context context, String phoneNumber, int requestCode, boolean isDoubleCard) {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.showCustomViewToast(context, "无效电话号码");
            return;
        }

        goToDial(context, phoneNumber, requestCode);
    }

    /**
     * 跳转到拨号界面
     *
     * @param context
     * @param phoneNumber
     * @param requestCode
     */
    private static void goToDial(Context context, String phoneNumber, int requestCode) {
        Intent intent;
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (requestCode > 0 && context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 直接拨打电话
     *
     * @param context
     * @param phoneNumber
     * @param requestCode
     * @param isDoubleCard
     */
    private static void goToCall(Context context, String phoneNumber, int requestCode, boolean isDoubleCard) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},
                        requestCode);
            } else {
                ToastUtil.showCustomViewToast(context, "请开启应用拨打电话权限");
            }
        } else {
            Intent intent;
            if (TextUtils.equals("110", phoneNumber) || isDoubleCard) {
                intent = new Intent(Intent.ACTION_DIAL);
            } else {
                intent = new Intent(Intent.ACTION_CALL);
            }
            intent.setData(Uri.parse("tel:" + phoneNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
