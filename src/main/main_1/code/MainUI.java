package main.main_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import login.login_1.code.LoginUI;
import me.me_3.code.MeUI;
import msg_main.msg_main_1.code.MsgMainUI;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bean.RequestReturnBean;
import cms_launch.cms_launch_1.code.CmsLaunchUI;
import cms_list.cms_list_1.code.CmsListUI;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.utils.Constant;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.AsyncEmchatLoginLoadTask;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 首页
 * 
 * @author peidongxu
 * 
 */
public class MainUI extends BaseUI implements BDLocationListener, OnMarkerClickListener {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 定位模式
	private MyLocationConfiguration.LocationMode mCurrentMode;
	// 定位端
	private LocationClient mLocClient;
	// 是否是第一次定位
	private boolean isFirstLoc = true;
	// 定位坐标
	private LatLng locationLatLng;
	private String locationCity;
	//
	private LatLng searchLatLng;

	private List<Map<String, String>> listMap;
	private BitmapDescriptor market_boy = BitmapDescriptorFactory.fromResource(R.drawable.market_boy);
	private BitmapDescriptor market_girl = BitmapDescriptorFactory.fromResource(R.drawable.market_girl);
	private BitmapDescriptor market_boy_down = BitmapDescriptorFactory.fromResource(R.drawable.market_boy_down);
	private BitmapDescriptor market_girl_down = BitmapDescriptorFactory.fromResource(R.drawable.market_girl_down);

	private View z_title;

