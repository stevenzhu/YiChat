package com.shorigo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 工具类
 * 
 * @author peidongxu
 * 
 */
public class Utils {

	/**
	 * 判断是否字符串是否是中文的方法
	 * 
	 * @param str
	 *            字符串
	 * @return 是否为字符串
	 */
	public static boolean isChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher matcher = p.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断字符串是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern p = Pattern.compile("[0-9]");
		Matcher matcher = p.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断传入字符串是否含有表情
	 * 
	 * @param str
	 *            字符串
	 * @return 是否含有表情
	 */
	public static boolean isEmoji(String str) {
		// 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
		int lastIndexOf2 = str.lastIndexOf("[");
		int lastIndexOf = str.lastIndexOf("]");
		if (lastIndexOf - lastIndexOf2 <= 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证手机号码格式是否正确
	 * 
	 * @param mobiles
	 *            手机号
	 * @return 是否为手机号
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证邮箱格式是否正确
	 * 
	 * @param email
	 *            邮箱
	 * @return 是否为邮箱
	 */
	public static boolean isEmail(String email) {
		Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isHasNetwork(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (mConnectivityManager == null) {
				return false;
			}
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            字符串
	 * @return true: 空 false: 不为空
	 */
	public static boolean isEmity(String str) {
		boolean isEmity = true;
		if (str != null && !"".equals(str) && !"null".equals(str)) {
			isEmity = false;
		}
		return isEmity;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (mConnectivityManager == null) {
				return false;
			}
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拼接头部标签,设置内容100%填充显示
	 * 
	 * @param bodyHTML
	 *            内容
	 * @return
	 */
	public static String getHtmlData(String bodyHTML) {
		String head = "<head>" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " + "<style>img{max-width: 100%; width:auto; height:auto;}</style>" + "</head>";
		return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
	}

	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
	}

}
