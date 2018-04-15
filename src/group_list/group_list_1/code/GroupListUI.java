package group_list.group_list_1.code;

import group_create.group_create_1.code.GroupCreateUI;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import chat.chat_1.code.ChatUI;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.utils.Constant;
import com.hyphenate.exceptions.HyphenateException;
import com.shorigo.BaseUI;
import com.shorigo.yichat.R;

/**
 * 群组
 * 
 * @author peidongxu
 * 
 */
public class GroupListUI extends BaseUI implements OnItemClickListener, OnRefreshListener {
	private ListView groupListView;
	private GroupListAdapter groupListAdapter;
	protected List<EMGroup> grouplist;
	private SwipeRefreshLayout swipeRefreshLayout;
	private boolean isFirst = true;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.group_list_1);
	}

	@Override
	protected void findView_AddListener() {
		groupListView = (ListView) findViewById(R.id.lv_msg_group);
		groupListView.setOnItemClickListener(this);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("群组");
		setRightButton("创建群组");

		grouplist = EMClient.getInstance().groupManager().getAllGroups();
		groupListAdapter = new GroupListAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupListAdapter);

		isFirst = true;
		if (isFirst) {
			getServerGroup();
			isFirst = false;
		}

		// 注册群组广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_GROUP_CHANAGED);
		registerReceiver(groupBroadcastReceiver, filter);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 创建群组
			startActivity(new Intent(this, GroupCreateUI.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 进入群聊
		Intent intent = new Intent(this, ChatUI.class);
		intent.putExtra("chatType", 2);
		intent.putExtra("id", groupListAdapter.getItem(position).getGroupId());
		startActivity(intent);
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		if (groupListView != null && groupListAdapter != null) {
			grouplist = EMClient.getInstance().groupManager().getAllGroups();
			groupListAdapter = new GroupListAdapter(this, 1, grouplist);
			groupListView.setAdapter(groupListAdapter);
			groupListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 同步服务器群组
	 */
	private void getServerGroup() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// 需异步处理
					List<EMGroup> grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
					runOnUiThread(new Runnable() {
						public void run() {
							refresh();
							swipeRefreshLayout.setRefreshing(false);
						}
					});
				} catch (HyphenateException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							swipeRefreshLayout.setRefreshing(false);
						}
					});
				}
			}
		}).start();
	}

	BroadcastReceiver groupBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getServerGroup();
		}
	};

	@Override
	public void onRefresh() {
		getServerGroup();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(groupBroadcastReceiver);
	}

	@Override
	protected void back() {
		finish();
	}
}
