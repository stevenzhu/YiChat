package chat.chat_1.code;

import java.util.UUID;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.HXHelper;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.yichat.R;

/**
 * 语音通话页面
 * 
 */
public class VoiceCallUI extends CallUI implements OnClickListener {
	private ImageView iv_refuse_call;// 挂断
	private ImageView iv_answer_call;// 接听
	private ImageView iv_mute;// 静音
	private ImageView iv_cancle;// 取消
	private ImageView iv_handsfree;// 免提
	private LinearLayout z_refuse_call;// 挂断
	private LinearLayout z_answer_call;// 接听
	private LinearLayout z_mute;// 静音
	private LinearLayout z_cancle;// 取消
	private LinearLayout z_handsfree;// 免提

	private boolean isMuteState;// 静音状态
	private boolean isHandsfreeState;// 免提状态

	private TextView tv_call_state;
	private boolean endCallTriggerByMe = false;
	private Chronometer chronometer;
	String tip;
	private TextView tv_network_status;
	private ImageView iv_avatar;
	private TextView tv_nick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			finish();
			return;
		}
		setContentView(R.layout.ease_activity_voice_call);

		HXHelper.getInstance().isVoiceCalling = true;
		callType = 0;

		z_refuse_call = (LinearLayout) findViewById(R.id.z_refuse_call);
		z_answer_call = (LinearLayout) findViewById(R.id.z_answer_call);
		z_mute = (LinearLayout) findViewById(R.id.z_mute);
		z_cancle = (LinearLayout) findViewById(R.id.z_cancle);
		z_handsfree = (LinearLayout) findViewById(R.id.z_handsfree);

		iv_refuse_call = (ImageView) findViewById(R.id.iv_refuse_call);
		iv_answer_call = (ImageView) findViewById(R.id.iv_answer_call);
		iv_mute = (ImageView) findViewById(R.id.iv_mute);
		iv_cancle = (ImageView) findViewById(R.id.iv_cancle);
		iv_handsfree = (ImageView) findViewById(R.id.iv_handsfree);

		tv_call_state = (TextView) findViewById(R.id.tv_call_state);
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		tv_network_status = (TextView) findViewById(R.id.tv_network_status);

		iv_refuse_call.setOnClickListener(this);
		iv_answer_call.setOnClickListener(this);
		iv_cancle.setOnClickListener(this);
		iv_mute.setOnClickListener(this);
		iv_handsfree.setOnClickListener(this);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		addCallStateListener();
		msgid = UUID.randomUUID().toString();

		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
		username = getIntent().getStringExtra("id");
		// 设置名称和头像
		new AsyncTopImgLoadTask(this, username, tv_nick, iv_avatar).execute();

		if (!isInComingCall) {
			// 呼叫
			z_mute.setVisibility(View.VISIBLE);
			z_cancle.setVisibility(View.VISIBLE);
			z_handsfree.setVisibility(View.VISIBLE);

			tip = getResources().getString(R.string.Are_connected_to_each_other);
			tv_call_state.setText(tip);
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
			handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
		} else {
			// 来电
			z_refuse_call.setVisibility(View.VISIBLE);
			z_answer_call.setVisibility(View.VISIBLE);

			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
			// Uri ringUri =
			// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			// audioManager.setMode(AudioManager.MODE_RINGTONE);
			// audioManager.setSpeakerphoneOn(true);
			// ringtone = RingtoneManager.getRingtone(this, ringUri);
			// ringtone.play();
		}
		final int MAKE_CALL_TIMEOUT = 50 * 1000;
		handler.removeCallbacks(timeoutHangup);
		handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
	}

	/**
	 * set call state listener
	 */
	void addCallStateListener() {
		callStateListener = new EMCallStateChangeListener() {

			@Override
			public void onCallStateChanged(CallState callState, final CallError error) {
				// Message msg = handler.obtainMessage();
				EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
				switch (callState) {
				case CONNECTING:
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_call_state.setText(tip);
						}
					});
					break;
				case CONNECTED:
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							String st3 = getResources().getString(R.string.have_connected_with_voice);
							tv_call_state.setText(st3);
						}
					});
					break;
				case ACCEPTED:
					handler.removeCallbacks(timeoutHangup);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								if (soundPool != null)
									soundPool.stop(streamID);
							} catch (Exception e) {
							}
							if (!isHandsfreeState)
								closeSpeakerOn();
							chronometer.setVisibility(View.VISIBLE);
							chronometer.setBase(SystemClock.elapsedRealtime());
							chronometer.start();
							tv_call_state.setText("");
							callingState = CallingState.NORMAL;
						}
					});
					break;
				case NETWORK_UNSTABLE:
					// 网络不稳定
					runOnUiThread(new Runnable() {
						public void run() {
							tv_network_status.setVisibility(View.VISIBLE);
							if (error == CallError.ERROR_NO_DATA) {
								tv_network_status.setText(R.string.no_call_data);
							} else {
								tv_network_status.setText(R.string.network_unstable);
							}
						}
					});
					break;
				case NETWORK_NORMAL:
					runOnUiThread(new Runnable() {
						public void run() {
							tv_network_status.setVisibility(View.GONE);
						}
					});
					break;
				case VOICE_PAUSE:
					// 静音
					break;
				case VOICE_RESUME:
					// 恢复静音
					break;
				case DISCONNECTED:
					handler.removeCallbacks(timeoutHangup);
					@SuppressWarnings("UnnecessaryLocalVariable")
					final CallError fError = error;
					runOnUiThread(new Runnable() {
						private void postDelayedCloseMsg() {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Log.d("AAA", "CALL DISCONNETED");
											removeCallStateListener();
											saveCallRecord();
											Animation animation = new AlphaAnimation(1.0f, 0.0f);
											animation.setDuration(800);
											findViewById(R.id.root_layout).startAnimation(animation);
											finish();
										}
									});
								}
							}, 200);
						}

						@Override
						public void run() {
							chronometer.stop();
							callDruationText = chronometer.getText().toString();
							String st1 = getResources().getString(R.string.Refused);
							String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
							String st3 = getResources().getString(R.string.Connection_failure);
							String st4 = getResources().getString(R.string.The_other_party_is_not_online);
							String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);

							String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
							String st7 = getResources().getString(R.string.hang_up);
							String st8 = getResources().getString(R.string.The_other_is_hang_up);

							String st9 = getResources().getString(R.string.did_not_answer);
							String st10 = getResources().getString(R.string.Has_been_cancelled);
							String st11 = getResources().getString(R.string.hang_up);

							if (fError == CallError.REJECTED) {
								callingState = CallingState.BEREFUSED;
								tv_call_state.setText(st2);
							} else if (fError == CallError.ERROR_TRANSPORT) {
								tv_call_state.setText(st3);
							} else if (fError == CallError.ERROR_UNAVAILABLE) {
								callingState = CallingState.OFFLINE;
								tv_call_state.setText(st4);
							} else if (fError == CallError.ERROR_BUSY) {
								callingState = CallingState.BUSY;
								tv_call_state.setText(st5);
							} else if (fError == CallError.ERROR_NORESPONSE) {
								callingState = CallingState.NO_RESPONSE;
								tv_call_state.setText(st6);
							} else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
								callingState = CallingState.VERSION_NOT_SAME;
								tv_call_state.setText(R.string.call_version_inconsistent);
							} else {
								if (isRefused) {
									callingState = CallingState.REFUSED;
									tv_call_state.setText(st1);
								} else if (isAnswered) {
									callingState = CallingState.NORMAL;
									if (endCallTriggerByMe) {
										// callStateTextView.setText(st7);
									} else {
										tv_call_state.setText(st8);
									}
								} else {
									if (isInComingCall) {
										callingState = CallingState.UNANSWERED;
										tv_call_state.setText(st9);
									} else {
										if (callingState != CallingState.NORMAL) {
											callingState = CallingState.CANCELLED;
											tv_call_state.setText(st10);
										} else {
											tv_call_state.setText(st11);
										}
									}
								}
							}
							postDelayedCloseMsg();
						}
					});
					break;
				default:
					break;
				}
			}
		};
		EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
	}

	void removeCallStateListener() {
		EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_refuse_call:
			// 挂断
			isRefused = true;
			iv_refuse_call.setEnabled(false);
			handler.sendEmptyMessage(MSG_CALL_REJECT);
			break;
		case R.id.iv_answer_call:
			// 接听
			iv_answer_call.setEnabled(false);
			closeSpeakerOn();
			z_refuse_call.setVisibility(View.GONE);
			z_answer_call.setVisibility(View.GONE);
			z_mute.setVisibility(View.VISIBLE);
			z_cancle.setVisibility(View.VISIBLE);
			z_handsfree.setVisibility(View.VISIBLE);
			handler.sendEmptyMessage(MSG_CALL_ANSWER);
			break;
		case R.id.iv_cancle:
			// 取消
			iv_cancle.setEnabled(false);
			chronometer.stop();
			endCallTriggerByMe = true;
			handler.sendEmptyMessage(MSG_CALL_END);
			break;
		case R.id.iv_mute:
			// 静音
			if (isMuteState) {
				iv_mute.setImageResource(R.drawable.em_icon_mute_normal);
				try {
					EMClient.getInstance().callManager().resumeVoiceTransfer();
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
				isMuteState = false;
			} else {
				iv_mute.setImageResource(R.drawable.em_icon_mute_on);
				try {
					EMClient.getInstance().callManager().pauseVoiceTransfer();
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree:
			// 免提
			if (isHandsfreeState) {
				iv_handsfree.setImageResource(R.drawable.em_icon_speaker_normal);
				closeSpeakerOn();
				isHandsfreeState = false;
			} else {
				iv_handsfree.setImageResource(R.drawable.em_icon_speaker_on);
				openSpeakerOn();
				isHandsfreeState = true;
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		HXHelper.getInstance().isVoiceCalling = false;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		callDruationText = chronometer.getText().toString();
	}

}
