package com.callme.platform.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class AdapterGridView extends GridView {

	public AdapterGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AdapterGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdapterGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
