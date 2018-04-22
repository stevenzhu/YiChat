package video_choose.video_choose_1.code;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import cms_launch_video.cms_launch_video_1.code.CmsLaunchVideoUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shorigo.BaseUI;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 选择本地视频
 * 
 * @author peidongxu
 * 
 */
public class VideoChooseUI extends BaseUI implements OnItemClickListener {

	private GridView gv_video_list;
	private List<VideoFileBean> listVideoFile;
	private VideoChooseAdapter adapter;

	private Gson gson;

	private ProgressDialog progressDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.video_choose_1);
	}

	@Override
	protected void findView_AddListener() {
		gv_video_list = (GridView) findViewById(R.id.gv_video_list);
		gv_video_list.setOnItemClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("选择视频");

		gson = new Gson();

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage("正在加载中...");
		progressDialog.show();

		Type listTemp = new TypeToken<List<VideoFileBean>>() {
		}.getType();
		listVideoFile = gson.fromJson(ACache.get(this).getAsString("list_bd_video"), listTemp);

		if (listVideoFile != null && listVideoFile.size() > 0) {
			if (progressDialog != null)
				progressDialog.dismiss();
		}

		adapter = new VideoChooseAdapter(this, listVideoFile);
		gv_video_list.setAdapter(adapter);

		ScannerAnsyTask ansyTask = new ScannerAnsyTask();
		ansyTask.execute();
	}

	@Override
	protected void onMyClick(View v) {

	}

	public class ScannerAnsyTask extends AsyncTask<Void, Integer, List<VideoFileBean>> {
		private List<VideoFileBean> videoInfos = new ArrayList<VideoFileBean>();

		@Override
		protected List<VideoFileBean> doInBackground(Void... params) {
			listVideoFile = getVideoFile(videoInfos, new File("/sdcard/DCIM/Camera"));
			//debug,魅族
			listVideoFile=getVideoFile(videoInfos,new File("/sdcard/DCIM/Video"));
//			listVideoFile = getVideoFile(videoInfos, Environment.getExternalStorageDirectory());
			runOnUiThread(new Runnable() {
				public void run() {
					if (progressDialog != null)
						progressDialog.dismiss();
					// 数据缓存
					String json = gson.toJson(listVideoFile);
					ACache.get(VideoChooseUI.this).put("list_bd_video", json);
					adapter.setData(listVideoFile);
				}
			});
			return listVideoFile;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(List<VideoFileBean> videoInfos) {
			super.onPostExecute(videoInfos);
		}

		/**
		 * 获取视频文件
		 * 
		 * @param list
		 * @param file
		 * @return
		 */
		private List<VideoFileBean> getVideoFile(final List<VideoFileBean> list, File file) {
			if(!file.exists()){return list;}
			file.listFiles(new FileFilter() {

				@Override
				public boolean accept(final File file) {
					String name = file.getName();
					int i = name.indexOf('.');
					if (i != -1) {
						name = name.substring(i);
						if (name.equalsIgnoreCase(".mp4")) {
							final VideoFileBean videoFileBean = new VideoFileBean();
							videoFileBean.setFileName(file.getName());
							videoFileBean.setFilePath(file.getAbsolutePath());
							videoFileBean.setThumbPath(file.getAbsolutePath());
							String ringDuring = getRingDuring(file.getAbsolutePath());
							if (!Utils.isEmity(ringDuring)) {
								videoFileBean.setDuration(ringDuring);
								list.add(videoFileBean);
							}
							return true;
						}
					} else if (file.isDirectory()) {
						getVideoFile(list, file);
					}
					return false;
				}
			});

			return list;
		}
	}

	@SuppressLint("NewApi")
	public static String getRingDuring(String mUri) {
		String duration = "";
		android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

		try {
			if (mUri != null) {
				mmr.setDataSource(mUri);
			}
			String temp_duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
			if (!Utils.isEmity(temp_duration)) {
				duration = TCUtils.duration(Long.parseLong(temp_duration));
			}
		} catch (Exception ex) {
		} finally {
			mmr.release();
		}
		return duration;
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
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		VideoFileBean videoFileBean = listVideoFile.get(position);
//		Intent intent = new Intent(this, CmsLaunchVideoUI.class);
//		intent.putExtra("url", videoFileBean.getFilePath());
////		intent.putExtra(VIDEO_SCREENSHOT, mMediaObject.getOutputVideoThumbPath());
//		startActivity(intent);

		//
		float fileSize=getFileSizeMB(videoFileBean.getFilePath());
		Log.d("--------------f",fileSize+"");
		if(fileSize>500){
			Toast.makeText(VideoChooseUI.this,"视频文件太大不支持上传！",Toast.LENGTH_SHORT).show();
			return ;
		}else{
			CmsLaunchVideoUI.toAc(this,videoFileBean.getFilePath(),false);
			back();
		}


	}

	@Override
	protected void back() {
		finish();
	}

}
