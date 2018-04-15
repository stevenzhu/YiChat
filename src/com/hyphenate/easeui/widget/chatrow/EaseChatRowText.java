package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.exceptions.HyphenateException;

public class EaseChatRowText extends EaseChatRow {

	private TextView contentView;

	public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	public void onSetUpView() {
		EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
		Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		contentView.setText(span, BufferType.SPANNABLE);

		handleTextMessage();
	}

	protected void handleTextMessage() {
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
		adapter.refresh();
	}

	@Override
	protected void onBubbleClick() {

	}

}
