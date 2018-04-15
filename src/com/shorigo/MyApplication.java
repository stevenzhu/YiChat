package com.shorigo;

import android.app.Application;
import android.widget.Toast;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.easeui.utils.HXHelper;
import com.umeng.socialize.PlatformConfig;

public class MyApplication extends Application {
	public static MyApplication main;

	@Override
	public void onCreate() {
		super.onCreate();
		main = this;
		// 设置程序崩溃处理
		//CrashHandler.getInstance().init(main);

		initBaidu();
		initUmeng();
		initEMChat();

		registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());
	}

	public static MyApplication getInstance() {
		return main;
	}

	/**
	 * 全局弹出消息提示
	 * 
	 * @param msg
	 */
	public void showToast(String msg) {
		Toast.makeText(main, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 初始化地图
	 */
	private void initBaidu() {
		SDKInitializer.initialize(main);
	}

	/**
	 * 初始化友盟
	 */
	private void initUmeng() {
		// 微信 appid appsecret
		PlatformConfig.setWeixin("", "");
//		// 新浪微博 appkey appsecret
//		PlatformConfig.setSinaWeibo("", "", "http://sns.whalecloud.com");
//		// QQ和Qzone appid appkey
//		PlatformConfig.setQQZone("", "");
	}

	/**
	 * 初始化环信
	 */
	private void initEMChat() {
		HXHelper.getInstance().init(main);
	}

}
