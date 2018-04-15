package com.shorigo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "JPush";
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

//		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//			LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
//		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//			LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//			this.context = context;
//			// 消息处理
//			analysisMsg(bundle.getString(JPushInterface.EXTRA_EXTRA));
//		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//			LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
//			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//			LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//			LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");
//			// 打开自定义的Activity
//			// Intent i = new Intent(context, TestActivity.class);
//			// i.putExtras(bundle);
//			// //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//			// Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			// context.startActivity(i);
//		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
//			LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
//		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//			LogUtils.d(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
//		} else {
//			LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
//		}
	}

	/**
	 * 解析推送消息
	 */
//	private void analysisMsg(String content) {
//		if (Utils.isEmity(content)) {
//			return;
//		}
//		JSONObject jsonObject;
//		try {
//			jsonObject = new JSONObject(content);
//			MsgBean msgBean = new MsgBean();
//			String type = "";
//			String alert = "";
//			String rid = "";
//			String user_id = "";
//			if (jsonObject.has("type"))
//				type = jsonObject.getString("type"); // 类型
//			if (jsonObject.has("alert"))
//				alert = jsonObject.getString("alert");
//			if (jsonObject.has("rid"))
//				rid = jsonObject.getString("rid");
//			if (jsonObject.has("user_id"))
//				user_id = jsonObject.getString("user_id");
//			msgBean.setType(type);
//			msgBean.setAlert(alert);
//			msgBean.setRid(rid);
//			msgBean.setRule_id(user_id);
//			msgBean.setTime(TimeUtil.getData());
//			msgBean.setState("0");
//			DbHelper.getInstance(context).save(msgBean);
//			// 提示新消息
//			AppNotifier.getAppNotifier(context).onNewMsg(msgBean);
//			// 发送广播
//			Intent intent = new Intent(Constants.CONTROL_ACTION);
//			intent.putExtra("type", 2);
//			context.sendBroadcast(intent);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

}
