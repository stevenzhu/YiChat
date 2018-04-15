package chat.chat_1.code;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.RedPacketConstant;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.yichat.R;

/**
 * 红包弹框
 */

public class RedPacketViewHolder implements OnClickListener {
	private ImageView mIvClose;
	private ImageView mIvAvatar;
	private TextView mTvName;
	private TextView mTvMsg;
	private ImageView mIvOpen;
	private LinearLayout z_select_detail;

	private Context mContext;
	private OnRedPacketDialogClickListener mListener;
	
	private int[] mImgResIds = new int[] { R.drawable.icon_open_red_packet1, R.drawable.icon_open_red_packet2, R.drawable.icon_open_red_packet3, R.drawable.icon_open_red_packet4, R.drawable.icon_open_red_packet5, R.drawable.icon_open_red_packet6, R.drawable.icon_open_red_packet7, R.drawable.icon_open_red_packet7, R.drawable.icon_open_red_packet8, R.drawable.icon_open_red_packet9, R.drawable.icon_open_red_packet4, R.drawable.icon_open_red_packet10,
			R.drawable.icon_open_red_packet11 };
	private FrameAnimation mFrameAnimation;

	public RedPacketViewHolder(Context context, View view) {
		mContext = context;
		mIvClose = (ImageView) view.findViewById(R.id.iv_close);
		mIvClose.setOnClickListener(this);
		mIvAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
		mTvName = (TextView) view.findViewById(R.id.tv_name);
		mTvMsg = (TextView) view.findViewById(R.id.tv_msg);
		mIvOpen = (ImageView) view.findViewById(R.id.iv_open);
		mIvOpen.setOnClickListener(this);
		z_select_detail = (LinearLayout) view.findViewById(R.id.z_select_detail);
		z_select_detail.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			stopAnim();
			if (mListener != null) {
				mListener.onCloseClick();
			}
			break;
		case R.id.iv_open:
			if (mFrameAnimation != null) {
				// 如果正在转动，则直接返回
				stopAnim();
				return;
			}

			startAnim();
			if (mListener != null) {
				mListener.onOpenClick();
			}
			break;
		case R.id.z_select_detail:
			// 查看
			if (mListener != null) {
				mListener.onOpenDetail();
			}
			break;
		}

	}

	public void setData(EMMessage message, String code) {
		String type = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_TYPE, "");
		String order_id = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, "");
		String receiveId = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, "");
		String greeting = message.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_GREETING, "");
		String name = message.getStringAttribute(RedPacketConstant.EXTRA_SPONSOR_NAME, "");
		String from = message.getFrom();
		new AsyncTopImgLoadTask(mContext, from, mTvName, mIvAvatar).execute();
		if ("1000".equals(code)) {
			// 正常
			mTvMsg.setText(greeting);
			mIvOpen.setVisibility(View.VISIBLE);
			z_select_detail.setVisibility(View.GONE);
		} else if ("4004".equals(code)) {
			// 红包已被抢光
			mTvMsg.setText("手慢了，红包派完了");
			mIvOpen.setVisibility(View.GONE);
			z_select_detail.setVisibility(View.VISIBLE);
		} else if ("4005".equals(code)) {
			// 红包已过期
			mTvMsg.setText("该红包已超过24小时。如已领取，可在红包记录中查看");
			mIvOpen.setVisibility(View.GONE);
			z_select_detail.setVisibility(View.VISIBLE);
		}
	}

	public void startAnim() {
		mFrameAnimation = new FrameAnimation(mIvOpen, mImgResIds, 125, true);
		mFrameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
			@Override
			public void onAnimationStart() {
				Log.i("", "start");
			}

			@Override
			public void onAnimationEnd() {
				Log.i("", "end");
			}

			@Override
			public void onAnimationRepeat() {
				Log.i("", "repeat");
			}

			@Override
			public void onAnimationPause() {
				mIvOpen.setBackgroundResource(R.drawable.icon_open_red_packet1);
			}
		});
	}

	public void stopAnim() {
		if (mFrameAnimation != null) {
			mFrameAnimation.release();
			mFrameAnimation = null;
		}
	}

	public void setOnRedPacketDialogClickListener(OnRedPacketDialogClickListener listener) {
		mListener = listener;
	}
}
