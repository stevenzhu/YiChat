package cms_comment_detail.cms_comment_detail_1.code;

import java.util.List;

import user_card.user_card_1.code.UserCardUI;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
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

import cms_detail.cms_detail_1.code.SmileUtils;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.yichat.R;

public class CmsCommentDetailAdapter extends BaseAdapter {
	private Context context;
	private List<CmsReplyBean> listReplyBean;
	private CmsCommentBean commentBean;
	private Handler mHandler;

	public CmsCommentDetailAdapter(Context context, List<CmsReplyBean> listReplyBean, CmsCommentBean commentBean, Handler mHandler) {
		this.context = context;
		this.listReplyBean = listReplyBean;
		this.commentBean = commentBean;
		this.mHandler = mHandler;
	}

	public void setData(List<CmsReplyBean> listReplyBean, CmsCommentBean commentBean) {
		this.listReplyBean = listReplyBean;
		this.commentBean = commentBean;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listReplyBean == null) {
			return 0;
		}
		return listReplyBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listReplyBean.get(position);
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
			convertView = View.inflate(context, R.layout.cms_comment_detail_1_item_reply, null);
			holder.z_item_all = (RelativeLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_nick = (TextView) convertView.findViewById(R.id.tv_item_nick);
			holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
			holder.tv_item_content = (TextView) convertView.findViewById(R.id.tv_item_content);
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

		final CmsReplyBean replyBean = listReplyBean.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_avatar, replyBean.getSend_avatar(), R.drawable.default_avatar);
		holder.iv_item_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!replyBean.getSend_user_id().equals(MyConfig.getUserInfo(context).get("user_id"))) {
					Intent intent = new Intent(context, UserCardUI.class);
					intent.putExtra("user_id", replyBean.getSend_user_id());
					context.startActivity(intent);
				}
			}
		});
		String time = TimeUtil.getStandardDate(replyBean.getAdd_time());
		holder.tv_item_nick.setText(replyBean.getSend_nick());
		holder.tv_item_time.setText(time);

		String str = "";
		if (commentBean.getUser_id().equals(replyBean.getReply_user_id())) {
			Spannable span = SmileUtils.getSmiledText(context, replyBean.getContent());
			SpannableString spannableString = new SpannableString(span);
			holder.tv_item_content.setText(spannableString);
		} else {
			str = "回复" + replyBean.getReply_nick() + ": " + replyBean.getContent();
			Spannable span = SmileUtils.getSmiledText(context, str);
			SpannableString spannableString = new SpannableString(span);
			spannableString.setSpan(new ClickableSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setUnderlineText(false); // 设置下划线
					ds.setColor(context.getResources().getColor(R.color.text_blue));
				}

				@Override
				public void onClick(View arg0) {
				}
			}, 2, replyBean.getReply_nick().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tv_item_content.setText(spannableString);
		}

		// 回复是否点赞
		final String is_like = replyBean.getIs_like();
		if ("1".equals(is_like)) {
			holder.z_item_laud.setSelected(true);
		} else {
			holder.z_item_laud.setSelected(false);
		}
		holder.tv_item_laud.setText(replyBean.getLike_num());

		if (replyBean.getSend_user_id().equals(MyConfig.getUserInfo(context).get("user_id"))) {
			// 自己的回复
			holder.tv_item_del.setVisibility(View.VISIBLE);
			holder.z_item_action.setVisibility(View.GONE);
			holder.tv_item_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 删除回复
					Message msg = new Message();
					msg.what = CmsCommentDetailUI.REPLY_DEL;
					msg.arg1 = position;
					mHandler.sendMessage(msg);
				}
			});
		} else {
			// 其他人的回复
			holder.tv_item_del.setVisibility(View.GONE);
			holder.z_item_action.setVisibility(View.VISIBLE);
			holder.z_item_laud.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 点赞--回复
					Message msg = new Message();
					if ("1".equals(is_like)) {
						msg.what = CmsCommentDetailUI.REPLY_LAUD_CANCLE;
					} else {
						msg.what = CmsCommentDetailUI.REPLY_LAUD;
					}
					msg.arg1 = position;
					mHandler.sendMessage(msg);
				}
			});
			holder.z_item_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 回复--回复
					Message msg = new Message();
					msg.what = CmsCommentDetailUI.REPLY_REPLY;
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
		TextView tv_item_del;
		LinearLayout z_item_action;
		LinearLayout z_item_laud;
		TextView tv_item_laud;
		ImageView iv_item_laud;
		LinearLayout z_item_reply;
	}

}
