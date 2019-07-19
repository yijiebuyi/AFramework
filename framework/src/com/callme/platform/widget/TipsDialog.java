package com.callme.platform.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.callme.platform.R;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：提示消息dialog
 * 作者：huangyong
 * 创建时间：2018/8/9
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TipsDialog extends DialogFragment implements
        DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener,
        View.OnClickListener {

    protected Context mContext;
    protected boolean mDestroy = false;
    /**
     * 当对话框dismiss的时候，是否需要关闭对应的Activity
     */
    private boolean mNeedCloseActivityOnDismiss = false;
    private View.OnClickListener mClickListener;

    private String mTitleStr;
    private CharSequence mMsgStr;
    private String[] mListMsgStr;
    private String mConfirmBtnStr;

    private TextView mCheckTipTv;
    private boolean mCheck;
    private String mCheckMsg;
    private int mCheckDrawId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDestroy = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDestroy = true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, R.style.DialogStyle);
        dialog.setCanceledOnTouchOutside(true);

        View view = onCreateView();
        Window dialogWindow = dialog.getWindow();

        //set attributes
        dialogWindow.setContentView(view);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        w = w - (int) (70 * dm.density); //70dip
        dialogWindow.setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        //set params
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.setAttributes(params);

        onViewCreated(view);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });

        setTipsText(view);
        bindMsg(view);
        bindListMsg(view);
        setCheckTipText(view);

        dialog.setOnCancelListener(this);
        dialog.setOnDismissListener(this);
        dialog.findViewById(R.id.confirm_know).setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity act = null;
        if (mNeedCloseActivityOnDismiss && (act = getActivity()) != null) {
            act.finish();
        }
    }

    protected View onCreateView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.base_tip_dialog_layout, null);
    }

    protected void onViewCreated(View view) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_know) {
            if (mClickListener != null) {
                mClickListener.onClick(v);
            }
            dismiss();
        }
    }

    public void setTitle(String title) {
        mTitleStr = title;
    }

    public void setMessage(CharSequence msg) {
        mMsgStr = msg;
    }

    public void setListMessage(String[] msgs) {
        mListMsgStr = msgs;
    }

    public void setNeedCloseActivityOnDismiss(boolean needCloseActivity) {
        mNeedCloseActivityOnDismiss = needCloseActivity;
    }

    public void setOnclickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }


    public void setConfirmBtnText(String btnText) {
        mConfirmBtnStr = btnText;
    }

    private void setTipsText(View root) {
        TextView t = (TextView) root.findViewById(R.id.title);
        TextView c = (TextView) root.findViewById(R.id.confirm_know);

        if (!TextUtils.isEmpty(mTitleStr)) {
            t.setText(mTitleStr);
        }

        if (!TextUtils.isEmpty(mConfirmBtnStr)) {
            c.setText(mConfirmBtnStr);
        }
    }

    /**
     * 绑定消息
     *
     * @param root
     */
    private void bindMsg(View root) {
        TextView m = (TextView) root.findViewById(R.id.message);
        if (!TextUtils.isEmpty(mMsgStr)) {
            m.setText(mMsgStr);
        }
    }

    /**
     * 绑定消息
     *
     * @param root
     */
    private void bindListMsg(View root) {
        ListView lv = (ListView) root.findViewById(R.id.list_message);
        if (mListMsgStr != null && mListMsgStr.length > 0) {
            lv.setVisibility(View.VISIBLE);
            SimpleMsgAdapter adapter = new SimpleMsgAdapter();
            lv.setAdapter(adapter);
        }
    }

    private void setCheckTipText(View root) {
        mCheckTipTv = (TextView) root.findViewById(R.id.check_tip);
        mCheckTipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckTipTv.setSelected(!mCheckTipTv.isSelected());
            }
        });
        if (mCheckTipTv != null && !TextUtils.isEmpty(mCheckMsg)) {
            mCheckTipTv.setVisibility(View.VISIBLE);
            mCheckTipTv.setSelected(mCheck);
            mCheckTipTv.setText(mCheckMsg);
            mCheckTipTv.setCompoundDrawablesWithIntrinsicBounds(mCheckDrawId, 0, 0, 0);
        }
    }

    public void setCheckTip(boolean check, String checkMsg, int drawId) {
        mCheck = check;
        mCheckMsg = checkMsg;
        mCheckDrawId = drawId;
    }

    public boolean isCheck() {
        return mCheckTipTv != null && mCheckTipTv.isSelected();
    }

    public class SimpleMsgAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mListMsgStr != null ? mListMsgStr.length : 0;
        }

        @Override
        public Object getItem(int position) {
            return mListMsgStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = initHolder();
            }
            holder = (Holder) convertView.getTag();

            bindData(holder, position);
            return convertView;
        }

        private View initHolder() {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_tip_list_msg, null);
            Holder h = new Holder();
            h.msg = (TextView) v.findViewById(R.id.msg_tv);
            v.setTag(h);
            return v;
        }

        private void bindData(Holder h, int position) {
            h.msg.setText(mListMsgStr[position]);
        }

        private class Holder {
            public TextView msg;
        }
    }
}

