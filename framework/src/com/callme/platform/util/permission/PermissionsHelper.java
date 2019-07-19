package com.callme.platform.util.permission;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.callme.platform.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：权限帮助类
 * 作者：zyl
 * 创建时间：on 2018/5/31.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PermissionsHelper {
    private final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private final String SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    private static class LazyHelper {
        private static final PermissionsHelper INSTANCE = new PermissionsHelper();
    }

    public static PermissionsHelper getInstance() {
        return LazyHelper.INSTANCE;
    }

    /**
     * APP基本权限请求
     *
     * @param context
     */
    public void basicPermissionsRequest(final Context context, final PermissionCallback permission) {
        if (context == null) {
            return;
        }

        final String[] basicPermissions = {
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE,
                READ_PHONE_STATE,
                SYSTEM_ALERT_WINDOW};

        requestPermissions(context, basicPermissions, permission);
    }

    /**
     * 权限请求.注意：同时请求多个权限的时候在AbsPermissionCallback.onGranted
     * 方法中要判断返回的权限集合中是否包有xx权限
     *
     * @param context
     * @param permission 要申请的权限
     * @param l
     */
    public void requestPermission(final Context context, final String permission
            , final PermissionCallback l) {
        requestPermissions(context, new String[]{permission}, l);
    }

    /**
     * 权限请求.注意：同时请求多个权限的时候在AbsPermissionCallback.onGranted
     * 方法中要判断返回的权限集合中是否包有xx权限
     *
     * @param context
     * @param permissions 要申请的权限
     * @param l
     */
    public void requestPermissions(final Context context, final String[] permissions
            , final PermissionCallback l) {
        if (context == null || permissions == null) {
            return;
        }

        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        onActionCallback(context, data, l);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        data = new ArrayList<>(Arrays.asList(permissions));
                        onActionCallback(context, data, l);
                    }
                }).start();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        return AndPermission.hasPermissions(context, permissions);
    }

    /**
     * 请求权限后的动作
     *
     * @param context
     * @param data
     * @param l
     */
    private void onActionCallback(Context context, List<String> data, PermissionCallback l) {
        List<String> granted = new ArrayList<>(data);
        List<String> denied = filterGrantedPermission(context, granted);

        if (granted.size() >= denied.size()) {
            granted.removeAll(denied);
        }

        onGrantedAction(context, granted, l);
        onDeniedAction(context, denied, l);
    }

    /**
     * 拒绝权限的动作
     *
     * @param context
     * @param data    拒绝的权限
     * @param l
     */
    private void onDeniedAction(Context context, List<String> data, PermissionCallback l) {
        if (data != null && data.size() > 0) {
            if (l == null || !l.onDenied(data)) {
                alwaysDeniedAction(context, data, l);
            }
        }
    }

    /**
     * @param data 允许的权限动作
     * @param l
     */
    private void onGrantedAction(Context context, List<String> data, PermissionCallback l) {
        if (l != null && data != null && data.size() > 0) {
            l.onGranted(data);
        }
    }

    /**
     * 过滤权限
     *
     * @param context
     * @param data
     * @return 请求失败的权限
     */
    private List<String> filterGrantedPermission(Context context, List<String> data) {
        if (context == null || data == null) {
            return null;
        }

        if (data.contains(ACCESS_COARSE_LOCATION) || data.contains(ACCESS_FINE_LOCATION)) {
            if (!isLocationEnabled(context)) {
                showLocationServiceDialog(context, context.getString(R.string.location_info),
                        context.getString(R.string.is_setting_location_info));

                //暴力解决，机型太多没法
                data.remove(ACCESS_FINE_LOCATION);
                data.remove(ACCESS_COARSE_LOCATION);
            } else if (!isGpsEnabled(context)) {
                showLocationServiceDialog(context, context.getString(R.string.location_info),
                        context.getString(R.string.is_gps_location_info));
            }
        }

        List<String> permissions = new ArrayList<>();
        if (data.contains(READ_EXTERNAL_STORAGE) && !checkExternalStorageCanRead()) {
            permissions.add(READ_EXTERNAL_STORAGE);
            data.remove(READ_EXTERNAL_STORAGE);
        }

        if (data.contains(WRITE_EXTERNAL_STORAGE) && !checkExternalStorageCanWrite()) {
            permissions.add(WRITE_EXTERNAL_STORAGE);
            data.remove(WRITE_EXTERNAL_STORAGE);
        }


        for (int i = 0; i < data.size(); i++) {
            String s = data.get(i);
            if (TextUtils.equals(s, SYSTEM_ALERT_WINDOW)) {
                if (!canDrawOverlays(context)) {
                    permissions.add(s);
                }
            } else if (!checkSelfPermission(context, s)) {
                permissions.add(s);
            }
        }

        return permissions;
    }


    /**
     * 提示用户设置位置信息
     *
     * @param context
     */
    private void showLocationServiceDialog(final Context context, String title, String msg) {
        if (context == null) {
            return;
        }

        final LocationServiceDialog dialog = new LocationServiceDialog();
        dialog.setDialogTitle(title);
        dialog.setDialogMessage(msg);
        dialog.setDialogListener(new DialogCallback() {
            @Override
            public void onPositive() {
                dialog.dismiss();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }

            @Override
            public void onNegative() {
                if (dialog != null && dialog.isAdded()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show(getFragmentActivity(context), null);
    }

    /**
     * 提示用户去设置权限的dialog
     *
     * @param context
     * @param data
     * @param l
     */
    private void showPermissionDialog(final Context context, final List<String> data
            , final PermissionCallback l) {
        if (context == null || data == null) {
            return;
        }

        if (!(context instanceof FragmentActivity) || (onlyHasSystemAlertWindow(data)
                && canDrawOverlays(context))) {
            return;
        }

        String message = getDeniedPermissionText(context, data);
        if (TextUtils.isEmpty(message)) {
            return;
        }

        String title = context.getString(R.string.permissions);
        final PermissionDialog dialog = new PermissionDialog();
        dialog.setDialogTitle(title);
        dialog.setDialogMessage(message);
        dialog.setDialogListener(new DialogCallback() {
            @Override
            public void onPositive() {
                dialog.dismiss();
                if (l != null) {
                    l.onPositive();
                }

                if (data.size() == 1 && data.contains(SYSTEM_ALERT_WINDOW)) {
                    jumpSystemAlertWindowPermissionSettings(context, l);
                } else {
                    jumpPermissionSettings(context, data, l);
                }
            }

            @Override
            public void onNegative() {
                dialog.dismiss();
                if (l != null) {
                    l.onNegative();
                }
            }

            @Override
            public void onDismiss() {
                super.onDismiss();
                if (l != null) {
                    l.onDismiss();
                }
            }
        });

        dialog.show(getFragmentActivity(context), title);
    }

    /**
     * 获取用户拒绝权限文本信息
     *
     * @param context
     * @param data    用户拒绝权限
     * @return
     */
    private String getDeniedPermissionText(Context context, List<String> data) {
        if (context == null || data == null) {
            return null;
        }

        if (!data.contains(ACCESS_COARSE_LOCATION) || !data.contains(ACCESS_FINE_LOCATION)) {
            data.remove(ACCESS_COARSE_LOCATION);
            data.remove(ACCESS_FINE_LOCATION);
        }

        if (data.size() <= 0) {
            return null;
        }

        List<String> permissions = Permission.transformText(context, data);
        StringBuilder builder = new StringBuilder();

        if (permissions != null && permissions.size() > 0) {
            builder.append("[");

            int size = permissions.size();
            for (int i = 0; i < size; i++) {
                builder.append(permissions.get(i));
                if (i != size - 1) {
                    builder.append(",");
                }
            }

            if (data.size() > permissions.size() && hasSystemAlertWindow(data)
                    && !canDrawOverlays(context)) {
                builder.append(",");
                builder.append(context.getString(R.string.system_alert_window));
            }

            builder.append("]");
        } else if (hasSystemAlertWindow(data) && !canDrawOverlays(context)) {
            builder.append("[");
            builder.append(context.getString(R.string.system_alert_window));
            builder.append("]");
        }

        builder.append(context.getString(R.string.authorization_failed));

        return builder.toString();
    }

    /**
     * 悬浮窗权限是否可用
     *
     * @param context
     * @return
     */
    private boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (aom != null) {
                int result = aom.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        android.os.Process.myUid(), context.getPackageName());
                return result == AppOpsManager.MODE_ALLOWED;
            }
        }
        return true;
    }

    /**
     * 集合中有且仅一个SystemAlertWindow权限
     *
     * @param data
     * @return
     */
    private boolean onlyHasSystemAlertWindow(List<String> data) {
        return data != null && data.size() == 1 && data.contains(SYSTEM_ALERT_WINDOW);
    }


    /**
     * 集合中含有SystemAlertWindow权限
     *
     * @param data
     * @return
     */
    private boolean hasSystemAlertWindow(List<String> data) {
        if (data == null) {
            return false;
        }

        return data.contains(SYSTEM_ALERT_WINDOW);
    }

    public boolean checkSelfPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager
                .PERMISSION_GRANTED;
    }

    /**
     * 总是被用户拒权限的动作
     *
     * @param context
     * @param permissions 被拒绝的权限
     * @param listener
     */
    private void alwaysDeniedAction(Context context, final List<String> permissions
            , final PermissionCallback listener) {
        showPermissionDialog(context, permissions, listener);
    }


    /**
     * 检查是否开启定位
     *
     * @param context
     * @return
     */
    public boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            return false;
        }
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查GPS是否开启定位
     *
     * @param context
     * @return
     */
    public boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            return false;
        }
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isFragmentActivity(Context context) {
        if (context == null) {
            return false;
        }

        return context instanceof FragmentActivity;
    }

    private FragmentActivity getFragmentActivity(Context context) {
        if (context == null || !isFragmentActivity(context)) {
            return null;
        }

        return (FragmentActivity) context;
    }

    private boolean isExternalMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 存储卡是否可写
     *
     * @return
     */
    private boolean checkExternalStorageCanWrite() {
        try {
            if (isExternalMounted()) {
                boolean canWrite = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath()).canWrite();
                if (canWrite) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存储卡是否可读
     *
     * @return
     */
    private boolean checkExternalStorageCanRead() {
        try {
            if (isExternalMounted()) {
                boolean canWrite = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath()).canRead();
                if (canWrite) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转到权限设置页面
     *
     * @param context
     * @param data
     * @param l
     */
    private void jumpPermissionSettings(final Context context, final List<String> data,
                                        final PermissionCallback l) {
        AndPermission.with(context).runtime().setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        onActionCallback(context, data, l);
                    }
                }).start();
    }

    /**
     * 跳转到悬浮窗权限设置页面
     *
     * @param context
     * @param l
     */
    private void jumpSystemAlertWindowPermissionSettings(
            final Context context, final PermissionCallback l) {
        AndPermission.with(context)
                .overlay()
                .rationale(new Rationale<Void>() {
                    @Override
                    public void showRationale(Context c, Void d, RequestExecutor e) {
                        e.execute();
                        //e.cancel();
                    }
                }).start();
    }
}
