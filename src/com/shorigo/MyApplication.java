package com.shorigo;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.easeui.utils.HXHelper;
import com.shorigo.utils.FileUtils;
import com.umeng.socialize.PlatformConfig;

public class MyApplication extends MultiDexApplication {
	public static MyApplication main;

	private static String endpoint = "oss-cn-beijing.aliyuncs.com";
	private static String accessKeyId = "LTAIubLSLaE6DBYg";
	private static String accessKeySecret = "ixRBESFFALGPT1iNHmGDGJtuT18meq";
    private static String securityToken="5adc630b569f2";
    private volatile static  OSS ossInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		main = this;
		// 设置程序崩溃处理
		//CrashHandler.getInstance().init(main);

		initBaidu();
		initUmeng();
		initEMChat();

		getOssInstance();
		registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());
	}

	public static MyApplication getInstance() {
		return main;
	}


   public static OSS getOssInstance(){
		if(ossInstance==null){
			synchronized (OSS.class){
				if(ossInstance==null){

					        /*
        * 移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。
        * */
					try {
						ClientConfiguration conf = new ClientConfiguration();
						conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
						conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
						conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
						conf.setMaxErrorRetry(2); // retry，default 2
						OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

						OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
						ossInstance = new OSSClient(getInstance(), endpoint, credentialProvider, conf);
						Log.d("------------","@@@@@11111");
						return ossInstance;
					} catch (Exception e) {
						Log.d("------------","@@@@@22222");
						e.printStackTrace();
					}
				}
			}
		}
		return ossInstance;
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
		//SDKInitializer.initialize(main);
		SDKInitializer.initialize(getApplicationContext());
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
