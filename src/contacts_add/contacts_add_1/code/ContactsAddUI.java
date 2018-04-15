package contacts_add.contacts_add_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 联系人添加
 * 
 * @author peidongxu
 * 
 */
public class ContactsAddUI extends BaseUI {
	private EditText et_content;// 输入框
	private String searchName;// 搜索名称

	private ListView lv_user;
	private List<Map<String, String>> listMap;
	private ContactsAddAdapter adapter;
	private ProgressDialog progressDialog;// 等待框

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.contacts_add_1);
	}

	@Override
	protected void findView_AddListener() {
		et_content = (EditText) findViewById(R.id.et_content);
		lv_user = (ListView) findViewById(R.id.lv_user);
	}

	@Override
	protected void prepareData() {
		setTitle("添加好友");
		setRightButton("查找");

		listMap = new ArrayList<Map<String, String>>();
		adapter = new ContactsAddAdapter(this, listMap, mHandler);
		lv_user.setAdapter(adapter);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 查找按钮
			searchName = et_content.getText().toString();
			if (Utils.isEmity(searchName)) {
				MyApplication.getInstance().showToast("请输入名称");
				return;
			}
			searchUser();
			break;
		default:
			break;
		}
	}

	/**
	 * 搜索用户
	 */
	private void searchUser() {
		String url = HttpUtil.getUrl("/user/searchUser");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("nick", searchName);
		map.put("p", "1");
		map.put("num", "100");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = ContactsAddJson.searchUser(response.toString());
				if (HttpUtil.isSuccess(ContactsAddUI.this, returnBean.getCode())) {
					listMap = returnBean.getListObject();
					adapter.setData(listMap);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				String user_id = Constants.PREFIX + listMap.get(msg.arg1).get("user_id");
				addContact(user_id);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 添加联系人
	 */
	public void addContact(final String user_id) {
		if (EMClient.getInstance().getCurrentUser().equals(user_id)) {
			new EaseAlertDialog(this, R.string.not_add_myself).show();
			return;
		}

		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					String s = getResources().getString(R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(user_id, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							MyApplication.getInstance().showToast(s1);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void back() {
		finish();
	}

}
