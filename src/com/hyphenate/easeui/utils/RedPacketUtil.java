package com.hyphenate.easeui.utils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import bean.UserBean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;

public class RedPacketUtil {

	/**
	 * 进入红包、转账页面的相关方法
	 * 
	 * @param activity
	 *            FragmentActivity
	 * @param itemType
	 *            转账、单聊红包、群聊红包、小额随机红包
	 * @param toChatUsername
	 *            接收者id或者群id
	 * @param callback
	 *            RPSendPacketCallback
	 */
	// public static void startRedPacket(final FragmentActivity activity, int
	// itemType, final String toChatUsername, RPSendPacketCallback callback) {
	// RedPacketInfo redPacketInfo = new RedPacketInfo();
	// if (itemType == RPConstant.RP_ITEM_TYPE_GROUP) {
	// //拉取最新群组数据
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// EMGroup group =
	// EMClient.getInstance().groupManager().getGroupFromServer(toChatUsername);
	// EMClient.getInstance().groupManager().fetchGroupMembers(toChatUsername,
	// "", group.getMemberCount());
	// } catch (HyphenateException e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	// RedPacket.getInstance().setRPGroupMemberListener(new
	// RPGroupMemberListener() {
	// @Override
	// public void getGroupMember(String groupId,
	// RPValueCallback<List<RPUserBean>> callback) {
	// EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
	// List<String> members = group.getMembers();
	// members.add(group.getOwner());
	// members.addAll(group.getAdminList());
	// List<RPUserBean> userBeanList = new ArrayList<RPUserBean>();
	// EaseUser user;
	// for (int i = 0; i < members.size(); i++) {
	// RPUserBean userBean = new RPUserBean();
	// userBean.userId = members.get(i);
	// if (userBean.userId.equals(EMClient.getInstance().getCurrentUser())) {
	// continue;
	// }
	// user = EaseUserUtils.getUserInfo(userBean.userId);
	// if (user != null) {
	// userBean.userAvatar = TextUtils.isEmpty(user.getAvatar()) ? "none" :
	// user.getAvatar();
	// userBean.userNickname = TextUtils.isEmpty(user.getNick()) ?
	// user.getUsername() : user.getNick();
	// } else {
	// userBean.userNickname = userBean.userId;
	// userBean.userAvatar = "none";
	// }
	// userBeanList.add(userBean);
	// }
	// callback.onSuccess(userBeanList);
	// }
	// });
	// EMGroup group =
	// EMClient.getInstance().groupManager().getGroup(toChatUsername);
	// redPacketInfo.toGroupId = group.getGroupId();
	// redPacketInfo.groupMemberCount = group.getMemberCount();
	// } else {
	// EaseUser easeToUser = EaseUserUtils.getUserInfo(toChatUsername);
	// String toAvatarUrl = "none";
	// String toUserName = "";
	// if (easeToUser != null) {
	// toAvatarUrl = TextUtils.isEmpty(easeToUser.getAvatar()) ? "none" :
	// easeToUser.getAvatar();
	// toUserName = TextUtils.isEmpty(easeToUser.getNick()) ?
	// easeToUser.getUsername() : easeToUser.getNick();
	// }
	// redPacketInfo.toUserId = toChatUsername;
	// redPacketInfo.toAvatarUrl = toAvatarUrl;
	// redPacketInfo.toNickName = toUserName;
	// }
	// RPRedPacketUtil.getInstance().startRedPacket(activity, itemType,
	// redPacketInfo, callback);
	// }

	/**
	 * 领取红包成功 发送消息到聊天窗口
	 * 
	 * @param activity
	 *            FragmentActivity
	 * @param chatType
	 *            聊天类型
	 * @param message
	 *            EMMessage
	 * @param toChatUsername
	 *            消息接收者id
	 * @param messageList
	 *            EaseChatMessageList
	 */
	public static void openRedPacket(Context context, final int chatType, final EMMessage message, final String toChatUsername, final EaseChatMessageList messageList) {
		String order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");
		String receiverId = EMClient.getInstance().getCurrentUser();
		String receiverNickname = receiverId;
		Map<String,String> userInfo = MyConfig.getUserInfo(context);
		if (userInfo != null) {
			receiverNickname = userInfo.get("user_nick");
		}
		Gson gson = new Gson();
		Type userType = new TypeToken<Map<String, String>>() {
		}.getType();
		String user_id = "";
		if (!Utils.isEmity(toChatUsername) && toChatUsername.contains(Constants.PREFIX)) {
			user_id = toChatUsername.replace(Constants.PREFIX, "");
		}
		Map<String, String> userMap = gson.fromJson(ACache.get(context).getAsString("user_" + user_id), userType);
		String senderId = toChatUsername;
		String senderNickname = userMap.get("user_nick");
		// if (chatType == EaseConstant.CHATTYPE_SINGLE) {
		// if (!isRandomRedPacket(message)) {
		// EMMessage msg =
		// EMMessage.createTxtSendMessage(String.format(context.getResources().getString(R.string.msg_someone_take_red_packet),
		// receiverNickname), toChatUsername);
		// msg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE,
		// true);
		// msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME,
		// receiverNickname);
		// msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME,
		// senderNickname);
		// msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
		// EMClient.getInstance().chatManager().sendMessage(msg);
		// }
		// } else {
		sendRedPacketAckMessage(context, chatType, message, senderId, senderNickname, receiverId, receiverNickname, new EMCallBack() {
			@Override
			public void onSuccess() {
				messageList.refresh();
			}

			@Override
			public void onError(int i, String s) {

			}

			@Override
			public void onProgress(int i, String s) {

			}
		});
		// }

	}

