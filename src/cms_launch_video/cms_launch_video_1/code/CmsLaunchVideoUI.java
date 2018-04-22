package cms_launch_video.cms_launch_video_1.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import bean.RequestReturnBean;
import silicompressorr.VideoCompress;
import yichat.util.ZFileMnger;
import yichat.util.ZToast;
import yichat.util.ZUIUtil;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyGridView;
import com.shorigo.yichat.R;

/**
 * 发布视频
 * 
 * @author peidongxu
 * 
 */
public class CmsLaunchVideoUI extends BaseUI implements OnTouchListener {
	// 主题内容
	private EditText et_title, et_content;
	private String title, content;
	// 图片和表情切换
	private ImageView iv_biaoqing;
	// 表情数据
	private List<String> listRes;
	private ViewPager vp_expression;
	// 键盘管理
	private InputMethodManager inputMethodManager;
	private ProgressDialog progressDialog;

	private String video_url;
	private VideoView video_view;// 视频播放控件

    private long startTime, endTime;
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_launch_video_1);
	}

	@Override
	protected void findView_AddListener() {
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		et_title = (EditText) findViewById(R.id.et_title);
		et_title.setOnTouchListener(this);
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setOnTouchListener(this);

		video_view = (VideoView) findViewById(R.id.video_view);

		iv_biaoqing = (ImageView) findViewById(R.id.iv_biaoqing);
		iv_biaoqing.setOnClickListener(this);
		StyleUtils.setTabBg(this, iv_biaoqing, new int[] { R.drawable.circle_launch_1_icon_keyboard, R.drawable.circle_launch_1_icon_biaoqing });

		vp_expression = (ViewPager) findViewById(R.id.vp_circle_launch);

	}

	private boolean hasDestroy=false;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hasDestroy=true;
		if(req!=null){
            req.cancel(true);
        }

	}

	//----
	private Boolean isFrom_shot;
	public static void toAc(Context ac, String url,Boolean isFrom_shot){
		Intent in=new Intent(ac,CmsLaunchVideoUI.class);
		in.putExtra("url",url);
		in.putExtra("isFrom_shot",isFrom_shot);
		ac.startActivity(in);
	}

	private void applyP(){
		Bundle p=getIntent().getExtras();
		video_url=p.getString("url");
		isFrom_shot=p.getBoolean("isFrom_shot");
	}
	//------

	@Override
	protected void prepareData() {
		applyP();
		setTitle("发布文章");
		setRightButton("发布");

//		Intent intent = getIntent();
//		video_url = intent.getStringExtra("url");

		playVideo();

		iv_biaoqing.setSelected(false);

		// 初始化表情
		listRes = getExpressionRes(35);
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		vp_expression.setAdapter(new ExpressionPagerAdapter(views));
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 发布
			title = et_title.getText().toString();
			content = et_content.getText().toString();
			if (Utils.isEmity(title)) {
				MyApplication.getInstance().showToast("请输入标题");
				return;
			}
//			if (progressDialog == null) {
//				progressDialog = new ProgressDialog(this);
//			}
//			progressDialog.setMessage("正在提交中...");
//			progressDialog.show();
//			sendDynamic();
			//debug
			if(isFrom_shot){
				sendDynamic(video_url);
			}else {

				ZUIUtil.showDlg(this, "视频处理中...");
				clipVideo();
			}
			break;
		case R.id.iv_biaoqing:
			// 表情
			if (iv_biaoqing.isSelected()) {
				// 切换键盘模式
				iv_biaoqing.setSelected(false);
				vp_expression.setVisibility(View.GONE);
				showKeyboard();
			} else {
				// 切换表情模式
				iv_biaoqing.setSelected(true);
				// 隐藏键盘
				hideKeyboard();
				vp_expression.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}
    private void setTime(Long time,String type){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(time);
        Log.i("--------------",type+" = "+dateFormat.format(date));
    }

    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();
            return (size / 1024f) / 1024f + "MB";
        }
    }

	private float getFileSizeMB(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return 0.0f;
		} else {
			long size = f.length();
			return (size / 1024f) / 1024f;
		}
	}
	private void clipVideo(){

        final String destPath = outputDir + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        VideoCompress.compressVideoLow2(video_url, destPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                startTime = System.currentTimeMillis();
                setTime(startTime,"开始时间");
                Log.i("--------------","压缩前大小 = "+getFileSize(video_url));
            }

            @Override
            public void onSuccess() {
                endTime = System.currentTimeMillis();
                setTime(endTime,"结束时间");
                Log.i("--------------","压缩后大小 = "+getFileSize(destPath));

                runOnUiThread(new Runnable() {
                    //					@Override
					public void run() {
						ZUIUtil.finishDlg();
						sendDynamic(destPath);
					}
				});

            }

            @Override
            public void onFail() {
                endTime = System.currentTimeMillis();
                setTime(endTime,"失败时间");
            }

            @Override
            public void onProgress(float percent) {
                Log.i("------",String.valueOf(percent) + "%");
                //ZUIUtil.showDlg(CmsLaunchVideoUI.this, "视频压缩中.."+String.valueOf(percent) + "%");
            }
        });

		//debug,截取60s
