package com.callme.platform.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.util.LogUtil;
import com.callme.platform.util.ResourcesUtil;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：通用dialog
 * 作者：mikeyou
 * 创建时间：2017-10-6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class ThemeCDialog extends Dialog implements OnItemClickListener {

    public static final String TAG = "ThemeCDialog";
    /**
     * 普通文字对话框
     */
    protected final int STYLE_WORD_TYPE = 100;
    /**
     * 列表选项对话框
     */
    protected final int STYLE_LIST_TYPE = 102;

    public static final int CUSTOM_VIEW = 105;

    // 列表框类型的子类型
    public static final int LIST_SUB_STYLE_NORMAL = 200;
    // 靠底部，并有取消的选项
    public static final int LIST_SUB_STYLE_AGLIN_BOTTOM = 201;

    private int currentStyle = STYLE_WORD_TYPE;
    private int subStyle = LIST_SUB_STYLE_NORMAL;

    private LinearLayout mContainer;
    private ScrollView mContentSl;
    private int mBgResId = -1;
    private RelativeLayout mTopContainerLl;
    private Spanned message;
    private Spanned title;
    protected Context mContext;
    private TextView mTitle;
    private View mLineTitle;
    private TextView mMessageWithTitle;
    private ListView mListView;
    private Button btnOK;
    private Button btnCancel;
    private View btnDivider;
    private String[] listTexts;
    private int[] listImages;
    private DialogItemOnClick listItemClickListener;
    private DialogOnClickListener positiveListener;
    private DialogOnClickListener negativeListener;
    private int positiveText;
    private int negativeText;
    private View btnContent;
    private TextView mBottomCancel;
    private int mMsgGravity = -1;
    protected View mCustomView;
    private ImageView mCloseIv;
    private LinearLayout mCustomLl;
    private View.OnClickListener mCloseViewListener;

    private boolean mCancellable = true;

    private boolean mHeightLimit = true;

    /**
     * 是否显示title
     *
     * @param showTitle
     */
    public void isShowTitle(boolean showTitle) {
        this.mHasTitle = showTitle;
    }

    //是否显示title
    private boolean mHasTitle = true;

    public interface DialogOnClickListener {
        public void onClick();
    }

    public interface DialogItemOnClick {
        public void onItemClick(int position, String text);
    }

    public ThemeCDialog(Context ctx, View customView) {
        super(ctx, R.style.CommonDialog);
        if (customView == null) {
            LogUtil.d(TAG, "customView is null");
        }

        mContext = ctx;
        mCustomView = customView;
        currentStyle = CUSTOM_VIEW;
    }

    /**
     * 普通文字对话框
     */
    public ThemeCDialog(Context context, int msg, int title) {
        super(context, R.style.CommonDialog);
        if (msg <= 0) {
            try {
                throw (new Throwable("message is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_WORD_TYPE, title, msg, 0, null);
        }
    }

    /**
     * 普通文字对话框
     */
    public ThemeCDialog(Context context, int msg, String title) {
        super(context, R.style.CommonDialog);
        if (msg <= 0) {
            try {
                throw (new Throwable("message is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_WORD_TYPE, title, msg, 0, null);
        }
    }

    /**
     * 普通文字对话框
     */
    public ThemeCDialog(Context context, Spanned msg, int title) {
        super(context, R.style.CommonDialog);
        if (TextUtils.isEmpty(msg)) {
            try {
                throw (new Throwable("message is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_WORD_TYPE, title, msg, 0, null);
        }
    }

    /**
     * 普通文字对话框
     */
    public ThemeCDialog(Context context, CharSequence msg, int title) {
        super(context, R.style.CommonDialog);
        if (TextUtils.isEmpty(msg)) {
            try {
                throw (new Throwable("message is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_WORD_TYPE, title, new SpannedString(msg), 0,
                    null);
        }
    }

    /**
     * 普通文字对话框
     */
    public ThemeCDialog(Context context, CharSequence msg, String title) {
        super(context, R.style.CommonDialog);
        if (TextUtils.isEmpty(msg)) {
            try {
                throw (new Throwable("message is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_WORD_TYPE, title, new SpannedString(msg), 0,
                    null);
        }
    }

    /**
     * 列表选项对话框
     */
    public ThemeCDialog(Context context, int title, int txtsArrayId, int[] images) {
        super(context, R.style.CommonDialog);
        if (txtsArrayId <= 0) {
            try {
                throw (new Throwable("list item text is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_LIST_TYPE, title, 0, txtsArrayId, images);
        }
    }

    /**
     * 列表选项对话框
     */
    public ThemeCDialog(Context context, int title, String[] txtsArray,
                        int[] images) {
        super(context, R.style.CommonDialog);
        if (txtsArray == null) {
            try {
                throw (new Throwable("list item text is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_LIST_TYPE, title, 0, txtsArray, images);
        }
    }

    /**
     * 列表选项对话框
     */
    public ThemeCDialog(Context context, int title, int txtsArrayId,
                        int[] images, int subType) {
        super(context, R.style.CommonDialog);
        this.subStyle = subType;
        if (txtsArrayId <= 0) {
            try {
                throw (new Throwable("list item text is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_LIST_TYPE, title, 0, txtsArrayId, images);
        }
    }

    /**
     * 列表选项对话框
     */
    public ThemeCDialog(Context context, int title, String[] txtsArray,
                        int[] images, int subType) {
        super(context, R.style.CommonDialog);
        this.subStyle = subType;
        if (txtsArray == null) {
            try {
                throw (new Throwable("list item text is null"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            init(context, STYLE_LIST_TYPE, title, 0, txtsArray, images);
        }
    }

    private void init(Context context, int styleId, int titleId, int msgId,
                      String[] txtsArray, int[] images) {
        mContext = context;
        currentStyle = styleId;
        listImages = images;
        listTexts = txtsArray;
        if (msgId != 0) {
            message = new SpannedString(mContext.getString(msgId));
        }
        if (titleId != 0) {
            this.title = new SpannedString(mContext.getString(titleId));
        }
    }

    private void init(Context context, int style, String title, int msg,
                      int txtsArrayId, int[] images) {
        mContext = context;
        currentStyle = style;
        listImages = images;
        if (!TextUtils.isEmpty(title)) {
            this.title = new SpannedString(title);
        }
        if (msg != 0) {
            message = new SpannedString(mContext.getString(msg));
        }
        if (txtsArrayId != 0) {
            listTexts = mContext.getResources().getStringArray(txtsArrayId);
        }
    }

    private void init(Context context, int style, int title, Spanned msg,
                      int txtsArrayId, int[] images) {
        mContext = context;
        currentStyle = style;
        message = msg;
        if (title > 0) {
            this.title = new SpannedString(mContext.getString(title));
        }
        listImages = images;
        if (txtsArrayId != 0) {
            listTexts = mContext.getResources().getStringArray(txtsArrayId);
        }
    }

    private void init(Context context, int style, String title, Spanned msg,
                      int txtsArrayId, int[] images) {
        mContext = context;
        currentStyle = style;
        message = msg;
        if (!TextUtils.isEmpty(title)) {
            this.title = new SpannedString(title);
        }
        listImages = images;
        if (txtsArrayId != 0) {
            listTexts = mContext.getResources().getStringArray(txtsArrayId);
        }
    }

    private void init(Context context, int styleId, int titleId, int msgId,
                      int txtsArrayId, int[] images) {
        mContext = context;
        currentStyle = styleId;
        listImages = images;
        if (txtsArrayId > 0) {
            listTexts = mContext.getResources().getStringArray(txtsArrayId);
        }
        if (msgId != 0) {
            message = new SpannedString(mContext.getString(msgId));
        }
        if (txtsArrayId != 0) {
            listTexts = mContext.getResources().getStringArray(txtsArrayId);
        }
        if (titleId != 0) {
            this.title = new SpannedString(mContext.getString(titleId));
        }
    }

    public void setHeightLimit(boolean limit) {
        mHeightLimit = limit;
    }

    public void setBgRes(int resId) {
        mBgResId = resId;
        if (mContainer != null) {
            mContainer.setBackgroundResource(resId);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_layout_c_common_dialog);
        /**
         * 设置对话框属性
         */
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        int width = dialogWindow.getWindowManager().getDefaultDisplay()
                .getWidth();

        if (LIST_SUB_STYLE_AGLIN_BOTTOM == subStyle) {
            dialogWindow.setGravity(Gravity.BOTTOM);
        } else {
//            width -= 2 * getContext().getResources().getDimensionPixelSize(
//                    R.dimen.px30);
            width = (int) (ResourcesUtil.getScreenWidth() * 4.14 / 5);
            dialogWindow.setGravity(Gravity.CENTER);
        }

        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        setCanceledOnTouchOutside(mCancellable);
        initView();
        setInfo();
        if (mBgResId != -1) {
            if (mContainer != null) {
                mContainer.setBackgroundResource(mBgResId);
            }
        }

        View v = dialogWindow.getDecorView();
        v.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int height = v.getMeasuredHeight();
        if (mHeightLimit && height > ResourcesUtil.getScreenHeight() * 2 / 3) {
            height = ResourcesUtil.getScreenHeight() * 2 / 3;
            if (mTopContainerLl != null) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, height);
                mTopContainerLl.setLayoutParams(lp);
            }
        }
        dialogWindow.setLayout(width, LayoutParams.WRAP_CONTENT);

    }

    public void setCancellable(boolean cancel) {
        mCancellable = cancel;
    }

    protected void setInfo() {
        setTitleStatus();
        setBtnStatus();
        switch (currentStyle) {
            case STYLE_WORD_TYPE:
                if (mMsgGravity >= 0) {
                    mMessageWithTitle.setGravity(mMsgGravity);
                }
                break;
            case STYLE_LIST_TYPE:
                mListView.setAdapter(new DialogListAdapter(mContext, this,
                        listTexts, listImages,
                        subStyle != LIST_SUB_STYLE_AGLIN_BOTTOM));
                mListView.setOnItemClickListener(this);
                mListView.setVisibility(View.VISIBLE);
                if (subStyle == LIST_SUB_STYLE_AGLIN_BOTTOM) {
                    mContainer.setBackgroundDrawable(new BitmapDrawable());
                    mContainer.setBackgroundColor(ResourcesUtil
                            .getColor(R.color.common_grey_bg_color));
                    mBottomCancel.setVisibility(View.VISIBLE);
                    mBottomCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancel();
                        }

                    });

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, getContext().getResources()
                            .getDimensionPixelOffset(R.dimen.px30), 0, 0);

                    mListView.setLayoutParams(lp);
                    setCanceledOnTouchOutside(true);
                }
                break;
            case CUSTOM_VIEW: {
                if (mCustomView != null) {
                    mCustomLl.setVisibility(View.VISIBLE);
                    mCustomLl.addView(mCustomView);
                }
            }
            default:
                break;
        }

    }

    protected void setTitleStatus() {
        if (mHasTitle) {
            mTitle.setVisibility(View.VISIBLE);
            mLineTitle.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }
        } else {
            mTitle.setVisibility(View.GONE);
            mLineTitle.setVisibility(View.GONE);
        }
        if (mCustomView == null) {
            mMessageWithTitle.setText(message);
            mContentSl.setVisibility(View.VISIBLE);
        } else {
            mContentSl.setVisibility(View.GONE);
        }
    }

    protected void setBtnStatus() {
        if (positiveText > 0 && positiveListener != null) {
            btnOK.setText(ResourcesUtil.getString(positiveText));
            btnOK.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    positiveListener.onClick();
                }
            });
            btnOK.setVisibility(View.VISIBLE);
        }
        if (negativeText > 0 && negativeListener != null) {
            btnCancel.setText(ResourcesUtil.getString(negativeText));
            btnCancel
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            negativeListener.onClick();
                        }
                    });
            btnCancel.setVisibility(View.VISIBLE);
        }
        if ((positiveText > 0 && positiveListener != null)
                && (negativeText > 0 && negativeListener != null)) {
            btnContent.setVisibility(View.VISIBLE);
            btnDivider.setVisibility(View.VISIBLE);
        } else if ((positiveText > 0 && positiveListener != null)
                || (negativeText > 0 && negativeListener != null)) {
            btnContent.setVisibility(View.VISIBLE);
        } else {
            btnContent.setVisibility(View.GONE);
        }
    }

    public void setPositiveButton(int textId,
                                  final DialogOnClickListener listener) {
        if (textId <= 0 || listener == null) {
            try {
                throw (new Throwable(
                        "please set button text and OnClickListener"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            positiveText = textId;
            positiveListener = listener;
        }
    }

    public void setNegativeButton(int textId,
                                  final DialogOnClickListener listener) {
        if (textId <= 0 || listener == null) {
            try {
                throw (new Throwable(
                        "please set button text and OnClickListener"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            negativeText = textId;
            negativeListener = listener;
        }
    }

    public void setItemClickListener(DialogItemOnClick listener) {
        listItemClickListener = listener;
    }

    public void initView() {
        mTopContainerLl = (RelativeLayout) findViewById(R.id.dialog_top);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mMessageWithTitle = (TextView) findViewById(R.id.dialog_msg);
        mListView = (ListView) findViewById(R.id.dialog_list);
        btnCancel = (Button) findViewById(R.id.dialog_cancel);
        btnOK = (Button) findViewById(R.id.dialog_ok);
        btnDivider = findViewById(R.id.btn_divider);
        mTitle = (TextView) findViewById(R.id.dialog_title);
        mLineTitle = (View) findViewById(R.id.line_title);
        btnContent = findViewById(R.id.dialog_btn_content);
        mBottomCancel = (TextView) findViewById(R.id.image_chooser_dialog_cancel);
        mContentSl = (ScrollView) findViewById(R.id.dialog_content_sl);
        mCloseIv = (ImageView) findViewById(R.id.close_dialog);
        mCustomLl = (LinearLayout) findViewById(R.id.custom_ll);
        if (mCloseViewListener != null) {
            mCloseIv.setVisibility(View.VISIBLE);
            mCloseIv.setOnClickListener(mCloseViewListener);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        cancel();
        listItemClickListener.onItemClick(position, listTexts[position]);
    }

    public void setMsgAlign(int gravity) {
        mMsgGravity = gravity;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mCancellable && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T t = super.findViewById(id);
        if (t == null && mCustomView != null) {
            t = mCustomView.findViewById(id);
        }
        return t;
    }

    public void setCloseViewListener(View.OnClickListener listener) {
        mCloseViewListener = listener;
    }
}
