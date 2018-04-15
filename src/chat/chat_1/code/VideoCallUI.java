package chat.chat_1.code;

import java.util.UUID;

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallManager.EMVideoCallHelper;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.HXHelper;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.yichat.R;
import com.superrtc.sdk.VideoView;

/**
 * 视频通话
 * 
 * @author peidongxu
 * 
 */
public class VideoCallUI extends CallUI implements OnClickListener {

	private boolean isMuteState;
	private boolean isAnswered;
	private boolean endCallTriggerByMe = false;

	// 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
	protected EMCallSurfaceView localSurface;
	protected EMCallSurfaceView oppositeSurface;
	private int surfaceState = -1;

	private RelativeLayout rootContainer;// 整体布局
	private ImageView iv_avatar;// 头像
	private TextView tv_nick;// 昵称
	private TextView tv_call_state;// 呼叫状态

	private LinearLayout z_mute;// 静音
	private LinearLayout z_hangup_call;// 挂断
	private LinearLayout z_switch_camera;// 切换摄像头
	private LinearLayout z_refuse_call;// 挂断
	private LinearLayout z_answer_call;// 接听
	private ImageView iv_mute;// 静音
	private ImageView iv_hangup_call;// 挂断
	private ImageView iv_switch_camera;// 切换摄像头
	private ImageView iv_refuse_call;// 挂断
	private ImageView iv_answer_call;// 接听
	private Chronometer chronometer;// 计时器
	private TextView netwrokStatusVeiw;// 网络状态

	private Handler uiHandler;

	private boolean isInCalling;
	boolean isRecording = false;
	private EMVideoCallHelper callHelper;

	private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();

	// dynamic adjust brightness
	class BrightnessDataProcess implements EMCameraDataProcessor {
		byte yDelta = 0;

		synchronized void setYDelta(byte yDelta) {
			Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
			this.yDelta = yDelta;
		}

		// data size is width*height*2
		// the first width*height is Y, second part is UV
		// the storage layout detailed please refer 2.x demo
		// CameraHelper.onPreviewFrame
		@Override
		public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
			int wh = width * height;
			for (int i = 0; i < wh; i++) {
				int d = (data[i] & 0xFF) + yDelta;
				d = d < 16 ? 16 : d;
				d = d > 235 ? 235 : d;
				data[i] = (byte) d;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			finish();
			return;
		}
		setContentView(R.layout.ease_activity_video_call);

		HXHelper.getInstance().isVideoCalling = true;
		callType = 1;

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		uiHandler = new Handler();

		rootContainer = (RelativeLayout) findViewById(R.id.root_layout);

		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		tv_call_state = (TextView) findViewById(R.id.tv_call_state);
		z_mute = (LinearLayout) findViewById(R.id.z_mute);
		z_hangup_call = (LinearLayout) findViewById(R.id.z_hangup_call);
		z_switch_camera = (LinearLayout) findViewById(R.id.z_switch_camera);
		z_refuse_call = (LinearLayout) findViewById(R.id.z_refuse_call);
		z_answer_call = (LinearLayout) findViewById(R.id.z_answer_call);
		iv_mute = (ImageView) findViewById(R.id.iv_mute);
		iv_hangup_call = (ImageView) findViewById(R.id.iv_hangup_call);
		iv_switch_camera = (ImageView) findViewById(R.id.iv_switch_camera);
		iv_refuse_call = (ImageView) findViewById(R.id.iv_refuse_call);
		iv_answer_call = (ImageView) findViewById(R.id.iv_answer_call);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);

		rootContainer.setOnClickListener(this);
		iv_hangup_call.setOnClickListener(this);
		iv_mute.setOnClickListener(this);
		iv_switch_camera.setOnClickListener(this);
		iv_refuse_call.setOnClickListener(this);
		iv_answer_call.setOnClickListener(this);

		msgid = UUID.randomUUID().toString();
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
		username = getIntent().getStringExtra("id");
		// 设置名称和头像
		new AsyncTopImgLoadTask(this, username, tv_nick, iv_avatar).execute();

		// local surfaceview
		localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
		localSurface.setOnClickListener(this);
		localSurface.setZOrderMediaOverlay(true);
		localSurface.setZOrderOnTop(true);

