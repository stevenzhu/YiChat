package cms_detail.cms_detail_1.code;

import java.util.List;

import user_card.user_card_1.code.UserCardUI;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bean.CmsCommentBean;
import bean.CmsReplyBean;
import cms_comment_detail.cms_comment_detail_1.code.CmsCommentDetailUI;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

public class CmsDetailCommentAdapter extends BaseAdapter {
	private Context context;
	private List<CmsCommentBean> listCommentsBean;
	private Handler mHandler;

	public CmsDetailCommentAdapter(Context context, List<CmsCommentBean> listCommentsBean, Handler mHandler) {
		this.context = context;
		this.listCommentsBean = listCommentsBean;
		this.mHandler = mHandler;
	}

	public void setData(List<CmsCommentBean> listCommentsBean) {
		this.listCommentsBean = listCommentsBean;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listCommentsBean == null) {
			return 0;
		}
		return listCommentsBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listCommentsBean.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.cms_detail_1_item_comment, null);
			holder.z_item_all = (RelativeLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_nick = (TextView) convertView.findViewById(R.id.tv_item_nick);
			holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
			holder.tv_item_content = (TextView) convertView.findViewById(R.id.tv_item_content);
			holder.z_item_reply_num = (LinearLayout) convertView.findViewById(R.id.z_item_reply_num);
			holder.tv_item_reply_num = (TextView) convertView.findViewById(R.id.tv_item_reply_num);
			holder.tv_item_del = (TextView) convertView.findViewById(R.id.tv_item_del);
			holder.z_item_action = (LinearLayout) convertView.findViewById(R.id.z_item_action);
			holder.z_item_laud = (LinearLayout) convertView.findViewById(R.id.z_item_laud);
			holder.tv_item_laud = (TextView) convertView.findViewById(R.id.tv_item_laud);
			holder.iv_item_laud = (ImageView) convertView.findViewById(R.id.iv_item_laud);
			holder.z_item_reply = (LinearLayout) convertView.findViewById(R.id.z_item_reply);
			convertView.setTag(holder);
			StyleUtils.setTabBg(context, holder.iv_item_laud, new int[] { R.drawable.cms_detail_1_icon_laud_down, R.drawable.cms_detail_1_icon_laud_up });
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CmsCommentBean commentBean = listCommentsBean.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_avatar, commentBean.getAvatar(), R.drawable.default_avatar);
		holder.iv_item_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!commentBean.getUser_id().equals(MyConfig.getUserInfo(context).get("user_id"))) {
					Intent intent = new Intent(context, UserCardUI.class);
					intent.putExtra("user_id", commentBean.getUser_id());
					context.startActivity(intent);
				}
			}
		});
		String time = TimeUtil.getStandardDate(commentBean.getAdd_time());
		holder.tv_item_nick.setText(commentBean.getNick());
		holder.tv_item_time.setText(time);
		if (!Utils.isEmity(commentBean.getContent())) {
			Spannable span = SmileUtils.getSmiledText(context, commentBean.getContent());
			SpannableString spannableString = new SpannableString(span);
			holder.tv_item_content.setText(spannableString);
			holder.tv_item_content.setVisibility(View.VISIBLE);
		} else {
			holder.tv_item_content.setVisibility(View.GONE);
		}

		List<CmsReplyBean> listReply = commentBean.getListReply();
		if (listReply != null && listReply.size() > 0) {
			holder.tv_item_reply_num.setText(String.valueOf(listReply.size()));
			holder.z_item_reply_num.setVisibility(View.VISIBLE);
			holder.z_item_reply_num.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, CmsCommentDetailUI.class);
					intent.putExtra("id", commentBean.getReview_id());
					context.startActivity(intent);
				}
			});
		} else {
			holder.z_item_reply_num.setVisibility(View.GONE);
		}

		// 评论是否点赞
		final String is_like = commentBean.getIs_like();
		if ("1".equals(is_like)) {
			holder.z_item_laud.setSelected(true);
		} else {
			holder.z_item_laud.setSelected(false);
		}
		holder.tv_item_laud.setText(commentBean.getLike_num());

		if (commentBean.getUser_id().equals(MyConfig.getUserInfo(context).get("user_id"))) {
			// 自己的评论
			holder.tv_item_del.setVisibility(View.VISIBLE);
			holder.z_item_action.setVisibility(View.GONE);
			holder.tv_item_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 删除评论
					Message msg = new Message();
					msg.what = CmsDetailUI.COMMENT_DEL;
					msg.arg1 = position;
					mHandler.sendMessage(msg);
				}
			});
		} else {
			// 其他人的评论
			holder.tv_item_del.setVisibility(View.GONE);
			holder.z_item_action.setVisibility(View.VISIBLE);
			holder.z_item_laud.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 点赞评论
					Message msg = new Message();
					if ("1".equals(is_like)) {
						msg.what = CmsDetailUI.COMMENT_LAUD_CANCLE;
					} else {
						msg.what = CmsDetailUI.COMMENT_LAUD;
					}
					msg.arg1 = position;
					mHandler.sendMessage(msg);
				}
			});
			holder.z_item_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 回复评论
					Message msg = new Message();
					msg.what = CmsDetailUI.REPLY_COMMENT;
					msg.arg1 = position;
					mHandler.sendMessage(msg);
				}
			});
		}

		return convertView;
	}

	public class ViewHolder {
		RelativeLayout z_item_all;
		ImageView iv_item_avatar;
		TextView tv_item_nick;
		TextView tv_item_time;
		TextView tv_item_content;
		LinearLayout z_item_reply_num;
		TextView tv_item_reply_num;
		TextView tv_item_del;
		LinearLayout z_item_action;
		LinearLayout z_item_laud;
		TextView tv_item_laud;
		ImageView iv_item_laud;
		LinearLayout z_item_reply;
	}

}
