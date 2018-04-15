/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.widget.chatrow;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.util.EMLog;

/**
 * 语音row播放点击事件监听
 * 
 */
public class EaseChatRowVoicePlayClickListener implements View.OnClickListener {
	private static final String TAG = "VoicePlayClickListener";
	EMMessage message;
	EMVoiceMessageBody voiceBody;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private ChatType chatType;
	private EaseMessageAdapter adapter;

	public static boolean isPlaying = false;
	public static EaseChatRowVoicePlayClickListener currentPlayListener = null;
	public static String playMsgId;

	private SensorManager mManager;// 传感器管理对象
	private MySensorEventListener eventListener;

	public EaseChatRowVoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity context) {
		this.message = message;
		voiceBody = (EMVoiceMessageBody) message.getBody();
		this.iv_read_status = iv_read_status;
		this.adapter = (EaseMessageAdapter) adapter;
		voiceIconView = v;
		this.activity = context;
		this.chatType = message.getChatType();
		mManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		eventListener = new MySensorEventListener();
		// mManager.registerListener(eventListener,
		// mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
		// SensorManager.SENSOR_DELAY_NORMAL);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型

	}

	public void stopPlayVoice() {

		voiceAnimation.stop();
		if (message.direct() == EMMessage.Direct.RECEIVE) {
			voiceIconView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.ease_chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		playMsgId = null;
		adapter.notifyDataSetChanged();
	}

	public void stopPlayVoiceOn() {

		voiceAnimation.stop();
		if (message.direct() == EMMessage.Direct.RECEIVE) {
			voiceIconView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.ease_chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		playMsgId = null;
		adapter.notifyDataSetChanged();
		mManager.unregisterListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
	}

	public void playVoice(String filePath) {
		mManager.registerListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
				SensorManager.SENSOR_DELAY_NORMAL);// 注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型

		if (!(new File(filePath).exists())) {
			return;
		}
		playMsgId = message.getMsgId();
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EaseUI.getInstance().getSettingsProvider().isSpeakerOpened()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			// 关闭扬声器
			audioManager.setSpeakerphoneOn(false);
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mediaPlayer.release();
					mediaPlayer = null;
					stopPlayVoice(); // stop animation
					mManager.unregisterListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
					try {
						// 如果是接收的消息
						if (message.direct() == EMMessage.Direct.RECEIVE) {
							if (!message.isAcked() && chatType == ChatType.Chat) {
								// 告知对方已读这条消息
								EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
								if (message.getBooleanAttribute("fire", false)) {
									new Handler().postDelayed(new Runnable() {

										@Override
										public void run() {
											// 消息所属会话
											EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
											conversation.removeMessage(message.getMsgId());
											adapter.refresh();
										}
									}, 10000);
								}
							}
						}
					} catch (Exception e) {
					}
				}
			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();
			// 如果是接收的消息
			if (message.direct() == EMMessage.Direct.RECEIVE) {
				if (!message.isListened() && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
					// 隐藏自己未播放这条语音消息的标志
					iv_read_status.setVisibility(View.INVISIBLE);
					message.setListened(true);
					EMClient.getInstance().chatManager().setMessageListened(message);
				}
			}
		} catch (Exception e) {
			System.out.println();
		}
	}

	// show the voice playing animation
	@SuppressLint("ResourceType")
	private void showAnimation() {
		// play voice, and start animation
		if (message.direct() == EMMessage.Direct.RECEIVE) {
			voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		if (isPlaying) {
			Log.d("Easevoice", "EaseChatRowVoicePlayClickListener=====OnClick===" + isPlaying);
			if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
				currentPlayListener.stopPlayVoiceOn();
				return;
			}
			currentPlayListener.stopPlayVoiceOn();
		}
		palyVoiceMessage();
	}

	private void palyVoiceMessage() {
		String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
		if (message.direct() == EMMessage.Direct.SEND) {
			// for sent msg, we will try to play the voice file directly
			playVoice(voiceBody.getLocalUrl());
		} else {
			if (message.status() == EMMessage.Status.SUCCESS) {
				File file = new File(voiceBody.getLocalUrl());
				if (file.exists() && file.isFile()) {
					playVoice(voiceBody.getLocalUrl());
				} else {
					EMLog.e(TAG, "file not exist");
				}
			} else if (message.status() == EMMessage.Status.INPROGRESS) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			} else if (message.status() == EMMessage.Status.FAIL) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						EMClient.getInstance().chatManager().downloadAttachment(message);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
					}
				}.execute();
			}
		}
	}

	class MySensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float[] its = event.values;

			if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
				if (isPlaying) {
					if (its[0] == 0.0) {
						// 贴近手机
						EaseUI.getInstance().getSettingsProvider().setSpeakerOpened(false);
						currentPlayListener.stopPlayVoice();
						palyVoiceMessage();
					} else {
						// 远离手机
						if (!EaseUI.getInstance().getSettingsProvider().isSpeakerOpened()) {
							EaseUI.getInstance().getSettingsProvider().setSpeakerOpened(true);
							currentPlayListener.stopPlayVoice();
							palyVoiceMessage();
						}
					}
				}
			}
		}
	}
}
