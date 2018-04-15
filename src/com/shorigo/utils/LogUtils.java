package com.shorigo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log记录类
 * 
 * @author peidongxu
 * 
 */
public class LogUtils {
	private static final boolean DEBUG = true;

	public static void i(String TAG, String msg) {
		if (DEBUG) {
			android.util.Log.i(TAG, "[" + getFileLineMethod() + "]" + msg);
		}
	}

	public static void d(String TAG, String method, String msg) {
		android.util.Log.d(TAG, "[" + method + "]" + msg);
	}

	public static void d(String TAG, String msg) {
		if (DEBUG) {
			android.util.Log.d(TAG, "[" + getFileLineMethod() + "]" + msg);
		}
	}

	public static void d(String msg) {
		if (DEBUG) {
			android.util.Log.d(_FILE_(), "[" + getLineMethod() + "]" + msg);
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			android.util.Log.e("UI", getFileLineMethod() + msg);
		}
	}

	public static void e(String TAG, String msg, Exception e) {
		if (DEBUG) {
			android.util.Log.e(TAG, msg, e);
		}
	}

	public static void e(String TAG, String msg) {
		if (DEBUG) {
			android.util.Log.e(TAG, getLineMethod() + msg);
		}
	}

	public static String getFileLineMethod() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
		StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("]");
		return toStringBuffer.toString();
	}

	public static String getLineMethod() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
		StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("]");
		return toStringBuffer.toString();
	}

	public static String _FILE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
		return traceElement.getFileName();
	}

	public static String _FUNC_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getMethodName();
	}

	public static int _LINE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getLineNumber();
	}

	public static String _TIME_() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(now);
	}
}
