package com.callme.platform.util;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：Activity的管理类
 * 作者：huangyong
 * 创建时间：2018/4/4
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CmActivityManager {
    private List<WeakReference<Activity>> mActivityList;
    private static CmActivityManager mInstance;

    private CmActivityManager() {
        mActivityList = new ArrayList<WeakReference<Activity>>();
    }

    public synchronized static CmActivityManager getInstance() {
        if (mInstance == null) {
            mInstance = new CmActivityManager();
        }
        return mInstance;
    }

    /**
     * 添加Activity
     *
     * @param act
     */
    public void addActivity(Activity act) {
        if (mActivityList != null && act != null) {
            mActivityList.add(new WeakReference<Activity>(act));
        }
    }

    /**
     * 从列表中移除元素
     *
     * @param targetAct
     */
    public void removeActivity(Activity targetAct) {
        if (mActivityList == null || targetAct == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            if (act == targetAct) {
                it.remove();
            }
        }
    }

    /**
     * 结束单个(或多个)activity
     *
     * @param cls
     */
    public void finishActivity(Class cls) {
        if (mActivityList == null || cls == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            if (act.getClass() == cls) {
                it.remove();
                act.finish();
                act = null;
            }
        }
    }

    /**
     * 结束所有Activity
     *
     * @param excludeCls 不包含该Activity
     */
    public void finishAllActivity(Class excludeCls) {
        if (mActivityList == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            //跳过不含的activity
            if (act.getClass() == excludeCls) {
                continue;
            }

            it.remove();
            act.finish();
            act = null;
        }
    }

    /**
     * 获取顶部Activity
     *
     * @return
     */
    public Activity getTopActivity() {
        if (mActivityList == null || mActivityList.isEmpty()) {
            return null;
        }

        WeakReference<Activity> wf = mActivityList.get(mActivityList.size() - 1);
        return wf.get();
    }

    public int getActivitySize() {
        if (mActivityList == null || mActivityList.isEmpty()) {
            return 0;
        }

        return mActivityList.size();
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        exit(null);
    }

    /**
     * @param excludeCls
     * @see #finishAllActivity(Class)
     */
    public void exit(Class excludeCls) {
        try {
            finishAllActivity(excludeCls);
            //System.exit(0);
        } catch (Exception e) {
        }
    }
}
