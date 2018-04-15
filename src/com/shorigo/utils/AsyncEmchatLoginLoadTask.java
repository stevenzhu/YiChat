package com.shorigo.utils;

import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.adapter.EMAError;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 环信异步登录
 * 
 * @author peidongxu
 * 
 */
public class AsyncEmchatLoginLoadTask extends AsyncTask<Void, Void, Void> {
	// 当前对象
	private Context context;

	public AsyncEmchatLoginLoadTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		hxRegister();
		super.onPostExecute(result);
	}

	/**
	 * 环信注册账号
	 */
	private void hxRegister() {
		final Map<String, String> map = MyConfig.getUserInfo(context);
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMClient.getInstance().createAccount(Constants.PREFIX + map.get("user_id"), "123456");
					LogUtils.i("环信注册", "注册成功");
					hxLogin();
				} catch (final HyphenateException e) {
					// 注册失败
					LogUtils.e("环信注册", e.getMessage());
					int errorCode = e.getErrorCode();
					if (errorCode == EMAError.NETWORK_ERROR) {
						LogUtils.e("环信注册", "网络异常，请检查网络！");
					} else if (errorCode == EMAError.USER_ALREADY_EXIST) {
						hxLogin();
						LogUtils.e("环信注册", "用户已存在！");
					} else if (errorCode == EMAError.USER_REG_FAILED) {
						LogUtils.e("环信注册", "注册失败，无权限！");
					}
				}
			}
		}).start();
	}

	/**
	 * 环信账号登录
	 */
	private void hxLogin() {
		final Map<String, String> map = MyConfig.getUserInfo(context);
		// 调用sdk登陆方法登陆聊天服务器
		EMClient.getInstance().login(Constants.PREFIX + map.get("user_id"), "123456", new EMCallBack() {

			@Override
			public void onSuccess() {
				LogUtils.i("环信登录", "登录成功");
				try {
					// 加载所有本地群和回话
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMClient.getInstance().updateCurrentUserNick(map.get("user_nick"));
				if (!updatenick) {
					LogUtils.e("环信登录", "修改昵称失败");
				}
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				LogUtils.e("环信登录", message);
			}
		});
	}

}
