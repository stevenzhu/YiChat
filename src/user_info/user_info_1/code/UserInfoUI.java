package user_info.user_info_1.code;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.main_1.code.MainUI;
import me_card.me_card_1.code.MeCardUI;

import org.apache.http.Header;
import org.json.JSONObject;

import register.register_2.code.RegisterAdapter;
import update_address.update_address_1.code.UpdateAddressUI;
import update_avatar.update_avatar_1.code.UpdateAvatarUI;
import update_nick.update_nick_1.code.UpdateNickUI;
import update_sex.update_sex_1.code.UpdateSexUI;
import update_sign.update_sign_1.code.UpdateSignUI;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.google.zxing.WriterException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.Utils;
import com.shorigo.view.wheel.OnWheelChangedListener;
import com.shorigo.view.wheel.StrericWheelAdapter;
import com.shorigo.view.wheel.WheelView;
import com.shorigo.yichat.R;
import com.shorigo.zxing.EncodingHandler;

/**
 * 用户信息页面
 * 
 * @author peidongxu
 * 
 */
public class UserInfoUI extends BaseUI implements OnItemClickListener {

	private ImageView iv_avatar;
	private TextView tv_nick, tv_sex, tv_phone, tv_sign, tv_family, tv_county, tv_address, tv_age;
	private Map<String, String> userMap;

	private ImageView iv_qrcode;
	private Bitmap createQRCode;

	/** 1:家族2:县 3:年龄 */
	private String type;
	private String family_id, family_name, city_id, city_name;
	private List<Map<String, String>> listFamiliesMap;
	private List<Map<String, String>> listCitysMap;
	private Dialog mDialog;

	private boolean isUpdate;

	/** 初始化时间数据 */
	public static String[] yearContent_str = null, monthContent_str = null, dayContent_str = null;
	public static String[] yearContent = null, monthContent = null, dayContent = null;

