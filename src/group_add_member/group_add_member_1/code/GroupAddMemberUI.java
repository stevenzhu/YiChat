package group_add_member.group_add_member_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.ListView;
import bean.RequestReturnBean;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 联系人
 * 
 * @author peidongxu
 * 
 */
public class GroupAddMemberUI extends BaseUI implements OnRefreshListener {
	private SwipeRefreshLayout srl_refresh;
	private List<Map<String, String>> listMap;
	private ListView mListView;
	private GroupAddMemberAdapter adapter;
	private Gson gson;
	// 联系人IDs
	private String contactsIds = "";

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.group_add_member_1);
	}

	@Override
	protected void findView_AddListener() {
		srl_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		srl_refresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		srl_refresh.setOnRefreshListener(this);
		mListView = (ListView) findViewById(R.id.lv_add_member);
	}

	@Override
	protected void prepareData() {
		setTitle("选择人员");
		setRightButton("完成");

		gson = new Gson();

		new Thread(new Runnable() {

			@Override
			public void run() {
				getContactsList();
			}
		}).start();

		adapter = new GroupAddMemberAdapter(this, listMap, mHandler);
		mListView.setAdapter(adapter);
	}

	/**
	 * 获取联系人
	 */
	private void getContactsList() {
		List<String> listUser;
		try {
			listUser = EMClient.getInstance().contactManager().getAllContactsFromServer();
			boolean isFirst = true;
			for (int i = 0; i < listUser.size(); i++) {
				if (isFirst) {
					isFirst = false;
					contactsIds = listUser.get(i).replace(Constants.PREFIX, "");
				} else {
					contactsIds += "," + listUser.get(i).replace(Constants.PREFIX, "");
				}
			}
		} catch (HyphenateException e) {
			srl_refresh.setRefreshing(false);
			e.printStackTrace();
		}
		if (Utils.isEmity(contactsIds)) {
			srl_refresh.setRefreshing(false);
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getContactsData();
			}
		});
	}

	private void getContactsData() {
		String url = HttpUtil.getUrl("/user/searchUserByIds");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("ids", contactsIds);

		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = GroupAddMemberJson.getContactsList(response.toString());
				if (HttpUtil.isSuccess(GroupAddMemberUI.this, returnBean.getCode())) {
					listMap = returnBean.getListObject();
					adapter.setData(listMap);
				}
				srl_refresh.setRefreshing(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				srl_refresh.setRefreshing(false);
			}
		});
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 添加
			List<Map<String, String>> listTempMap = new ArrayList<Map<String, String>>();
			if (listMap != null && listMap.size() > 0) {
				Map<String, String> map;
				for (int i = 0; i < listMap.size(); i++) {
					map = listMap.get(i);
					if ("1".equals(map.get("is_select"))) {
						listTempMap.add(map);
					}
				}
				if (listMap != null && listMap.size() > 0) {
					ACache.get(this).put("select_member", gson.toJson(listTempMap));
					setResult(0);
					back();
				} else {
					MyApplication.getInstance().showToast("请选择人员");
				}
			} else {
				MyApplication.getInstance().showToast("请选择人员");
			}
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int position = msg.arg1;
			switch (msg.what) {
			case 1:
				// false
				listMap.get(position).put("is_select", "0");
				adapter.setData(listMap);
				break;
			case 2:
				// true
				listMap.get(position).put("is_select", "1");
				adapter.setData(listMap);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onRefresh() {
		getContactsList();
	}

	@Override
	protected void back() {
		finish();
	}

}
