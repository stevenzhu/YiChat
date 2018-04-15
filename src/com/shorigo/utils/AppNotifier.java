package com.shorigo.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import bean.MsgBean;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;

/**
 * 应用内部--新消息提醒
 * 
 * @author peidongxu
 * 
 */
public class AppNotifier {
	private final static String TAG = "notify";
	Ringtone ringtone = null;
	protected static int notifyID = 0525; // start notification id
	protected static int foregroundNotifyID = 0555;
	protected NotificationManager notificationManager = null;
	protected int notificationNum = 0;
	protected Context appContext;
	protected String packageName;
	protected long lastNotifiyTime;
	protected AudioManager audioManager;
	protected Vibrator vibrator;
	protected AppNotificationInfoProvider notificationInfoProvider;

	private static AppNotifier instance;

	public AppNotifier(Context context) {
		init(context);
	}

	/**
	 * 单一实例
	 */
	public static AppNotifier getAppNotifier(Context context) {
		if (instance == null) {
			instance = new AppNotifier(context);
		}
		return instance;
	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param context
	 * @return
	 */
	public AppNotifier init(Context context) {
		appContext = context;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		packageName = appContext.getApplicationInfo().packageName;
		audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
		vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
		return this;
	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 */
	public void reset() {
		resetNotificationCount();
		cancelNotificaton();
	}

	void resetNotificationCount() {
		notificationNum = 0;
	}

	void cancelNotificaton() {
		if (notificationManager != null)
			notificationManager.cancel(notifyID);
	}

	/**
	 * 处理新收到的消息，然后发送通知
	 * 
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param message
	 */
	public synchronized void onNewMsg(MsgBean msgBean) {
		if (msgBean == null) {
			return;
		}
		// 判断app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(msgBean, true);
        } else {
            sendNotification(msgBean, true);
        }
		viberateAndPlayTone(msgBean);
	}

	/**
	 * 判断屏幕状态
	 * 
	 * @return
	 */
	private boolean isScreenOn() {
		PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();// 如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
	}

	protected void sendNotification(MsgBean msgBean, boolean isForeground) {
		sendNotification(msgBean, isForeground, true);
	}

	/**
	 * 发送通知栏提示 This can be override by subclass to provide customer
	 * implementation
	 * 
	 * @param message
	 */
	protected void sendNotification(MsgBean msgBean, boolean isForeground, boolean numIncrease) {
		try {
			PackageManager packageManager = appContext.getPackageManager();
			String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());

			// create and send notificaiton
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext).setSmallIcon(appContext.getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setAutoCancel(true);

			Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
			if (notificationInfoProvider != null) {
				// 设置自定义的notification点击跳转intent
				msgIntent = notificationInfoProvider.getLaunchIntent(msgBean);
			}

			PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			if (numIncrease) {
				// prepare latest event info section
				if (!isForeground) {
					notificationNum++;
				}
			}

			if (notificationInfoProvider != null) {
				// small icon
				int smallIcon = notificationInfoProvider.getSmallIcon(msgBean);
				if (smallIcon != 0) {
					mBuilder.setSmallIcon(smallIcon);
				}
			}

			mBuilder.setContentTitle(appname);
			mBuilder.setTicker(appname);
			mBuilder.setContentText(msgBean.getAlert());
			mBuilder.setContentIntent(pendingIntent);
			// mBuilder.setNumber(notificationNum);
			Notification notification = mBuilder.build();

			if (isForeground) {
				notificationManager.notify(foregroundNotifyID, notification);
				notificationManager.cancel(foregroundNotifyID);
			} else {
				notificationManager.notify(notifyID, notification);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 手机震动和声音提示
	 */
	public void viberateAndPlayTone(MsgBean msgBean) {
		if (msgBean == null) {
			return;
		}
		if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
			// received new messages within 2 seconds, skip play ringtone
			return;
		}
		try {
			lastNotifiyTime = System.currentTimeMillis();
			// 判断是否处于静音模式
			if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
				EMLog.e(TAG, "in slient mode now");
				return;
			}
			boolean isVibrator = MyConfig.getConfig(appContext, Constants.CONFIG_NAME, "isVibrator", true);
			if (isVibrator) {
				long[] pattern = new long[] { 0, 180, 80, 120 };
				vibrator.vibrate(pattern, -1);
			}
			boolean isVoice = MyConfig.getConfig(appContext, Constants.CONFIG_NAME, "isVoice", true);
			if (isVoice) {
				if (ringtone == null) {
					Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

					ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
					if (ringtone == null) {
						LogUtils.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
						return;
					}
				}

				if (!ringtone.isPlaying()) {
					String vendor = Build.MANUFACTURER;
					ringtone.play();
					if (vendor != null && vendor.toLowerCase().contains("samsung")) {
						Thread ctlThread = new Thread() {
							public void run() {
								try {
									Thread.sleep(3000);
									if (ringtone.isPlaying()) {
										ringtone.stop();
									}
								} catch (Exception e) {
								}
							}
						};
						ctlThread.run();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置NotificationInfoProvider
	 * 
	 * @param provider
	 */
	public void setNotificationInfoProvider(AppNotificationInfoProvider provider) {
		notificationInfoProvider = provider;
	}

	public interface AppNotificationInfoProvider {
		/**
		 * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @return null为使用默认
		 */
		String getDisplayedText(EMMessage message);

		/**
		 * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @param fromUsersNum
		 *            发送人的数量
		 * @param messageNum
		 *            消息数量
		 * @return null为使用默认
		 */
		String getLatestText(MsgBean msgBean, int fromUsersNum, int messageNum);

		/**
		 * 设置notification标题
		 * 
		 * @param message
		 * @return null为使用默认
		 */
		String getTitle(MsgBean msgBean);

		/**
		 * 设置小图标
		 * 
		 * @param message
		 * @return 0使用默认图标
		 */
		int getSmallIcon(MsgBean msgBean);

		/**
		 * 设置notification点击时的跳转intent
		 * 
		 * @param message
		 *            显示在notification上最近的一条消息
		 * @return null为使用默认
		 */
		Intent getLaunchIntent(MsgBean msgBean);
	}
}
