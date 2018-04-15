package com.shorigo.utils;

import java.util.ArrayList;
import java.util.List;

import bean.UserBean;

/**
 * 常量类
 * 
 * @author peidongxu
 * 
 */
public class Constants {
	// 缓存文件路径
	public static String path;
	public final static String _path = "/com.shorigo.yichat/";
	public final static String _image = _path + "image/ico";
	public final static String _audio = _path + "audio";
	public final static String _video = _path + "video";
	public final static String _anex = _path + "anex";
	public final static String _log = _path + "log";
	public final static String log_name = "error.log";
	// 网络请求成功标识
	public final static String HTTP_STATUS_SUCCESS = "200";
	public final static String HTTP_STATUS_SUCCESS_0 = "0";
	// 幻灯片切换
	public final static int VIEWPAGE_SWITCH = 1000;
	// 幻灯片点击
	public final static int VIEWPAGE_CLICK = 1001;
	// 屏幕宽度、高度
	public static int width;
	public static int height;
	// 环信注册用户前缀
	public final static String PREFIX = "yichat_";
	// 配合文件名称
	public static final String CONFIG_NAME = PREFIX + "user_config";
	// 缓存文件名称
	public final static String CACHE_NAME = PREFIX + "user_cache";

	/** 返回退出的页面 */
	public static List<String> listActivity = new ArrayList<String>();
	/** 群组中人员 */
	public static List<String> listMember = new ArrayList<String>();
	/** 选中人员 */
	public static List<UserBean> listUserBean = new ArrayList<UserBean>();

}