//		EpVideo tmpVd=new EpVideo(video_url);
//		tmpVd.clip(0,60);
//		final String outPath= ZFileMnger.getTmpFile_date(".mp4").getAbsolutePath();
//		EpEditor.OutputOption opt=new EpEditor.OutputOption(outPath);
//		EpEditor.exec(tmpVd, opt, new OnEditorListener() {
//			@Override
//			public void onSuccess() {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						ZUIUtil.finishDlg();
//						sendDynamic(outPath);
//					}
//				});
//			}
//
//			@Override
//			public void onFailure() {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
////						progressDialog.dismiss();
//						ZUIUtil.finishDlg();
//						ZToast.show("视频处理失败!");
//					}
//				});
//			}
//
//			@Override
//			public void onProgress(float progress) {
//
//			}
//		});
	}

	private RequestHandle req;
	/**
	 * 发布动态
	 */
	private void sendDynamic(String videoPath) {
		if(hasDestroy){return;}
		ZUIUtil.showDlg(CmsLaunchVideoUI.this,"上传中...");
		String url = HttpUtil.getUrl("/dynamic/send");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("title", title);
		map.put("content", content);
		RequestParams params = new RequestParams();
		try {
//			video_url
			params.put("video", new File(videoPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		req=HttpUtil.post(this, url, map, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsLaunchVideoJson.analysis(response.toString());
//				progressDialog.dismiss();
				ZUIUtil.finishDlg();
				if (HttpUtil.isSuccess(CmsLaunchVideoUI.this, returnBean.getCode())) {
					back();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
//				progressDialog.dismiss();
				ZUIUtil.finishDlg();
			}
		});
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.cms_launch_1_expression_gridview, null);
		MyGridView gv = (MyGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = listRes.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(listRes.subList(20, listRes.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (filename != "delete_expression") { // 不是删除键，显示表情
						// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
						Class clz = SmileUtils.class;
						Field field = clz.getField(filename);
						et_content.append(SmileUtils.getSmiledText(CmsLaunchVideoUI.this, (String) field.get(null)));
					} else { // 删除文字或者表情
						if (!TextUtils.isEmpty(et_content.getText())) {
							int selectionStart = et_content.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = et_content.getText().toString();
								String tempStr = body.substring(0, selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1) {
									CharSequence cs = tempStr.substring(i, selectionStart);
									if (SmileUtils.containsKey(cs.toString()))
										et_content.getEditableText().delete(i, selectionStart);
									else
										et_content.getEditableText().delete(selectionStart - 1, selectionStart);
								} else {
									et_content.getEditableText().delete(selectionStart - 1, selectionStart);
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;
	}

	private void playVideo() {
		video_view.setVideoPath(video_url);
		video_view.start();
		video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setVolume(0, 0);
				mp.start();
				mp.setLooping(true);
			}
		});
		video_view.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return true;// 如果设置true就可以防止他弹出错误的提示框！
			}
		});

		video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.setVolume(0, 0);
				video_view.setVideoPath(video_url);
				video_view.start();
			}
		});

	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示软键盘
	 */
	private void showKeyboard() {
		et_content.setFocusable(true);
		et_content.setFocusableInTouchMode(true);
		et_content.requestFocus();
		inputMethodManager = (InputMethodManager) et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(et_content, 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		iv_biaoqing.setSelected(false);
		vp_expression.setVisibility(View.GONE);
		return false;
	}

	@Override
	protected void back() {
		setResult(RESULT_OK);
		finish();

	}

//	private Locale getLocale() {
//		Configuration config = getResources().getConfiguration();
//		Locale sysLocale = null;
//		if (Build.VERSION.SDK_INT >= 24) {
//			sysLocale = getSystemLocale(config);
//		} else {
//			sysLocale = getSystemLocaleLegacy(config);
//		}
//
//		return sysLocale;
//	}
//
//	@SuppressWarnings("deprecation")
//	public static Locale getSystemLocaleLegacy(Configuration config){
//		return config.locale;
//	}
//
//
//	public static Locale getSystemLocale(Configuration config){
//		return config.getLocales().get(0);
//	}
}
