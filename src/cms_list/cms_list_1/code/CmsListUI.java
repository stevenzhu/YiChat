package cms_list.cms_list_1.code;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import video_choose.video_choose_1.code.VideoChooseUI;
import video_record.video_record_1.code.VideoRecordUI;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import bean.CmsBean;
import bean.RequestReturnBean;
import cms_detail.cms_detail_1.code.CmsDetailUI;
import cms_detail_img.cms_detail_img_1.code.CmsDetailImgUI;
import cms_launch.cms_launch_1.code.CmsLaunchUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.utils.Constant;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.MyConfig;
import com.shorigo.view.MyListView;
import com.shorigo.view.refresh.PullRefreshUtil;
import com.shorigo.view.refresh.PullRefreshView;
import com.shorigo.view.refresh.PullRefreshView.OnPullListener;
import com.shorigo.yichat.R;

/**
 * 动态列表
 * 
 * @author peidongxu
 * 
 */
public class CmsListUI extends BaseUI implements OnPullListener, OnItemClickListener {
	private View z_title;
	private TextView tv_hot, tv_new,tvCity;
	private PullRefreshView refresh_view;
	private int currertPage; // 当前页数
	private boolean isRef; // 是否刷新

	private List<CmsBean> listCmsBean;
	private MyListView lv_list;
	private CmsListAdapter adapter;

	private Gson gson;

