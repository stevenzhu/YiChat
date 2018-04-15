package com.shorigo.utils;

import android.text.TextUtils;

import yichat.util.ZUIUtil;
import yichat.util.ZUtil;

/**
 * 请求URL常量类
 * 
 * @author peidongxu
 * 
 */
public class UrlConstants {

	/** 服务器url */
//	public static String SERVICE_HOST_URL_TEXT = "http://192.168.1.66:8883";
//	public static String SERVICE_HOST_URL = "http://api.buwaibao.com:8883";
	public static String SERVICE_HOST_URL = ""; //http://localhost";
	static{
		if(!ZUtil.isDebugMode() || TextUtils.isEmpty(SERVICE_HOST_URL)){
			SERVICE_HOST_URL = "http://59.110.225.146";
		}
	}
}
