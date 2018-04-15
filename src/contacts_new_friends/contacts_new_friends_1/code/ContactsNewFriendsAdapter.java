package contacts_new_friends.contacts_new_friends_1.code;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.db.InviteMessgeDao;
import com.hyphenate.easeui.domain.InviteMessage;
import com.hyphenate.easeui.domain.InviteMessage.InviteMesageStatus;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.yichat.R;

public class ContactsNewFriendsAdapter extends ArrayAdapter<InviteMessage> {
	private Context context;
	private InviteMessgeDao messgeDao;
	private Handler mHandler;

	public ContactsNewFriendsAdapter(Context context, int textViewResourceId, List<InviteMessage> objects, Handler mHandler) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.mHandler = mHandler;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.contacts_new_friends_1_item, null);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_reason = (TextView) convertView.findViewById(R.id.tv_item_reason);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.tv_item_user_state = (TextView) convertView.findViewById(R.id.tv_item_user_state);
			holder.tv_item_agree = (TextView) convertView.findViewById(R.id.tv_item_agree);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);

		String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(R.string.invite_join_group);
		String str5 = context.getResources().getString(R.string.Has_agreed_to);
		String str6 = context.getResources().getString(R.string.Has_refused_to);
		final InviteMessage msg = getItem(position);
		if (msg != null) {
			if (msg.getGroupId() != null) {
				// 显示群聊提示
				holder.iv_item_avatar.setImageResource(R.drawable.contacts_list_1_icon_group);
				holder.tv_item_name.setText(msg.getGroupName());
				holder.tv_item_user_state.setVisibility(View.INVISIBLE);
			} else {
				new AsyncTopImgLoadTask(context, msg.getFrom(), holder.tv_item_name, holder.iv_item_avatar).execute();
			}

			holder.tv_item_reason.setText(msg.getReason());
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.tv_item_user_state.setVisibility(View.GONE);
				holder.tv_item_agree.setVisibility(View.GONE);
				holder.tv_item_reason.setText(str1);
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMesageStatus.BEAPPLYED || msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
				holder.tv_item_user_state.setVisibility(View.GONE);
				holder.tv_item_agree.setVisibility(View.VISIBLE);
				holder.tv_item_agree.setText(str2);
				holder.tv_item_agree.setEnabled(true);
				if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.tv_item_reason.setText(str3);
					}
				} else {
					// 入群申请
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.tv_item_reason.setText(str4 + msg.getGroupName());
					}
				}
				// 设置点击事件
				holder.tv_item_agree.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						if (msg.getGroupId() == null) {
							Message message = new Message();
							message.what = 1;
							message.obj = msg.getFrom();
							mHandler.sendMessage(message);
						}
						acceptInvitation(holder.tv_item_agree, holder.tv_item_user_state,msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.tv_item_user_state.setVisibility(View.VISIBLE);
				holder.tv_item_agree.setVisibility(View.GONE);
				holder.tv_item_user_state.setText(str5);
				holder.tv_item_user_state.setEnabled(false);
			} else if (msg.getStatus() == InviteMesageStatus.REFUSED) {
				holder.tv_item_user_state.setVisibility(View.VISIBLE);
				holder.tv_item_agree.setVisibility(View.GONE);
				holder.tv_item_user_state.setText(str6);
				holder.tv_item_user_state.setEnabled(false);
			}
		}

		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final TextView tv_item_agree, final TextView tv_item_user_state, final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		final String str2 = context.getResources().getString(R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {
						EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
						EMMessage message = EMMessage.createTxtSendMessage("我们已经是好友了，开始聊天了", msg.getFrom());
						EMClient.getInstance().chatManager().sendMessage(message);
					} else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) {
						EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
					} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
						EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
					}
					msg.setStatus(InviteMesageStatus.AGREED);
					// 更新db
					ContentValues values = new ContentValues();
					values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
					messgeDao.updateMessage(msg.getId(), values);
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							tv_item_agree.setVisibility(View.GONE);
							tv_item_user_state.setVisibility(View.VISIBLE);
							tv_item_user_state.setText(str2);
							tv_item_user_state.setEnabled(false);
						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}
			}
		}).start();
	}

	private static class ViewHolder {
		ImageView iv_item_avatar;
		TextView tv_item_name;
		TextView tv_item_reason;
		TextView tv_item_agree;
		TextView tv_item_user_state;
	}

}
