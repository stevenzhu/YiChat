package update_address.update_address_1.code;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import android.widget.EditText;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 修改地址
 * 
 * @author peidongxu
 * 
 */
public class UpdateAddressUI extends BaseUI {

	private EditText et_content;
	private String content;
	private Map<String, String> userInfo;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.update_address_1);
	}

	@Override
	protected void findView_AddListener() {
		et_content = (EditText) findViewById(R.id.et_content);

	}

	@Override
	protected void prepareData() {
		setTitle("修改地址");
		setRightButton("确定");

		userInfo = MyConfig.getUserInfo(this);
		if (userInfo != null && !Utils.isEmity(userInfo.get("address"))) {
			content = userInfo.get("address");
			et_content.setText(content);
			et_content.setSelection(content.length());
		}
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 确定
			content = et_content.getText().toString();
			setUserInfo();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置用户信息
	 */
	private void setUserInfo() {
		String url = HttpUtil.getUrl("/user/updateUserInfo");
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", MyConfig.getToken(this));
		params.put("address", content);
		HttpUtil.post(this, url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UpdateAddressJson.setUserInfo(response.toString());
				if (HttpUtil.isSuccess(UpdateAddressUI.this, returnBean.getCode())) {
					userInfo.put("address", content);
					MyConfig.saveUserInfo(UpdateAddressUI.this, userInfo);
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
