package update_sign.update_sign_1.code;

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

public class UpdateSignUI extends BaseUI {

	private EditText et_content;
	private String content;
	private Map<String, String> userMap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.update_sign_1);
	}

	@Override
	protected void findView_AddListener() {
		et_content = (EditText) findViewById(R.id.et_content);

	}

	@Override
	protected void prepareData() {
		setTitle("修改个性签名");
		setRightButton("确定");

		userMap = MyConfig.getUserInfo(this);
		if (userMap != null) {
			content = userMap.get("sign_desc");
			if (!Utils.isEmity(content)) {
				et_content.setText(content);
				et_content.setSelection(content.length());
			}
		}
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			content = et_content.getText().toString();
			updateUserInfo();
			break;
		default:
			break;
		}
	}

	/**
	 * 修改资料
	 */
	private void updateUserInfo() {
		String url = HttpUtil.getUrl("/user/updateUserInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("sign_desc", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UpdateSignJson.analysis(response.toString());
				if (HttpUtil.isSuccess(UpdateSignUI.this, returnBean.getCode())) {
					// 完成
					userMap.put("sign_desc", content);
					MyConfig.saveUserInfo(UpdateSignUI.this, userMap);
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
