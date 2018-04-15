package update_nick.update_nick_1.code;

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

public class UpdateNickUI extends BaseUI {

	private EditText et_content;
	private String content;
	private Map<String, String> userMap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.update_nick_1);
	}

	@Override
	protected void findView_AddListener() {
		et_content = (EditText) findViewById(R.id.et_content);

	}

	@Override
	protected void prepareData() {
		setTitle("修改昵称");
		setRightButton("确定");

		userMap = MyConfig.getUserInfo(this);
		if (userMap != null) {
			content = userMap.get("user_nick");
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
		map.put("user_nick", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UpdateNickJson.analysis(response.toString());
				if (HttpUtil.isSuccess(UpdateNickUI.this, returnBean.getCode())) {
					// 完成
					userMap.put("user_nick", content);
					MyConfig.saveUserInfo(UpdateNickUI.this, userMap);
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
