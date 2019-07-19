package com.callme.platform.widget.datapicker;

import android.content.Context;

import com.callme.platform.widget.datapicker.core.AbstractWheelPicker;
import com.callme.platform.widget.datapicker.view.MultiplePickerData;
import com.callme.platform.widget.datapicker.view.MultipleTextWheelPicker;
import com.callme.platform.widget.datapicker.view.TextWheelPicker;
import com.callme.platform.widget.datapicker.view.TextWheelPickerAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/4/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TimeWheelPicker extends MultipleTextWheelPicker {
    private List<ArrayList<String>> mTime;
    private List<String> mTimeDay;

    public TimeWheelPicker(Context context) {
        super(context);
    }

    public void setTimeAndDay(List<String> timeDay, List<ArrayList<String>> time) {
        setTimeAndDay(null, timeDay, time);
    }

    public void setTimeAndDay(List<String> timeDay, List<ArrayList<String>> time, boolean placeholder) {
        setTimeAndDay(null, timeDay, time, placeholder);
    }

    public TimeWheelPicker(Context context, List<String> timeDay, List<ArrayList<String>> time) {
        this(context, timeDay, time, null);
    }

    public TimeWheelPicker(Context context, List<String> timeDay, List<ArrayList<String>> time,
                           List<String> pickedTime) {
        super(context);
        setTimeAndDay(pickedTime, timeDay, time);
    }

    public void setTimeAndDay(List<String> pickedTime, List<String> timeDay, List<ArrayList<String>> time) {
        mTimeDay = timeDay;
        mTime = time;

        List<MultiplePickerData> mpData = new ArrayList<MultiplePickerData>();

        int index = 0;
        if (timeDay != null && !timeDay.isEmpty()) {
            MultiplePickerData timeDayMp = new MultiplePickerData();
            timeDayMp.texts = timeDay;
            if (pickedTime != null && !pickedTime.isEmpty()) {
                timeDayMp.currentText = pickedTime.get(0);
            }
            index = timeDay.indexOf(timeDayMp.currentText);
            mpData.add(timeDayMp);
        }

        if (time != null && !time.isEmpty() && time.get(0) != null && !time.get(0).isEmpty()) {
            MultiplePickerData timeMp = new MultiplePickerData();
            index = index < 0 || index >= time.size() ? 0 : index;
            timeMp.texts = time.get(index);
            if (pickedTime != null && pickedTime.size() > 1) {
                timeMp.currentText = pickedTime.get(1);
            }
            mpData.add(timeMp);
        }

        //app 3.0后UI不需要占位
        //MultiplePickerData placeHold = new MultiplePickerData();
        //placeHold.placeHoldView = true;
        //mpData.add(placeHold);

        setData(mpData);
    }

//    public void setTimeAndDay(List<String> pickedTime, List<String> timeDay, List<ArrayList<String>> time) {
//        setTimeAndDay(pickedTime, timeDay, time, true);
//    }

    public void setTimeAndDay(List<String> pickedTime, List<String> timeDay, List<ArrayList<String>> time, boolean placeholder) {
        mTimeDay = timeDay;
        mTime = time;

        List<MultiplePickerData> mpData = new ArrayList<MultiplePickerData>();

        int index = 0;
        if (timeDay != null && !timeDay.isEmpty()) {
            MultiplePickerData timeDayMp = new MultiplePickerData();
            timeDayMp.texts = timeDay;
            if (pickedTime != null && !pickedTime.isEmpty()) {
                timeDayMp.currentText = pickedTime.get(0);
            }
            index = timeDay.indexOf(timeDayMp.currentText);
            mpData.add(timeDayMp);
        }

        if (time != null && !time.isEmpty() && time.get(0) != null && !time.get(0).isEmpty()) {
            MultiplePickerData timeMp = new MultiplePickerData();
            index = index < 0 || index >= time.size() ? 0 : index;
            timeMp.texts = time.get(index);
            if (pickedTime != null && pickedTime.size() > 1) {
                timeMp.currentText = pickedTime.get(1);
            }
            mpData.add(timeMp);
        }

        MultiplePickerData placeHold = new MultiplePickerData();
        placeHold.placeHoldView = placeholder;
        mpData.add(placeHold);

        setData(mpData);
    }

    @Override
    public void onWheelSelected(AbstractWheelPicker wheelPicker, int index, Object data) {
        TextWheelPickerAdapter adapter = null;
        switch (wheelPicker.getId()) {
            case 0:
                if (mTextWheelPickerAdapters.size() > 1) {
                    TextWheelPicker picker = (TextWheelPicker) mWheelPickers.get(1);
                    adapter = (TextWheelPickerAdapter) mTextWheelPickerAdapters.get(1);
                    List<String> d = mTime.get(index);
                    adapter.setData(d);
                    picker.setTouchable(d.size() != 1);
                }
                break;
        }

        mPickedData.set(wheelPicker.getId(), (String) data);
        if (mOnMultiPickListener != null) {
            mOnMultiPickListener.onDataPicked(mPickedData);
        }
    }
}
