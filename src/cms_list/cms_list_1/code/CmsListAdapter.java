package cms_list.cms_list_1.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.CmsBean;
import cms_detail.cms_detail_1.code.CmsDetailUI;
import cn.jzvd.JZVideoPlayerStandard;

import com.bumptech.glide.Glide;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.Utils;
import com.shorigo.view.jcvideoplayer.JCVideoPlayer;
import com.shorigo.yichat.R;

public class CmsListAdapter extends BaseAdapter {
	private Context context;
	private List<CmsBean> listCmsBean;
	private static final int TYPE_COUNT = 4;// item类型的总数
	private static final int TYPE_1 = 0;// 单图
	private static final int TYPE_2 = 1;// 多图
	private static final int TYPE_3 = 2;// 组图
	private static final int TYPE_4 = 3;// 视频
	private int currentType;// 当前item类型
	private String user_id;
	private Handler mHandler;

	public CmsListAdapter(Context context, List<CmsBean> listCmsBean, Handler mHandler) {
		this.context = context;
		this.listCmsBean = listCmsBean;
		this.mHandler = mHandler;
		user_id = MyConfig.getUserInfo(context).get("user_id");
	}

	public void setData(List<CmsBean> listCmsBean) {
		this.listCmsBean = listCmsBean;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listCmsBean == null)
			return 0;
		return listCmsBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listCmsBean.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		CmsBean cmsBean = listCmsBean.get(position);
		if ("1".equals(cmsBean.getType())) {
			return TYPE_1;
		} else if ("2".equals(cmsBean.getType())) {
			return TYPE_2;
		} else if ("3".equals(cmsBean.getType())) {
			return TYPE_3;
		} else if ("4".equals(cmsBean.getType())) {
			return TYPE_4;
		} else {
			return TYPE_1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view_1 = null;
		View view_2 = null;
		View view_3 = null;
		View view_4 = null;
		currentType = getItemViewType(position);
		if (currentType == TYPE_1) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				view_1 = LayoutInflater.from(context).inflate(R.layout.cms_list_1_frament_item_1, null);
				holder.z_item_all = (LinearLayout) view_1.findViewById(R.id.z_item_all);
				holder.iv_item_img = (ImageView) view_1.findViewById(R.id.iv_item_img);
				holder.tv_item_title = (TextView) view_1.findViewById(R.id.tv_item_title);
				holder.tv_item_source = (TextView) view_1.findViewById(R.id.tv_item_source);
				holder.tv_item_view_num = (TextView) view_1.findViewById(R.id.tv_item_view_num);
				holder.tv_item_comment_num = (TextView) view_1.findViewById(R.id.tv_item_comment_num);
				holder.tv_item_time = (TextView) view_1.findViewById(R.id.tv_item_time);
				holder.tv_item_del = (TextView) view_1.findViewById(R.id.tv_item_del);
				view_1.setTag(holder);
				convertView = view_1;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CmsBean cmsBean = listCmsBean.get(position);

			List<String> listImg = cmsBean.getListImg();
			if (listImg != null && listImg.size() > 0) {
				BitmapHelp.loadImg(context, holder.iv_item_img, listImg.get(0), R.drawable.default_img);
				holder.iv_item_img.setVisibility(View.VISIBLE);
			} else {
				holder.iv_item_img.setVisibility(View.GONE);
			}
			holder.tv_item_title.setText(cmsBean.getTitle());
			if (Utils.isEmity(cmsBean.getView_num())) {
				holder.tv_item_view_num.setText("0浏览");
			} else {
				holder.tv_item_view_num.setText(cmsBean.getView_num() + "浏览");
			}
			if (Utils.isEmity(cmsBean.getComment_num())) {
				holder.tv_item_comment_num.setText("0评论");
			} else {
				holder.tv_item_comment_num.setText(cmsBean.getComment_num() + "评论");
			}
			holder.tv_item_source.setText(cmsBean.getSource());
			if (!Utils.isEmity(cmsBean.getAdd_time())) {
				String time = TimeUtil.getStandardDate(cmsBean.getAdd_time());
				holder.tv_item_time.setText(time);
			}
			Map<String, String> userMap = cmsBean.getUserMap();
			if (userMap != null && user_id.equals(userMap.get("user_id"))) {
				holder.tv_item_del.setVisibility(View.VISIBLE);
				holder.tv_item_del.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
					}
				});
			} else {
				holder.tv_item_del.setVisibility(View.GONE);
			}
		} else if (currentType == TYPE_2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				view_2 = LayoutInflater.from(context).inflate(R.layout.cms_list_1_frament_item_2, null);
				holder.z_item_all = (LinearLayout) view_2.findViewById(R.id.z_item_all);
				holder.multiImagView = (MultiImageView) view_2.findViewById(R.id.multiImagView);
				holder.tv_item_title = (TextView) view_2.findViewById(R.id.tv_item_title);
				holder.tv_item_source = (TextView) view_2.findViewById(R.id.tv_item_source);
				holder.tv_item_view_num = (TextView) view_2.findViewById(R.id.tv_item_view_num);
				holder.tv_item_comment_num = (TextView) view_2.findViewById(R.id.tv_item_comment_num);
				holder.tv_item_time = (TextView) view_2.findViewById(R.id.tv_item_time);
				holder.tv_item_del = (TextView) view_2.findViewById(R.id.tv_item_del);
				view_2.setTag(holder);
				convertView = view_2;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final CmsBean cmsBean = listCmsBean.get(position);

			List<String> list_img = cmsBean.getListImg();
			if (list_img != null && list_img.size() > 0) {
				holder.multiImagView.setVisibility(View.VISIBLE);
				holder.multiImagView.setList(list_img);
				holder.multiImagView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
					@Override
					public void onItemClick(View view, int position) {
						Intent intent = new Intent(context, CmsDetailUI.class);
						intent.putExtra("id", cmsBean.getId());
						context.startActivity(intent);
					}
				});
			} else {
				holder.multiImagView.setVisibility(View.GONE);
			}

			holder.tv_item_title.setText(cmsBean.getTitle());
			if (Utils.isEmity(cmsBean.getView_num())) {
				holder.tv_item_view_num.setText("0浏览");
			} else {
				holder.tv_item_view_num.setText(cmsBean.getView_num() + "浏览");
			}
			if (Utils.isEmity(cmsBean.getComment_num())) {
				holder.tv_item_comment_num.setText("0评论");
			} else {
				holder.tv_item_comment_num.setText(cmsBean.getComment_num() + "评论");
			}
			holder.tv_item_source.setText(cmsBean.getSource());
			if (!Utils.isEmity(cmsBean.getAdd_time())) {
				String time = TimeUtil.getStandardDate(cmsBean.getAdd_time());
				holder.tv_item_time.setText(time);
			}
			Map<String, String> userMap = cmsBean.getUserMap();
			if (userMap != null && user_id.equals(userMap.get("user_id"))) {
				holder.tv_item_del.setVisibility(View.VISIBLE);
				holder.tv_item_del.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
					}
				});
			} else {
				holder.tv_item_del.setVisibility(View.GONE);
			}
		} else if (currentType == TYPE_3) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				view_3 = LayoutInflater.from(context).inflate(R.layout.cms_list_1_frament_item_3, null);
				holder.z_item_all = (LinearLayout) view_3.findViewById(R.id.z_item_all);
				holder.iv_item_img = (ImageView) view_3.findViewById(R.id.iv_item_img);
				holder.tv_item_img_num = (TextView) view_3.findViewById(R.id.tv_item_img_num);
				holder.tv_item_title = (TextView) view_3.findViewById(R.id.tv_item_title);
				holder.tv_item_source = (TextView) view_3.findViewById(R.id.tv_item_source);
				holder.tv_item_view_num = (TextView) view_3.findViewById(R.id.tv_item_view_num);
				holder.tv_item_comment_num = (TextView) view_3.findViewById(R.id.tv_item_comment_num);
				holder.tv_item_time = (TextView) view_3.findViewById(R.id.tv_item_time);
				holder.tv_item_del = (TextView) view_3.findViewById(R.id.tv_item_del);
				view_3.setTag(holder);
				convertView = view_3;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CmsBean cmsBean = listCmsBean.get(position);

			List<Map<String, String>> listImgMap = cmsBean.getListImgMap();
			if (listImgMap != null && listImgMap.size() > 0) {
				holder.tv_item_img_num.setText(listImgMap.size() + "图");
				BitmapHelp.loadImg(context, holder.iv_item_img, listImgMap.get(0).get("img"));
				holder.iv_item_img.setVisibility(View.VISIBLE);
			} else {
				holder.iv_item_img.setVisibility(View.GONE);
			}
			holder.tv_item_title.setText(cmsBean.getTitle());
			if (Utils.isEmity(cmsBean.getView_num())) {
				holder.tv_item_view_num.setText("0浏览");
			} else {
				holder.tv_item_view_num.setText(cmsBean.getView_num() + "浏览");
			}
			if (Utils.isEmity(cmsBean.getComment_num())) {
				holder.tv_item_comment_num.setText("0评论");
			} else {
				holder.tv_item_comment_num.setText(cmsBean.getComment_num() + "评论");
			}
			holder.tv_item_source.setText(cmsBean.getSource());
			if (!Utils.isEmity(cmsBean.getAdd_time())) {
				String time = TimeUtil.getStandardDate(cmsBean.getAdd_time());
				holder.tv_item_time.setText(time);
			}
			Map<String, String> userMap = cmsBean.getUserMap();
			if (userMap != null && user_id.equals(userMap.get("user_id"))) {
				holder.tv_item_del.setVisibility(View.VISIBLE);
				holder.tv_item_del.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
					}
				});
			} else {
				holder.tv_item_del.setVisibility(View.GONE);
			}
		} else if (currentType == TYPE_4) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				view_4 = LayoutInflater.from(context).inflate(R.layout.cms_list_1_frament_item_4, null);
				holder.z_item_all = (LinearLayout) view_4.findViewById(R.id.z_item_all);
				holder.tv_item_title = (TextView) view_4.findViewById(R.id.tv_item_title);
				holder.video_view = (JZVideoPlayerStandard) view_4.findViewById(R.id.video_view);
				holder.tv_item_source = (TextView) view_4.findViewById(R.id.tv_item_source);
				holder.tv_item_view_num = (TextView) view_4.findViewById(R.id.tv_item_view_num);
				holder.tv_item_comment_num = (TextView) view_4.findViewById(R.id.tv_item_comment_num);
				holder.tv_item_time = (TextView) view_4.findViewById(R.id.tv_item_time);
				holder.tv_item_del = (TextView) view_4.findViewById(R.id.tv_item_del);
				view_4.setTag(holder);
				convertView = view_4;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CmsBean cmsBean = listCmsBean.get(position);

			//holder.video_view.setUp(cmsBean.getVideo(), "", cmsBean.getVideo_img());

			holder.video_view.setUp(cmsBean.getVideo()
					, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
			Glide.with(context).load(cmsBean.getVideo_img()).into(holder.video_view.thumbImageView);
			//holder.video_view.thumbImageView.setImage(cmsBean.getVideo_img());
			//holder.video_view.thumbImageView.setImageURI(new U);
			//jzVideoPlayerStandard.thumbImageView.setImage(cmsBean.getVideo_img());

			holder.tv_item_title.setText(cmsBean.getTitle());
			if (Utils.isEmity(cmsBean.getView_num())) {
				holder.tv_item_view_num.setText("0浏览");
			} else {
				holder.tv_item_view_num.setText(cmsBean.getView_num() + "浏览");
			}
			if (Utils.isEmity(cmsBean.getComment_num())) {
				holder.tv_item_comment_num.setText("0评论");
			} else {
				holder.tv_item_comment_num.setText(cmsBean.getComment_num() + "评论");
			}
			holder.tv_item_source.setText(cmsBean.getSource());
			if (!Utils.isEmity(cmsBean.getAdd_time())) {
				String time = TimeUtil.getStandardDate(cmsBean.getAdd_time());
				holder.tv_item_time.setText(time);
			}
			Map<String, String> userMap = cmsBean.getUserMap();
			if (userMap != null && user_id.equals(userMap.get("user_id"))) {
				holder.tv_item_del.setVisibility(View.VISIBLE);
				holder.tv_item_del.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
					}
				});
			} else {
				holder.tv_item_del.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	private class ViewHolder {
		LinearLayout z_item_all;
		ImageView iv_item_img;
		TextView tv_item_img_num;
		MultiImageView multiImagView;
		JZVideoPlayerStandard video_view;
		TextView tv_item_title;
		TextView tv_item_source;
		TextView tv_item_view_num;
		TextView tv_item_comment_num;
		TextView tv_item_time;
		TextView tv_item_del;
	}
}
