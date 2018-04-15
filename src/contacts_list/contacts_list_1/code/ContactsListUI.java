package contacts_list.contacts_list_1.code;

import group_list.group_list_1.code.GroupListUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.utils.Constant;
import com.hyphenate.exceptions.HyphenateException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseFragmentUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.CharacterParser;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.PinyinComparator;
import com.shorigo.utils.Utils;
import com.shorigo.view.badgeview.Badge;
import com.shorigo.view.badgeview.QBadgeView;
import com.shorigo.yichat.R;

import contacts_list.contacts_list_1.code.MySideBar.OnTouchingLetterChangedListener;
import contacts_new_friends.contacts_new_friends_1.code.ContactsNewFriendsUI;

/**
 * 联系人
 * 
 * @author peidongxu
 * 
 */
public class ContactsListUI extends BaseFragmentUI implements OnTouchingLetterChangedListener {
	private ImageView iv_new_friend;// 新的朋友

	private List<Map<String, String>> listMap;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private ListView lv_contact;
	private ContactsListAdapter adapter;
	// 侧边栏
	private MySideBar msb_contact;
	// 点击侧边栏显示的大写字母
	private PopSB popSB;
	// 联系人IDs
	private String contactsIds = "";

	private Badge badge;

	@Override
	protected void loadViewLayout(LayoutInflater inflater, ViewGroup container) {
		view = inflater.inflate(R.layout.contacts_list_1, null);
	}

	@Override
	protected void findView_AddListener() {
		lv_contact = (ListView) view.findViewById(R.id.lv_contact);

		msb_contact = (MySideBar) view.findViewById(R.id.msb_contact);
		msb_contact.setOnTouchingLetterChangedListener(this);

		View headView = View.inflate(getActivity(), R.layout.contacts_list_1_head, null);
		LinearLayout z_new_friend = (LinearLayout) headView.findViewById(R.id.z_new_friend);
		z_new_friend.setOnClickListener(this);
		iv_new_friend = (ImageView) headView.findViewById(R.id.iv_new_friend);
		LinearLayout z_group = (LinearLayout) headView.findViewById(R.id.z_group);
		z_group.setOnClickListener(this);

		lv_contact.addHeaderView(headView);
	}

	@Override
	protected void prepareData() {
		// 初始化对象
		listMap = new ArrayList<Map<String, String>>();
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		adapter = new ContactsListAdapter(getActivity(), listMap);
		lv_contact.setAdapter(adapter);

		badge = new QBadgeView(getActivity()).bindTarget(iv_new_friend);

		// 注册联系人广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_CONTACT_CHANAGED);
		getActivity().registerReceiver(contactsBroadcastReceiver, filter);

	}

	@Override
	public void onResume() {
		super.onResume();

		InviteMessgeDao dao = new InviteMessgeDao(getActivity());
		int unreadMessagesCount = dao.getUnreadMessagesCount();
		if (unreadMessagesCount > 0) {
			badge.setBadgeNumber(-1);
		} else {
			badge.setBadgeNumber(0);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				getContactsList();
			}
		}).start();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_new_friend:
			// 新的朋友
			startActivity(new Intent(getActivity(), ContactsNewFriendsUI.class));
			break;
		case R.id.z_group:
			// 群组
			startActivity(new Intent(getActivity(), GroupListUI.class));
			break;
		default:
			break;
		}
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
			e.printStackTrace();
		}
		if (Utils.isEmity(contactsIds)) {
			return;
		}

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getContactsData();
			}
		});
	}

	private void getContactsData() {
		String url = HttpUtil.getUrl("/user/searchUserByIds");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("access_token", MyConfig.getToken(getActivity()));
		map.put("ids", contactsIds);
		HttpUtil.post(getActivity(), url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = ContactsListJson.getContactsList(response.toString());
				if (HttpUtil.isSuccess(getActivity(), returnBean.getCode())) {
					listMap = returnBean.getListObject();
					setData();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	private void setData() {
		listMap = filledData();
		// 根据a-z进行排序源数据
		Collections.sort(listMap, pinyinComparator);
		adapter.setData(listMap);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @return
	 */
	private List<Map<String, String>> filledData() {
		if (listMap == null) {
			listMap = new ArrayList<Map<String, String>>();
		}
		List<Map<String, String>> listSort = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		for (int i = 0; i < listMap.size(); i++) {
			map = listMap.get(i);
			String name = map.get("user_nick");
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(name);
			String sort = pinyin.substring(0, 1).toUpperCase();
			map.put("sort", sort);
			// 正则表达式，判断首字母是否是英文字母
			if (sort.matches("[A-Z]")) {
				map.put("sort", sort.toUpperCase());
			} else {
				map.put("sort", "#");
			}
			listSort.add(map);
		}
		return listSort;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		if (popSB == null) {
			popSB = new PopSB(view, getActivity());
		}
		popSB.setText(s);
	}

	@Override
	public void onTouchingLetterChanged(int idx) {
		lv_contact.setSelection(idx);
	}

	@Override
	public void onTouchingLetterUP() {
		if (popSB != null) {
			popSB.dismiss();
			popSB = null;
		}
	}

	BroadcastReceiver contactsBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			InviteMessgeDao dao = new InviteMessgeDao(getActivity());
			int unreadMessagesCount = dao.getUnreadMessagesCount();
			if (unreadMessagesCount > 0) {
				new QBadgeView(getActivity()).bindTarget(iv_new_friend).setBadgeNumber(-1);
			} else {
				new QBadgeView(getActivity()).bindTarget(iv_new_friend).setBadgeNumber(0);
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					getContactsList();
				}
			}).start();
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(contactsBroadcastReceiver);
	}

}
