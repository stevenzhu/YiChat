package chat.chat_1.code;

import group_detail.group_detail_1.code.GroupDetailUI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import user_card.user_card_1.code.UserCardJson;
import user_card.user_card_1.code.UserCardUI;
import user_info.user_info_1.code.UserInfoUI;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.utils.Constant;
import com.hyphenate.easeui.utils.RedPacketConstant;
import com.hyphenate.easeui.utils.RedPacketUtil;
import com.hyphenate.easeui.widget.chatrow.ChatRowRedPacket;
import com.hyphenate.easeui.widget.chatrow.ChatRowRedPacketAck;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoiceCall;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.PathUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 聊天页面
 * 
 * @author peidongxu
 * 
 */
public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper {

	// constant start from 11 to avoid conflict with constant in base class
	private static final int ITEM_VIDEO = 11;
	private static final int ITEM_FILE = 12;
	private static final int ITEM_VOICE_CALL = 13;
	private static final int ITEM_VIDEO_CALL = 14;

	private static final int REQUEST_CODE_SELECT_VIDEO = 11;
	private static final int REQUEST_CODE_SELECT_FILE = 12;
	private static final int REQUEST_CODE_GROUP_DETAIL = 13;
	private static final int REQUEST_CODE_CONTEXT_MENU = 14;
	private static final int REQUEST_CODE_SELECT_AT_USER = 15;

	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

	// red packet code : 红包功能使用的常量
	private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
	private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
	private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
	private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
	private static final int REQUEST_CODE_SEND_RED_PACKET = 16;
	private static final int ITEM_RED_PACKET = 16;

	// end of red packet code

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void setUpView() {
		setChatFragmentHelper(this);
		super.setUpView();
		titleBar.setBackgroundResource(R.drawable.bg_title);
		// set click listener
		titleBar.setLeftLayoutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// ((EaseEmojiconMenu)
		// inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
		// if (chatType == EaseConstant.CHATTYPE_GROUP) {
		// inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new
		// TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
		// startActivityForResult(new Intent(getActivity(),
		// PickAtUserActivity.class).putExtra("groupId", toChatUsername),
		// REQUEST_CODE_SELECT_AT_USER);
		// }
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		//
		// }
		// });
		// }
		if (chatType == EaseConstant.CHATTYPE_SINGLE) {
			getUserInfo();
		} else {
			isFire = false;
		}
	}

	@Override
	protected void registerExtendMenuItem() {
		// use the menu in base class
		super.registerExtendMenuItem();
		// extend menu items
		// inputMenu.registerExtendMenuItem(R.string.attach_video,
		// R.drawable.em_chat_video_selector, ITEM_VIDEO,
		// extendMenuItemClickListener);
		// inputMenu.registerExtendMenuItem(R.string.attach_file,
		// R.drawable.em_chat_file_selector, ITEM_FILE,
		// extendMenuItemClickListener);
		if (chatType == Constant.CHATTYPE_SINGLE) {
			inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
			inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
		} else {
			titleBar.setRightLayoutVisibility(View.VISIBLE);
		}

		// 聊天室暂时不支持红包功能
		// red packet code : 注册红包菜单选项
		// if (chatType != Constant.CHATTYPE_CHATROOM) {
		// inputMenu.registerExtendMenuItem(R.string.attach_hongbao,
		// R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET,
		// extendMenuItemClickListener);
		// }
		// end of red packet code
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SELECT_VIDEO:
				// send the video
				if (data != null) {
					int duration = data.getIntExtra("dur", 0);
					String videoPath = data.getStringExtra("path");
					File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
					try {
						FileOutputStream fos = new FileOutputStream(file);
						Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
						ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
						fos.close();
						sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case REQUEST_CODE_SELECT_FILE:
				// send the file
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFileByUri(uri);
					}
				}
				break;
			case REQUEST_CODE_SELECT_AT_USER:
				if (data != null) {
					String username = data.getStringExtra("id");
					inputAtUsername(username, false);
				}
				break;
			case REQUEST_CODE_SEND_RED_PACKET:
				if (data != null) {
					sendMessage(RedPacketUtil.createRPMessage(getActivity(), data, toChatUsername));
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onSetMessageAttributes(EMMessage message) {
	}

	@Override
	public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
		return new CustomChatRowProvider();
	}

	@Override
	public void onEnterToChatDetails() {
		if (chatType == Constant.CHATTYPE_GROUP) {
			EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
			if (group == null) {
				Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(getActivity(), GroupDetailUI.class);
			intent.putExtra("id", toChatUsername);
			startActivity(intent);
		} else if (chatType == Constant.CHATTYPE_CHATROOM) {
			Intent intent = new Intent(getActivity(), GroupDetailUI.class);
			intent.putExtra("id", toChatUsername);
			startActivity(intent);
		}
	}

	@Override
	public void onAvatarClick(String username) {
		// 头像点击事件
		if (Utils.isEmity(username)) {
			return;
		}
		if (username.contains(Constants.PREFIX)) {
			username = username.replace(Constants.PREFIX, "");
		}
		if (username.equals(MyConfig.getUserInfo(getActivity()).get("user_id"))) {
			// 自己
			Intent intent = new Intent(getActivity(), UserInfoUI.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), UserCardUI.class);
			intent.putExtra("user_id", username);
			startActivity(intent);
		}
	}

	@Override
	public void onAvatarLongClick(String username) {
		// 头像长按事件
		// inputAtUsername(username);
	}

	@Override
	public boolean onMessageBubbleClick(EMMessage message) {
		// 消息框点击事件
		// red packet code : 拆红包页面
		// TODO red packet code : 拆红包页面 消息框点击事件，demo这里不做覆盖，如需覆盖，return true
		if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
			String order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");
			String currentUser = EMClient.getInstance().getCurrentUser();
			if (chatType == EaseConstant.CHATTYPE_SINGLE && currentUser.equals(message.getFrom())) {
				// 单聊、自己发的红包
				Intent intent = new Intent(getActivity(), RedPickageDetailUI.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			} else {
				receiveCheck(message, order_id);
			}
			return true;
		}
		// end of red packet code
		return false;
	}

	@Override
	public void onCmdMessageReceived(List<EMMessage> messages) {
		// red packet code : 处理红包回执透传消息
		// TODO 处理红包回执透传消息
		for (EMMessage message : messages) {
			EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
			String action = cmdMsgBody.action();// 获取自定义action
			if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
				RedPacketUtil.receiveRedPacketAckMessage(getActivity(), message);
				messageList.refresh();
			}
		}
		// end of red packet code
		super.onCmdMessageReceived(messages);
	}

	@Override
	public void onMessageBubbleLongClick(View view, EMMessage message) {
		// 消息长按事件
		new MenuPop(view, getActivity(), actionHandler, message.getType().ordinal());
	}

	/**
	 * 消息长按操作
	 */
	private Handler actionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MenuPop.RESULT_CODE_FORWARD:
				// 转发消息
				Intent intent = new Intent(getActivity(), ForwardMessageUI.class);
				intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
				startActivity(intent);
				break;
			case MenuPop.RESULT_CODE_COPY:
				// 复制消息
				clipboard.setPrimaryClip(ClipData.newPlainText(null, ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
				MyApplication.getInstance().showToast("已复制到粘贴板");
				break;
			case MenuPop.RESULT_CODE_DELETE:
				// 删除消息
				conversation.removeMessage(contextMenuMessage.getMsgId());
				messageList.refresh();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onExtendMenuItemClick(int itemId, View view) {
		switch (itemId) {
		case ITEM_VIDEO:
			// 选择视频文件
			// Intent intent = new Intent(getActivity(),
			// ImageGridActivity.class);
			// startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
			break;
		case ITEM_FILE:
			// 选择图片文件
			selectFileFromLocal();
			break;
		case ITEM_VOICE_CALL:
			// 语音聊天
			startVoiceCall();
			break;
		case ITEM_VIDEO_CALL:
			// 视频聊天
			startVideoCall();
			break;
		case ITEM_RED_PACKET:
			// 进入发红包页面
			Intent intentRed = new Intent(getActivity(), RedPickageUI.class);
			intentRed.putExtra("id", toChatUsername);
			intentRed.putExtra("chatType", chatType);
			startActivityForResult(intentRed, REQUEST_CODE_SEND_RED_PACKET);
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * 选择 文件
	 */
	protected void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 发起 音频通话
	 */
	protected void startVoiceCall() {
		if (!EMClient.getInstance().isConnected()) {
			Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
		} else {
			startActivity(new Intent(getActivity(), VoiceCallUI.class).putExtra("id", toChatUsername).putExtra("isComingCall", false));
			inputMenu.hideExtendMenuContainer();
		}
	}

	/**
	 * 发起 视频通话
	 */
	protected void startVideoCall() {
		if (!EMClient.getInstance().isConnected())
			Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
		else {
			startActivity(new Intent(getActivity(), VideoCallUI.class).putExtra("id", toChatUsername).putExtra("isComingCall", false));
			inputMenu.hideExtendMenuContainer();
		}
	}

	/**
	 * chat row provider
	 * 
	 */
	private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
		@Override
		public int getCustomChatRowTypeCount() {
			// here the number is the message type in EMMessage::Type
			// which is used to count the number of different chat row
			return 10;
		}

		@Override
		public int getCustomChatRowType(EMMessage message) {
			if (message.getType() == EMMessage.Type.TXT) {
				// voice call
				if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
				} else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
					// video call
					return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
				} else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
					// 发送红包消息
					return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET : MESSAGE_TYPE_SEND_RED_PACKET;
				} else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
					// 领取红包消息
					return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ACK : MESSAGE_TYPE_SEND_RED_PACKET_ACK;
				}
			}
			return 0;
		}

		@Override
		public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
			if (message.getType() == EMMessage.Type.TXT) {
				// voice call or video call
				if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
					return new EaseChatRowVoiceCall(getActivity(), message, position, adapter);
				} else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {// 发送红包消息
					return new ChatRowRedPacket(getActivity(), message, position, adapter);
				} else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {// open
					return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
				}
			}
			return null;
		}
	}

	/**
	 * 领红包检测
	 */
	private void receiveCheck(final EMMessage message, final String order_id) {
		String url = HttpUtil.getUrl("/redpacket/receiveCheck");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(getActivity()));
		map.put("id", order_id);
		HttpUtil.post(getActivity(), url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = RedPickageJson.receiveCheck(response.toString());
				if ("1000".equals(returnBean.getCode())) {
					showRedPacketDialog(message, returnBean.getCode(), order_id);
				} else if ("4004".equals(returnBean.getCode())) {
					// 红包已被抢光
					showRedPacketDialog(message, returnBean.getCode(), order_id);
				} else if ("4005".equals(returnBean.getCode())) {
					// 红包已过期
					showRedPacketDialog(message, returnBean.getCode(), order_id);
				} else if ("4006".equals(returnBean.getCode())) {
					// 红包已领取
					Intent intent = new Intent(getActivity(), RedPickageDetailUI.class);
					intent.putExtra("order_id", order_id);
					startActivity(intent);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 领红包
	 */
	private void receive(final EMMessage message, final String order_id) {
		String url = HttpUtil.getUrl("/redpacket/receive");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(getActivity()));
		map.put("id", order_id);
		HttpUtil.post(getActivity(), url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = RedPickageJson.receive(response.toString());
				if ("1000".equals(returnBean.getCode())) {
					mRedPacketDialog.dismiss();
					RedPacketUtil.openRedPacket(getActivity(), chatType, message, message.getFrom(), messageList);
					Intent intent = new Intent(getActivity(), RedPickageDetailUI.class);
					intent.putExtra("order_id", order_id);
					startActivity(intent);
				} else if ("4004".equals(returnBean.getCode())) {
					// 红包已被抢光
					mRedPacketDialog.dismiss();
					showRedPacketDialog(message, returnBean.getCode(), order_id);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	private View mRedPacketDialogView;
	private RedPacketViewHolder mRedPacketViewHolder;
	private CustomDialog mRedPacketDialog;

	/**
	 * 显示红包弹框
	 * 
	 * @param message
	 */
	public void showRedPacketDialog(final EMMessage message, String code, final String order_id) {
		if (mRedPacketDialogView == null) {
			mRedPacketDialogView = View.inflate(getActivity(), R.layout.ease_activity_redpickage_dialog, null);
			mRedPacketViewHolder = new RedPacketViewHolder(getActivity(), mRedPacketDialogView);
			mRedPacketDialog = new CustomDialog(getActivity(), mRedPacketDialogView, R.style.custom_dialog_style);
			mRedPacketDialog.setCancelable(false);
		}

		mRedPacketViewHolder.setData(message, code);
		mRedPacketViewHolder.setOnRedPacketDialogClickListener(new OnRedPacketDialogClickListener() {
			@Override
			public void onCloseClick() {
				mRedPacketDialog.dismiss();
			}

			@Override
			public void onOpenClick() {
				// 领取红包,调用接口
				receive(message, order_id);
			}

			@Override
			public void onOpenDetail() {
				// 查看红包详情
				mRedPacketDialog.dismiss();
				Intent intent = new Intent(getActivity(), RedPickageDetailUI.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			}
		});

		mRedPacketDialog.show();
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		String user_id = "";
		if (toChatUsername.contains(Constants.PREFIX)) {
			user_id = toChatUsername.replace(Constants.PREFIX, "");
		}
		String url = HttpUtil.getUrl("/user/userInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(getActivity()));
		map.put("user_id", user_id);
		HttpUtil.post(getActivity(), url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserCardJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(getActivity(), returnBean.getCode())) {
					// 成功
					Map<String, String> userMap = (Map<String, String>) returnBean.getObject();
					if (userMap != null && !Utils.isEmity(userMap.get("friend"))) {
						// 是否好友 0：不是好友 1：是好友
						String friend = userMap.get("friend");
						if ("1".equals(friend)) {
							isFire = false;
						} else {
							isFire = true;
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

}
