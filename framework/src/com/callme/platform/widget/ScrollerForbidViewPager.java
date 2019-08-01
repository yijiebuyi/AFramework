package com.callme.platform.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：禁止任何点击的ViewPager，由外部操作控制
 * <p>
 * <p>
 * 作者：zhanghui 2019/7/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ScrollerForbidViewPager extends ScrollerViewPager {
    public ScrollerForbidViewPager(Context context) {
        super(context);
    }

    public ScrollerForbidViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
