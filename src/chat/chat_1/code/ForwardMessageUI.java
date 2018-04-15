package chat.chat_1.code;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 转发选择人员
 * 
 * @author peidongxu
 * 
 */
public class ForwardMessageUI extends BaseUI implements TextWatcher, OnKeyListener {
	private EditText et_search;
	private String search_name;

	private String contactsIds = "";// 联系人IDs
	private List<Map<String, String>> listSearchMap;
	private List<Map<String, String>> listAllMap;
	private ListView mListView;
	private ForwardMessageAdapter adapter;

	private String forward_msg_id;// 转发消息ID
	private String img;// 图片链接

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.ease_activity_forward_message);
	}

	@Override
	protected void findView_AddListener() {
		et_search = (EditText) findViewById(R.id.et_search);
		et_search.addTextChangedListener(this);
		et_search.setOnKeyListener(this);

		mListView = (ListView) findViewById(R.id.lv_select_member);
	}

	@Override
	protected void prepareData() {
		setTitle("选择人员");
		setRightButton("确定");

		Intent intent = getIntent();
		forward_msg_id = intent.getStringExtra("forward_msg_id");
		img = intent.getStringExtra("img");

		new Thread(new Runnable() {

			@Override
			public void run() {
				getContactsList();
			}
		}).start();

		adapter = new ForwardMessageAdapter(this, listAllMap, mHandler);
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
					contactsIds = listUser.get(i).replace(Constants.PREFIX, "");
				} else {
					contactsIds += "," + listUser.get(i).replace(Constants.PREFIX, "");
				}
			}
		} catch (HyphenateException e) {
			e.printStackTrace();
		}
		if (Utils.isEmity(contactsIds)) {
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
				RequestReturnBean returnBean = ForwardMessageJson.getContactsList(response.toString());
				if (HttpUtil.isSuccess(ForwardMessageUI.this, returnBean.getCode())) {
					listAllMap = returnBean.getListObject();
					adapter.setData(listAllMap);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 添加
			List<Map<String, String>> listTempMap = new ArrayList<Map<String, String>>();
			if (listAllMap != null && listAllMap.size() > 0) {
				Map<String, String> map;
				for (int i = 0; i < listAllMap.size(); i++) {
					map = listAllMap.get(i);
					if ("1".equals(map.get("is_select"))) {
						listTempMap.add(map);
					}
				}
				if (listAllMap != null && listAllMap.size() > 0) {
					String user_id;
					for (int i = 0; i < listAllMap.size(); i++) {
						user_id = listAllMap.get(i).get("user_id");
						if (!Utils.isEmity(forward_msg_id)) {
							// 直接转发
							forwardMessage(Constants.PREFIX + user_id, forward_msg_id);
						} else {
							// 图片转发
							sendImageMessage(Constants.PREFIX + user_id, img);
						}
					}
					MyApplication.getInstance().showToast("已发送");
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
				listAllMap.get(position).put("is_select", "0");
				adapter.setData(listAllMap);
				break;
			case 2:
				// true
				listAllMap.get(position).put("is_select", "1");
				adapter.setData(listAllMap);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
			// 先隐藏键盘
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			if (listAllMap == null) {
				return false;
			}
			search_name = et_search.getText().toString();
			listSearchMap = new ArrayList<Map<String, String>>();
			Map<String, String> map;
			for (int i = 0; i < listAllMap.size(); i++) {
				map = listAllMap.get(i);
				if (!Utils.isEmity(map.get("user_nick")) && map.get("user_nick").equals(search_name)) {
					listSearchMap.add(map);
				}
			}
			adapter.setData(listSearchMap);
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (listAllMap == null) {
			return;
		}
		if (s.length() > 0) {
			search_name = s.toString();
			listSearchMap = new ArrayList<Map<String, String>>();
			Map<String, String> map;
			for (int i = 0; i < listAllMap.size(); i++) {
				map = listAllMap.get(i);
				if (!Utils.isEmity(map.get("user_nick")) && map.get("user_nick").equals(search_name)) {
					listSearchMap.add(map);
				}
			}
			adapter.setData(listSearchMap);
		} else {
			adapter.setData(listAllMap);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	/**
	 * forward message
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String toChatUsername, String forward_msg_id) {
		final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// get the content and send it
			String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
			sendTextMessage(toChatUsername, content);
			break;
		case IMAGE:
			// send image
			String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// send thumb nail if original image does not exist
					filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
				}
				sendImageMessage(toChatUsername, filePath);
			}
			break;
		default:
			break;
		}
	}

	// send text
	protected void sendTextMessage(String toChatUsername, String content) {
		EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
		sendMessage(message);
	}

	// send image
	protected void sendImageMessage(String toChatUsername, String imagePath) {
		EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
		sendMessage(message);
	}

	// send message
	protected void sendMessage(EMMessage message) {
		if (message == null) {
			return;
		}
		// send message
		EMClient.getInstance().chatManager().sendMessage(message);
	}

	@Override
	protected void back() {
		finish();
	}

}
