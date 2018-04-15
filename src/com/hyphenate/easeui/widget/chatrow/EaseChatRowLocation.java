package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.LatLng;

public class EaseChatRowLocation extends EaseChatRow {

	private TextView locationView;
	private EMLocationMessageBody locBody;

	public EaseChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_location : R.layout.ease_row_sent_location, this);
	}

	@Override
	protected void onFindViewById() {
		locationView = (TextView) findViewById(R.id.tv_location);
		locationView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBubbleClick();
			}
		});
	}

	@Override
	protected void onSetUpView() {
		locBody = (EMLocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());

		// handle sending message
		if (message.direct() == EMMessage.Direct.SEND) {
			setMessageSendCallback();
			switch (message.status()) {
			case CREATE:
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				break;
			case SUCCESS:
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.GONE);
				break;
			case FAIL:
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS:
				progressBar.setVisibility(View.VISIBLE);
				statusView.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} else {
			if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
				try {
					EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
					if (message.getBooleanAttribute("fire", false)) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// 消息所属会话
								EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), EMConversation.EMConversationType.Chat, true);
								conversation.removeMessage(message.getMsgId());
								adapter.refresh();
							}
						}, 10000);
					}
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onUpdateView() {
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onBubbleClick() {
		Intent intent = new Intent(context, EaseBaiduMapActivity.class);
		intent.putExtra("latitude", locBody.getLatitude());
		intent.putExtra("longitude", locBody.getLongitude());
		intent.putExtra("address", locBody.getAddress());
		activity.startActivity(intent);
	}

	/*
	 * listener for map clicked
	 */
	protected class MapClickListener implements OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;
		}

		@Override
		public void onClick(View v) {
		}
	}

}
