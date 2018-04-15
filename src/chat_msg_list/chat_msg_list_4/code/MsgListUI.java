package chat_msg_list.chat_msg_list_4.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import chat.chat_1.code.ChatUI;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.Constant;
import com.shorigo.BaseFragmentUI;
import com.shorigo.yichat.R;

/**
 * 消息
 * 
 * @author peidongxu
 * 
 */
public class MsgListUI extends BaseFragmentUI implements OnItemClickListener, OnRefreshListener {
	private SwipeRefreshLayout srl_msg;
	private ListView mListView;
	private MsgListAdapter msgAdapter;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	@Override
	protected void loadViewLayout(LayoutInflater inflater, ViewGroup container) {
		view = inflater.inflate(R.layout.chat_msg_list_4, null);
	}

	@Override
	protected void findView_AddListener() {
		srl_msg = (SwipeRefreshLayout) view.findViewById(R.id.srl_msg);
		srl_msg.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		srl_msg.setOnRefreshListener(this);
		mListView = (ListView) view.findViewById(R.id.lv_msg);
		mListView.setOnItemClickListener(this);
		registerForContextMenu(mListView);
	}

	@Override
	protected void prepareData() {
		conversationList.addAll(loadConversationList());
		msgAdapter = new MsgListAdapter(getActivity(), 1, conversationList);
		mListView.setAdapter(msgAdapter);

		// 注册消息广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_MESSAGE_CHANAGED);
		getActivity().registerReceiver(msgBroadcastReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
		getActivity().sendBroadcast(new Intent(Constant.ACTION_MESSAGE_CHANAGED));
	}

	@Override
	protected void onMyClick(View v) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		EMConversation conversation = msgAdapter.getItem(position);
		String username = conversation.conversationId();
		// 进入聊天页面
		Intent intent = new Intent(getActivity(), ChatUI.class);
		if (conversation.isGroup()) {
			// 群组
			intent.putExtra("chatType", 2);
			intent.putExtra("id", username);
		} else {
			// 个人
			intent.putExtra("id", username);
		}
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean deleteMessage = false;
		EMConversation tobeDeleteCons = msgAdapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		if (tobeDeleteCons == null) {
			return true;
		}
		if (tobeDeleteCons.getType() == EMConversationType.GroupChat) {
			EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
		}
		try {
			// delete conversation
			EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		refresh();
		getActivity().sendBroadcast(new Intent(Constant.ACTION_MESSAGE_CHANAGED));
		return true;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationList());
		if (msgAdapter != null) {
			msgAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获取会话列表
	 * 
	 * @param context
	 * @return
	 */
	private List<EMConversation> loadConversationList() {
		// 加载所有本地群和回话
		Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first.equals(con2.first)) {
					return 0;
				} else if (con2.first.longValue() > con1.first.longValue()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	BroadcastReceiver msgBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	};

	@Override
	public void onRefresh() {
		refresh();
		srl_msg.setRefreshing(false);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().unregisterReceiver(msgBroadcastReceiver);
	}

}
