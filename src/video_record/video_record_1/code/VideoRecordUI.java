package video_record.video_record_1.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cms_launch_video.cms_launch_video_1.code.CmsLaunchVideoUI;

import com.shorigo.BaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.yichat.R;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;

/**
 * 小视频录制
 * 
 * @author peidongxu
 * 
 */
public class VideoRecordUI extends BaseUI implements TXRecordCommon.ITXVideoRecordListener, View.OnTouchListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
	private static final String TAG = "TCVideoRecordActivity";
	private boolean mRecording = false;
	private boolean mStartPreview = false;
	private boolean mFront = false;
	private TXUGCRecord mTXCameraRecord;
	private TXRecordCommon.TXRecordResult mTXRecordResult;
	private long mDuration; // 视频总时长

	private TXCloudVideoView mVideoView;
	private ImageView mIvConfirm;
	private TextView mProgressTime;
	private ImageView mIvTorch;

	private AudioManager mAudioManager;
	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusListener;
	private boolean mPause = false;
	private int mCurrentAspectRatio;
	private FrameLayout mMaskLayout;
	private RecordProgressView mRecordProgressView;
	private ImageView mIvDeleteLastPart;
	private boolean isSelected = false; // 回删状态
	private long mLastClickTime;
	private boolean mIsTorchOpen = false; // 闪光灯的状态

	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private float mScaleFactor;
	private float mLastScaleFactor;
    private ImageView iv_record;
	private int mRecommendQuality = TXRecordCommon.VIDEO_QUALITY_MEDIUM;
	private int mMinDuration = 1 * 1000;
	private int mMaxDuration = 15 * 1000;
	private int mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_3_4; // 视频比例
	private int mRecordSpeed = TXRecordCommon.RECORD_SPEED_NORMAL;

	@Override
	protected void loadViewLayout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_record_1);
	}

	@Override
	protected void findView_AddListener() {

	}

	@Override
	protected void prepareData() {

		initViews();

		getData();
	}

	private void getData() {
		mCurrentAspectRatio = mAspectRatio;
		mRecordProgressView.setMaxDuration(mMaxDuration);
		mRecordProgressView.setMinDuration(mMinDuration);
	}

	private void startCameraPreview() {
		if (mStartPreview)
			return;
		mStartPreview = true;

		mTXCameraRecord = TXUGCRecord.getInstance(this.getApplicationContext());
		mTXCameraRecord.setVideoRecordListener(this);

		// 推荐配置
		TXRecordCommon.TXUGCSimpleConfig simpleConfig = new TXRecordCommon.TXUGCSimpleConfig();
		simpleConfig.videoQuality = mRecommendQuality;
		simpleConfig.minDuration = mMinDuration;
		simpleConfig.maxDuration = mMaxDuration;
		simpleConfig.isFront = mFront;

		mTXCameraRecord.setRecordSpeed(mRecordSpeed);
		mTXCameraRecord.startCameraSimplePreview(simpleConfig, mVideoView);
		// mTXCameraRecord.setAspectRatio(mCurrentAspectRatio);
	}

	private void initViews() {
		mMaskLayout = (FrameLayout) findViewById(R.id.mask);
		mMaskLayout.setOnTouchListener(this);

		iv_record = (ImageView) findViewById(R.id.iv_record);
		iv_record.setOnClickListener(this);
		mIvConfirm = (ImageView) findViewById(R.id.btn_confirm);
		mIvConfirm.setOnClickListener(this);
		mIvConfirm.setImageResource(R.drawable.ugc_confirm_disable);
		mIvConfirm.setEnabled(false);

		mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
		mVideoView.enableHardwareDecode(true);

		mProgressTime = (TextView) findViewById(R.id.progress_time);
		mIvDeleteLastPart = (ImageView) findViewById(R.id.btn_delete_last_part);
		mIvDeleteLastPart.setOnClickListener(this);

		mRecordProgressView = (RecordProgressView) findViewById(R.id.record_progress_view);

		mGestureDetector = new GestureDetector(this, this);
		mScaleGestureDetector = new ScaleGestureDetector(this, this);

		mIvTorch = (ImageView) findViewById(R.id.btn_torch);
		mIvTorch.setOnClickListener(this);

		if (!mFront) {
			mIvTorch.setImageResource(R.drawable.ugc_torch_disable);
			mIvTorch.setEnabled(false);
		} else {
			mIvTorch.setImageResource(R.drawable.selector_torch_close);
			mIvTorch.setEnabled(true);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (hasPermission()) {
			startCameraPreview();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mTXCameraRecord != null) {
			mTXCameraRecord.setVideoProcessListener(null); // 这里要取消监听，否则在上面的回调中又会重新开启预览
			mTXCameraRecord.stopCameraPreview();
			mStartPreview = false;
			// 设置闪光灯的状态为关闭
			if (mIsTorchOpen) {
				mIsTorchOpen = false;
				if (!mFront) {
					mIvTorch.setImageResource(R.drawable.ugc_torch_disable);
					mIvTorch.setEnabled(false);
				} else {
					mIvTorch.setImageResource(R.drawable.selector_torch_close);
					mIvTorch.setEnabled(true);
				}
			}
		}
		if (mRecording && !mPause) {
			pauseRecord();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		TXCLog.i(TAG, "onDestroy");
		if (mRecordProgressView != null) {
			mRecordProgressView.release();
		}

		if (mTXCameraRecord != null) {
			mTXCameraRecord.stopCameraPreview();
			mTXCameraRecord.setVideoRecordListener(null);
			mTXCameraRecord.getPartsManager().deleteAllParts();
			mTXCameraRecord.release();
			mTXCameraRecord = null;
			mStartPreview = false;
		}
		abandonAudioFocus();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mTXCameraRecord != null) {
			mTXCameraRecord.stopCameraPreview();
		}

		if (mRecording && !mPause) {
			pauseRecord();
		}
		mStartPreview = false;

		startCameraPreview();
	}

	@Override
	public void onMyClick(View view) {
		switch (view.getId()) {
		case R.id.btn_switch_camera:
			mFront = !mFront;
			mIsTorchOpen = false;
			if (!mFront) {
				mIvTorch.setImageResource(R.drawable.ugc_torch_disable);
				mIvTorch.setEnabled(false);
			} else {
				mIvTorch.setImageResource(R.drawable.selector_torch_close);
				mIvTorch.setEnabled(true);
			}
			if (mTXCameraRecord != null) {
				TXCLog.i(TAG, "switchCamera = " + mFront);
				mTXCameraRecord.switchCamera(mFront);
			}
			break;
		case R.id.iv_record:
			switchRecord();
			break;
		case R.id.btn_confirm:
			stopRecord();
			break;
		case R.id.btn_delete_last_part:
			deleteLastPart();
			break;
		case R.id.btn_torch:
			toggleTorch();
			break;
		default:
			break;
		}
	}

	private void toggleTorch() {
		if (mIsTorchOpen) {
			mTXCameraRecord.toggleTorch(false);
			mIvTorch.setImageResource(R.drawable.selector_torch_close);
		} else {
			mTXCameraRecord.toggleTorch(true);
			mIvTorch.setImageResource(R.drawable.selector_torch_open);
		}
		mIsTorchOpen = !mIsTorchOpen;
	}

	private void deleteLastPart() {
		Log.d("--------mRecording",mRecording+"--isSelected"+isSelected);
		if (!mRecording) {
			return;
		}
		if (isSelected) {
			isSelected = true;
			mRecordProgressView.selectLast();
		} else {
			isSelected = false;
			mRecordProgressView.deleteLast();
			mTXCameraRecord.getPartsManager().deleteLastPart();
			int timeSecond = mTXCameraRecord.getPartsManager().getDuration() / 1000;
			mProgressTime.setText(String.format(Locale.CHINA, "00:%02d", timeSecond));
			if (timeSecond < mMinDuration / 1000) {
				mIvConfirm.setImageResource(R.drawable.ugc_confirm_disable);
				mIvConfirm.setEnabled(false);
			} else {
				mIvConfirm.setImageResource(R.drawable.selector_record_confirm);
				mIvConfirm.setEnabled(true);
			}

		}
	}

	private void switchRecord() {
		long currentClickTime = System.currentTimeMillis();
		if (currentClickTime - mLastClickTime < 200) {
			return;
		}
		if (mRecording) {
			if (mPause) {
				if (mTXCameraRecord.getPartsManager().getPartsPathList().size() == 0) {
					iv_record.setImageResource(R.drawable.pause_rd);
					startRecord();
				} else {
					iv_record.setImageResource(R.drawable.pause_rd);
					resumeRecord();
				}
			} else {
				iv_record.setImageResource(R.drawable.video_record_1_icon_record);
				pauseRecord();
			}
		} else {
			//iv_record.setImageResource(R.drawable.video_record_1_icon_record);
			iv_record.setImageResource(R.drawable.pause_rd);
			startRecord();
		}
		mLastClickTime = currentClickTime;
	}

	private void resumeRecord() {
		if (mTXCameraRecord == null) {
			return;
		}
		int startResult = mTXCameraRecord.resumeRecord();

		mIvDeleteLastPart.setImageResource(R.drawable.ugc_delete_last_part_disable);
		mIvDeleteLastPart.setEnabled(false);

		mPause = false;
		isSelected = false;
		requestAudioFocus();
	}

	private void pauseRecord() {
		mPause = true;
		mIvDeleteLastPart.setImageResource(R.drawable.selector_delete_last_part);
		mIvDeleteLastPart.setEnabled(true);

		if (mTXCameraRecord != null) {
			mTXCameraRecord.pauseRecord();
		}
		abandonAudioFocus();

	}

	private void stopRecord() {
		if (mTXCameraRecord != null) {
			mTXCameraRecord.stopRecord();
		}
		mRecording = false;
		mPause = false;
		abandonAudioFocus();

	}

	private void startRecord() {
		// 在开始录制的时候，就不能再让activity旋转了，否则生成视频出错
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		if (mTXCameraRecord == null) {
			mTXCameraRecord = TXUGCRecord.getInstance(this.getApplicationContext());
		}

		String customVideoPath = getCustomVideoOutputPath();
		String customCoverPath = customVideoPath.replace(".mp4", ".jpg");

		int result = mTXCameraRecord.startRecord(customVideoPath, customCoverPath);

		mIvDeleteLastPart.setImageResource(R.drawable.ugc_delete_last_part_disable);
		mIvDeleteLastPart.setEnabled(false);

		mRecording = true;
		mPause = false;
		requestAudioFocus();

	}

	private String getCustomVideoOutputPath() {
		String tempOutputPath = Constants.path + Constants._video + "/" + System.currentTimeMillis() + ".mp4";
		return tempOutputPath;
	}

	private void startPreview() {
		if (mTXRecordResult != null && (mTXRecordResult.retCode == TXRecordCommon.RECORD_RESULT_OK || mTXRecordResult.retCode == TXRecordCommon.RECORD_RESULT_OK_REACHED_MAXDURATION || mTXRecordResult.retCode == TXRecordCommon.RECORD_RESULT_OK_LESS_THAN_MINDURATION)) {
//			Intent intent = new Intent(getApplicationContext(), CmsLaunchVideoUI.class);
//			intent.putExtra("url", mTXRecordResult.videoPath);
//			startActivity(intent);
			CmsLaunchVideoUI.toAc(this,mTXRecordResult.videoPath,true);
			back();
		}
	}

	@Override
	public void onRecordEvent(int event, Bundle param) {
		TXCLog.d(TAG, "onRecordEvent event id = " + event);
		if (event == TXRecordCommon.EVT_ID_PAUSE) {
			mRecordProgressView.clipComplete();
		} else if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
//			Toast.makeText(this, "摄像头打开失败，请检查权限", Toast.LENGTH_SHORT).show();
		} else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
//			Toast.makeText(this, "麦克风打开失败，请检查权限", Toast.LENGTH_SHORT).show();
		} else if (event == TXRecordCommon.EVT_ID_RESUME) {

		}
	}

	@Override
	public void onRecordProgress(long milliSecond) {
		TXCLog.i(TAG, "onRecordProgress, mRecordProgressView = " + mRecordProgressView);
		if (mRecordProgressView == null) {
			return;
		}
		mRecordProgressView.setProgress((int) milliSecond);
		float timeSecondFloat = milliSecond / 1000f;
		int timeSecond = Math.round(timeSecondFloat);
		mProgressTime.setText(String.format(Locale.CHINA, "00:%02d", timeSecond));
		if (timeSecondFloat < mMinDuration / 1000) {
			mIvConfirm.setImageResource(R.drawable.ugc_confirm_disable);
			mIvConfirm.setEnabled(false);
		} else {
			mIvConfirm.setImageResource(R.drawable.selector_record_confirm);
			mIvConfirm.setEnabled(true);
		}
	}

	@Override
	public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
		mTXRecordResult = result;

		TXCLog.i(TAG, "onRecordComplete, result retCode = " + result.retCode + ", descMsg = " + result.descMsg + ", videoPath + " + result.videoPath + ", coverPath = " + result.coverPath);
		if (mTXRecordResult.retCode < 0) {
			ImageView liveRecord = (ImageView) findViewById(R.id.iv_record);
			if (liveRecord != null)
				liveRecord.setBackgroundResource(R.drawable.start_record);
			mRecording = false;

			int timeSecond = mTXCameraRecord.getPartsManager().getDuration() / 1000;
			mProgressTime.setText(String.format(Locale.CHINA, "00:%02d", timeSecond));
			Toast.makeText(VideoRecordUI.this.getApplicationContext(), "录制失败，原因：" + mTXRecordResult.descMsg, Toast.LENGTH_SHORT).show();
		} else {
			mDuration = mTXCameraRecord.getPartsManager().getDuration();
			if (mTXCameraRecord != null) {
				mTXCameraRecord.getPartsManager().deleteAllParts();
			}
			startPreview();
		}
	}

	private void requestAudioFocus() {
		if (null == mAudioManager) {
			mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		}

		if (null == mOnAudioFocusListener) {
			mOnAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

				@Override
				public void onAudioFocusChange(int focusChange) {
					try {
						TXCLog.i(TAG, "requestAudioFocus, onAudioFocusChange focusChange = " + focusChange);

						if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
							pauseRecord();
						} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
							pauseRecord();
						} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

						} else {
							pauseRecord();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}
		try {
			mAudioManager.requestAudioFocus(mOnAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void abandonAudioFocus() {
		try {
			if (null != mAudioManager && null != mOnAudioFocusListener) {
				mAudioManager.abandonAudioFocus(mOnAudioFocusListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
		case 100:
			for (int ret : grantResults) {
				if (ret != PackageManager.PERMISSION_GRANTED) {
					return;
				}
			}
			startCameraPreview();
			break;
		default:
			break;
		}
	}

	private boolean hasPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			List<String> permissions = new ArrayList<String>();
			if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
				permissions.add(Manifest.permission.CAMERA);
			}
			if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
				permissions.add(Manifest.permission.RECORD_AUDIO);
			}
			if (permissions.size() != 0) {
				ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (view == mMaskLayout) {
			if (motionEvent.getPointerCount() >= 2) {
				mScaleGestureDetector.onTouchEvent(motionEvent);
			} else if (motionEvent.getPointerCount() == 1) {
				mGestureDetector.onTouchEvent(motionEvent);
			}
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}

	@Override
	public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
		int maxZoom = mTXCameraRecord.getMaxZoom();
		if (maxZoom == 0) {
			TXCLog.i(TAG, "camera not support zoom");
			return false;
		}

		float factorOffset = scaleGestureDetector.getScaleFactor() - mLastScaleFactor;

		mScaleFactor += factorOffset;
		mLastScaleFactor = scaleGestureDetector.getScaleFactor();
		if (mScaleFactor < 0) {
			mScaleFactor = 0;
		}
		if (mScaleFactor > 1) {
			mScaleFactor = 1;
		}

		int zoomValue = Math.round(mScaleFactor * maxZoom);
		mTXCameraRecord.setZoom(zoomValue);
		return false;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
		mLastScaleFactor = scaleGestureDetector.getScaleFactor();
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

	}

	@Override
	public void onBackPressed() {
		back();
	}

	@Override
	protected void back() {
		if (!mRecording) {
			finish();
		}
		if (mPause) {
			if (mTXCameraRecord != null) {
				mTXCameraRecord.getPartsManager().deleteAllParts();
			}
			finish();
		} else {
			pauseRecord();
		}
	}

}
