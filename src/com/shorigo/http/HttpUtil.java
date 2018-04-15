package com.shorigo.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import login.login_1.code.LoginUI;
import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.shorigo.MyApplication;
import com.shorigo.utils.LogUtils;
import com.shorigo.utils.MD5Util;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.UrlConstants;
import com.shorigo.utils.Utils;

/**
 * 网络请求工具类
 * 
 * @author peidongxu
 * 
 */
public class HttpUtil {

	// 网络请求成功标识
	public final static String HTTP_STATUS_SUCCESS = "1000";
	private static Map<String, String> mapTip;

	// 实例话对象
	private static AsyncHttpClient client = new AsyncHttpClient();

	static {
		client.setTimeout(11000); // 设置链接超时，如果不设置，默认为10s
	}

	/**
	 * 无参数get请求
	 * 
	 * @param url
	 *            请求链接
	 * @param res
	 * @return 返回String
	 */
	public static void get(String url, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		client.get(url, res);
	}

	/**
	 * 有参数get请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回String
	 */
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("params:", params.toString());
		client.get(url, params, res);
	}

	/**
	 * 无参数get请求
	 * 
	 * @param url
	 *            请求链接
	 * @param res
	 * @return 返回JSON对象或数组
	 */
	public static void get(String url, JsonHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		client.get(url, res);
	}

	/**
	 * 有参数get请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回JSON对象或数组
	 */
	public static void get(String url, RequestParams params, JsonHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("params:", params.toString());
		client.get(url, params, res);
	}

	/**
	 * 无参数get请求 （下载）
	 * 
	 * @param url
	 *            请求链接
	 * @param bHandler
	 * @return 返回byte数据
	 */
	public static void get(String url, BinaryHttpResponseHandler bHandler) {
		client.get(url, bHandler);
	}

	/**
	 * 无参数post请求
	 * 
	 * @param url
	 *            请求链接
	 * @param res
	 * @return 返回String
	 */
	public static void post(String url, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		client.post(url, res);
	}

	/**
	 * 有参数post请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回String
	 */
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("params:", params.toString());
		client.post(url, params, res);
	}

	/**
	 * 有参数post请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回String
	 */
	public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("====:", params.toString());
		client.post(context, url, params, res);
	}

	/**
	 * 有参数put请求
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回String
	 */
	public static void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("====:", params.toString());
		client.put(context, url, params, res);
	}

	/**
	 * 有参数post请求/支持文件上传
	 * 
	 * @param url
	 *            请求链接
	 * @param map
	 *            参数对象
	 * @param params
	 *            传参对象：文件存入该对象
	 * @param res
	 * @return 返回String
	 */
	public static RequestHandle post(Context context, String url, Map<String, String> map, RequestParams params, AsyncHttpResponseHandler res) {
		if (params == null) {
			params = new RequestParams();
		}

		if (map != null) {
			Set<String> keySet = map.keySet();
			for (String keyName : keySet) {
				Object keyValue = map.get(keyName);
				if (null == keyValue) {
					params.put(keyName, "");
				} else {
					params.put(keyName, keyValue.toString());
				}
			}
		}
		params.put("sign", getSign(context, map));

		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("params:", params.toString());

		return client.post(context, url, params, res);
	}

	/**
	 * 有参数post请求/不支持文件上传
	 * 
	 * @param url
	 *            请求链接
	 * @param params
	 *            参数
	 * @param res
	 * @return 返回String
	 */
	public static void post(Context context, String url, Map<String, String> map, AsyncHttpResponseHandler res) {
		RequestParams params = new RequestParams();
		if (map != null) {
			Set<String> keySet = map.keySet();
			for (String keyName : keySet) {
				Object keyValue = map.get(keyName);
				if (null == keyValue) {
					params.put(keyName, "");
				} else {
					params.put(keyName, keyValue.toString());
				}
			}
		}
		params.put("sign", getSign(context, map));

		LogUtils.i("url:", url);
		LogUtils.i("====:", "============");
		LogUtils.i("params:", params.toString());

		client.post(context, url, params, res);
	}

	/**
	 * 获取sign的方法
	 * 
	 * @param map
	 *            数据集
	 */
	public static String getSign(Context context, Map<String, String> map) {
		MD5Util md5Util = new MD5Util();
		String sign = "";
		try {
			String app_key = "36f73cc4b9fef993fe052d932e253e9f";
			if (map != null && map.size() > 0) {
				Set<String> keySet = map.keySet();
				for (String keyName : keySet) {
					String keyValue = map.get(keyName);
					if (Utils.isEmity(keyValue)) {
						keyValue = "";
					}
					sign += keyName + "=" + keyValue + "&";
				}
				sign = md5Util.createMD5(sign + "key=" + app_key);
			} else {
				sign = md5Util.createMD5(sign + "key=" + app_key);
			}
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * 获取接口URL
	 * 
	 * @param moduleUrl
	 *            模块地址
	 * @return
	 */
	public static String getUrl(String moduleUrl) {
		return UrlConstants.SERVICE_HOST_URL + moduleUrl;
	}

	/**
	 * 检测网络是否请求成功
	 * 
	 * @param context
	 * @param status
	 * @return true:成功;false:失败
	 */
	public static boolean isSuccess(Context context, String code) {
		boolean isSuccess = false;
		if (HTTP_STATUS_SUCCESS.equals(code)) {
			isSuccess = true;
		} else if ("1002".equals(code)) {
			isSuccess = false;
			MyConfig.saveToken(context, "");
			Intent intent = new Intent(context, LoginUI.class);
			context.startActivity(intent);
		} else {
			isSuccess = false;
			MyApplication.getInstance().showToast(getError(code));
		}
		return isSuccess;
	}

	private static String getError(String errorCode) {
		if (mapTip == null) {
			mapTip = new HashMap<String, String>();
			mapTip.put("-1", "系统繁忙，稍后再试");
			mapTip.put("-2", "系统处理超时");
			mapTip.put("1000", "处理成功");
			mapTip.put("1001", "处理失败");
			mapTip.put("1002", "access_token非法");
			mapTip.put("1003", "sign校验失败");
			mapTip.put("1004", "请求参数缺失");
			mapTip.put("1005", "请求中的appid不存在");
			mapTip.put("1006", "请求中的appid无效/受限");
			mapTip.put("1007", "要访问的api无访问权限");
			mapTip.put("2000", "账号或密码不正确");
			mapTip.put("2001", "账号被冻结");
			mapTip.put("2002", "验证码错误");
			mapTip.put("2003", "手机号码已被注册");
			mapTip.put("2005", "该手机号码未被注册");
			mapTip.put("2006", "旧密码验证失败");
			mapTip.put("2007", "上传图片验证失败");
		}
		return mapTip.get(errorCode);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
}
