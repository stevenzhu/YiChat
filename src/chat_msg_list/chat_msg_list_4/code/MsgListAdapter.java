package chat_msg_list.chat_msg_list_4.code;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.EMLog;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.yichat.R;

public class MsgListAdapter extends ArrayAdapter<EMConversation> {

	private static final String TAG = "MsgListAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private ConversationFilter conversationFilter;
	private boolean notiyfyByFilter;

	public MsgListAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.conversationList = objects;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.chat_msg_list_4_item, parent, false);
			holder.z_item_all = (RelativeLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.iv_item_tag = (ImageView) convertView.findViewById(R.id.iv_item_tag);
			holder.tv_item_unread_msg = (TextView) convertView.findViewById(R.id.tv_item_unread_msg);
			holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.iv_item_state = (ImageView) convertView.findViewById(R.id.iv_item_state);
			holder.tv_item_message = (TextView) convertView.findViewById(R.id.tv_item_message);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		EMConversation conversation = getItem(position);
		// 获取用户ID或群组ID
		String username = conversation.conversationId();

		if (conversation.getType() == EMConversationType.GroupChat) {
			// 群聊消息，显示群聊头像
			holder.iv_item_avatar.setImageResource(R.drawable.contacts_list_1_icon_group);
			EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
			holder.tv_item_name.setText(group != null ? group.getGroupName() : username);
		} else {
			// 设置用户昵称、头像
			new AsyncTopImgLoadTask(context, username, holder.tv_item_name, holder.iv_item_avatar).execute();
		}

		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.tv_item_unread_msg.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.tv_item_unread_msg.setVisibility(View.VISIBLE);
		} else {
			holder.tv_item_unread_msg.setVisibility(View.INVISIBLE);
		}

		if (conversation.getAllMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			holder.tv_item_message.setText(EaseSmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))), BufferType.SPANNABLE);

			holder.tv_item_time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
				holder.iv_item_state.setVisibility(View.VISIBLE);
			} else {
				holder.iv_item_state.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	/**
	 * 获取提示消息
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	public static String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION:
			if (message.direct() == EMMessage.Direct.RECEIVE) {
				digest = context.getResources().getString(R.string.location_recv);
				return digest;
			} else {
				digest = context.getResources().getString(R.string.location_prefix);
			}
			break;
		case IMAGE:
			digest = context.getResources().getString(R.string.picture);
			break;
		case VOICE:
			digest = context.getResources().getString(R.string.voice_prefix);
			break;
		case VIDEO:
			digest = context.getResources().getString(R.string.video);
			break;
		case TXT:
			EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
			if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				digest = context.getResources().getString(R.string.voice_call) + txtBody.getMessage();
			} else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
				digest = context.getResources().getString(R.string.video_call) + txtBody.getMessage();
			} else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
				if (!TextUtils.isEmpty(txtBody.getMessage())) {
					digest = txtBody.getMessage();
				} else {
					digest = context.getResources().getString(R.string.dynamic_expression);
				}
			} else {
				digest = txtBody.getMessage();
			}
			break;
		case FILE:
			digest = context.getResources().getString(R.string.file);
			break;
		default:
			EMLog.e(TAG, "error, unknow type");
			return "";
		}

		return digest;
	}

	class ViewHolder {
		RelativeLayout z_item_all;
		ImageView iv_item_avatar;
		ImageView iv_item_tag;
		TextView tv_item_unread_msg;
		TextView tv_item_time;
		TextView tv_item_name;
		ImageView iv_item_state;
		TextView tv_item_message;
	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	@Override
	public Filter getFilter() {
		if (conversationFilter == null) {
			conversationFilter = new ConversationFilter(conversationList);
		}
		return conversationFilter;
	}

	private class ConversationFilter extends Filter {
		List<EMConversation> mOriginalValues = null;

		public ConversationFilter(List<EMConversation> mList) {
			mOriginalValues = mList;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<EMConversation>();
			}
			if (prefix == null || prefix.length() == 0) {
				results.values = copyConversationList;
				results.count = copyConversationList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalValues.size();
				final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

				for (int i = 0; i < count; i++) {
					final EMConversation value = mOriginalValues.get(i);
					String username = value.conversationId();

					EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
					if (group != null) {
						username = group.getGroupName();
					}

					if (username.startsWith(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = username.split(" ");
						final int wordCount = words.length;

						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			conversationList.clear();
			conversationList.addAll((List<EMConversation>) results.values);
			if (results.count > 0) {
				notiyfyByFilter = true;
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (!notiyfyByFilter) {
			copyConversationList.clear();
			copyConversationList.addAll(conversationList);
			notiyfyByFilter = false;
		}
	}

}