	private String type;// 2热门 1最新

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_list_1);
	}

	@Override
	protected void findView_AddListener() {

		z_title = findViewById(R.id.z_title);
		tv_hot = (TextView) findViewById(R.id.tv_hot);
		tv_hot.setOnClickListener(onClick_tab);
		tv_new = (TextView) findViewById(R.id.tv_new);
		tv_new.setOnClickListener(onClick_tab);
//		StyleUtils.setTabBg(this, tv_hot, new int[] { R.drawable.video_topic_list_1_bg_tab_left_down, R.drawable.video_topic_list_1_bg_tab_left_up });\
		tvCity = (TextView) findViewById(R.id.tvCity);
		tvCity.setOnClickListener(onClick_tab);

		refresh_view = (PullRefreshView) findViewById(R.id.refresh_view);
		refresh_view.setOnPullListener(this);
		PullRefreshUtil.setRefresh(refresh_view, true, false);
		lv_list = (MyListView) findViewById(R.id.lv_list);
		lv_list.setOnItemClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("动态");
		setRightButton(R.drawable.circle_list_1_icon_camera);

		currertPage = 1;
		isRef = true;
		type = "2";
		tv_hot.setSelected(true);
		tv_new.setSelected(false);

		gson = new Gson();
		Type listCmsTemp = new TypeToken<List<CmsBean>>() {
		}.getType();
		listCmsBean = gson.fromJson(ACache.get(this).getAsString("cms_list_1_" + type), listCmsTemp);

		adapter = new CmsListAdapter(this, listCmsBean, mHandler);
		lv_list.setAdapter(adapter);

		getCmslist();
	}

	private View.OnClickListener onClick_tab=new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.tv_hot:
					// 最热
					type = "2";
					break;
				case R.id.tv_new:
					// 最新
					type="1";
					break;
				case R.id.tvCity:
					//同城
					type = "3";
					break;
				default:
					break;
			}
			isRef=true;
			currertPage=1;
			getCmslist();
		}
	};

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			new MoreMenuPop(z_title, this, mHandler);
			break;
		default:
			break;
		}
	}

	/**
	 * 分类下cms列表
	 */
	private void getCmslist() {
		String url = HttpUtil.getUrl("/dynamic/list");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("type", type);
		map.put("p", String.valueOf(currertPage));
		map.put("num", "15");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.d("##---------",response.toString());
				RequestReturnBean returnBean = CmsListJson.getCmsList(response.toString());
				if (HttpUtil.isSuccess(CmsListUI.this, returnBean.getCode())) {
					List<CmsBean> listTemp = returnBean.getListObject();
					if (listTemp != null && listTemp.size() == 15) {
						PullRefreshUtil.setRefresh(refresh_view, true, true);
					} else {
						PullRefreshUtil.setRefresh(refresh_view, true, false);
					}
					if (isRef) {
						listCmsBean = listTemp;
						// 数据缓存
						String json = gson.toJson(listCmsBean);
						ACache.get(CmsListUI.this).put("cms_list_1_" + type, json);
					} else {
						listCmsBean.addAll(listTemp);
					}

					adapter.setData(listCmsBean);
				}
				if (refresh_view != null) {
					refresh_view.refreshFinish();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				if (refresh_view != null) {
					refresh_view.refreshFinish();
				}
			}
		});
	}

	/**
	 * 删除cms
	 */
	private void delCms(final int position) {
		String url = HttpUtil.getUrl("/dynamic/del");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", listCmsBean.get(position).getId());
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsListJson.delCms(response.toString());
				if (HttpUtil.isSuccess(CmsListUI.this, returnBean.getCode())) {
					listCmsBean.remove(position);
					adapter.setData(listCmsBean);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		CmsBean cmsBean = listCmsBean.get(position);
		if ("1".equals(cmsBean.getType())) {
			Intent intent = new Intent(this, CmsDetailUI.class);
			intent.putExtra("id", cmsBean.getId());
			startActivity(intent);
		} else if ("2".equals(cmsBean.getType())) {
			Intent intent = new Intent(this, CmsDetailUI.class);
			intent.putExtra("id", cmsBean.getId());
			startActivity(intent);
		} else if ("3".equals(cmsBean.getType())) {
			Intent intent = new Intent(this, CmsDetailImgUI.class);
			intent.putExtra("id", cmsBean.getId());
			startActivity(intent);
		} else if ("4".equals(cmsBean.getType())) {
			Intent intent = new Intent(this, CmsDetailUI.class);
			intent.putExtra("id", cmsBean.getId());
			startActivity(intent);
		}
		//默认,无图片
		else{
			Intent intent = new Intent(this, CmsDetailUI.class);
			intent.putExtra("id", cmsBean.getId());
			startActivity(intent);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 删除
				delCms(msg.arg1);
				break;
			case 100:
				// 发布图文
				startActivity(new Intent(CmsListUI.this, CmsLaunchUI.class));
				break;
			case 101:
				// 发布视频
				startActivity(new Intent(CmsListUI.this, VideoRecordUI.class));
				break;
			case 102:
				// 选择视频
				startActivity(new Intent(CmsListUI.this, VideoChooseUI.class));
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onRefresh() {
		// 刷新
		currertPage = 1;
		isRef = true;
		getCmslist();
	}

	@Override
	public void onLoad() {
		// 加载更多
		currertPage++;
		isRef = false;
		getCmslist();
	}

	@Override
	protected void back() {
		finish();
	}


	/**
	 * 打开相册初始化,回传数据在onActivityResult方法中
	 *
	 * @param chooseMode 打开的类型
	 */
	/*public void initPictureSelector(int chooseMode) {
		PictureSelector.create(this)
				.openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
				.theme(R.style.picture_default_style)// 主题样式设置 具体参考 libray中values/styles
				.maxSelectNum(9)// 最大图片选择数量
				.minSelectNum(1)// 最小选择数量
				.imageSpanCount(4)// 每行显示个数
				.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
				.previewImage(true)// 是否可预览图片
				.previewVideo(true)// 是否可预览视频
				.enablePreviewAudio(false)// 是否预览音频
//                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
				.isCamera(true)// 是否显示拍照按钮
				.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
				.setOutputCameraPath(Constant.IMAGE_CACHE)// 自定义拍照保存路径
				.compress(true)// 是否压缩
				.compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
				.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
				.isGif(false)// 是否显示gif图片
				.openClickSound(false)// 是否开启点击声音
//                .selectionMedia(selectList)// 是否传入已选图片
//                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
//                .compressGrade(Luban.CUSTOM_GEAR)
				.compressGrade(Luban.CUSTOM_GEAR)
				.compressMaxKB(1024)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
				.minimumCompressSize(500) //add by tanhaiqin, 图片大小 <= 500KB(数字可变) 不需要压缩
//                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
//                //.videoQuality()// 视频录制质量 0 or 1
				.videoSecond(5 * 60)//显示多少秒以内的视频
//                //.recordVideoSecond()//录制视频秒数 默认60秒
				.forResult(PictureConfig.CHOOSE_REQUEST);
	}*/

}
