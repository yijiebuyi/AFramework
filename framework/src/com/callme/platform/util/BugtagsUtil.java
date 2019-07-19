package com.callme.platform.util;

import android.app.Activity;
import android.app.Application;
import android.view.MotionEvent;

import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tgl on 2018/1/5.
 * Describe: 只预发布接入使用
 */

public class BugtagsUtil {
    public static void onResume(Activity activity, int type) {
        if (type == 2) {
            Method method = getExternalMethod("onResume", new Class[]{Activity.class});
            if (method != null) {
                try {
                    method.invoke(null, activity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onPause(Activity activity, int type) {
        if (type == 2) {
            Method method = getExternalMethod("onPause", new Class[]{Activity.class});
            if (method != null) {
                try {
                    method.invoke(null, activity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onDispatchTouchEvent(Activity activity, MotionEvent event, int type) {
        if (type == 2) {
            Method method = getExternalMethod("onDispatchTouchEvent", new Class[]{Activity.class, MotionEvent.class});
            if (method != null) {
                try {
                    method.invoke(null, activity, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void start(String appKey, Application application, int event, int type) {
        if (type == 2) {
            Method method = null;
            Object optionsObject = null;
            Class<?> optionsClazz = null;
            try {
                Class<?> builderClazz = null;
                builderClazz = Class.forName("com.bugtags.library.BugtagsOptions$Builder");
                method = builderClazz.getMethod("enableCapturePlus", boolean.class);
                Object object = builderClazz.newInstance();
                method.invoke(object, true);
                Method methodOption = builderClazz.getMethod("build");
                optionsObject = methodOption.invoke(object);
                optionsClazz = optionsObject.getClass();
            } catch (Exception e) {

            }
            if (optionsClazz == null) {
                method = getExternalMethod("start", new Class[]{String.class, Application.class, int.class});
            } else {
                method = getExternalMethod("start", new Class[]{String.class, Application.class, int.class, optionsClazz});
            }
            if (method != null) {
                try {
                    if (optionsObject == null) {
                        method.invoke(null, appKey, application, event);
                    } else {
                        method.invoke(null, appKey, application, event, optionsObject);

                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else if (type == 3) {
            CrashReport.initCrashReport(application, "e0253c2ef4", false);
        }
    }

    public static Method getExternalMethod(String methodName, Class<?>[] paramsType) {
        Class<?> threadClazz = null;
        Method method = null;
        try {
            threadClazz = Class.forName("com.bugtags.library.Bugtags");
            method = threadClazz.getMethod(methodName, paramsType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }
}
