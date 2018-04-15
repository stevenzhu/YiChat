package contacts_new_friends.contacts_new_friends_1.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import bean.RequestReturnBean;

import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.domain.InviteMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.yichat.R;

import contacts_add.contacts_add_1.code.ContactsAddUI;

/**
 * 新的朋友
 * 
 * @author peidongxu
 * 
 */
public class ContactsNewFriendsUI extends BaseUI {
	private ListView listView;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.contacts_new_friends_1);
	}

	@Override
	protected void findView_AddListener() {
		listView = (ListView) findViewById(R.id.list);
	}

	@Override
	protected void prepareData() {
		setTitle("新的朋友");
		setRightButton("添加朋友");

		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		dao.saveUnreadMessageCount(0);

		ContactsNewFriendsAdapter adapter = new ContactsNewFriendsAdapter(this, 1, msgs, mHandler);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 添加朋友
			startActivity(new Intent(this, ContactsAddUI.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 添加好友
	 */
	private void addFriend(String to_user_id) {
		String url = HttpUtil.getUrl("/user/addFriend");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		if (to_user_id.contains(Constants.PREFIX)) {
			to_user_id = to_user_id.replace(Constants.PREFIX, "");
		}
		map.put("to_user_id", to_user_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = ContactsNewFriendsJson.addFriend(response.toString());
				if (HttpUtil.isSuccess(ContactsNewFriendsUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String to_user_id = (String) msg.obj;
				addFriend(to_user_id);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void back() {
		finish();
	}
}
