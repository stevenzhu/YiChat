package update_sex.update_sex_1.code;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.MyConfig;
import com.shorigo.yichat.R;

public class UpdateSexUI extends BaseUI {

	private RelativeLayout z_man, z_woman;
	private ImageView iv_man_state, iv_woman_state;
	private String sex;
	private Map<String, String> userMap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.update_sex_1);
	}

	@Override
	protected void findView_AddListener() {
		z_man = (RelativeLayout) findViewById(R.id.z_man);
		z_man.setOnClickListener(this);
		iv_man_state = (ImageView) findViewById(R.id.iv_man_state);
		z_woman = (RelativeLayout) findViewById(R.id.z_woman);
		z_woman.setOnClickListener(this);
		iv_woman_state = (ImageView) findViewById(R.id.iv_woman_state);

		StyleUtils.setTabBg(this, iv_man_state, new int[] { R.drawable.checkbox_down, R.drawable.checkbox_up });
		StyleUtils.setTabBg(this, iv_woman_state, new int[] { R.drawable.checkbox_down, R.drawable.checkbox_up });

	}

	@Override
	protected void prepareData() {
		setTitle("修改性别");
		setRightButton("确定");

		userMap = MyConfig.getUserInfo(this);
		if (userMap != null) {
			sex = userMap.get("sex");
			if ("1".equals(sex)) {
				iv_man_state.setSelected(true);
				iv_woman_state.setSelected(false);
			} else if ("2".equals(sex)) {
				iv_man_state.setSelected(false);
				iv_woman_state.setSelected(true);
			}
		}

	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_man:
			sex = "1";
			iv_man_state.setSelected(true);
			iv_woman_state.setSelected(false);
			break;
		case R.id.z_woman:
			sex = "2";
			iv_man_state.setSelected(false);
			iv_woman_state.setSelected(true);
			break;
		case R.id.z_right:
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
		map.put("sex", sex);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UpdateSexJson.analysis(response.toString());
				if (HttpUtil.isSuccess(UpdateSexUI.this, returnBean.getCode())) {
					// 完成
					userMap.put("sex", sex);
					MyConfig.saveUserInfo(UpdateSexUI.this, userMap);
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
