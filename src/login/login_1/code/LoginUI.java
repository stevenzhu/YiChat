package login.login_1.code;

import java.util.HashMap;
import java.util.Map;

import main.main_1.code.MainUI;

import org.apache.http.Header;
import org.json.JSONObject;

import register.register_2.code.RegisterUI;
import user_info.user_info_1.code.UserInfoUI;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.AsyncEmchatLoginLoadTask;
import com.shorigo.utils.MD5Util;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import forget_pass.forget_pass_1.code.ForgetPassUI;

/**
 * 登录模块
 * 
 * @Description 手机号、密码登录，第三方登录
 * @author peidongxu
 * 
 */
public class LoginUI extends BaseUI {
	// 手机号、密码
	private EditText et_phone, et_pass;
	private String phone, pass;
	private UMShareAPI shareAPI;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.login_1);
	}

	@Override
	protected void findView_AddListener() {
		TextView tv_login = (TextView) findViewById(R.id.tv_login);
		tv_login.setOnClickListener(this);
		TextView tv_forget_pass = (TextView) findViewById(R.id.tv_forget_pass);
		tv_forget_pass.setOnClickListener(this);
		TextView tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setOnClickListener(this);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pass = (EditText) findViewById(R.id.et_pass);

		LinearLayout z_wechat = (LinearLayout) findViewById(R.id.z_wechat);
		z_wechat.setOnClickListener(this);

	}

	@Override
	protected void prepareData() {
		setTitle("登录");
		// 初始化
		shareAPI = UMShareAPI.get(this);

	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_login:
			// 登录
			phone = et_phone.getText().toString();
			pass = et_pass.getText().toString();
			if (Utils.isEmity(phone)) {
				MyApplication.getInstance().showToast("手机号不能为空");
				return;
			}
			if (!Utils.isMobileNO(phone)) {
				MyApplication.getInstance().showToast("手机号格式有误");
				return;
			}
			if (Utils.isEmity(pass)) {
				MyApplication.getInstance().showToast("密码不能为空");
				return;
			}
			userLogin();
			break;
		case R.id.tv_register:
			// 找回密码
			startActivity(new Intent(this, RegisterUI.class));
			break;
		case R.id.tv_forget_pass:
			// 找回密码
			startActivity(new Intent(this, ForgetPassUI.class));
			break;
		case R.id.z_wechat:
			authorize(SHARE_MEDIA.WEIXIN);
			break;
		default:
			break;
		}
	}

	/**
	 * 用户登录
	 */
	private void userLogin() {
		String url = HttpUtil.getUrl("/auth/login");
		Map<String, String> map = new HashMap<String, String>();
		try {
			MD5Util md5Util = new MD5Util();
			map.put("name", phone);
			map.put("pwd", md5Util.createMD5(pass));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = LoginJson.login(response.toString());
				if (HttpUtil.isSuccess(LoginUI.this, returnBean.getCode())) {
					// 登录成功、保存用户信息
					Map<String, String> map = (Map<String, String>) returnBean.getObject();
					MyConfig.saveToken(LoginUI.this, map.get("access_token"));
					Map<String, String> userMap = new HashMap<String, String>();
					userMap.put("user_id", map.get("user_id"));
					MyConfig.saveUserInfo(LoginUI.this, userMap);
					new AsyncEmchatLoginLoadTask(LoginUI.this).execute();
					getUserInfo();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Log.d("00",statusCode+"-"+errorResponse);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 第三方登录
	 */
	private void otherLogin(String openId, String avatar, String nick, String sex, final String type) {
		String url = HttpUtil.getUrl("/auth/authLogin");
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);// 1微信 2QQ 3新浪微博
		params.put("open_id", openId);// 第三方ID
		params.put("nick", nick);// 昵称
		params.put("sex", sex);// 性别0：男 1：女
		params.put("avatar", avatar);// 头像
		HttpUtil.post(this, url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = LoginJson.login(response.toString());
				if (HttpUtil.isSuccess(LoginUI.this, returnBean.getCode())) {
					// 登录成功、保存用户信息
					Map<String, String> map = (Map<String, String>) returnBean.getObject();
					MyConfig.saveToken(LoginUI.this, map.get("access_token"));
					Map<String, String> userMap = new HashMap<String, String>();
					userMap.put("user_id", map.get("user_id"));
					MyConfig.saveUserInfo(LoginUI.this, userMap);
					new AsyncEmchatLoginLoadTask(LoginUI.this).execute();
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
				RequestReturnBean returnBean = LoginJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(LoginUI.this, returnBean.getCode())) {
					// 保存用户信息
					Map<String, String> userMap = (Map<String, String>) returnBean.getObject();
					MyConfig.saveUserInfo(LoginUI.this, userMap);
					if (userMap != null && !Utils.isEmity(userMap.get("family_id")) && !"0".equals(userMap.get("family_id"))) {
						startActivity(new Intent(LoginUI.this, MainUI.class));
					} else {
						Intent intent = new Intent(LoginUI.this, UserInfoUI.class);
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

	// 执行授权,获取用户信息
	private void authorize(SHARE_MEDIA platform) {
		shareAPI.deleteOauth(this, platform, null);
		shareAPI.doOauthVerify(this, platform, umAuthListener);
	}

	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
			getUserInfo(platform);
		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			MyApplication.getInstance().showToast("登录失败");
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			MyApplication.getInstance().showToast("登录取消");
		}

		@Override
		public void onStart(SHARE_MEDIA arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 获取用户信息
	 * 
	 * @param platform
	 */
	private void getUserInfo(final SHARE_MEDIA platform) {
		shareAPI.getPlatformInfo(this, platform, new UMAuthListener() {

			@Override
			public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int status, Map<String, String> info) {
				if (info != null) {
					String sex = "";
					String type = "";
					String openId = info.get("uid");
					String avatar = info.get("iconurl");
					String nick = info.get("name");
					String gender = info.get("gender");
					if ("男".equals(gender)) {
						sex = "0";
					} else if ("女".equals(gender)) {
						sex = "1";
					}
					// 1微信 2QQ 3新浪微博
					if (platform == SHARE_MEDIA.QQ) {
						type = "2";
					} else if (platform == SHARE_MEDIA.SINA) {
						type = "3";
					} else if (platform == SHARE_MEDIA.WEIXIN) {
						type = "1";
					}
					otherLogin(openId, avatar, nick, sex, type);
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA arg0, int arg1) {
			}

			@Override
			public void onStart(SHARE_MEDIA arg0) {
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareAPI.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void back() {
		finish();
	}

}
