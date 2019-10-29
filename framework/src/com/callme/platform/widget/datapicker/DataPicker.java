package com.callme.platform.widget.datapicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.callme.platform.R;
import com.callme.platform.widget.BottomSheet;
import com.callme.platform.widget.datapicker.view.MultipleTextWheelPicker;
import com.callme.platform.widget.datapicker.view.TextWheelPicker;
import com.callme.platform.widget.datapicker.view.TextWheelPickerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：
 *
 * 作者：huangyong
 * 创建时间：2017/11/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DataPicker {
    private static int mYear;
    private static int mMonth;
    private static int mDay;

    private static int mHour;
    private static int mMinute;
    private static int mSecond;

    /**
     * 选择生日
     *
     * @param context
     * @param birthday
     * @param pickListener
     */
    public static void pickBirthday(Context context, String title, Date birthday, final OnBirthdayPickListener pickListener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        DateWheelPicker picker = new DateWheelPicker(context);
        picker.setWheelPickerVisibility(DateWheelPicker.TYPE_HH_MM_SS, View.GONE);
        setTextPickerStyle(context, picker);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dy = calendar.get(Calendar.YEAR);
        int dm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DATE);

        picker.setOnDatePickListener(new DateWheelPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
            }
        });

        picker.setDateRange(dy - 100, dy);
        //after set onDatePickerLister
        if (birthday != null) {
            calendar.setTime(birthday);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DATE);
        } else {
            mYear = dy;
            mMonth = dm;
            mDay = dd;
        }
        picker.setCurrentDate(mYear, mMonth, mDay);
        picker.notifyDataSetChanged();

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onBirthPicked(mYear, mMonth, mDay);
                }
            }
        });
    }

    /**
     * 选择时间，默认是显示的“年 月 日"组合的时间选择器
     * 时间范围为当前时间100年前后100年后
     *
     * @param context
     * @param selectedDate
     * @param pickListener
     */
    public static void pickDate(Context context, String title, Date selectedDate, final OnDatePickListener pickListener) {
        pickDate(context, title, selectedDate, DateWheelPicker.TYPE_YY_MM_DD, View.VISIBLE,
                100, 100, pickListener);
    }

    /**
     * 选择时间，默认是显示的“年 月 日 时 分 秒"组合的时间选择器
     * 通过@param whichWheelPick和@param visibility可以设置哪些些时间控件显示或隐藏
     * 时间范围为当前时间100年前后100年后
     *
     * @param context
     * @param selectedDate
     * @param whichWheelPick 需要显示或隐藏的空间
     *                       {@link DateWheelPicker#TYPE_YEAR}{@link DateWheelPicker#TYPE_MONTH}
     *                       {@link DateWheelPicker#TYPE_DAY}{@link DateWheelPicker#TYPE_HOUR}
     *                       {@link DateWheelPicker#TYPE_MINUTE}{@link DateWheelPicker#TYPE_SECOND}
     * @param pickListener
     */
    public static void pickDate(Context context, String title, Date selectedDate, int whichWheelPick,
                                int visibility, final OnDatePickListener pickListener) {
        pickDate(context, title, selectedDate, whichWheelPick, visibility,
                100, 100, pickListener);
    }

    public static void pickDate(Context context, String title, Date selectedDate, int whichWheelPick, int visibility,
                                int aheadYear, int afterYear, final OnDatePickListener pickListener) {
        DataPicker.pickDate(context, title, selectedDate, whichWheelPick, visibility, aheadYear,
                afterYear, pickListener, 0, 0, 0);
    }

    /**
     * 选择时间，默认是显示的“年 月 日 时 分 秒"组合的时间选择器
     * 通过@param whichWheelPick和@param visibility可以设置哪些些时间控件显示或隐藏
     *
     * @param context
     * @param selectedDate   选择的日期
     * @param whichWheelPick 哪些控件
     *                       {@link DateWheelPicker#TYPE_YEAR}{@link DateWheelPicker#TYPE_MONTH}
     *                       {@link DateWheelPicker#TYPE_DAY}{@link DateWheelPicker#TYPE_HOUR}
     *                       {@link DateWheelPicker#TYPE_MINUTE}{@link DateWheelPicker#TYPE_SECOND}
     * @param visibility     设置的哪些控件需要显示或隐藏
     * @param aheadYear      当前时间多少年前
     * @param afterYear      当前时间多少年后
     * @param pickListener
     */
    public static void pickDate(Context context, String title, Date selectedDate, int whichWheelPick, int visibility,
                                int aheadYear, int afterYear, final OnDatePickListener pickListener,
                                int year_start, int month_start, int day_start) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        DateWheelPicker picker = month_start > 0 ?
                new DateWheelPicker(context, year_start, month_start, day_start) : new DateWheelPicker(context);
        picker.setWheelPickerVisibility(whichWheelPick, visibility);
        setTextPickerStyle(context, picker);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dy = calendar.get(Calendar.YEAR);
        int dm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DATE);
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);

        picker.setOnDatePickListener(new DateWheelPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
                mHour = hour;
                mMinute = minute;
                mSecond = second;
            }
        });


        //当前时间向前推aheadYear年，向后推afterYear年
        picker.setDateRange(dy - aheadYear, dy + afterYear);
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DATE);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
            mSecond = calendar.get(Calendar.SECOND);
        } else {
            mYear = dy;
            mMonth = dm;
            mDay = dd;
            mHour = hh;
            mMinute = mm;
            mSecond = ss;
        }

        picker.setCurrentTime(mHour, mMinute, mSecond);
        picker.setCurrentDate(mYear, mMonth, mDay);
        picker.notifyDataSetChanged();

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onDatePicked(mYear, mMonth, mDay, mHour, mMinute, mSecond);
                }
            }
        });
    }

    /**
     * 选择未来时间，默认100年后
     *
     * @param context
     * @param currentDate
     * @param whichWheelPick 需要显示或隐藏的空间
     *                       {@link DateWheelPicker#TYPE_YEAR}{@link DateWheelPicker#TYPE_MONTH}
     *                       {@link DateWheelPicker#TYPE_DAY}{@link DateWheelPicker#TYPE_HOUR}
     *                       {@link DateWheelPicker#TYPE_MINUTE}{@link DateWheelPicker#TYPE_SECOND}
     * @param year           往后推多少年
     * @param pickListener
     */
    public static void pickFutureDate(Context context, String title, Date currentDate, int whichWheelPick, int visibility,
                                      int year, final OnDatePickListener pickListener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        DateWheelPicker picker = new DateWheelPicker(context);
        picker.setWheelPickerVisibility(whichWheelPick, visibility);
        setTextPickerStyle(context, picker);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dy = calendar.get(Calendar.YEAR);
        int dm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DATE);
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int ss = calendar.get(Calendar.SECOND);

        picker.setOnDatePickListener(new DateWheelPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
                mHour = hour;
                mMinute = minute;
                mSecond = second;
            }
        });

        if (year <= 0) {
            year = 100;
        }
        picker.setDateRange(dy, dy + year);
        //after set onDatePickerLister
        if (currentDate != null) {
            calendar.setTime(currentDate);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DATE);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
            mSecond = calendar.get(Calendar.SECOND);
        } else {
            mYear = dy;
            mMonth = dm;
            mDay = dd;
            mHour = hh;
            mMinute = mm;
            mSecond = ss;
        }
        picker.setCurrentTime(mHour, mMinute, mSecond);
        picker.setCurrentDate(mYear, mMonth, mDay);
        picker.notifyDataSetChanged();

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onDatePicked(mYear, mMonth, mDay, mHour, mMinute, mSecond);
                }
            }
        });
    }

    /**
     * 选择未来时间
     *
     * @param context
     * @param currentDate
     * @param days         多少天后(如果传的时间小于等于0，默认是365天)
     * @param pickListener
     */
    public static void pickFutureDate(Context context, String title, Date currentDate, int days,
                                      final OnDatePickListener pickListener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        FutureTimePicker picker = new FutureTimePicker(context);
        setTextPickerStyle(context, picker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (days < 0) {
            days = 365;
        }
        picker.setFutureDuration(days);

        picker.setOnFutureDatePickListener(new FutureTimePicker.OnFutureDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
                mHour = hour;
                mMinute = minute;
                mSecond = second;
            }
        });

        if (currentDate != null) {
            picker.setPickedTime(currentDate.getTime());
        }

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onDatePicked(mYear, mMonth, mDay, mHour, mMinute, mSecond);
                }
            }
        });

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);
    }

    /**
     * 选择时间
     *
     * @param context
     */
    public static BottomSheet pickTime(Context context, String title, final List<String> timeDay, List<ArrayList<String>> time,
                                       final MultipleTextWheelPicker.OnMultiPickListener listener) {
        return pickTime(context, title, null, timeDay, time, listener);
    }

    /**
     * 选择数据
     *
     * @param context
     */
    public static BottomSheet pickTime(Context context, String title, List<String> selectTime,
                                       final List<String> timeDay, List<ArrayList<String>> time,
                                       final MultipleTextWheelPicker.OnMultiPickListener listener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        final MultipleTextWheelPicker picker = new TimeWheelPicker(context);
        setTextPickerStyle(context, picker);
        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    List<String> pData = picker.getPickedData();
                    listener.onDataPicked(pData);
                }
            }
        });
        bottomSheet.setOnDismissListener(new BottomSheet.OnDialogCloseListener() {
            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
        ((TimeWheelPicker) picker).setTimeAndDay(selectTime, timeDay, time);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);
        return bottomSheet;
    }

    /**
     * 选择数据
     *
     * @param context
     */
    public static void pickTime(Context context, String title, List<String> selectTime,
                                final List<String> timeDay, List<ArrayList<String>> time,
                                boolean placeholder, final MultipleTextWheelPicker.OnMultiPickListener listener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        final MultipleTextWheelPicker picker = new TimeWheelPicker(context);
        setTextPickerStyle(context, picker);
        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    List<String> pData = picker.getPickedData();
                    listener.onDataPicked(pData);
                }
            }
        });
        bottomSheet.setOnDismissListener(new BottomSheet.OnDialogCloseListener() {
            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
        ((TimeWheelPicker) picker).setTimeAndDay(selectTime, timeDay, time, placeholder);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);
    }

    public static void pickData(Context context, String title, final List<String> data,
                                final OnDataPickListener pickedListener) {
        DataPicker.pickData(context, title, null, data, pickedListener);
    }

    /**
     * 选择数据
     *
     * @param context
     */
    public static void pickTime(Context context, List<String> pickedTime, String title, final List<String> timeDay,
                                List<ArrayList<String>> time, boolean placeholder,
                                final MultipleTextWheelPicker.OnMultiPickListener listener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);

        final MultipleTextWheelPicker picker = new TimeWheelPicker(context);
        setTextPickerStyle(context, picker);
        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    List<String> pData = picker.getPickedData();
                    listener.onDataPicked(pData);
                }
            }
        });
        bottomSheet.setOnDismissListener(new BottomSheet.OnDialogCloseListener() {
            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }

            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
        ((TimeWheelPicker) picker).setTimeAndDay(pickedTime, timeDay, time, placeholder);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);
    }

    /**
     * 选择数据
     *
     * @param context
     * @param data           字符串数组
     * @param pickedListener
     */
    public static BottomSheet pickData(Context context, String title, String content, final List<String> data,
                                       final OnDataPickListener pickedListener) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setMiddleText(title);
        if (content != null) {
            bottomSheet.setContentText(content);
        }
        final TextWheelPicker picker = new TextWheelPicker(context);
        setTextPickerStyle(context, picker);

        TextWheelPickerAdapter adapter = new TextWheelPickerAdapter(data);
        picker.setAdapter(adapter);

        picker.setCurrentItem(0);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.setRightBtnText(context.getString(R.string.common_sure));
        showBootSheet(context, bottomSheet);

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedListener != null) {
                    Object pData = picker.getPickedData() != null ? picker.getPickedData() : data.get(0);
                    pickedListener.onDataPicked(pData);
                }
            }
        });
        return bottomSheet;
    }

    public static void setTextPickerStyle(Context context, Object picker) {
        if (picker instanceof DateWheelPicker) {
            ((DateWheelPicker) picker).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            ((DateWheelPicker) picker).setTextColor(context.getResources().getColor(R.color.font_black));
            ((DateWheelPicker) picker).setVisibleItemCount(7);
            ((DateWheelPicker) picker).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_36px));
            ((DateWheelPicker) picker).setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));
            ((DateWheelPicker) picker).setLineColor(context.getResources().getColor(R.color.common_bar_bg_color));
        } else if (picker instanceof MultipleTextWheelPicker) {
            ((MultipleTextWheelPicker) picker).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            ((MultipleTextWheelPicker) picker).setTextColor(context.getResources().getColor(R.color.font_black));
            ((MultipleTextWheelPicker) picker).setVisibleItemCount(7);
            ((MultipleTextWheelPicker) picker).setFakeBoldText(true);
            ((MultipleTextWheelPicker) picker).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_38px));
            ((MultipleTextWheelPicker) picker).setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));
            ((MultipleTextWheelPicker) picker).setLineColor(context.getResources().getColor(R.color.common_bar_bg_color));
        } else if (picker instanceof FutureTimePicker) {
            ((FutureTimePicker) picker).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            ((FutureTimePicker) picker).setTextColor(context.getResources().getColor(R.color.font_black));
            ((FutureTimePicker) picker).setVisibleItemCount(7);
            ((FutureTimePicker) picker).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_36px));
            ((FutureTimePicker) picker).setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));
            ((FutureTimePicker) picker).setLineColor(context.getResources().getColor(R.color.common_bar_bg_color));
        } else if (picker instanceof TextWheelPicker) {
            ((TextWheelPicker) picker).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            ((TextWheelPicker) picker).setTextColor(context.getResources().getColor(R.color.font_black));
            ((TextWheelPicker) picker).setVisibleItemCount(7);
            ((TextWheelPicker) picker).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_38px));
            ((TextWheelPicker) picker).getPaint().setFakeBoldText(true);
            ((TextWheelPicker) picker).setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));
            ((TextWheelPicker) picker).setLineColor(context.getResources().getColor(R.color.common_bar_bg_color));
        }
    }

    public interface OnBirthdayPickListener {
        public void onBirthPicked(int year, int month, int day);
    }

    public interface OnDatePickListener {
        public void onDatePicked(int year, int month, int day, int hour, int minute, int second);
    }

    public interface OnDatePickListener1 {
        public void onDatePicked(Date date);
    }

    public interface OnDataPickListener {
        public void onDataPicked(Object data);
    }

    private static void showBootSheet(Context context, BottomSheet bottomSheet) {
        boolean showBottom = true;
        if (context instanceof Activity) {
            showBottom = !((Activity) context).isDestroyed() && !((Activity) context).isFinishing();
        }
        if (showBottom) {
            bottomSheet.show();
        }
    }

}

