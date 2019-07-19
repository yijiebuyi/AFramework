package com.callme.platform.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.util.ResourcesUtil;
import com.callme.platform.util.TimeUtil;
import com.callme.platform.widget.datapicker.DataPicker;
import com.callme.platform.widget.datapicker.DateWheelPicker;
import com.callme.platform.widget.datapicker.view.MultipleTextWheelPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2018/8/16
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DateQueryView extends FrameLayout implements View.OnClickListener {

    private TextView mYear;
    private OnDateQueryListener mListener;
    private OnNotDataListener mNotDataListener;
    private int mSelectedYear;
    private int YEAR_STAR = 2016;//公司2016年起运营
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mTotalWeek;

    private boolean mShowWeek;
    private boolean mShowDay;

    private List<String> mPicked;
    private Date mPickedDate;
    //开始月份
    private int MONTH_START;
    //开始日期
    private int DAY_START;

    public DateQueryView(@NonNull Context context) {
        super(context);
        init();
    }

    public DateQueryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateQueryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public DateQueryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_year_view, this);
        mYear = findViewById(R.id.year_name);
        mYear.setOnClickListener(this);
        mSelectedYear = TimeUtil.getCurrentYear();
        mCurrentYear = TimeUtil.getCurrentYear();
        mCurrentMonth = TimeUtil.getCurrentMonth();
        mPicked = new ArrayList<>();
        mPickedDate = new Date(System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
        List<String> years = getYears();
        List<ArrayList<String>> sec = null;
        if (mShowWeek) {
            List<String> weeks = getWeeks();
            if (weeks == null || weeks.size() == 0) {
                if (mNotDataListener != null) {
                    mNotDataListener.onListener();
                }
                return;
            }
            if (mPicked.isEmpty()) {
                mPicked.add(weeks.get(weeks.size() - 1));
            }
            DataPicker.pickTime(getContext(), mPicked, ResourcesUtil.getString(R.string.chose_time), weeks,
                    null, false, new MultipleTextWheelPicker.OnMultiPickListener() {
                        @Override
                        public void onDataPicked(List<String> pickedData) {
                            if (mListener != null && pickedData != null && pickedData.size() > 0) {
                                mPicked = pickedData;
                                mListener.onQuery(0, convert(pickedData.get(0)), 0);
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        } else if (mShowDay) {
            int y = mCurrentYear - YEAR_STAR;
            DataPicker.pickDate(getContext(), ResourcesUtil.getString(R.string.chose_time),
                    mPickedDate, DateWheelPicker.TYPE_YY_MM_DD, View.VISIBLE,
                    y, 0, new DataPicker.OnDatePickListener() {
                        @Override
                        public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                            if (mListener != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                mPickedDate = calendar.getTime();
                                //返回的month从0开始，需要+1
                                mListener.onQuery(year, month + 1, day);
                            }
                        }
                    }, YEAR_STAR, MONTH_START, DAY_START);
        } else {
            sec = new ArrayList<>();
            for (int i = 0; i < years.size(); i++) {
                sec.add(getMonths(convert(years.get(i))));
            }
            if (mPicked.isEmpty()) {
                mPicked.add(years.get(0));
                List<String> s = sec.get(0);
                mPicked.add(s.get(s.size() - 1));
            }
            DataPicker.pickTime(getContext(), mPicked, ResourcesUtil.getString(R.string.chose_time), years,
                    sec, false, new MultipleTextWheelPicker.OnMultiPickListener() {
                        @Override
                        public void onDataPicked(List<String> pickedData) {
                            if (mListener != null && pickedData != null && pickedData.size() == 2) {
                                mPicked = pickedData;
                                mListener.onQuery(convert(pickedData.get(0)), convert(pickedData.get(1)), 0);
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }


    }

    private ArrayList<String> getWeeks() {
        ArrayList<String> weeks = new ArrayList<>();
        for (int i = 1; i <= mTotalWeek; i++) {
            weeks.add(i + ResourcesUtil.getString(R.string._week));
        }

        return weeks;
    }

    private ArrayList<String> getMonths(int year) {
        ArrayList<String> months = new ArrayList<>();
        if (mCurrentYear == year) {
            int s = 0;
            if (MONTH_START > 0 && year == YEAR_STAR) {
                s = MONTH_START - 1;
            }
            for (int i = s; i < mCurrentMonth + 1; i++) {
                int m = i + 1;
                months.add(m + ResourcesUtil.getString(R.string._month));
            }
            return months;
        }
        if (MONTH_START > 0 && year == YEAR_STAR) {
            for (int i = MONTH_START; i < 13; i++) {
                months.add(i + ResourcesUtil.getString(R.string._month));
            }
            return months;
        }
        for (int i = 1; i < 13; i++) {
            months.add(i + ResourcesUtil.getString(R.string._month));
        }
        return months;
    }

    private List<String> getYears() {
        List<String> years = new ArrayList<>();
        int y = mCurrentYear - YEAR_STAR;
        for (int i = y; i >= 0; i--) {
            years.add(String.valueOf((YEAR_STAR + i) + ResourcesUtil.getString(R.string._year)));
        }
        return years;
    }

    private int convert(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }

        if (str.contains(ResourcesUtil.getString(R.string._year))) {
            str = str.replace(ResourcesUtil.getString(R.string._year), "");
        }

        if (str.contains(ResourcesUtil.getString(R.string._month))) {
            str = str.replace(ResourcesUtil.getString(R.string._month), "");
        }

        if (str.contains(ResourcesUtil.getString(R.string._week))) {
            str = str.replace(ResourcesUtil.getString(R.string._week), "");
        }

        return Integer.parseInt(str);
    }

    public void setOnDateQuery(OnDateQueryListener listener) {
        mListener = listener;
    }

    public void setShowWeek(boolean showWeek) {
        mShowWeek = showWeek;
    }

    public boolean getShowWeek() {
        return mShowWeek;
    }

    public void setShowDay(boolean show) {
        mShowDay = show;
    }

    public boolean getShowDay() {
        return mShowDay;
    }

    public void setTotalWeek(int total) {
        mTotalWeek = total;
    }

    public int getSelectedYear() {
        return mSelectedYear;
    }

    public void setYEAR_STAR(int year_star) {
        YEAR_STAR = year_star;
    }

    public void setMONTH_START(int month_start) {
        MONTH_START = month_start;
    }

    public void setDAY_START(int day_start) {
        DAY_START = day_start;
    }

    public TextView getTextView() {
        return mYear;
    }

    public interface OnYearSelectListener {
        void onSelect(int year);
    }

    public interface OnDateQueryListener {
        void onQuery(int year, int sec, int third);
    }

    public void setNotDataListener(OnNotDataListener notDataListener) {
        mNotDataListener = notDataListener;
    }

    /**
     * 选择数据依赖接口时，若接口返回失败，设置view不可点击时回调
     */
    public interface OnNotDataListener {
        void onListener();
    }
}
