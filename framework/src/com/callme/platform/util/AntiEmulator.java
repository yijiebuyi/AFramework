package com.callme.platform.util;

/**
 *
 * @author zyl
 * @time 2017/4/24 10:17
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.callme.platform.util.permission.PermissionsHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static android.Manifest.permission.READ_PHONE_STATE;

public class AntiEmulator {
    private static String[] known_pipes = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    private static String[] known_qemu_drivers = {
            "goldfish"
    };

    private static String[] known_files = {
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
    };

    private static String[] known_numbers = {"15555215554", "15555215556",
            "15555215558", "15555215560", "15555215562", "15555215564",
            "15555215566", "15555215568", "15555215570", "15555215572",
            "15555215574", "15555215576", "15555215578", "15555215580",
            "15555215582", "15555215584",};

    private static String[] known_device_ids = {
            "000000000000000" // 默认ID
    };

    private static String[] known_imsi_ids = {
            "310260000000000" // 默认的 imsi id
    };

    //检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道
    public static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                Log.v("Result:", "Find pipes!");
                return true;
            }
        }
        Log.i("Result:", "Not Find pipes!");
        return false;
    }

    // 检测驱动文件内容
    // 读取文件内容，然后检查已知QEmu的驱动程序的列表
    public static Boolean checkQEmuDriverFile() {
        File driver_file = new File("/proc/tty/drivers");
        if (driver_file.exists() && driver_file.canRead()) {
            byte[] data = new byte[1024];  //(int)driver_file.length()
            try {
                InputStream inStream = new FileInputStream(driver_file);
                inStream.read(data);
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            String driver_data = new String(data);
            for (String known_qemu_driver : AntiEmulator.known_qemu_drivers) {
                if (driver_data.indexOf(known_qemu_driver) != -1) {
                    Log.i("Result:", "Find know_qemu_drivers!");
                    return true;
                }
            }
        }
        Log.i("Result:", "Not Find known_qemu_drivers!");
        return false;
    }

    //检测模拟器上特有的几个文件
    public static Boolean CheckEmulatorFiles() {
        for (int i = 0; i < known_files.length; i++) {
            String file_name = known_files[i];
            File qemu_file = new File(file_name);
            if (qemu_file.exists()) {
                Log.v("Result:", "Find Emulator Files!");
                return true;
            }
        }
        Log.v("Result:", "Not Find Emulator Files!");
        return false;
    }

    // 检测模拟器默认的电话号码
    @SuppressLint("MissingPermission")
    public static Boolean CheckPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phonenumber = "";
        if (!PermissionsHelper.hasPermissions(context, READ_PHONE_STATE)) {
            PermissionsHelper.getInstance().requestPermission(context, READ_PHONE_STATE, null);
        } else {
            phonenumber = telephonyManager.getLine1Number();
        }

        for (String number : known_numbers) {
            if (number.equalsIgnoreCase(phonenumber)) {
                Log.v("Result:", "Find PhoneNumber!");
                return true;
            }
        }
        Log.v("Result:", "Not Find PhoneNumber!");
        return false;
    }

    //检测设备IDS 是不是 “000000000000000”
    @SuppressLint("MissingPermission")
    public static Boolean CheckDeviceIDS(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String device_ids = "";
        if (!PermissionsHelper.hasPermissions(context, READ_PHONE_STATE)) {
            PermissionsHelper.getInstance().requestPermission(context, READ_PHONE_STATE, null);
        } else {
            device_ids = telephonyManager.getDeviceId();
        }
        for (String know_deviceid : known_device_ids) {
            if (know_deviceid.equalsIgnoreCase(device_ids)) {
                Log.v("Result:", "Find ids: 000000000000000!");
                return true;
            }
        }
        Log.v("Result:", "Not Find ids: 000000000000000!");
        return false;
    }

    // 检测imsi id是不是“310260000000000”
    public static Boolean CheckImsiIDS(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        String imsi_ids = telephonyManager.getSubscriberId();

        for (String know_imsi : known_imsi_ids) {
            if (know_imsi.equalsIgnoreCase(imsi_ids)) {
                Log.v("Result:", "Find imsi ids: 310260000000000!");
                return true;
            }
        }
        Log.v("Result:", "Not Find imsi ids: 310260000000000!");
        return false;
    }

    //检测手机上的一些硬件信息
    public static Boolean CheckEmulatorBuild(Context context) {
        String BOARD = Build.BOARD;
        String BOOTLOADER = Build.BOOTLOADER;
        String BRAND = Build.BRAND;
        String DEVICE = Build.DEVICE;
        String HARDWARE = Build.HARDWARE;
        String MODEL = Build.MODEL;
        String PRODUCT = Build.PRODUCT;
        if (BOARD == "unknown" || BOOTLOADER == "unknown"
                || BRAND == "generic" || DEVICE == "generic"
                || MODEL == "sdk" || PRODUCT == "sdk"
                || HARDWARE == "goldfish") {
            Log.v("Result:", "Find Emulator by EmulatorBuild!");
            return true;
        }
        Log.v("Result:", "Not Find Emulator by EmulatorBuild!");
        return false;
    }


    /**
     * 判断是否是模拟器
     *
     * @return 模拟器
     */
    public static boolean isEmulator() {
        Log.e("DEBUG-WCL", "Build.FINGERPRINT: " + Build.FINGERPRINT
                + ", Build.MODEL: " + Build.MODEL
                + ", Build.MANUFACTURER: " + Build.MANUFACTURER
                + ", Build.BRAND: " + Build.BRAND
                + ", Build.DEVICE: " + Build.DEVICE
                + ", Build.PRODUCT: " + Build.PRODUCT);
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
//                || Build.FINGERPRINT.startsWith("unknown") // 魅族MX4: unknown
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }


    //检测手机运营商家
    /*public static boolean CheckOperatorNameAndroid(Context context) {
        String szOperatorName = ((TelephonyManager)
                context.getSystemService("phone")).getNetworkOperatorName();

        if (szOperatorName.toLowerCase().equals("android") == true) {
            Log.v("Result:", "Find Emulator by OperatorName!");
            return true;
        }
        Log.v("Result:", "Not Find Emulator by OperatorName!");
        return false;
    }*/
}