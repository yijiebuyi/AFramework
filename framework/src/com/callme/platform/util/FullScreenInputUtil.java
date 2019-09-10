package com.callme.platform.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.callme.platform.R;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：解决全屏界面时输入法遮挡输入控件，无法弹起问题
 * <p>
 * <p>
 * 作者：zhanghui 2019/9/10
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FullScreenInputUtil {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity) {
        new FullScreenInputUtil(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private FullScreenInputUtil(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow
                    //状态栏高度
                    - ResourcesUtil.getDimensionPixelSize(R.dimen.px60);
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        /*if(r.top==0){
            r.top= ResourcesUtil.getDimensionPixelSize(R.dimen.px60);//状态栏目的高度
        }*/
        return (r.bottom - r.top);
    }

}
