package com.hyphenate.easeui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import bean.UserBean;
import chat.chat_1.code.ChatUI;
import chat.chat_1.code.EmojiconExampleGroupData;
import chat.chat_1.code.VideoCallUI;
import chat.chat_1.code.VoiceCallUI;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.db.DemoDBManager;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.db.UserDao;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.InviteMessage;
import com.hyphenate.easeui.domain.InviteMessage.InviteMesageStatus;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

public class HXHelper {
	/**
	 * data sync listener
	 */
	public interface DataSyncListener {
		/**
		 * sync complete
		 * 
		 * @param success
		 *            true：data sync successful，false: failed to sync data
		 */
		void onSyncComplete(boolean success);
	}

	protected static final String TAG = "HXHelper";

	private EaseUI easeUI;

	/**
	 * EMEventListener
	 */
	protected EMMessageListener messageListener = null;

	private Map<String, EaseUser> contactList;

	private static HXHelper instance = null;

	/**
	 * sync groups status listener
	 */
	private List<DataSyncListener> syncGroupsListeners;
	/**
	 * sync contacts status listener
	 */
	private List<DataSyncListener> syncContactsListeners;
	/**
	 * sync blacklist status listener
	 */
	private List<DataSyncListener> syncBlackListListeners;

	private boolean isSyncingGroupsWithServer = false;
	private boolean isSyncingContactsWithServer = false;
	private boolean isSyncingBlackListWithServer = false;
	private boolean isGroupsSyncedWithServer = false;
	private boolean isContactsSyncedWithServer = false;
	private boolean isBlackListSyncedWithServer = false;

	public boolean isVoiceCalling;
	public boolean isVideoCalling;

	private Context appContext;

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	private boolean isGroupAndContactListenerRegisted;

	public static final String MSG_ACTION = "com.hyphenate.easeui.utils.msg_action";

	private HXHelper() {
	}

