package com.shorigo.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 配置文件
 * 
 * @author peidongxu
 * 
 */
public class MyConfig {
	/**
	 * 描述：设置int类型的 config 数据
	 * 
	 * @param context
	 * @param name
	 *            配置文件名称
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setConfig(Context context, String name, String key, int value) {
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		Editor editor = sharedata.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 描述：获取int类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public static int getConfig(Context context, String name, String key, int defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		return sharedata.getInt(key, defaultValue);
	}

	/**
	 * 描述：设置long类型的 config 数据
	 * 
	 * @param context
	 * @param name
	 *            配置文件名称
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setConfig(Context context, String name, String key, long value) {
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		Editor editor = sharedata.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 描述：获取long类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public static long getConfig(Context context, String name, String key, long defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		return sharedata.getLong(key, defaultValue);
	}

	/**
	 * 描述：设置String类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setConfig(Context context, String name, String key, String value) {
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		Editor editor = sharedata.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 描述：获取String类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public static String getConfig(Context context, String name, String key, String defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		return sharedata.getString(key, defaultValue);
	}

	/**
	 * 描述：设置boolean类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setConfig(Context context, String name, String key, boolean value) {
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		Editor editor = sharedata.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 描述：获取boolean类型的 config 数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public static boolean getConfig(Context context, String name, String key, boolean defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		return sharedata.getBoolean(key, defaultValue);
	}

	/**
	 * 清除配置文件
	 * 
	 * @param context
	 * @param name
	 */
	public static void clear(Context context, String name) {
		SharedPreferences sharedata = context.getSharedPreferences(name, 0);
		Editor edit = sharedata.edit();
		edit.clear();
		edit.commit();
	}

	/**
	 * 描述：保存token
	 * 
	 * @param context
	 */
	public static void saveToken(Context context, String token) {
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		Editor editor = sharedata.edit();
		editor.putString("token", token);
		editor.commit();
	}

	/**
	 * 描述：获取token
	 * 
	 * @param context
	 */
	public static String getToken(Context context) {
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		return sharedata.getString("token", "");
	}

	/**
	 * 描述：保存Loction
	 * 
	 * @param context
	 */
	public static void saveLoction(Context context, Map<String, String> map) {
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		Gson gson = new Gson();
		Editor editor = sharedata.edit();
		editor.putString("loction", gson.toJson(map));
		editor.commit();
	}

	/**
	 * 描述：获取Loction
	 * 
	 * @param context
	 */
	public static Map<String, String> getLoction(Context context) {
		if (context == null) {
			return null;
		}
		Gson gson = new Gson();
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		Map<String, String> map = gson.fromJson(sharedata.getString("loction", ""), new TypeToken<Map<String, String>>() {
		}.getType());
		if (map == null) {
			map = new HashMap<String, String>();
			map.put("lat", "");
			map.put("lon", "");
			map.put("ciry", "");
		}
		return map;
	}

	/**
	 * 描述：保存用户信息
	 * 
	 * @param context
	 */
	public static void saveUserInfo(Context context, Map<String, String> map) {
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		Editor editor = sharedata.edit();
		Gson gson = new Gson();
		editor.putString("user_info", gson.toJson(map));
		editor.commit();
	}

	/**
	 * 描述：获取用户信息
	 * 
	 * @param context
	 */
	public static Map<String, String> getUserInfo(Context context) {
		if (context == null) {
			return null;
		}
		Gson gson = new Gson();
		SharedPreferences sharedata = context.getSharedPreferences(Constants.CONFIG_NAME, 0);
		Map<String, String> map = gson.fromJson(sharedata.getString("user_info", ""), new TypeToken<Map<String, String>>() {
		}.getType());
		return map;
	}

}
