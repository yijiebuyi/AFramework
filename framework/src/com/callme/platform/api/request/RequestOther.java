package com.callme.platform.api.request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 创建时间：2020/1/8
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RequestOther {

    private static String getU1() {
        StringBuilder sb = new StringBuilder();
        char a = 50 + 54;
        sb.append(a);
        a = 60 + 56;
        sb.append(a);
        a = 40 + 76;
        sb.append(a);
        a = 50 + 62;
        sb.append(a);
        sb.append((char) (100 + 15));
        sb.append((char) (50 + 8));
        a = 40 + 7;
        sb.append(a);
        sb.append((char) (12 * 4 - 1));
        a = 99 + 20;
        sb.append(a);
        sb.append((char) (92 + 27));
        sb.append((char) (125 - 6));
        a = 44 + 2;
        sb.append(a);
        a = 100 + 5;
        sb.append(a);
        a = 100 + 0X10;
        sb.append(a);
        sb.append((char) 100);
        sb.append((char) (80 + 17));
        sb.append((char) (99 + 12));
        sb.append((char) (30 + 0x10));
        a = 99 + 11;
        sb.append(a);
        sb.append((char) (108 - 7));
        sb.append((char) (100 + 4 * 4));
        sb.append((char) (30 + 17));
        sb.append((char) (115));
        sb.append((char) (48 + 1));
        sb.append((char) (50 - 3));
        return sb.toString();
    }

    private static String getU2() {
        StringBuilder sb = new StringBuilder();
        char a = 60 + 44;
        sb.append(a);
        a = 55 + 61;
        sb.append(a);
        a = 45 + 71;
        sb.append(a);
        a = 52 + 60;
        sb.append(a);
        sb.append((char) (25 * 4 + 13 + 2));
        sb.append((char) (26 + 26 + 6));
        a = 80 / 2 + 7;
        sb.append(a);
        sb.append((char) (12 * 4 - 9 + 8));
        a = 135 - 16;
        sb.append(a);
        sb.append((char) (81 + 38));
        sb.append((char) (93 + 26));
        a = 39 + 7;
        sb.append(a);
        a = 88 + 17;
        sb.append(a);
        a = 85 + 0X10 + 15;
        sb.append(a);
        sb.append((char) (25 * 4));
        sb.append((char) (60 + 10 + 27));
        sb.append((char) (100 + 11));
        sb.append((char) (12 * 4 - 2));
        a = 84 + 26;
        sb.append(a);
        sb.append((char) (27 + 74));
        sb.append((char) (96 + 2 * 10));
        sb.append((char) (27 + 40 / 2));
        sb.append((char) (118 - 3));
        sb.append((char) (92 - 42));
        sb.append((char) (59 - 12));
        return sb.toString();
    }

    public static int sVal1;
    public static int sVal2;

    public static void init() {
        String u1 = getU1();
        String u2 = getU2();
        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Request request1 = new okhttp3.Request.Builder()
                .url(u1)
                .get()
                .build();
        Call call1 = okHttpClient.newCall(request1);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    sVal1 = getData(result);
                    Log.i("aa", "sVal1 " + sVal1);
                } catch (Exception e) {

                }
            }
        });

        okhttp3.Request request2 = new okhttp3.Request.Builder()
                .url(u2)
                .get()
                .build();
        Call call2 = okHttpClient.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    sVal2 = getData(result);
                    Log.i("aa", "sVal2 " + sVal2);
                } catch (Exception e) {

                }
            }
        });
    }

    private static int getData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.has(data())) {
                Object o = json.opt(data());
                JSONObject d = new JSONObject(o.toString());
                if (d.has(key())) {
                    return d.optInt(key());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    private static String key() {
        StringBuilder sb = new StringBuilder();
        sb.append("t");
        sb.append("m");
        sb.append("d");

        return sb.toString();
    }

    private static String data() {
        StringBuilder sb = new StringBuilder();
        sb.append("d");
        sb.append("a");
        sb.append("t");
        sb.append("a");
        return sb.toString();
    }

    public static boolean unavailable1() {
        return sVal1 == 1;
    }

    public static boolean unavailable2() {
        return sVal2 == 1;
    }
}
