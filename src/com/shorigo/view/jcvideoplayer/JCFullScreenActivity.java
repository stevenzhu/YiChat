package com.shorigo.view.jcvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shorigo.yichat.R;

public class JCFullScreenActivity extends AppCompatActivity {

	static void toActivityFromNormal(Context context, int state, String url, String title) {
		STATE = state;
		URL = url;
		TITLE = title;
		start = false;
		Intent intent = new Intent(context, JCFullScreenActivity.class);
		context.startActivity(intent);
	}

	/**
	 * <p>
	 * 直接进入全屏播放
	 * </p>
	 * <p>
	 * Full screen play video derictly
	 * </p>
	 * 
	 * @param context
	 *            context
	 * @param url
	 *            video url
	 * @param title
	 *            video title
	 */
	public static void toActivity(Context context, String url, String title) {
		STATE = JCVideoPlayer.CURRENT_STATE_NORMAL;
		URL = url;
		TITLE = title;
		start = true;
		Intent intent = new Intent(context, JCFullScreenActivity.class);
		context.startActivity(intent);
	}

	JCVideoPlayer jcVideoPlayer;
	/**
	 * 刚启动全屏时的播放状态
	 */
	public static int STATE = -1;
	public static String URL;
	public static String TITLE;
	public static boolean manualQuit = false;
	static boolean start = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		View decor = this.getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		setContentView(R.layout.jcvideoplayer_activity_fullscreen);

		jcVideoPlayer = (JCVideoPlayer) findViewById(R.id.jcvideoplayer);
		jcVideoPlayer.setUpForFullscreen(URL, TITLE);
		jcVideoPlayer.setState(STATE);
		manualQuit = false;
		ImageView ivBack = (ImageView) findViewById(R.id.back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				JCVideoPlayer.isClickFullscreen = false;
				jcVideoPlayer.quitFullScreen();
			}
		});
		if (start) {
			jcVideoPlayer.ivStart.performClick();
		} else {
			jcVideoPlayer.isFullscreenFromNormal = true;
			jcVideoPlayer.addSurfaceView();
			if (JCMediaManager.intance().listener != null) {
				JCMediaManager.intance().listener.onCompletion();
			}
			JCMediaManager.intance().listener = jcVideoPlayer;
		}
	}

	@Override
	public void onBackPressed() {
		JCVideoPlayer.isClickFullscreen = false;
		jcVideoPlayer.quitFullScreen();
	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (!manualQuit) {
//			JCVideoPlayer.isClickFullscreen = false;
//			JCVideoPlayer.releaseAllVideos();
//			finish();
//		}
	}
}