	private WheelView wv_year, wv_month, wv_day;
	private Dialog mTimeDialog;
	private String year;
	private String month;
	private String day;
	private int year_index;
	private int month_index;
	private int day_index;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.user_info_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		RelativeLayout z_avatar = (RelativeLayout) findViewById(R.id.z_avatar);
		z_avatar.setOnClickListener(this);
		iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
		iv_qrcode.setOnClickListener(this);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		RelativeLayout z_nick = (RelativeLayout) findViewById(R.id.z_nick);
		z_nick.setOnClickListener(this);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		RelativeLayout z_sex = (RelativeLayout) findViewById(R.id.z_sex);
		z_sex.setOnClickListener(this);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		RelativeLayout z_phone = (RelativeLayout) findViewById(R.id.z_phone);
		z_phone.setOnClickListener(this);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		RelativeLayout z_sign = (RelativeLayout) findViewById(R.id.z_sign);
		z_sign.setOnClickListener(this);
		tv_address = (TextView) findViewById(R.id.tv_address);
		RelativeLayout z_address = (RelativeLayout) findViewById(R.id.z_address);
		z_address.setOnClickListener(this);
		tv_family = (TextView) findViewById(R.id.tv_family);
		RelativeLayout z_family = (RelativeLayout) findViewById(R.id.z_family);
		z_family.setOnClickListener(this);
		tv_county = (TextView) findViewById(R.id.tv_county);
		RelativeLayout z_county = (RelativeLayout) findViewById(R.id.z_county);
		z_county.setOnClickListener(this);
		tv_age = (TextView) findViewById(R.id.tv_age);
		RelativeLayout z_age = (RelativeLayout) findViewById(R.id.z_age);
		z_age.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {

		Intent intent = getIntent();
		isUpdate = intent.getBooleanExtra("isUpdate", false);
		if (isUpdate) {
			setTitle("完善资料");
			setRightButton("完成");
		} else {
			setTitle("用户信息");
		}

		getUserInfo();
		getFamilies();
		getCitys();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setValue();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			if (userMap == null) {
				return;
			}
			String family_id = userMap.get("family_id");
			if (Utils.isEmity(family_id) || "0".equals(family_id)) {
				MyApplication.getInstance().showToast("请选择所属家族");
				return;
			}
			String city_id = userMap.get("city_id");
			if (Utils.isEmity(city_id) || "0".equals(city_id)) {
				MyApplication.getInstance().showToast("请选择所在县");
				return;
			}
			String sex = userMap.get("sex");
			if (Utils.isEmity(sex) || "0".equals(sex)) {
				MyApplication.getInstance().showToast("请设置性别");
				return;
			}
			startActivity(new Intent(this, MainUI.class));
			back();
			break;
		case R.id.z_avatar:
			startActivity(new Intent(this, UpdateAvatarUI.class));
			break;
		case R.id.z_nick:
			startActivity(new Intent(this, UpdateNickUI.class));
			break;
		case R.id.z_sex:
			startActivity(new Intent(this, UpdateSexUI.class));
			break;
		case R.id.z_sign:
			startActivity(new Intent(this, UpdateSignUI.class));
			break;
		case R.id.z_address:
			startActivity(new Intent(this, UpdateAddressUI.class));
			break;
		case R.id.iv_qrcode:
			startActivity(new Intent(this, MeCardUI.class));
			break;
		case R.id.z_family:
			type = "1";
			if (listFamiliesMap == null) {
				getFamilies();
				return;
			}
			showDialogList(this, listFamiliesMap);
			break;
		case R.id.z_county:
			type = "2";
			if (listCitysMap == null) {
				getCitys();
				return;
			}
			showDialogList(this, listCitysMap);
			break;
		case R.id.z_age:
			//
			if (userMap != null && !Utils.isEmity(userMap.get("birthday")) && !"0".equals(userMap.get("birthday"))) {
				String birthday = TimeUtil.getData("yyyy-MM-dd", userMap.get("birthday"));
				year = birthday.split("-")[0];
				month = birthday.split("-")[1];
				day = birthday.split("-")[2];
			}
			initContent();
			showChooseTime(this);
			break;
		case R.id.tv_time_choose_dialog_ok:
			// 日期选择框-确认
			mTimeDialog.dismiss();
			// 获取时间
			getTime();
			type = "3";
			updateUserInfo();
			break;
		case R.id.tv_time_choose_dialog_cancle:
			// 日期选择框-取消
			mTimeDialog.dismiss();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		String url = HttpUtil.getUrl("/user/userInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("user_id", "");

		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserInfoJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(UserInfoUI.this, returnBean.getCode())) {
					// 登录成功、保存用户信息
					userMap = (Map<String, String>) returnBean.getObject();
					MyConfig.saveUserInfo(UserInfoUI.this, userMap);
					setValue();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 获取家族列表
	 */
	private void getFamilies() {
		String url = HttpUtil.getUrl("/public/families");
		Map<String, String> map = new HashMap<String, String>();
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserInfoJson.getFamilies(response.toString());
				if (HttpUtil.isSuccess(UserInfoUI.this, returnBean.getCode())) {
					listFamiliesMap = returnBean.getListObject();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 获取城市列表
	 */
	private void getCitys() {
		String url = HttpUtil.getUrl("/public/citys");
		Map<String, String> map = new HashMap<String, String>();
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserInfoJson.getCitys(response.toString());
				if (HttpUtil.isSuccess(UserInfoUI.this, returnBean.getCode())) {
					listCitysMap = returnBean.getListObject();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 修改资料
	 */
	private void updateUserInfo() {
		String url = HttpUtil.getUrl("/user/updateUserInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		if ("1".equals(type)) {
			map.put("family_id", family_id);
		} else if ("2".equals(type)) {
			map.put("city_id", city_id);
		} else if ("3".equals(type)) {
			map.put("birthday", userMap.get("birthday"));
		}
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserInfoJson.updateUserInfo(response.toString());
				if (HttpUtil.isSuccess(UserInfoUI.this, returnBean.getCode())) {
					// 完成
					if ("1".equals(type)) {
						userMap.put("family_id", family_id);
						userMap.put("family_name", family_name);
					} else if ("2".equals(type)) {
						userMap.put("city_id", city_id);
						userMap.put("city_name", city_name);
					}
					MyConfig.saveUserInfo(UserInfoUI.this, userMap);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 页面赋值
	 */
	private void setValue() {
		userMap = MyConfig.getUserInfo(this);
		if (userMap == null) {
			return;
		}

		BitmapHelp.loadImg(this, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
		tv_nick.setText(userMap.get("user_nick"));
		// 性别1：男 2：女
		String sex = userMap.get("sex");
		if ("1".equals(sex)) {
			tv_sex.setText("男");
		} else if ("2".equals(sex)) {
			tv_sex.setText("女");
		}
		tv_phone.setText(userMap.get("phone"));
		tv_sign.setText(userMap.get("sign_desc"));
		tv_family.setText(userMap.get("family_name"));
		tv_county.setText(userMap.get("city_name"));
		tv_address.setText(userMap.get("address"));
		// 年龄
		tv_age.setText(userMap.get("age"));

		setQrcode();
	}

	/**
	 * 选择显示框
	 */
	public void showDialogList(Context context, List<Map<String, String>> listMap) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.user_info_1_dialog, null);

		ListView lv_dialog = (ListView) view.findViewById(R.id.lv_dialog);
		lv_dialog.setOnItemClickListener(this);

		RegisterAdapter adapter = new RegisterAdapter(context, listMap);
		lv_dialog.setAdapter(adapter);

		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.width = Constants.width - Constants.width / 10 * 2;
		if (listMap != null && listMap.size() > 10) {
			attributes.height = Constants.height - Constants.height / 10 * 3;
		}
		mDialog.getWindow().setAttributes(attributes);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mDialog.dismiss();
		if ("1".equals(type)) {
			Map<String, String> map = listFamiliesMap.get(position);
			family_id = map.get("id");
			family_name = map.get("name");
			tv_family.setText(family_name);
		} else if ("2".equals(type)) {
			Map<String, String> map = listCitysMap.get(position);
			city_id = map.get("id");
			city_name = map.get("name");
			tv_county.setText(city_name);
		}
		updateUserInfo();
	}

	/**
	 * 设置二维码
	 */
	private void setQrcode() {
		if (userMap == null) {
			return;
		}
		try {
			createQRCode = EncodingHandler.createQRCode(Constants.PREFIX + userMap.get("user_id"), 800);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		iv_qrcode.setImageBitmap(createQRCode);
	}

	/**
	 * 获取时间
	 */
	private void getTime() {
		year = yearContent[wv_year.getCurrentItem()];// 年
		month = monthContent[wv_month.getCurrentItem()];// 月
		day = (wv_day.getCurrentItem() + 1) + "";// 日

		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		String birthday = year + "-" + month + "-" + day;
		String age = String.valueOf(curYear - Integer.parseInt(year));
		userMap.put("birthday", String.valueOf(TimeUtil.getDataUnix("yyyy-MM-dd", birthday)));
		userMap.put("age", age);
		MyConfig.saveUserInfo(this, userMap);
		tv_age.setText(age);
	}

	/**
	 * 选择时间的弹出框
	 * 
	 * @param context
	 * @return
	 */
	public void showChooseTime(Context context) {
		mTimeDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.dialog_time_choose, null);
		// 取消
		RelativeLayout rl_all = (RelativeLayout) view.findViewById(R.id.rl_time_choose_dialog_all);
		rl_all.setOnClickListener(this);
		// 确定
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_time_choose_dialog_ok);
		tv_ok.setOnClickListener(this);
		// 取消
		TextView tv_cancle = (TextView) view.findViewById(R.id.tv_time_choose_dialog_cancle);
		tv_cancle.setOnClickListener(this);

		mTimeDialog.setContentView(view);
		mTimeDialog.setCanceledOnTouchOutside(true);
		mTimeDialog.show();

		LayoutParams attributes = mTimeDialog.getWindow().getAttributes();
		attributes.width = Constants.width;
		mTimeDialog.getWindow().setAttributes(attributes);
		attributes = null;

		initShowChooseTime(view);
	}

	/**
	 * 设置时间
	 * 
	 * @param showChooseTime
	 */
	private void initShowChooseTime(View showChooseTime) {
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		final int curDay = calendar.get(Calendar.DATE);
		wv_year = (WheelView) showChooseTime.findViewById(R.id.year);
		wv_month = (WheelView) showChooseTime.findViewById(R.id.month);
		wv_day = (WheelView) showChooseTime.findViewById(R.id.day);

		if (Utils.isEmity(year)) {
			year_index = curYear - 1900;
		} else {
			for (int i = 0; i < yearContent.length; i++) {
				if (year.equals(yearContent[i])) {
					year_index = i;
					break;
				}
			}
		}
		wv_year.setAdapter(new StrericWheelAdapter(yearContent));
		wv_year.setCurrentItem(year_index);
		wv_year.setCyclic(true);
		wv_year.setInterpolator(new AnticipateOvershootInterpolator());

		if (Utils.isEmity(month)) {
			month = String.valueOf(curMonth - 1);
		} else {
			for (int i = 0; i < monthContent.length; i++) {
				if (month.equals(monthContent[i])) {
					month_index = i;
					break;
				}
			}
		}
		wv_month.setAdapter(new StrericWheelAdapter(monthContent));
		wv_month.setCurrentItem(month_index);
		wv_month.setCyclic(true);
		wv_month.setInterpolator(new AnticipateOvershootInterpolator());

		wv_month.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (Integer.parseInt(monthContent[newValue]) <= 7) {
					if (Integer.parseInt(monthContent[newValue]) == 2) {
						dayContent = new String[28];
					} else if (Integer.parseInt(monthContent[newValue]) % 2 == 0) {
						dayContent = new String[30];
					} else {
						dayContent = new String[31];
					}
				} else {
					if (Integer.parseInt(monthContent[newValue]) % 2 == 0) {
						dayContent = new String[31];
					} else {
						dayContent = new String[30];
					}
				}
				for (int i = 0; i < dayContent.length; i++) {
					dayContent[i] = String.valueOf(i + 1);
					if (dayContent[i].length() < 2) {
						dayContent[i] = "0" + dayContent[i];
					}
				}
				for (int i = 0; i < monthContent.length; i++) {
					if (month.equals(monthContent[i])) {
						month_index = i;
						break;
					}
				}
				wv_day.setAdapter(new StrericWheelAdapter(dayContent));
				wv_day.setCurrentItem(month_index);
				wv_day.setCyclic(true);
				wv_day.setInterpolator(new AnticipateOvershootInterpolator());
			}
		});

		if (Integer.parseInt(monthContent[curMonth - 1]) <= 7) {
			if (Integer.parseInt(monthContent[curMonth - 1]) == 2) {
				dayContent = new String[28];
			} else if (Integer.parseInt(monthContent[curMonth - 1]) % 2 == 0) {
				dayContent = new String[30];
			} else {
				dayContent = new String[31];
			}
		} else {
			if (Integer.parseInt(monthContent[curMonth - 1]) % 2 == 0) {
				dayContent = new String[31];
			} else {
				dayContent = new String[30];
			}
		}
		for (int i = 0; i < dayContent.length; i++) {
			dayContent[i] = String.valueOf(i + 1);
			if (dayContent[i].length() < 2) {
				dayContent[i] = "0" + dayContent[i];
			}
		}
		if (Utils.isEmity(day)) {
			day = String.valueOf(curDay - 1);
		} else {
			for (int i = 0; i < dayContent.length; i++) {
				if (day.equals(dayContent[i])) {
					day_index = i;
					break;
				}
			}
		}
		wv_day.setAdapter(new StrericWheelAdapter(dayContent));
		wv_day.setCurrentItem(day_index);
		wv_day.setCyclic(true);
		wv_day.setInterpolator(new AnticipateOvershootInterpolator());
	}

	/**
	 * 初始化年月日时间数据
	 */
	public void initContent() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR) + 1;
		yearContent = new String[year - 1900];
		for (int i = 0; i < year - 1900; i++) {
			yearContent[i] = String.valueOf(i + 1900);
		}
		monthContent = new String[12];
		for (int i = 0; i < 12; i++) {
			monthContent[i] = String.valueOf(i + 1);
			if (monthContent[i].length() < 2) {
				monthContent[i] = "0" + monthContent[i];
			}
		}
		dayContent = new String[31];
		for (int i = 0; i < 31; i++) {
			dayContent[i] = String.valueOf(i + 1);
			if (dayContent[i].length() < 2) {
				dayContent[i] = "0" + dayContent[i];
			}
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
