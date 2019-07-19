package com.callme.platform.base;

import android.app.Application;
import android.os.Bundle;

import com.callme.platform.listener.GlobalDataListener;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * <p>
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：Created by tgl on 2018/12/12.
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class BaseApplication extends Application implements GlobalDataListener {

    private static BaseApplication mInstance;

    public static BaseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }
}
