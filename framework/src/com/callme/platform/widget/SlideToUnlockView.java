package com.callme.platform.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callme.platform.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by tgl on 2017/9/18.
 * Describe 滑动解锁
 **/
public class SlideToUnlockView extends RelativeLayout {
    private static int DISTANCE_LIMIT = 600;//滑动阈值
    private static int SPEED_LIMIT = 2000;
    private static int SCOPE = 50;
    private int slipDistance;//可滑动距离
    private static float DEFAULT_THRESHOLD = 0.25F;
    private static float THRESHOLD = DEFAULT_THRESHOLD;//滑动阈值比例:默认是0.5,即滑动超过父容器宽度的一半再松手就会触发
    protected Context mContext;
    private ImageView iv_slide;//滑块
    private TextView tv_hint;//提示文本
    private RelativeLayout rl_slide;//滑动view
    private boolean mIsUnLocked;//已经滑到最右边,将不再响应touch事件
    private CallBack mCallBack;//回调


    private int slideImageViewWidth;//滑块宽度
    private int slideImageViewHeight;//滑块高度
    private int slideImageViewResId;//滑块资源
    private int slideImageViewResIdAfter;//滑动到右边时,滑块资源id
    private int viewBackgroundResId;//root 背景
    private String textHint;//文本
    //    private int textSize;//单位是sp,只拿数值
    private int textColorResId;//颜色,@color
    private String textGravity;//颜色,@color
    private String orientation;//滑动方向
    private final String ORIENTATIONRIGHT = "right";
    private final String ORIENTATIONLEFT = "left";
    private int theEnd = 0;//判断是否滑动到头，0：默认，1：滑动到了，2：滑动过后
    private int slideDuration;//动画时长
    private int mActionDownX, mLastX, mSlidedDistance;
    private boolean isTouch;

    //private boolean mSlideViewSelected = false;
    private boolean mSlideInViewRange = false;
    private boolean mSlideReset = false;
    private VelocityTracker mVelocityTracker;
    private RectF mTempRect;

    public SlideToUnlockView(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        initView();
    }

