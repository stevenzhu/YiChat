package forget_pass.forget_pass_1.code;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.CountTimer;
import com.shorigo.utils.MD5Util;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 找回密码
 * 
 * @author peidongxu
 * 
 */
public class ForgetPassUI extends BaseUI {
	// 手机号、验证码、密码
	private EditText et_phone, et_code, et_pass;
	private String phone, code, pass;
	// 获取验证码
	private TextView tv_get_code;
	// 下发验证码计时
	private CountTimer mc;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.forget_pass_1);
	}

	@Override
	protected void findView_AddListener() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		et_pass = (EditText) findViewById(R.id.et_pass);
		tv_get_code = (TextView) findViewById(R.id.tv_get_code);
		tv_get_code.setOnClickListener(this);
		TextView tv_submit = (TextView) findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);

	}

	@Override
	protected void prepareData() {
		setTitle("找回密码");
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_submit:
			// 保存
			phone = et_phone.getText().toString();
			code = et_code.getText().toString();
			pass = et_pass.getText().toString();
			if (Utils.isEmity(phone)) {
				MyApplication.getInstance().showToast("手机号不能为空");
				return;
			}
			if (!Utils.isMobileNO(phone)) {
				MyApplication.getInstance().showToast("手机号格式有空");
				return;
			}
			if (Utils.isEmity(code)) {
				MyApplication.getInstance().showToast("验证码不能为空");
				return;
			}
			if (Utils.isEmity(pass)) {
				MyApplication.getInstance().showToast("密码不能为空");
				return;
			}
			forgetPass();
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
			if (mc == null) {
				mc = new CountTimer(tv_get_code, 60000, 1000);
			}
			mc.start();
			getCode();
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
		map.put("type", "2");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = ForgetPassJson.getCode(response.toString());
				if (HttpUtil.isSuccess(ForgetPassUI.this, returnBean.getCode())) {
				} else {
					mc.cancel();
					tv_get_code.setText("发送验证码");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 找回密码
	 */
	private void forgetPass() {
		String url = HttpUtil.getUrl("/auth/findPassByPhone");
		Map<String, String> map = new HashMap<String, String>();
		try {
			MD5Util md5Util = new MD5Util();
			map.put("phone", phone);
			map.put("code", code);
			map.put("pwd", md5Util.createMD5(pass));
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = ForgetPassJson.forgetPass(response.toString());
				if (HttpUtil.isSuccess(ForgetPassUI.this, returnBean.getCode())) {
					MyApplication.getInstance().showToast("密码重置成功");
					back();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	protected void back() {
		finish();
	}

}
