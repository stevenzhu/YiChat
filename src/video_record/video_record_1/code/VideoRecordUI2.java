package video_record.video_record_1.code;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import com.shorigo.BaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.yichat.R;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cms_launch_video.cms_launch_video_1.code.CmsLaunchVideoUI;

/**
 * 小视频录制
 * 
 * @author peidongxu
 * 
 */
public class VideoRecordUI2 extends BaseUI  {


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


	}

	@Override
	protected void onMyClick(View v) {

	}


	@Override
	public void onBackPressed() {
		back();
	}

	@Override
	protected void back() {

	}

}
