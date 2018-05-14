package cms_list.cms_list_1.code;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.MyConfig;
import com.shorigo.view.MyListView;
import com.shorigo.view.refresh.PullRefreshUtil;
import com.shorigo.view.refresh.PullRefreshView;
import com.shorigo.view.refresh.PullRefreshView.OnPullListener;
import com.shorigo.yichat.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.CmsBean;
import bean.RequestReturnBean;
import cms_detail.cms_detail_1.code.CmsDetailUI;
import cms_detail_img.cms_detail_img_1.code.CmsDetailImgUI;
import cms_launch.cms_launch_1.code.CmsLaunchUI;
import video_choose.video_choose_1.code.VideoChooseUI;
import video_record.video_record_1.code.VideoRecordUI;

/**
 * 动态列表
 * 
 * @author peidongxu
 * 
 */
public class CollectionListUI extends BaseUI implements OnPullListener, OnItemClickListener {
	private View z_title;
	private PullRefreshView refresh_view;
	private int currertPage; // 当前页数
	private boolean isRef; // 是否刷新
    private int pageShowSize=15;
	private List<CmsBean> listCmsBean;
	private MyListView lv_list;
	private CollectioinListAdapter adapter;

	private Gson gson;

	private String type;// 2热门 1最新
	private MyBroadcastReceiver myBroadcastReceiver;

	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("upload.video.success")){
				isRef=true;
				currertPage=1;
				getCmslist();
			}else if(intent.getAction().equals("update.comment.count")){
				isRef=true;
				currertPage=1;
				getCmslist();
			}
		}
	}
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.collection_list_1);
		//注册广播
		myBroadcastReceiver=new MyBroadcastReceiver();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction("upload.video.success");
		intentFilter.addAction("update.comment.count");
		registerReceiver(myBroadcastReceiver,intentFilter);
	}

	@Override
	protected void findView_AddListener() {

		z_title = findViewById(R.id.z_title);

		refresh_view = (PullRefreshView) findViewById(R.id.refresh_view);
		refresh_view.setOnPullListener(this);
		PullRefreshUtil.setRefresh(refresh_view, true, false);
		lv_list = (MyListView) findViewById(R.id.lv_list);
		lv_list.setOnItemClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("我的收藏");
		currertPage = 1;
		isRef = true;
		type = "2";
		gson = new Gson();
		Type listCmsTemp = new TypeToken<List<CmsBean>>() {
		}.getType();
		listCmsBean = gson.fromJson(ACache.get(this).getAsString("collection_list_1_" + type), listCmsTemp);

		adapter = new CollectioinListAdapter(this, listCmsBean, mHandler,type);
		lv_list.setAdapter(adapter);

		getCmslist();
	}


	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			//new MoreMenuPop(z_title, this, mHandler);
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
		map.put("num", pageShowSize+"");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.d("##---------",response.toString());
				RequestReturnBean returnBean = CmsListJson.getCmsList(response.toString());
				if (HttpUtil.isSuccess(CollectionListUI.this, returnBean.getCode())) {
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
						ACache.get(CollectionListUI.this).put("collection_list_1_" + type, json);
					} else {
						listCmsBean.addAll(listTemp);
					}

					adapter.setData(listCmsBean,type);
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
				if (HttpUtil.isSuccess(CollectionListUI.this, returnBean.getCode())) {
					listCmsBean.remove(position);
					adapter.setData(listCmsBean,type);
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


}
