/*
 * Copyright (c) 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.callme.platform.util;

import android.content.Intent;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class OtherUtils {
	private OtherUtils() {
	}

	/**
	 *            if null, use the default format (Mozilla/5.0 (Linux; U;
	 *            Android %s) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0
	 *            %sSafari/534.30).
	 * @return
	 */
	public static String getUserAgent() {
		String webUserAgent = "Android_%s_%s";
		String version = Build.VERSION.RELEASE;
		String name = Build.BRAND + " " + Build.MODEL;
		String userAgent = String.format(webUserAgent, version, name);
		try {
			return URLEncoder.encode(userAgent, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;

		// if (context != null) {
		// try {
		// Class sysResCls = Class.forName("com.android.internal.R$string");
		// Field webUserAgentField =
		// sysResCls.getDeclaredField("web_user_agent");
		// Integer resId = (Integer) webUserAgentField.get(null);
		// webUserAgent = context.getString(resId);
		// } catch (Throwable ignored) {
		// }
		// }
		// if (TextUtils.isEmpty(webUserAgent)) {
		// webUserAgent =
		// "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 %sSafari/533.1";
		// }
		//
		// Locale locale = Locale.getDefault();
		// StringBuffer buffer = new StringBuffer();
		// // Add version
		// final String version = Build.VERSION.RELEASE;
		// if (version.length() > 0) {
		// buffer.append(version);
		// } else {
		// // default to "1.0"
		// buffer.append("1.0");
		// }
		// buffer.append("; ");
		// final String language = locale.getLanguage();
		// if (language != null) {
		// buffer.append(language.toLowerCase());
		// final String country = locale.getCountry();
		// if (country != null) {
		// buffer.append("-");
		// buffer.append(country.toLowerCase());
		// }
		// } else {
		// // default to "en"
		// buffer.append("en");
		// }
		// // add the model for the release build
		// if ("REL".equals(Build.VERSION.CODENAME)) {
		// final String model = Build.MODEL;
		// if (model.length() > 0) {
		// buffer.append("; ");
		// buffer.append(model);
		// }
		// }
		// final String id = Build.ID;
		// if (id.length() > 0) {
		// buffer.append(" Build/");
		// buffer.append(id);
		// }
		// return String.format(webUserAgent, buffer, "Mobile ");
	}

	public static long getAvailableSpace(File dir) {
		try {
			final StatFs stats = new StatFs(dir.getPath());
			return (long) stats.getBlockSize()
					* (long) stats.getAvailableBlocks();
		} catch (Throwable e) {
			LogUtil.e("", e.getMessage());
			return -1;
		}

	}

	private static final int STRING_BUFFER_LENGTH = 100;

	public static long sizeOfString(final String str, String charset)
			throws UnsupportedEncodingException {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		int len = str.length();
		if (len < STRING_BUFFER_LENGTH) {
			return str.getBytes(charset).length;
		}
		long size = 0;
		for (int i = 0; i < len; i += STRING_BUFFER_LENGTH) {
			int end = i + STRING_BUFFER_LENGTH;
			end = end < len ? end : len;
			String temp = getSubString(str, i, end);
			size += temp.getBytes(charset).length;
		}
		return size;
	}

	// get the sub string for large string
	public static String getSubString(final String str, int start, int end) {
		return new String(str.substring(start, end));
	}

	public static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[3];
	}

	public static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}

	// 判断是否从长按home返回
	public static boolean isBackFromHistory(Intent startintent) {
		int flag = Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
		int flag_sdk = 0;

		// 4.0以上的机器从历史启动的flag有所不同
		if (DevicesUtil.getSystemVersionLevel() >= 14) {
			flag_sdk = Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
					| Intent.FLAG_ACTIVITY_TASK_ON_HOME;
		} else {
			flag_sdk = Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
		}

		return startintent.getFlags() == flag
				|| startintent.getFlags() == flag_sdk;
	}

	/**
	 * Returns the input value x clamped to the range [min, max].
	 *
	 * @param x
	 * @param min
	 * @param max
	 * @return
	 */
	public static float clamp(float x, float min, float max) {
		if (x > max) {
			return max;
		}

		if (x < min) {
			return min;
		}
		return x;
	}
}