    public SlideToUnlockView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.mContext = mContext;
        TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                R.styleable.SlideToUnlockView);
        init(mTypedArray);
        initView();
    }

    public SlideToUnlockView(Context mContext, AttributeSet attrs, int defStyleAttr) {
        super(mContext, attrs, defStyleAttr);
        this.mContext = mContext;

        TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                R.styleable.SlideToUnlockView);
        init(mTypedArray);

        initView();

    }

    /**
     * @param :[mTypedArray]
     * @return type:void
     * @method name:init
     * @des:获取自定义属性
     * @date 创建时间:2017/5/24
     * @author Chuck
     **/
    private void init(TypedArray mTypedArray) {

        slideImageViewWidth = (int) mTypedArray.getDimension(R.styleable.SlideToUnlockView_slideImageViewWidth, dp2px(getContext(), 50));
        slideImageViewHeight = (int) mTypedArray.getDimension(R.styleable.SlideToUnlockView_slideImageViewHeight, dp2px(getContext(), 50));
        slideImageViewResId = mTypedArray.getResourceId(R.styleable.SlideToUnlockView_slideImageViewResId, -1);
        slideImageViewResIdAfter = mTypedArray.getResourceId(R.styleable.SlideToUnlockView_slideImageViewResIdAfter, -1);
        viewBackgroundResId = mTypedArray.getResourceId(R.styleable.SlideToUnlockView_viewBackgroundResId, -1);
        textHint = mTypedArray.getString(R.styleable.SlideToUnlockView_textHint);
//        textSize = mTypedArray.getInteger(R.styleable.SlideToUnlockView_textSize, 7);
        textColorResId = mTypedArray.getColor(R.styleable.SlideToUnlockView_textColorResId, getResources().getColor(android.R.color.white));
        THRESHOLD = mTypedArray.getFloat(R.styleable.SlideToUnlockView_slideThreshold, DEFAULT_THRESHOLD);
        SCOPE = dp2px(mContext, 20);
        orientation = mTypedArray.getString(R.styleable.SlideToUnlockView_slideOrientation);
        textGravity = mTypedArray.getString(R.styleable.SlideToUnlockView_textGravity);
        slideDuration = mTypedArray.getInteger(R.styleable.SlideToUnlockView_slideDuration, 120);
        if (TextUtils.isEmpty(orientation)) {
            orientation = ORIENTATIONRIGHT;
        } else if (!TextUtils.equals(orientation, ORIENTATIONRIGHT) && TextUtils.equals(orientation, ORIENTATIONLEFT)) {
            new IllegalArgumentException("It can only be set to move left or right");
        }
        mTypedArray.recycle();
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (!inRangeOfView(iv_slide,ev)){
//            return true;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 初始化界面布局
     */
    protected void initView() {

        LayoutInflater.from(mContext).inflate(R.layout.layout_slide_to_unlock,
                this, true);
        rl_slide = (RelativeLayout) findViewById(R.id.rl_slide);
        iv_slide = (ImageView) findViewById(R.id.iv_slide);
        tv_hint = (TextView) findViewById(R.id.tv_hint);

        LayoutParams params = (LayoutParams) iv_slide.getLayoutParams();
        //获取当前控件的布局对象
        params.width = slideImageViewWidth;//设置当前控件布局的高度
        params.height = slideImageViewHeight;
        iv_slide.setLayoutParams(params);//将设置好的布局参数应用到控件中

        setImageDefault();
        if (viewBackgroundResId > 0) {
            rl_slide.setBackgroundResource(viewBackgroundResId);//rootView设置背景
        }

//        tv_hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp2px(getContext(), textSize));
        tv_hint.setTextColor(textColorResId);
        tv_hint.setText(TextUtils.isEmpty(textHint) ? "滑动解锁" : textHint);
        LayoutParams tv_hit_lp = (LayoutParams) tv_hint.getLayoutParams();
        tv_hit_lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        final LayoutParams ivSlideLp = (LayoutParams) iv_slide.getLayoutParams();
        ivSlideLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        if (isOrientation()) {
            if (TextUtils.equals("center", textGravity)) {
                tv_hit_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            } else {
                tv_hit_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            }
            ivSlideLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        } else {
            if (TextUtils.equals("center", textGravity)) {
                tv_hit_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            } else {
                tv_hit_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            }
            ivSlideLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        }

        tv_hint.setLayoutParams(tv_hit_lp);
        iv_slide.setLayoutParams(ivSlideLp);

        rl_slide.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                slipDistance = rl_slide.getMeasuredWidth() - iv_slide.getMeasuredWidth();
                DISTANCE_LIMIT = (int) (slipDistance * THRESHOLD);//默认阈值是控件宽度的一半
                rl_slide.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        setClickable(true);
        //添加滑动监听
        rl_slide.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下时记录纵坐标
                        if (mIsUnLocked) {//滑块已经在解锁状态则不处理touch
                            return true;
                        }
                        mSlideInViewRange = true;
                        mSlideReset = false;

                        initVelocityTracker(event);
                        isTouch = true;
                        mLastX = (int) event.getRawX();//最后一个action时x值
                        mActionDownX = mLastX;//按下的瞬间x
                        if (!iv_slide.isSelected()) {
                            iv_slide.setSelected(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE://上滑才处理,如果用户一开始就下滑,则过掉不处理
                        if (mIsUnLocked) {//滑块已经在解锁状态则不处理touch
                            return true;
                        }
                        computeVelocity(event);

                        if (mSlideInViewRange) {
                            mSlideInViewRange = inThisOfView(SlideToUnlockView.this, event);
                        }

                        if (!mSlideInViewRange) {
                            if (!mSlideReset) {
                                resetView();
                                mSlideReset = true;
                            }
                            break;
                        }

                        final MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
                        int left = params.leftMargin;
                        int top = params.topMargin;
                        int right = params.rightMargin;
                        int bottom = params.bottomMargin;
                        int leftNew;
                        int rightNew;
                        if (isOrientation()) {//向右滑动解锁
                            int dX = (int) event.getRawX() - mLastX;
                            mSlidedDistance = (int) event.getRawX() - mActionDownX;
                            leftNew = left + dX;
                            rightNew = right - dX;

                            if (slipDistance <= mSlidedDistance) {
                                if (theEnd == 0) {
                                    theEnd = 1;
                                } else if (theEnd == 1) {
                                    theEnd = 2;
                                }
                            } else {
                                theEnd = 0;
                            }
                        } else {//向左滑动解锁
                            int dX = mLastX - (int) event.getRawX();
                            mSlidedDistance = mActionDownX - (int) event.getRawX();
                            leftNew = left - dX;
                            rightNew = right + dX;
                            if (slipDistance <= mSlidedDistance) {
                                if (theEnd == 0) {
                                    theEnd = 1;
                                } else if (theEnd == 1) {
                                    theEnd = 2;
                                }
                            } else {
                                theEnd = 0;
                            }
                        }
                        if (theEnd == 2) {
                            return true;
                        }
                        if (mSlidedDistance > 0) {//直接通过margin实现滑动
                            params.setMargins(leftNew, top, rightNew, bottom);
                            v.setLayoutParams(params);
                            //回调
                            if (mCallBack != null) {
                                mCallBack.onSlide(tv_hint, mSlidedDistance);
                            }
                            mLastX = (int) event.getRawX();
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isTouch = false;
                        //Log.i("aaa", "======" + mSlideViewSelected + "  " + mSlideInViewRange);
                        if (mSlideInViewRange) {
                            actionUp(v);
                        } else {
                            slideIfExceedSpeedLimit(v);
                        }
                        break;
                }
                return true;
            }
        });


    }

    private boolean actionUp(View v) {
        if (mIsUnLocked) {//滑块已经在解锁状态则不处理touch
            return true;
        }
        //记录本次移动距离，为计算下次移动距离
        if (Math.abs(mSlidedDistance) > DISTANCE_LIMIT) {
            if (isOrientation()) {
                scrollToRight(v);//右边
            } else {
                scrollToOrientationLeftEnd(v);//左边
            }
        } else {
            if (isOrientation()) {
                scrollToLeft(v);//左边
            } else {
                scrollToOrientationLeftStart(v);//往右边回到起点
            }
        }
        return false;
    }

    /**
     * 如果超过速度阀值，滑动
     *
     * @param v
     */
    private void slideIfExceedSpeedLimit(View v) {
        if (mVelocityTracker != null) {
            float xVelocity = mVelocityTracker.getXVelocity();
            //Log.i("aaa", "xVelocity" + xVelocity);
            if (Math.abs(xVelocity) > SPEED_LIMIT) {
                if (isOrientation()) {
                    scrollToRight(v);//右边
                } else {
                    scrollToOrientationLeftEnd(v);//左边
                }
            }
        }
    }

    /**
     * 点击点是否在view上
     *
     * @param view
     * @param ev
     * @return
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        if (mTempRect == null) {
            mTempRect = new RectF();
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        mTempRect.set(x, y, x + view.getWidth(), y + view.getHeight());
        mTempRect.inset(-SCOPE, -SCOPE);
        return mTempRect.contains(ev.getRawX(), ev.getRawY());
    }

    private boolean inThisOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int y = location[1];
        return !(ev.getRawY() < (y - SCOPE)) && !(ev.getRawY() > (y + view.getHeight() + SCOPE));
    }

//    /**
//     * @param :[mSlidedDistance]
//     * @return type:void
//     * @method name:resetTextViewAlpha
//     * @des: 重置提示文本的透明度
//     * @date 创建时间:2017/5/24
//     * @author Chuck
//     **/
//    private void resetTextViewAlpha(int distance) {
//        if (Math.abs(distance) >= Math.abs(DISTANCE_LIMIT)) {
//            tv_hint.setAlpha(0.0f);
//        } else {
//            tv_hint.setAlpha(1.0f - Math.abs(distance) * 1.0f / Math.abs(DISTANCE_LIMIT));
//        }
//    }


    /**
     * @param :[v]
     * @return type:void
     * @method name:scrollToLeft
     * @des: 滑动未到阈值时松开手指, 弹回到最左边
     * @date 创建时间:2017/5/24
     * @author Chuck
     **/
    private void scrollToLeft(final View v) {

        final MarginLayoutParams params1 = (MarginLayoutParams) v.getLayoutParams();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rl_slide, "translationX", ViewHelper.getTranslationX(v), -params1.leftMargin);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                iv_slide.setSelected(false);
                if (isOrientation()) {//向右滑动，左边是起点
                    tv_hint.setAlpha(1.0f);
                    mIsUnLocked = false;
                    MarginLayoutParams para = (MarginLayoutParams) v.getLayoutParams();
                    para.setMargins(para.leftMargin, para.topMargin, para.rightMargin - params1.leftMargin, para.bottomMargin);
                    v.setLayoutParams(para);
                    setImageDefault();
                } else {
                    mIsUnLocked = true;
                    if (slideImageViewResIdAfter > 0) {
                        iv_slide.setImageResource(slideImageViewResIdAfter);//滑块imagview设置资源
                    }
                }
                mSlidedDistance = 0;
                if (mCallBack != null) {
                    mCallBack.onSlide(tv_hint, mSlidedDistance);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setDuration(slideDuration);
        objectAnimator.start();
    }


    /**
     * @param :[v]
     * @return type:void
     * @method name:scrollToRight
     * @des:滑动到右边,并触发回调
     * @date 创建时间:2017/5/24
     * @author Chuck
     **/
    private void scrollToRight(final View v) {

        final MarginLayoutParams params1 = (MarginLayoutParams) v.getLayoutParams();
        //移动到最右端  移动的距离是 父容器宽度
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rl_slide, "translationX", ViewHelper.getTranslationX(v), slipDistance - mSlidedDistance + ViewHelper.getTranslationX(v));
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                iv_slide.setSelected(false);
                if (isOrientation()) {
                    mIsUnLocked = true;
                    if (slideImageViewResIdAfter > 0) {
                        iv_slide.setImageResource(slideImageViewResIdAfter);//滑块imagview设置资源
                    }
                } else {//向左滑动，右边是起点
                    tv_hint.setAlpha(1.0f);
                    MarginLayoutParams para = (MarginLayoutParams) v.getLayoutParams();
                    para.setMargins(para.leftMargin, para.topMargin, para.rightMargin - params1.leftMargin, para.bottomMargin);
                    v.setLayoutParams(para);
                    setImageDefault();
                }
                mSlidedDistance = 0;
                //回调
                if (mCallBack != null) {
                    mCallBack.onUnlocked(tv_hint);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setDuration(slideDuration);
        objectAnimator.start();
    }


    /**
     * 向左滑动解锁,松开滑动到结束点
     *
     * @param v
     */
    private void scrollToOrientationLeftEnd(final View v) {
        final MarginLayoutParams params1 = (MarginLayoutParams) v.getLayoutParams();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rl_slide, "translationX", ViewHelper.getTranslationX(v), mSlidedDistance - slipDistance + ViewHelper.getTranslationX(v));
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                iv_slide.setSelected(false);
                mIsUnLocked = true;
                tv_hint.setAlpha(0.0f);
                if (slideImageViewResIdAfter > 0) {
                    iv_slide.setImageResource(slideImageViewResIdAfter);//滑块imagview设置资源
                }
                mSlidedDistance = 0;
                if (mCallBack != null) {
                    mCallBack.onSlide(tv_hint, mSlidedDistance);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setDuration(slideDuration);
        objectAnimator.start();
    }

    /**
     * 向左滑动解锁,松开滑动到起点
     **/
    private void scrollToOrientationLeftStart(final View v) {

        final MarginLayoutParams params1 = (MarginLayoutParams) v.getLayoutParams();
        //移动到最右端  移动的距离是 父容器宽度
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rl_slide, "translationX", ViewHelper.getTranslationX(v), mSlidedDistance + ViewHelper.getTranslationX(v));
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                iv_slide.setSelected(false);
                //向左滑动，右边是起点
                tv_hint.setAlpha(1.0f);
                setImageDefault();
                mSlidedDistance = 0;
                //回调
                if (mCallBack != null) {
                    mCallBack.onUnlocked(tv_hint);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setDuration(slideDuration);
        objectAnimator.start();
    }

    public void resetView() {
        mIsUnLocked = false;
        setImageDefault();
        scrollToLeft(rl_slide);
    }

    private void setImageDefault() {
        /**
         * @method name:setImageDefault
         * @des: 设置默认图片
         * @param :[]
         * @return type:void
         * @date 创建时间:2017/5/25
         * @author Chuck
         **/

        if (slideImageViewResId > 0) {
            iv_slide.setImageResource(slideImageViewResId);//滑块imagview设置资源
        }
    }

    public interface CallBack {
        /**
         * @param hintView 滑块上显示的文本view
         * @param distance 滑动距离
         */
        void onSlide(TextView hintView, int distance);//右滑距离回调

        /**
         * 解锁回调
         *
         * @param hintView 滑块上显示的文本view
         */
        void onUnlocked(TextView hintView);//滑动到了目标位置,事件回调
    }

    public TextView getTv_hint() {
        return tv_hint;
    }

    public void setTvHintTxt(String txt) {
        if (tv_hint != null) {
            tv_hint.setText(txt);
        }
    }

    public CallBack getmCallBack() {
        return mCallBack;
    }

    public void setmCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    private boolean isOrientation() {
        return TextUtils.equals(orientation, "right");
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseVelocityTracker();
    }

    private void initVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
        mVelocityTracker.addMovement(event);
    }

    private void computeVelocity(MotionEvent event) {
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
            mVelocityTracker.computeCurrentVelocity(1000);
        }
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
