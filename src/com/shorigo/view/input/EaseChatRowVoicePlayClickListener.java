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
package com.shorigo.view.input;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;

/**
 * 语音row播放点击事件监听
 * 
 */
public class EaseChatRowVoicePlayClickListener implements View.OnClickListener {
	private static final String TAG = "EaseChatRowVoicePlayClickListener";
	ImageView voiceIconView;
	private String voiceUrl;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	Activity activity;

	public static boolean isPlaying = false;
	public static EaseChatRowVoicePlayClickListener currentPlayListener = null;
	public static String playMsgId;

	private SensorManager mManager;// 传感器管理对象
	private MySensorEventListener eventListener;

	public EaseChatRowVoicePlayClickListener(Activity context, ImageView v, String voiceUrl) {
		this.activity = context;
		voiceIconView = v;
		this.voiceUrl = voiceUrl;
		mManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		eventListener = new MySensorEventListener();
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		voiceIconView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		playMsgId = null;
	}

	public void stopPlayVoiceOn() {

		voiceAnimation.stop();
		voiceIconView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		playMsgId = null;
		mManager.unregisterListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
	}

	public void playVoice(String filePath) {
		mManager.registerListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
				SensorManager.SENSOR_DELAY_NORMAL);// 注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型

		if (!(new File(filePath).exists())) {
			return;
		}
		playMsgId = voiceUrl;
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
				}

			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();
		} catch (Exception e) {
			System.out.println();
		}
	}

	// show the voice playing animation
	@SuppressLint("ResourceType")
	private void showAnimation() {
		// play voice, and start animation
		voiceIconView.setImageResource(R.anim.voice_from_icon);
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		if (isPlaying) {
			Log.d("Easevoice", "EaseChatRowVoicePlayClickListener=====OnClick===" + isPlaying);
			if (playMsgId != null && playMsgId.equals(voiceUrl)) {
				currentPlayListener.stopPlayVoiceOn();
				return;
			}
			currentPlayListener.stopPlayVoiceOn();
		}
		palyVoiceMessage();
	}

	private void palyVoiceMessage() {
		playVoice(voiceUrl);
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