	private TextView tv_msg_count;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.main_1);
	}

	@Override
	protected void findView_AddListener() {
		z_title = findViewById(R.id.z_title);

		mMapView = (MapView) findViewById(R.id.mapView);

		ImageView iv_location = (ImageView) findViewById(R.id.iv_location);
		iv_location.setOnClickListener(this);
		ImageView iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
		iv_refresh.setOnClickListener(this);
		ImageView iv_dynamic = (ImageView) findViewById(R.id.iv_dynamic);
		iv_dynamic.setOnClickListener(this);

		RelativeLayout z_me = (RelativeLayout) findViewById(R.id.z_me);
		z_me.setOnClickListener(this);
		RelativeLayout z_msg = (RelativeLayout) findViewById(R.id.z_msg);
		z_msg.setOnClickListener(this);
		tv_msg_count = (TextView) findViewById(R.id.tv_msg_count);
		LinearLayout z_launch = (LinearLayout) findViewById(R.id.z_launch);
		z_launch.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		// 隐藏放大缩小按钮
		mMapView.showZoomControls(false);
		// 隐藏百度logo
		mMapView.removeViewAt(1);
		// 隐藏比例尺控件
		mMapView.showScaleControl(false);

		mBaiduMap = mMapView.getMap();
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().zoom(17).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位图层显示方式
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

		/**
		 * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效 customMarker用户自定义定位图标
		 * enableDirection是否允许显示方向信息 locationMode定位图层显示方式
		 */
		// mBaiduMap.setMyLocationConfigeration(new
		// MyLocationConfiguration(mCurrentMode, true, null));
		// mBaiduMap.setMyLocationConfigeration(new
		// MyLocationConfiguration(mCurrentMode, true, null));

		// 初始化定位
		mLocClient = new LocationClient(getApplicationContext());
		// 注册定位监听
		mLocClient.registerLocationListener(this);
		setLocationOption();
		// 开始定位
		mLocClient.start();

		// 注册消息广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_MESSAGE_CHANAGED);
		registerReceiver(msgBroadcastReceiver, filter);

		if (!Utils.isEmity(MyConfig.getToken(this))) {
			new AsyncEmchatLoginLoadTask(this).execute();
		}
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		refresh();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_me:
			if (Utils.isEmity(MyConfig.getToken(this))) {
				startActivity(new Intent(this, LoginUI.class));
				return;
			}
			startActivity(new Intent(this, MeUI.class));
			break;
		case R.id.z_msg:
			if (Utils.isEmity(MyConfig.getToken(this))) {
				startActivity(new Intent(this, LoginUI.class));
				return;
			}
			startActivity(new Intent(this, MsgMainUI.class));
			break;
		case R.id.z_launch:
			if (Utils.isEmity(MyConfig.getToken(this))) {
				startActivity(new Intent(this, LoginUI.class));
				return;
			}
			startActivity(new Intent(this, CmsLaunchUI.class));
			break;
		case R.id.iv_dynamic:
			if (Utils.isEmity(MyConfig.getToken(this))) {
				startActivity(new Intent(this, LoginUI.class));
				return;
			}
			startActivity(new Intent(this, CmsListUI.class));
			break;
		case R.id.iv_location:
			if (locationLatLng != null) {
				searchLatLng = locationLatLng;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(locationLatLng, 17); // 设置地图中心点以及缩放级别
				mBaiduMap.animateMapStatus(u);
			}
			break;
		case R.id.iv_refresh:
			if (locationLatLng != null) {
				searchLatLng = locationLatLng;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(locationLatLng, 17); // 设置地图中心点以及缩放级别
				mBaiduMap.animateMapStatus(u);
				getNearList();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 获取附近列表
	 */
	private void getNearList() {
		String url = HttpUtil.getUrl("/user/getUsersByLbs");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("lat", String.valueOf(locationLatLng.latitude));
		map.put("lon", String.valueOf(locationLatLng.longitude));
		map.put("p", "1");
		map.put("num", "1000");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = MainJson.getNearList(response.toString());
				if (HttpUtil.isSuccess(MainUI.this, returnBean.getCode())) {
					listMap = returnBean.getListObject();
					setMarkersData();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 设置检索到的附近的人
	 */
	private void setMarkersData() {
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mMapView.getMap().clear();
		if (listMap == null) {
			return;
		}
		String sex = "";
		Gson gson = new Gson();
		Type listStringTemp = new TypeToken<List<String>>() {
		}.getType();
		List<String> listIds = gson.fromJson(ACache.get(this).getAsString("list_user_id"), listStringTemp);
		if (listIds == null) {
			listIds = new ArrayList<String>();
		}
		for (int i = 0; i < listMap.size(); i++) {
			sex = listMap.get(i).get("sex");
			if (listIds.contains(listMap.get(i).get("user_id"))) {
				if ("1".equals(sex)) {
					initMapMarker(listMap.get(i), market_boy_down);
				} else if ("2".equals(sex)) {
					initMapMarker(listMap.get(i), market_girl_down);
				}
			} else {
				if ("1".equals(sex)) {
					initMapMarker(listMap.get(i), market_boy);
				} else if ("2".equals(sex)) {
					initMapMarker(listMap.get(i), market_girl);
				}
			}
		}
		mBaiduMap.setOnMarkerClickListener(this);
	}

	/**
	 * 设置浮层
	 * 
	 * @param map
	 * @return
	 */
	private void initMapMarker(Map<String, String> map, BitmapDescriptor bitmap) {
		// 定义Maker坐标点
		LatLng point = new LatLng(Double.parseDouble(map.get("lat")), Double.parseDouble(map.get("lon")));
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().zIndex(Integer.parseInt(map.get("user_id"))).position(point).icon(bitmap).draggable(true);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
	}

	/**
	 * 设置定位参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		mLocClient.setLocOption(option);
	}

	/**
	 * 定位监听
	 * 
	 * @param bdLocation
	 */
	@Override
	public void onReceiveLocation(BDLocation bdLocation) {

		// 如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
		if (bdLocation == null || mBaiduMap == null) {
			return;
		}

		// 定位数据
		MyLocationData data = new MyLocationData.Builder()
		// 定位精度bdLocation.getRadius()
				.accuracy(0)
				// .accuracy(bdLocation.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(360)
				// .direction(bdLocation.getDirection())
				// 经度
				.latitude(bdLocation.getLatitude())
				// 纬度
				.longitude(bdLocation.getLongitude())
				// 构建
				.build();

		// 设置定位数据
		mBaiduMap.setMyLocationData(data);

		// 是否是第一次定位
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
			mBaiduMap.animateMapStatus(msu);

			// 获取坐标，待会用于POI信息点与定位的距离
			locationLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
			searchLatLng = locationLatLng;
			// 获取城市，待会用于POISearch
			locationCity = bdLocation.getCity();

			// 获取附近
			getNearList();
		}
	}

	MapPersonalPop mapPersonalPop;

	/**
	 * 覆盖物的点击事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (listMap == null) {
			return true;
		}
		for (int i = 0; i < listMap.size(); i++) {
			if (String.valueOf(marker.getZIndex()).equals(listMap.get(i).get("user_id"))) {
				if (mapPersonalPop != null && mapPersonalPop.getPopIsShow()) {
					mapPersonalPop.dismissPop();
				}
				mapPersonalPop = new MapPersonalPop(z_title, this, listMap.get(i));
				break;
			}
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时停止定位
		mLocClient.stop();
		// 退出时关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
		mMapView = null;

		unregisterReceiver(msgBroadcastReceiver);
	}

	BroadcastReceiver msgBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	};


	private void refresh() {
		int count = 0;
		// 未读消息数
		count = EMClient.getInstance().chatManager().getUnreadMessageCount();
		// 邀请消息
		InviteMessgeDao dao = new InviteMessgeDao(this);
		count += dao.getUnreadMessagesCount();
		if (count > 0) {
			tv_msg_count.setText(count + "");
			tv_msg_count.setVisibility(View.VISIBLE);
		} else {
			tv_msg_count.setVisibility(View.GONE);
		}
	}

	@Override
	protected void back() {
		exit();
	}
}
