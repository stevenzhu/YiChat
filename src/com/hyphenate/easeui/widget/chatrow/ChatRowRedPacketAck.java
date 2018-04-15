package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chat.chat_1.code.RedPickageDetailUI;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.RedPacketConstant;

public class ChatRowRedPacketAck extends EaseChatRow implements OnClickListener {

	private Context context;
	private TextView mTvMessage;
	private TextView mTvMessageRed;
	private String order_id;

	public ChatRowRedPacketAck(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
		this.context = context;
	}

	@Override
	protected void onInflateView() {
		if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
			inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.em_row_red_packet_ack_message : R.layout.em_row_red_packet_ack_message, this);
		}
	}

	@Override
	protected void onFindViewById() {
		mTvMessage = (TextView) findViewById(R.id.ease_tv_money_msg);
		mTvMessageRed = (TextView) findViewById(R.id.ease_tv_money_redpacket);
		mTvMessageRed.setOnClickListener(this);
	}

	@Override
	protected void onSetUpView() {
		String currentUser = EMClient.getInstance().getCurrentUser();
		order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");// 红包ID
		// 红包发送者
		String fromUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME, "");// 红包发送者
		// 红包接收者
		String toUser = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME, "");// 红包接收者
		String senderId;
		if (message.direct() == EMMessage.Direct.SEND) {
			if (message.getChatType().equals(EMMessage.ChatType.GroupChat)) {
				senderId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID, "");
				if (senderId.equals(currentUser)) {
					mTvMessage.setText(R.string.msg_take_red_packet);
				} else {
					mTvMessage.setText(String.format(getResources().getString(R.string.msg_take_someone_red_packet), fromUser));
				}
			} else {
				mTvMessage.setText(String.format(getResources().getString(R.string.msg_take_someone_red_packet), fromUser));
			}
		} else {
			mTvMessage.setText(String.format(getResources().getString(R.string.msg_someone_take_red_packet), toUser));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ease_tv_money_redpacket:
			Intent intent = new Intent(context, RedPickageDetailUI.class);
			intent.putExtra("order_id", order_id);
			context.startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onUpdateView() {

	}

	@Override
	protected void onBubbleClick() {
	}

}