	public synchronized static HXHelper getInstance() {
		if (instance == null) {
			instance = new HXHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            main context
	 */
	public void init(Context context) {
		EMOptions options = initChatOptions();

		// use default options if options is null
		if (EaseUI.getInstance().init(context, options)) {
			appContext = context;

			// debug mode, you'd better set it to false, if you want release
			// your App officially.
			EMClient.getInstance().setDebugMode(true);
			// get easeui instance
			easeUI = EaseUI.getInstance();
			// to set user's profile and avatar
			setEaseUIProviders();

			setGlobalListeners();
			initDbDao();
		}
	}

	private EMOptions initChatOptions() {
		Log.d(TAG, "init HuanXin Options");

		EMOptions options = new EMOptions();
		// set if accept the invitation automatically
		options.setAcceptInvitationAlways(false);
		options.setAutoAcceptGroupInvitation(false);
		// set if you need read ack
		options.setRequireAck(true);
		// set if you need delivery ack
		options.setRequireDeliveryAck(false);

		// you need apply & set your own id if you want to use google cloud
		// messaging.
		// options.setGCMNumber("324169311137");
		// you need apply & set your own id if you want to use Mi push
		// notification
		// options.setMipushConfig("2882303761517426801", "5381742660801");
		// you need apply & set your own id if you want to use Huawei push
		// notification
		// options.setHuaweiPushAppId("10492024");

		return options;
	}

	public class CallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// username
			String from = intent.getStringExtra("from");
			// call type
			String type = intent.getStringExtra("type");
			if ("video".equals(type)) {
				// video call
				context.startActivity(new Intent(context, VideoCallUI.class).putExtra("username", from).putExtra("isComingCall", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			} else {
				// voice call
				context.startActivity(new Intent(context, VoiceCallUI.class).putExtra("username", from).putExtra("isComingCall", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
			EMLog.d("CallReceiver", "app received a incoming call");
		}

	}

	protected void setEaseUIProviders() {
		// set user avatar to circle shape
		EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
		avatarOptions.setAvatarShape(1);
		easeUI.setAvatarOptions(avatarOptions);

		// set profile provider if you want easeUI to handle avatar and nickname
		easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

			@Override
			public EaseUser getUser(String username) {
				return getUserInfo(username);
			}
		});

		// set emoji icon provider
		easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {

			@Override
			public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
				EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
				for (EaseEmojicon emojicon : data.getEmojiconList()) {
					if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
						return emojicon;
					}
				}
				return null;
			}

			@Override
			public Map<String, Object> getTextEmojiconMapping() {
				return null;
			}
		});

		// set notification options, will use default if you don't set it
		easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// you can update title here
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// you can update icon here
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// be used on notification bar, different text according the
				// message type.
				String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				EaseUser user = getUserInfo(message.getFrom());
				if (user != null) {
					if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
						return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
					}
					return user.getNick() + ": " + ticker;
				} else {
					if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
						return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
					}
					return message.getFrom() + ": " + ticker;
				}
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
				// here you can customize the text.
				// return fromUsersNum + "contacts send " + messageNum +
				// "messages to you";
				return null;
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// you can set what activity you want display when user click
				// the notification
				Intent intent = new Intent(appContext, ChatUI.class);
				// open calling activity if there is call
				if (isVideoCalling) {
					intent = new Intent(appContext, VideoCallUI.class);
					intent.putExtra("id", message.getFrom());
				} else if (isVoiceCalling) {
					intent = new Intent(appContext, VoiceCallUI.class);
					intent.putExtra("id", message.getFrom());
				} else {
					ChatType chatType = message.getChatType();
					if (chatType == ChatType.Chat) { // single chat message
						intent.putExtra("id", message.getFrom());
						intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
					} else { // group chat message
						// message.getTo() is the group id
						intent.putExtra("id", message.getTo());
						if (chatType == ChatType.GroupChat) {
							intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
						} else {
							intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
						}

					}
				}
				return intent;
			}
		});
	}

	EMConnectionListener connectionListener;

	/**
	 * set global listener
	 */
	protected void setGlobalListeners() {
		syncGroupsListeners = new ArrayList<DataSyncListener>();
		syncContactsListeners = new ArrayList<DataSyncListener>();
		syncBlackListListeners = new ArrayList<DataSyncListener>();

		// create the global connection listener
		connectionListener = new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {
//				EMLog.d("global listener", "onDisconnect" + error);
//				if (error == EMError.USER_REMOVED) {
//					// 显示帐号已经被移除
//					MyApplication.getInstance().showToast("显示帐号已经被移除");
//				} else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
//					// 显示帐号在其他设备登陆
//					MyConfig.saveToken(appContext, "");
//					Intent intent = new Intent(appContext, LoginUI.class);
//					appContext.startActivity(intent);
//				} else {
//					if (NetUtils.hasNetwork(appContext)) {
//						// 连接不到聊天服务器
//					} else {
//						// 当前网络不可用，请检查网络设置
//					}
//				}
			}

			@Override
			public void onConnected() {
				// in case group and contact were already synced, we supposed to
				// notify sdk we are ready to receive the events
				if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
					EMLog.d(TAG, "group and contact already synced with servre");
				} else {
					if (!isGroupsSyncedWithServer) {
						asyncFetchGroupsFromServer(null);
					}

					if (!isContactsSyncedWithServer) {
						asyncFetchContactsFromServer(null);
					}

					if (!isBlackListSyncedWithServer) {
						asyncFetchBlackListFromServer(null);
					}
				}
			}
		};

		IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
		appContext.registerReceiver(new CallReceiver(), callFilter);
		// register incoming call receiver
		// register connection listener
		EMClient.getInstance().addConnectionListener(connectionListener);
		// register group and contact event listener
		registerGroupAndContactListener();
		// register message event listener
		registerMessageListener();

	}

	private void initDbDao() {
		inviteMessgeDao = new InviteMessgeDao(appContext);
		userDao = new UserDao(appContext);
	}

	/**
	 * register group and contact listener, you need register when login
	 */
	public void registerGroupAndContactListener() {
		if (!isGroupAndContactListenerRegisted) {
			EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
			EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
			isGroupAndContactListenerRegisted = true;
		}

	}

	/**
	 * group change listener
	 */
	class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// user invite you to join group
			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			if (!Utils.isEmity(groupName)) {
				msg.setGroupName(groupName);
			} else {
				try {
					EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
					msg.setGroupName(group.getGroupName());
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
			}
			msg.setReason(reason);
			msg.setGroupInviter(inviter);
			Log.d(TAG, "receive invitation to join the group：" + groupName);
			msg.setStatus(InviteMesageStatus.GROUPINVITATION);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationAccepted(String groupId, String invitee, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// user accept your invitation
			boolean hasGroup = false;
			EMGroup _group = null;
			for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					_group = group;
					break;
				}
			}
			if (!hasGroup)
				return;

			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(_group == null ? groupId : _group.getGroupName());
			msg.setReason(reason);
			msg.setGroupInviter(invitee);
			Log.d(TAG, invitee + "Accept to join the group：" + _group == null ? groupId : _group.getGroupName());
			msg.setStatus(InviteMesageStatus.GROUPINVITATION_ACCEPTED);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// user declined your invitation
			EMGroup group = null;
			for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (_group.getGroupId().equals(groupId)) {
					group = _group;
					break;
				}
			}
			if (group == null)
				return;

			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(group.getGroupName());
			msg.setReason(reason);
			msg.setGroupInviter(invitee);
			Log.d(TAG, invitee + "Declined to join the group：" + group.getGroupName());
			msg.setStatus(InviteMesageStatus.GROUPINVITATION_DECLINED);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// user is removed from group
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onGroupDestroyed(String groupId, String groupName) {
			// group is dismissed,
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

			// user apply to join group
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " Apply to join group：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

			String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
			// your main was accepted
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(accepter + " " + st4));
			msg.setStatus(Status.SUCCESS);
			// save accept message
			EMClient.getInstance().chatManager().saveMessage(msg);
			// notify the accept message
			getNotifier().vibrateAndPlayTone(msg);

			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
			// your main was declined, we do nothing here in demo
		}

		@Override
		public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
			// got an invitation
			String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(inviter + " " + st3));
			msg.setStatus(EMMessage.Status.SUCCESS);
			// save invitation as messages
			EMClient.getInstance().chatManager().saveMessage(msg);
			// notify invitation message
			getNotifier().vibrateAndPlayTone(msg);
			EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
			appContext.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		// ============================= group_reform new add api begin
		@Override
		public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
			StringBuilder sb = new StringBuilder();
			for (String member : mutes) {
				sb.append(member).append(",");
			}
		}

		@Override
		public void onMuteListRemoved(String groupId, final List<String> mutes) {
			StringBuilder sb = new StringBuilder();
			for (String member : mutes) {
				sb.append(member).append(",");
			}
		}

		@Override
		public void onAdminAdded(String groupId, String administrator) {
		}

		@Override
		public void onAdminRemoved(String groupId, String administrator) {
		}

		@Override
		public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
		}

		@Override
		public void onMemberJoined(String groupId, String member) {
			EMLog.d(TAG, "onMemberJoined");
		}

		@Override
		public void onMemberExited(String groupId, String member) {
			EMLog.d(TAG, "onMemberJoined");
		}

		@Override
		public void onAnnouncementChanged(String groupId, String announcement) {

		}

		@Override
		public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {

		}

		@Override
		public void onSharedFileDeleted(String groupId, String fileId) {

		}
		// ============================= group_reform new add api end
	}

	/***
	 * 好友变化listener
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(String username) {
			// save contact
			Map<String, EaseUser> localUsers = getContactList();
			Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
			EaseUser user = new EaseUser(username);

			if (!localUsers.containsKey(username)) {
				userDao.saveContact(user);
			}
			toAddUsers.put(username, user);
			localUsers.putAll(toAddUsers);

			appContext.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactDeleted(String username) {
			Map<String, EaseUser> localUsers = HXHelper.getInstance().getContactList();
			localUsers.remove(username);
			userDao.deleteContact(username);
			inviteMessgeDao.deleteMessage(username);

			EMClient.getInstance().chatManager().deleteConversation(username, false);

			appContext.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactInvited(String username, String reason) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// save invitation as message
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "apply to be your friend,reason: " + reason);
			// set invitation status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onFriendRequestAccepted(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// save invitation as message
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "accept your request");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewInviteMessage(msg);
			appContext.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onFriendRequestDeclined(String username) {
			// your request was refused
			Log.d(username, username + " refused to your request");
		}
	}

	/**
	 * save and notify invitation message
	 * 
	 * @param msg
	 */
	private void notifyNewInviteMessage(InviteMessage msg) {
		if (inviteMessgeDao == null) {
			inviteMessgeDao = new InviteMessgeDao(appContext);
		}
		inviteMessgeDao.saveMessage(msg);
		// increase the unread message count
		inviteMessgeDao.saveUnreadMessageCount(1);
		// notify there is new message
		getNotifier().vibrateAndPlayTone(null);
	}

	private EaseUser getUserInfo(String username) {
		Map<String,String> userMap = MyConfig.getUserInfo(appContext);
		EaseUser user;
		if (userMap != null) {
			user = new EaseUser(userMap.get("user_nick"));
			user.setAvatar(userMap.get("avatar"));
			EaseCommonUtils.setUserInitialLetter(user);
		} else {
			user = new EaseUser(username);
			EaseCommonUtils.setUserInitialLetter(user);
		}
		return user;
	}

	/**
	 * 消息监听
	 */
	protected void registerMessageListener() {
		messageListener = new EMMessageListener() {

			@Override
			public void onMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
					// in background, do not refresh UI, notify it in
					// notification bar
					if (!easeUI.hasForegroundActivies()) {
						getNotifier().onNewMsg(message);
						appContext.sendBroadcast(new Intent(Constant.ACTION_MESSAGE_CHANAGED));
					}
				}
			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					EMLog.d(TAG, "receive command message");
					// get message body
					EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
					final String action = cmdMsgBody.action();// 获取自定义action
				}
			}

			@Override
			public void onMessageRead(List<EMMessage> messages) {
			}

			@Override
			public void onMessageDelivered(List<EMMessage> message) {
			}

			@Override
			public void onMessageChanged(EMMessage message, Object change) {
				EMLog.d(TAG, "change:");
				EMLog.d(TAG, "change:" + change);
			}
		};

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	/**
	 * if ever logged in
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}

	/**
	 * logout
	 * 
	 * @param unbindDeviceToken
	 *            whether you need unbind your device token
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		endCall();
		Log.d(TAG, "logout: " + unbindDeviceToken);
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}

	/**
	 * get instance of EaseNotifier
	 * 
	 * @return
	 */
	public EaseNotifier getNotifier() {
		return easeUI.getNotifier();
	}

	/**
	 * update contact list
	 * 
	 * @param aContactList
	 */
	public void setContactList(Map<String, EaseUser> aContactList) {
		if (aContactList == null) {
			if (contactList != null) {
				contactList.clear();
			}
			return;
		}

		contactList = aContactList;
	}

	/**
	 * save single contact
	 */
	public void saveContact(EaseUser user) {
		contactList.put(user.getUsername(), user);
	}

	/**
	 * get contact list
	 * 
	 * @return
	 */
	public Map<String, EaseUser> getContactList() {
		// return a empty non-null object to avoid app crash
		if (contactList == null) {
			return new Hashtable<String, EaseUser>();
		}

		return contactList;
	}

	/**
	 * update user list to cache and database
	 * 
	 * @param contactInfoList
	 */
	public void updateContactList(List<EaseUser> contactInfoList) {
		for (EaseUser u : contactInfoList) {
			contactList.put(u.getUsername(), u);
		}
		ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
		mList.addAll(contactList.values());
	}

	void endCall() {
		try {
			EMClient.getInstance().callManager().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.add(listener);
		}
	}

	public void removeSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.remove(listener);
		}
	}

	public void addSyncContactListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactsListeners.contains(listener)) {
			syncContactsListeners.add(listener);
		}
	}

	public void removeSyncContactListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactsListeners.contains(listener)) {
			syncContactsListeners.remove(listener);
		}
	}

	public void addSyncBlackListListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncBlackListListeners.contains(listener)) {
			syncBlackListListeners.add(listener);
		}
	}

	public void removeSyncBlackListListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncBlackListListeners.contains(listener)) {
			syncBlackListListeners.remove(listener);
		}
	}

	/**
	 * Get group list from server This method will save the sync state
	 * 
	 * @throws HyphenateException
	 */
	public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
		if (isSyncingGroupsWithServer) {
			return;
		}

		isSyncingGroupsWithServer = true;

		new Thread() {
			@Override
			public void run() {
				try {
					EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isGroupsSyncedWithServer = false;
						isSyncingGroupsWithServer = false;
						noitifyGroupSyncListeners(false);
						return;
					}

					isGroupsSyncedWithServer = true;
					isSyncingGroupsWithServer = false;

					// notify sync group list success
					noitifyGroupSyncListeners(true);

					if (callback != null) {
						callback.onSuccess();
					}
				} catch (HyphenateException e) {
					isGroupsSyncedWithServer = false;
					isSyncingGroupsWithServer = false;
					noitifyGroupSyncListeners(false);
					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void noitifyGroupSyncListeners(boolean success) {
		for (DataSyncListener listener : syncGroupsListeners) {
			listener.onSyncComplete(success);
		}
	}

	public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback) {
		if (isSyncingContactsWithServer) {
			return;
		}

		isSyncingContactsWithServer = true;

		new Thread() {
			@Override
			public void run() {
				List<String> usernames = null;
				try {
					usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isContactsSyncedWithServer = false;
						isSyncingContactsWithServer = false;
						notifyContactsSyncListener(false);
						return;
					}

					Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
					for (String username : usernames) {
						EaseUser user = new EaseUser(username);
						EaseCommonUtils.setUserInitialLetter(user);
						userlist.put(username, user);
					}
					// save the contact list to cache
					getContactList().clear();
					getContactList().putAll(userlist);
					// save the contact list to database
					UserDao dao = new UserDao(appContext);
					List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
					dao.saveContactList(users);

					isContactsSyncedWithServer = true;
					isSyncingContactsWithServer = false;

					// notify sync success
					notifyContactsSyncListener(true);

					if (callback != null) {
						callback.onSuccess(usernames);
					}
				} catch (HyphenateException e) {
					isContactsSyncedWithServer = false;
					isSyncingContactsWithServer = false;
					notifyContactsSyncListener(false);
					e.printStackTrace();
					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void notifyContactsSyncListener(boolean success) {
		for (DataSyncListener listener : syncContactsListeners) {
			listener.onSyncComplete(success);
		}
	}

	public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {

		if (isSyncingBlackListWithServer) {
			return;
		}

		isSyncingBlackListWithServer = true;

		new Thread() {
			@Override
			public void run() {
				try {
					List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();

					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isBlackListSyncedWithServer = false;
						isSyncingBlackListWithServer = false;
						notifyBlackListSyncListener(false);
						return;
					}

					isBlackListSyncedWithServer = true;
					isSyncingBlackListWithServer = false;

					notifyBlackListSyncListener(true);
					if (callback != null) {
						callback.onSuccess(usernames);
					}
				} catch (HyphenateException e) {
					isBlackListSyncedWithServer = false;
					isSyncingBlackListWithServer = true;
					e.printStackTrace();

					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void notifyBlackListSyncListener(boolean success) {
		for (DataSyncListener listener : syncBlackListListeners) {
			listener.onSyncComplete(success);
		}
	}

	public boolean isSyncingGroupsWithServer() {
		return isSyncingGroupsWithServer;
	}

	public boolean isSyncingContactsWithServer() {
		return isSyncingContactsWithServer;
	}

	public boolean isSyncingBlackListWithServer() {
		return isSyncingBlackListWithServer;
	}

	public boolean isGroupsSyncedWithServer() {
		return isGroupsSyncedWithServer;
	}

	public boolean isContactsSyncedWithServer() {
		return isContactsSyncedWithServer;
	}

	public boolean isBlackListSyncedWithServer() {
		return isBlackListSyncedWithServer;
	}

	synchronized void reset() {
		isSyncingGroupsWithServer = false;
		isSyncingContactsWithServer = false;
		isSyncingBlackListWithServer = false;

		isGroupsSyncedWithServer = false;
		isContactsSyncedWithServer = false;
		isBlackListSyncedWithServer = false;

		isGroupAndContactListenerRegisted = false;

		setContactList(null);
		DemoDBManager.getInstance().closeDB();
	}

	public void pushActivity(Activity activity) {
		easeUI.pushActivity(activity);
	}

	public void popActivity(Activity activity) {
		easeUI.popActivity(activity);
	}

}
