package register.register_2.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.main_1.code.MainUI;

import org.apache.http.Header;
import org.json.JSONObject;

import protocol.protocol_1.code.ProtocolUI;
import user_info.user_info_1.code.UserInfoUI;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.AsyncEmchatLoginLoadTask;
import com.shorigo.utils.Constants;
import com.shorigo.utils.CountTimer;
import com.shorigo.utils.MD5Util;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 注册页
 * 
 * @author peidongxu
 * 
 */
public class RegisterUI extends BaseUI implements OnItemClickListener {
	// 手机号、验证码、密码、昵称、详情地址
	private EditText et_phone, et_code, et_pass, et_nick;
	private String phone, code, pass, nick;
	private TextView tv_family, tv_county;
	private String family, county;
	// 获取验证码
	private TextView tv_get_code;
	private CountTimer mc;
	private ImageView iv_tick;
	/** 1:家族2:县 */
	private String type;
	private List<Map<String, String>> listMap;
	private Dialog mDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.register_2);
	}

	@Override
	protected void findView_AddListener() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		et_nick = (EditText) findViewById(R.id.et_name);
		et_pass = (EditText) findViewById(R.id.et_pass);

		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		tv_get_code.setOnClickListener(this);

		tv_family = (TextView) findViewById(R.id.tv_family);
		tv_family.setOnClickListener(this);
		tv_county = (TextView) findViewById(R.id.tv_county);
		tv_county.setOnClickListener(this);

		iv_tick = (ImageView) findViewById(R.id.iv_tick);
		iv_tick.setOnClickListener(this);

		TextView tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
		tv_xieyi.setOnClickListener(this);

		TextView tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setOnClickListener(this);

	}

	@Override
	protected void prepareData() {
		setTitle("注册");

		iv_tick.setSelected(false);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_register:
			// 注册
			phone = et_phone.getText().toString();
			code = et_code.getText().toString();
			nick = et_nick.getText().toString();
			pass = et_pass.getText().toString();
			if (Utils.isEmity(phone)) {
				MyApplication.getInstance().showToast("手机号不能为空");
				return;
			}
			if (!Utils.isMobileNO(phone)) {
				MyApplication.getInstance().showToast("手机号格式有误");
				return;
			}
			if (Utils.isEmity(code)) {
				MyApplication.getInstance().showToast("验证码不能为空");
				return;
			}
			if (Utils.isEmity(nick)) {
				MyApplication.getInstance().showToast("昵称不能为空");
				return;
			}
			if (Utils.isEmity(pass)) {
				MyApplication.getInstance().showToast("密码不能为空");
				return;
			}
			if (!iv_tick.isSelected()) {
				MyApplication.getInstance().showToast("请同意软件协议");
				return;
			}

			register();
			break;
		case R.id.tv_get_code:
			// 获取验证码
			phone = et_phone.getText().toString();
			if (Utils.isEmity(phone)) {
				MyApplication.getInstance().showToast("手机号不能为空");
				return;
			}
			if (!Utils.isMobileNO(phone)) {
				MyApplication.getInstance().showToast("手机号格式有误");
				return;
			}
			tv_get_code.setEnabled(false);
			if (mc == null) {
				mc = new CountTimer(tv_get_code, 60000, 1000);
			}
			mc.start();
			getCode();
			break;
		case R.id.iv_tick:
			//
			if (iv_tick.isSelected()) {
				iv_tick.setSelected(false);
			} else {
				iv_tick.setSelected(true);
			}
			break;
		case R.id.tv_xieyi:
			// 查看协议
			startActivity(new Intent(this, ProtocolUI.class));
			break;
		case R.id.tv_family:
			type = "1";
			showDialogList(this);
			break;
		case R.id.tv_county:
			type = "2";
			showDialogList(this);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取验证码
	 */
	private void getCode() {
		String url = HttpUtil.getUrl("/auth/code");
		Map<String, String> map = new HashMap<String, String>();
		map.put("phone", phone);
		map.put("type", "1");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean<?> returnBean = RegisterJson.getCode(response.toString());
				if (HttpUtil.isSuccess(RegisterUI.this, returnBean.getCode())) {
				} else {
					mc.cancel();
					tv_get_code.setText("发送验证码");
				}
				tv_get_code.setEnabled(true);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				tv_get_code.setEnabled(true);
			}
		});
	}

	/**
	 * 注册
	 */
	private void register() {
		String url = HttpUtil.getUrl("/auth/register");
		Map<String, String> map = new HashMap<String, String>();
		try {
			MD5Util md5Util = new MD5Util();
			map.put("name", phone);
			map.put("code", code);
			map.put("pwd", md5Util.createMD5(pass));
			map.put("nick", nick);
			map.put("family", family);
			map.put("county", county);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean<?> returnBean = RegisterJson.register(response.toString());
				if (HttpUtil.isSuccess(RegisterUI.this, returnBean.getCode())) {
					// 注册成功
					Map<String, String> map = (Map<String, String>) returnBean.getObject();
					MyConfig.saveToken(RegisterUI.this, map.get("access_token"));
					Map<String, String> userMap = new HashMap<String, String>();
					userMap.put("user_id", map.get("user_id"));
					MyConfig.saveUserInfo(RegisterUI.this, userMap);
					new AsyncEmchatLoginLoadTask(RegisterUI.this).execute();
					getUserInfo();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
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
				RequestReturnBean returnBean = RegisterJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(RegisterUI.this, returnBean.getCode())) {
					// 保存用户信息
					Map<String, String> userMap = (Map<String, String>) returnBean.getObject();
					MyConfig.saveUserInfo(RegisterUI.this, userMap);
					if (userMap != null && !Utils.isEmity(userMap.get("family_id")) && !"0".equals(userMap.get("family_id"))) {
						startActivity(new Intent(RegisterUI.this, MainUI.class));
					} else {
						Intent intent = new Intent(RegisterUI.this, UserInfoUI.class);
						intent.putExtra("isUpdate", true);
						startActivity(intent);
					}
					back();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 选择显示框
	 */
	public void showDialogList(Context context) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.register_2_dialog, null);

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
			Map<String, String> map = listMap.get(position);
			family = map.get("name");
			tv_family.setText(family);
		} else if ("2".equals(type)) {
			Map<String, String> map = listMap.get(position);
			county = map.get("name");
			tv_county.setText(county);
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