	/**
	 * 创建红包消息
	 * 
	 * @param context
	 *            上下文
	 * @param redPacketInfo
	 *            RedPacketInfo
	 * @param toChatUsername
	 *            接收者id或群id
	 * @return
	 */
	public static EMMessage createRPMessage(Context context, Intent data, String toChatUsername) {
		String type = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_TYPE);
		String order_id = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_ID);
		String receiveId = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);
		String greeting = data.getStringExtra(RedPacketConstant.EXTRA_RED_PACKET_GREETING);
		EMMessage message = EMMessage.createTxtSendMessage("[" + context.getResources().getString(R.string.easemob_red_packet) + "]" + greeting, toChatUsername);
		message.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, true);
		message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_TYPE, type);
		message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
		message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, receiveId);
		message.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_GREETING, greeting);
		message.setAttribute(RedPacketConstant.EXTRA_SPONSOR_NAME, context.getResources().getString(R.string.easemob_red_packet));
		return message;
	}

	/**
	 * 使用cmd消息发送领到红包之后的回执消息
	 */
	private static void sendRedPacketAckMessage(final Context context, final int chatType, final EMMessage message, final String senderId, final String senderNickname, String receiverId, final String receiverNickname, final EMCallBack callBack) {
		final String order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");
		// 创建透传消息
		final EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
		cmdMsg.setChatType(EMMessage.ChatType.Chat);
		EMCmdMessageBody cmdBody = new EMCmdMessageBody(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
		cmdMsg.addBody(cmdBody);
		cmdMsg.setTo(senderId);
		// 设置扩展属性
		cmdMsg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, receiverId);
		cmdMsg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_GROUP_ID, message.getTo());
		cmdMsg.setMessageStatusCallback(new EMCallBack() {
			@Override
			public void onSuccess() {
				if (chatType == EaseConstant.CHATTYPE_SINGLE) {
					if (!isRandomRedPacket(message)) {
						EMMessage msg = EMMessage.createTxtSendMessage("你领取了" + senderNickname + "的红包", senderId);
						msg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
						msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
						msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
						msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
						EMClient.getInstance().chatManager().saveMessage(msg);
						callBack.onSuccess();
					}
				} else {
					// 保存消息到本地
					EMMessage sendMessage = EMMessage.createTxtSendMessage("你领取了" + senderNickname + "的红包", message.getTo());
					sendMessage.setChatType(EMMessage.ChatType.GroupChat);
					sendMessage.setFrom(message.getFrom());
					sendMessage.setTo(message.getTo());
					sendMessage.setMsgId(UUID.randomUUID().toString());
					sendMessage.setMsgTime(cmdMsg.getMsgTime());
					sendMessage.setUnread(false);// 去掉未读的显示
					sendMessage.setDirection(EMMessage.Direct.SEND);
					sendMessage.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
					sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
					sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
					sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
					sendMessage.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
					EMClient.getInstance().chatManager().saveMessage(sendMessage);
					callBack.onSuccess();
				}
			}

			@Override
			public void onError(int i, String s) {

			}

			@Override
			public void onProgress(int i, String s) {

			}
		});
		EMClient.getInstance().chatManager().sendMessage(cmdMsg);
	}

	/**
	 * 使用cmd消息收取领到红包之后的回执消息
	 */
	public static void receiveRedPacketAckMessage(Context context, EMMessage message) {
		String order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");
		String senderNickname = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, "");
		String receiverNickname = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, "");
		String senderId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, "");
		String receiverId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, "");
		String groupId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_GROUP_ID, "");
		String currentUser = EMClient.getInstance().getCurrentUser();
		// 更新UI为 xx领取了你的红包
		if (currentUser.equals(senderId) && !receiverId.equals(senderId)) {// 如果不是自己领取的红包更新此类消息UI
			EMMessage msg = EMMessage.createTxtSendMessage(receiverNickname + "领取了你的红包", groupId);
			msg.setChatType(EMMessage.ChatType.GroupChat);
			msg.setFrom(message.getFrom());
			if (TextUtils.isEmpty(groupId)) {
				msg.setTo(message.getTo());
			} else {
				msg.setTo(groupId);
			}
			msg.setMsgId(UUID.randomUUID().toString());
			msg.setMsgTime(message.getMsgTime());
			msg.setDirection(EMMessage.Direct.RECEIVE);
			msg.setUnread(false);// 去掉未读的显示
			msg.setAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, true);
			msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
			msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, senderNickname);
			msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, receiverNickname);
			msg.setAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, senderId);
			// 保存消息
			EMClient.getInstance().chatManager().saveMessage(msg);
		}
	}

	/**
	 * 判断红包类型是否为小额随机红包
	 * 
	 * @param message
	 *            EMMessage
	 * @return true or false
	 */
	public static boolean isRandomRedPacket(EMMessage message) {
		String redPacketType = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_TYPE, "");
		return !TextUtils.isEmpty(redPacketType) && "2".equals(redPacketType);
	}

}