		// remote surfaceview
		oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);
		oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

		// set call state listener
		addCallStateListener();
		if (!isInComingCall) {
			// 呼叫
			z_hangup_call.setVisibility(View.VISIBLE);
			z_mute.setVisibility(View.VISIBLE);
			z_switch_camera.setVisibility(View.VISIBLE);
			z_refuse_call.setVisibility(View.GONE);
			z_answer_call.setVisibility(View.GONE);

			String st = getResources().getString(R.string.Are_connected_to_each_other);
			tv_call_state.setText(st);
			EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
			handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);

			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
		} else {
			// 来电
			if (EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
				finish();
				return;
			}
			z_hangup_call.setVisibility(View.GONE);
			z_mute.setVisibility(View.GONE);
			z_switch_camera.setVisibility(View.GONE);
			z_refuse_call.setVisibility(View.VISIBLE);
			z_answer_call.setVisibility(View.VISIBLE);
			localSurface.setVisibility(View.INVISIBLE);
			EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);

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

		// get instance of call helper, should be called after setSurfaceView
		// was called
		callHelper = EMClient.getInstance().callManager().getVideoCallHelper();
		// 美颜
		// dataProcessor.setYDelta((byte) (20.0f * (progress - 50) / 50.0f));
		EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);
	}

	/**
	 * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
	 */
	private void changeCallView() {
		if (surfaceState == 0) {
			surfaceState = 1;
			EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
		} else {
			surfaceState = 0;
			EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
		}
	}

	/**
	 * set call state listener
	 */
	void addCallStateListener() {
		callStateListener = new EMCallStateChangeListener() {

			@Override
			public void onCallStateChanged(final CallState callState, final CallError error) {
				switch (callState) {

				case CONNECTING:
					// is connecting
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_call_state.setText(R.string.Are_connected_to_each_other);
						}

					});
					break;
				case CONNECTED:
					// connected
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tv_call_state.setText(R.string.have_connected_with_video);
						}

					});
					break;

				case ACCEPTED:
					// call is accepted
					surfaceState = 0;
					handler.removeCallbacks(timeoutHangup);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								if (soundPool != null)
									soundPool.stop(streamID);
								EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
							} catch (Exception e) {
							}
							openSpeakerOn();
							isInCalling = true;
							chronometer.setVisibility(View.VISIBLE);
							chronometer.setBase(SystemClock.elapsedRealtime());
							// call durations start
							chronometer.start();
							iv_avatar.setVisibility(View.INVISIBLE);
							tv_nick.setVisibility(View.INVISIBLE);
							tv_call_state.setVisibility(View.INVISIBLE);
							callingState = CallingState.NORMAL;
						}

					});
					break;
				case NETWORK_DISCONNECTED:
					runOnUiThread(new Runnable() {
						public void run() {
							netwrokStatusVeiw.setVisibility(View.VISIBLE);
							netwrokStatusVeiw.setText(R.string.network_unavailable);
						}
					});
					break;
				case NETWORK_UNSTABLE:
					runOnUiThread(new Runnable() {
						public void run() {
							netwrokStatusVeiw.setVisibility(View.VISIBLE);
							if (error == CallError.ERROR_NO_DATA) {
								netwrokStatusVeiw.setText(R.string.no_call_data);
							} else {
								netwrokStatusVeiw.setText(R.string.network_unstable);
							}
						}
					});
					break;
				case NETWORK_NORMAL:
					runOnUiThread(new Runnable() {
						public void run() {
							netwrokStatusVeiw.setVisibility(View.INVISIBLE);
						}
					});
					break;
				case VIDEO_PAUSE:
					// runOnUiThread(new Runnable() {
					// public void run() {
					// Toast.makeText(getApplicationContext(), "VIDEO_PAUSE",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
					break;
				case VIDEO_RESUME:
					// runOnUiThread(new Runnable() {
					// public void run() {
					// Toast.makeText(getApplicationContext(), "VIDEO_RESUME",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
					break;
				case VOICE_PAUSE:
					// runOnUiThread(new Runnable() {
					// public void run() {
					// Toast.makeText(getApplicationContext(), "VOICE_PAUSE",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
					break;
				case VOICE_RESUME:
					// runOnUiThread(new Runnable() {
					// public void run() {
					// Toast.makeText(getApplicationContext(), "VOICE_RESUME",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
					break;
				case DISCONNECTED:
					// call is disconnected
					handler.removeCallbacks(timeoutHangup);
					@SuppressWarnings("UnnecessaryLocalVariable")
					final CallError fError = error;
					runOnUiThread(new Runnable() {
						private void postDelayedCloseMsg() {
							uiHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									removeCallStateListener();
									saveCallRecord();
									Animation animation = new AlphaAnimation(1.0f, 0.0f);
									animation.setDuration(1200);
									rootContainer.startAnimation(animation);
									finish();
								}

							}, 200);
						}

						@Override
						public void run() {
							chronometer.stop();
							callDruationText = chronometer.getText().toString();
							String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
							String s2 = getResources().getString(R.string.Connection_failure);
							String s3 = getResources().getString(R.string.The_other_party_is_not_online);
							String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
							String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

							String s6 = getResources().getString(R.string.hang_up);
							String s7 = getResources().getString(R.string.The_other_is_hang_up);
							String s8 = getResources().getString(R.string.did_not_answer);
							String s9 = getResources().getString(R.string.Has_been_cancelled);
							String s10 = getResources().getString(R.string.Refused);

							if (fError == CallError.REJECTED) {
								callingState = CallingState.BEREFUSED;
								tv_call_state.setText(s1);
							} else if (fError == CallError.ERROR_TRANSPORT) {
								tv_call_state.setText(s2);
							} else if (fError == CallError.ERROR_UNAVAILABLE) {
								callingState = CallingState.OFFLINE;
								tv_call_state.setText(s3);
							} else if (fError == CallError.ERROR_BUSY) {
								callingState = CallingState.BUSY;
								tv_call_state.setText(s4);
							} else if (fError == CallError.ERROR_NORESPONSE) {
								callingState = CallingState.NO_RESPONSE;
								tv_call_state.setText(s5);
							} else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
								callingState = CallingState.VERSION_NOT_SAME;
								tv_call_state.setText(R.string.call_version_inconsistent);
							} else {
								if (isRefused) {
									callingState = CallingState.REFUSED;
									tv_call_state.setText(s10);
								} else if (isAnswered) {
									callingState = CallingState.NORMAL;
									if (endCallTriggerByMe) {
										// callStateTextView.setText(s6);
									} else {
										tv_call_state.setText(s7);
									}
								} else {
									if (isInComingCall) {
										callingState = CallingState.UNANSWERED;
										tv_call_state.setText(s8);
									} else {
										if (callingState != CallingState.NORMAL) {
											callingState = CallingState.CANCELLED;
											tv_call_state.setText(s9);
										} else {
											tv_call_state.setText(s6);
										}
									}
								}
							}
							// Toast.makeText(VideoCallUI.this,
							// tv_call_state.getText(),
							// Toast.LENGTH_SHORT).show();
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
		case R.id.local_surface:
			changeCallView();
			break;
		case R.id.iv_refuse_call:
			// 挂断
			isRefused = true;
			iv_refuse_call.setEnabled(false);
			handler.sendEmptyMessage(MSG_CALL_REJECT);
			break;
		case R.id.iv_answer_call:
			// 接听
			EMLog.d(TAG, "btn_answer_call clicked");
			iv_answer_call.setEnabled(false);
			openSpeakerOn();
			if (ringtone != null)
				ringtone.stop();

			tv_call_state.setText("answering...");
			handler.sendEmptyMessage(MSG_CALL_ANSWER);
			isAnswered = true;
			z_hangup_call.setVisibility(View.VISIBLE);
			z_mute.setVisibility(View.VISIBLE);
			z_switch_camera.setVisibility(View.VISIBLE);
			z_refuse_call.setVisibility(View.GONE);
			z_answer_call.setVisibility(View.GONE);
			localSurface.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_hangup_call:
			// 挂断（自己挂）
			iv_hangup_call.setEnabled(false);
			chronometer.stop();
			endCallTriggerByMe = true;
			tv_call_state.setText(getResources().getString(R.string.hanging_up));
			if (isRecording) {
				callHelper.stopVideoRecord();
			}
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
		case R.id.root_layout:
			// 视频图像大小屏切换
			// if (callingState == CallingState.NORMAL) {
			// if (bottomContainer.getVisibility() == View.VISIBLE) {
			// topContainer.setVisibility(View.GONE);
			// oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
			// } else {
			// topContainer.setVisibility(View.VISIBLE);
			// oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
			// }
			// }
			break;
		case R.id.iv_switch_camera:
			// 切换摄像头像
			handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		HXHelper.getInstance().isVideoCalling = false;
		if (isRecording) {
			callHelper.stopVideoRecord();
			isRecording = false;
		}
		localSurface.getRenderer().dispose();
		localSurface = null;
		oppositeSurface.getRenderer().dispose();
		oppositeSurface = null;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		callDruationText = chronometer.getText().toString();
		super.onBackPressed();
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		if (isInCalling) {
			try {
				EMClient.getInstance().callManager().pauseVideoTransfer();
			} catch (HyphenateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isInCalling) {
			try {
				EMClient.getInstance().callManager().resumeVideoTransfer();
			} catch (HyphenateException e) {
				e.printStackTrace();
			}
		}
	}

}
