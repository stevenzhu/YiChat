package msg_main.msg_main_1.code;

import group_create.group_create_1.code.GroupCreateUI;

import java.lang.ref.WeakReference;

import user_card.user_card_1.code.UserCardUI;
import zxing.zxing_1.code.CaptureUI;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import chat_msg_list.chat_msg_list_4.code.MsgListUI;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.utils.Constant;
import com.shorigo.FBaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.utils.Utils;
import com.shorigo.view.badgeview.QBadgeView;
import com.shorigo.yichat.R;

import contacts_add.contacts_add_1.code.ContactsAddUI;
import contacts_list.contacts_list_1.code.ContactsListUI;

/**
 * 首页
 * 
 * @author peidongxu
 * 
 */
public class MsgMainUI extends FBaseUI {
	private View z_title;
	// 消息、通讯录
	private RelativeLayout z_msg, z_friend;
	private View v_msg, v_friend;

	Fragment msgFragment;
	Fragment contactsFragment;

	private ActivitiesReceiver receiver;
	private static final String ACTION = "msg_main.msg_main_1.code.MsgMainUI";

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.msg_main_1);
	}

	@Override
	protected void findView_AddListener() {
		z_title = findViewById(R.id.z_title);
		z_msg = (RelativeLayout) findViewById(R.id.z_msg);
		z_msg.setOnClickListener(this);
		z_friend = (RelativeLayout) findViewById(R.id.z_friend);
		z_friend.setOnClickListener(this);

		v_msg = findViewById(R.id.v_msg);
		v_friend = findViewById(R.id.v_friend);

		RelativeLayout z_add = (RelativeLayout) findViewById(R.id.z_add);
		z_add.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {

		updateSelect(0);
		msgFragment = new MsgListUI();
		mContent = msgFragment;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.fl_layout, msgFragment);
		transaction.commitAllowingStateLoss();

		receiver = new ActivitiesReceiver(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		registerReceiver(receiver, filter);

		// 注册消息广播
		IntentFilter msgfilter = new IntentFilter();
		msgfilter.addAction(Constant.ACTION_MESSAGE_CHANAGED);
		registerReceiver(msgBroadcastReceiver, msgfilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onMyClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.z_msg:
			// 消息
			updateSelect(0);
			intent = new Intent(ACTION);
			intent.putExtra("key", 1);
			this.sendBroadcast(intent);
			break;
		case R.id.z_friend:
			// 通讯录
			updateSelect(1);
			intent = new Intent(ACTION);
			intent.putExtra("key", 2);
			this.sendBroadcast(intent);
			break;
		case R.id.z_add:
			// 添加
			new MoreMenuPop(z_title, this, mHandler);
			break;
		}
	}

	public class ActivitiesReceiver extends BroadcastReceiver {
		WeakReference<MsgMainUI> ref;

		ActivitiesReceiver(MsgMainUI activity) {
			ref = new WeakReference<MsgMainUI>(activity);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			int key = intent.getIntExtra("key", 0);
			if (0 == key) {
				return;
			}
			switch (key) {
			case 1:
				// 消息
				if (msgFragment == null) {
					msgFragment = new MsgListUI();
				}
				switchContent(msgFragment);
				break;
			case 2:
				// 通讯录
				if (contactsFragment == null) {
					contactsFragment = new ContactsListUI();
				}
				switchContent(contactsFragment);
				break;
			}
		}
	}

	private Fragment mContent;

	/**
	 * 修改显示的内容 不会重新加载
	 **/
	public void switchContent(Fragment to) {
		if (mContent != to) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(mContent).add(R.id.fl_layout, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
			mContent = to;
		}
	}

	/**
	 * 滑动tab状态设置
	 */
	private void updateSelect(int position) {
		if (0 == position) {
			z_msg.setSelected(true);
			z_friend.setSelected(false);
			v_msg.setVisibility(View.VISIBLE);
			v_friend.setVisibility(View.GONE);
		} else if (1 == position) {
			z_msg.setSelected(false);
			z_friend.setSelected(true);
			v_msg.setVisibility(View.GONE);
			v_friend.setVisibility(View.VISIBLE);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				// 群组创建
				startActivity(new Intent(MsgMainUI.this, GroupCreateUI.class));
				break;
			case 101:
				// 添加好友
				startActivity(new Intent(MsgMainUI.this, ContactsAddUI.class));
				break;
			case 102:
				// 扫一扫
				startActivityForResult(new Intent(MsgMainUI.this, CaptureUI.class), 100);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
			String content = data.getStringExtra("content");
			if (!Utils.isEmity(content) && content.contains(Constants.PREFIX)) {
				content = content.replace(Constants.PREFIX, "");
			}
			Intent intent = new Intent(this, UserCardUI.class);
			intent.putExtra("user_id", content);
			startActivity(intent);
		}
	}

	/**
	 * 滑动状态监听
	 * 
	 * @author peidongxu
	 */
	class ViewPagetOnPagerChangedLisenter implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			updateSelect(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	BroadcastReceiver msgBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	};

	private void refresh() {
		int count = 0;
		// 未读消息数
		count = EMClient.getInstance().chatManager().getUnreadMessageCount();
		if (count > 0) {
			new QBadgeView(this).bindTarget(z_msg).setBadgeNumber(-1);
		} else {
			new QBadgeView(this).bindTarget(z_msg).setBadgeNumber(0);
		}
		// 邀请消息
		InviteMessgeDao dao = new InviteMessgeDao(this);
		int unreadMessagesCount = dao.getUnreadMessagesCount();
		if (unreadMessagesCount > 0) {
			new QBadgeView(this).bindTarget(z_friend).setBadgeNumber(-1);
		} else {
			new QBadgeView(this).bindTarget(z_friend).setBadgeNumber(0);
		}
	}

	@Override
	protected void back() {
		finish();
	}
}
