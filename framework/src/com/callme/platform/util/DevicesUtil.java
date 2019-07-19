package com.callme.platform.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.callme.platform.util.permission.PermissionsHelper;

import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：和设备相关的工具类
 * <p>
 * 获取设备Metrics{@link #getDisplayMetrics(Context)}
 * 屏幕尺寸{@link #getScreenW} {@link #getScreenH}
 * dp和px互转 {@link #dip2px(Context, float)} {@link #px2dip(Context, float)}
 * sp和px互转 {@link #sp2px(Context, float)} {@link #px2sp(Context, float)}
 * 网络状态 {@link #isNetworkAvailable} {@link #isWifiEnabled}
 * ime号 {@link #getSimSerialNumber} {@link #getIMEI}
 * sim卡唯一标识 {@link #getSIMEI}
 * 手机号码 {@link #getPhoneNum}
 * 运营商 {@link #getPhoneProvider}
 * id地址 {@link #getIPAddress}
 * mac地址 {@link #getMacAddress}
 * 手机型号，厂商 {@link #getModel()} {@link #getBrand()}
 * 系统版本 {@link #getSystemVersion()} {@link #getSystemVersionLevel()} {@link #getAndroidVersion()}
 * 当前设备可用的内存 {@link #getAvailMemory}
 * 当前设备是不是平板 {@link #isPadType}
 * 当前设备是否是模拟器 {@link #isEmulator()}
 * 当前设备是否已root {@link #isRootSystem()}
 * 当前设备是否已经rot或者模拟器 {@link #isRootOrEmulator(Context)}
 * 当前设备是否安装了某应用 {@link #isAppInstalled(Context, String)}
 * <p>
 * 作者：huangyong
 * 创建时间：2018/14/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DevicesUtil {

    public static boolean isMX3 = false;

    static {
        String model = Build.MODEL.trim().toLowerCase();
        if (model.equalsIgnoreCase("m353")
                || Build.DEVICE.equalsIgnoreCase("mx3")) {
            isMX3 = true;
        }
    }

    /**
     * 获取设备Metrics
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取当前屏幕宽度
     * <p/>
     * return 屏幕宽度
     */
    public static int getScreenW(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取当前屏幕宽度
     * <p/>
     * return 屏幕高度
     */
    public static int getScreenH(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * dip 转px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dip2px(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /**
     * px 转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px 转 sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp 转 px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 检查是否有网
     *
     * @param context
     * @return true or false
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo n = cm.getActiveNetworkInfo();
            if (n != null)
                return n.isAvailable();
        }

        return false;
    }

    /**
     * 是否连接WIFI
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }


    /**
     * 获取手机SIM卡序列号
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getSimSerialNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getSimSerialNumber();
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param context
     * @return 获取手机IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(final Context context) {
        final String permission = Manifest.permission.READ_PHONE_STATE;
        if (PermissionsHelper.hasPermissions(context, permission)) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                if (tm == null) {
                    return "";
                }

                String imei = tm.getDeviceId();
                if (null == imei) {
                    imei = "";
                }
                return imei;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * @param slotId slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            return (String) method.invoke(manager, slotId);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param context
     * @return sim卡唯一标识
     */
    @SuppressLint("MissingPermission")
    public static String getSIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return "";
            }

            String simei = tm.getSubscriberId();
            if (null == simei) {
                simei = "";
            }

            return simei;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取手机号码
     * 权限配置在app模块中的manifest
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNum(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String num = tm.getLine1Number();
            if (null == num) {
                return "";
            } else {
                return num;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getPhoneProvider(Context context) {
        String simei = getSIMEI(context);
        if (TextUtils.isEmpty(simei)) {
            return "";
        }

        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (simei.startsWith("4600001")) {
            return "中国联通";
        } else if (simei.startsWith("4600002")) {
            return "中国移动";
        } else if (simei.startsWith("4600003")) {
            return "中国电信";
        } else {
            return "";
        }
    }

    /**
     * 获取当前手机IP地址
     *
     * @param context
     * @return
     */
    /**
     * 权限配置在app模块中的manifest
     */
    @SuppressLint("MissingPermission")
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return TextUtils.isEmpty(inetAddress.getHostAddress()) ? "" : inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return TextUtils.isEmpty(ipAddress) ? "" : ipAddress;
            }
        }
        return "";
    }


    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder sb = new StringBuilder();
                for (byte b : macBytes) {
                    sb.append(String.format("%02X:", b));
                }

                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                return TextUtils.isEmpty(sb.toString()) ? "" : sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取系统版本Level
     *
     * @return
     */
    public static int getSystemVersionLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取当前系统的android版本号
     *
     * @return
     */
    public static String getAndroidVersion() {
        return "Android" + android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 手机品牌
     *
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 手机型号
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前系统可用内存
     *
     * @param ctx
     * @return
     */
    public static String getAvailMemory(Context ctx) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(ctx, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 判断是否是平板
     *
     * @param ctx
     * @return
     */
    public static boolean isPadType(Context ctx) {
        TelephonyManager telMg = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telMg.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return true;
        } else {
            return false;
        }
    }

    // 获取状态栏高度
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 判断真机和模拟器
     *
     * @return
     */
    public static boolean isEmulator() {
        try {
            /*boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean emu = "1".equalsIgnoreCase(getSystemProperty("ro.kernel.qemu"));
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            if (emu || goldfish || sdk) {
                return true;
            }*/

            return (Build.MODEL.equals("sdk"))
                    || (Build.MODEL.equals("google_sdk"));
        } catch (Exception e) {
        }
        return false;
    }


    private static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, new Object[]{name});
    }

    /**
     * 手机是否root
     *
     * @return
     */
    public static boolean isRootSystem() {
        if (isRootSystem1() || isRootSystem2()) {
            //TODO 可加其他判断 如是否装了权限管理的apk，大多数root 权限 申请需要app配合，也有不需要的，这个需要改su源码。因为管理su权限的app太多，无法列举所有的app，特别是国外的，暂时不做判断是否有root权限管理app
            //多数只要su可执行就是root成功了，但是成功后用户如果删掉了权限管理的app，就会造成第三方app无法申请root权限，此时是用户删root权限管理app造成的。
            //市场上常用的的权限管理app的包名   com.qihoo.permmgr  com.noshufou.android.su  eu.chainfire.supersu   com.kingroot.kinguser  com.kingouser.com  com.koushikdutta.superuser
            //com.dianxinos.superuser  com.lbe.security.shuame com.geohot.towelroot 。。。。。。
            return true;
        } else {
            return false;
        }
    }

    private static boolean isRootSystem1() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isRootSystem2() {
        List<String> pros = getPath();
        File f = null;
        try {
            for (int i = 0; i < pros.size(); i++) {
                f = new File(pros.get(i), "su");
                System.out.println("f.getAbsolutePath():" + f.getAbsolutePath());
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }

    /**
     * 是否是root或者模拟器
     *
     * @param context
     * @return
     */
    public static boolean isRootOrEmulator(Context context) {
        boolean isEmulator = isEmulator();//判断是否是在模拟器上运行
        if (!isEmulator) {
            isEmulator = AntiEmulator.CheckEmulatorBuild(context.getApplicationContext());
        }
        if (!isEmulator) {
            isEmulator = AntiEmulator.isEmulator();
        }
        boolean isRoot = isRootSystem();
        if (isEmulator || isRoot) {
            return true;
        }

        return false;
    }

    /**
     * 判断手机是否安装某应用
     * <p>
     * 百度：com.baidu.BaiduMap
     * 腾讯：com.tencent.map
     * 高德：com.autonavi.minimap
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置当前activity的亮度
     *
     * @param activity
     * @param brightValue
     */
    public static void setBrightness(Activity activity, int brightValue) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = (brightValue <= 0 ? -1.0f : brightValue / 255f);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 是否隐藏键盘
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
