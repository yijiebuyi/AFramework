package com.callme.platform.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.common.dialog.LoadingProgressDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
	protected LinearLayout mContainer;
	private View mFragmentFailed;
	private View mFragmentEmpty;
	protected Context mContext;

	protected Unbinder mBinder;

	public abstract View getContainerView();

	public abstract void initData();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//register eventbus
		if (needRegisterEventBus()) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.base_fragment, null);
		mContainer = (LinearLayout) view.findViewById(R.id.base_fragment_container);
		mFragmentFailed = view.findViewById(R.id.base_fragment_failed);
		mFragmentEmpty = view.findViewById(R.id.base_fragment_empty);

		if (needSetBackground()) {
			view.setBackgroundResource(R.color.common_grey_bg_color);
		}

		initDefault();
		View containerView = getContainerView();
		if (!delayBind()){
			mBinder = ButterKnife.bind(this, containerView);
		}
		addContainerView(containerView);
		initData();
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	private void initDefault() {
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mFragmentFailed.setLayoutParams(params);
		mContainer.setLayoutParams(params);
	}

	public final void addContainerView(View view) {
		if (view != null) {
			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);
			mContainer.removeAllViews();
			mContainer.addView(view);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mBinder != null) {
			mBinder.unbind();
		}
	}

	protected boolean delayBind(){
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		//unregister eventbus
		if (needRegisterEventBus() && EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	/**
	 * 显示view形式的加载失败提示
	 * 
	 * @param listener
	 *            数据加载失败的点击事件
	 */
	public final void showFailedView(OnClickListener listener) {
		mFragmentFailed.setVisibility(View.VISIBLE);
		mFragmentFailed.setOnClickListener(listener);
	}

	/**
	 * 显示fragment空数据页面提示 如果三个参数中的某些参数为0，则该项用默认图或文字
	 * 
	 * @param imgSrcId
	 *            提示图片ID
	 * @param titleId
	 *            提示文字黑色大字
	 * @param desId
	 *            提示文字描述文字
	 */
	public final void showEmptyView(int imgSrcId, int titleId, int desId) {
		if (imgSrcId > 0) {
			((ImageView) mFragmentEmpty.findViewById(R.id.empty_icon)).setImageResource(imgSrcId);
		}
		TextView title = (TextView) mFragmentEmpty.findViewById(R.id.empty_title);
		if (titleId > 0) {
			title.setText(titleId);
		} else {
			title.setVisibility(View.GONE);
		}
		if (desId > 0) {
			TextView desp = (TextView) mFragmentEmpty.findViewById(R.id.empty_tip);
			desp.setText(desId);
		}
		mFragmentEmpty.setVisibility(View.VISIBLE);
		mFragmentFailed.setVisibility(View.GONE);
	}

	/**
	 * 显示对话框形式的加载提示
	 * <p>
	 * 数据访问的handler
	 *
	 * @param cancelable 是否可以取消请求
	 */
	public final void showProgressDialog(boolean cancelable) {
		Activity activity = getActivity();
		if (activity instanceof BaseActivity) {
			((BaseActivity) activity).showProgressDialog(cancelable);
		}
	}

	/**
	 * 取消加载对话框
	 */
	public final void closeProgressDialog() {
		Activity activity = getActivity();
		if (activity instanceof BaseActivity) {
			((BaseActivity) activity).closeProgressDialog();
		}
	}

	/**
	 * 是否需要注册eventBus
	 *
	 * @return 默认不需要注册eventBus
	 */
	protected boolean needRegisterEventBus() {
		return false;
	}

	/**
	 * 是否需要设置背景颜色
	 *
	 * @return
	 */
	protected boolean needSetBackground() {
		return false;
	}
}
