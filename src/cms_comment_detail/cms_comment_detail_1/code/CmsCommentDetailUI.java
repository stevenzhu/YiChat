package cms_comment_detail.cms_comment_detail_1.code;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import user_card.user_card_1.code.UserCardUI;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.CmsCommentBean;
import bean.CmsReplyBean;
import bean.RequestReturnBean;

import cms_detail.cms_detail_1.code.SmileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.DialogUtil;
import com.shorigo.utils.DialogUtil.DialogClickCallBack;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyListView;
import com.shorigo.view.input.EaseChatInputMenu;
import com.shorigo.view.input.EaseChatInputMenu.ChatInputMenuListener;
import com.shorigo.view.input.model.EaseEmojicon;
import com.shorigo.yichat.R;

/**
 * 评论详情
 * 
 * @author peidongxu
 * 
 */
public class CmsCommentDetailUI extends BaseUI implements DialogClickCallBack {

	private Gson gson;
	private String id;
	private CmsCommentBean commentBean;

	private ImageView iv_avatar;
	private TextView tv_nick;
	private TextView tv_time;
	private TextView tv_content;
	private TextView tv_del;
	private LinearLayout z_action;
	private LinearLayout z_laud;
	private TextView tv_laud;
	private ImageView iv_laud;
	private LinearLayout z_reply;

	private List<CmsReplyBean> listReplyBean;
	private MyListView lv_reply;
	private CmsCommentDetailAdapter adapter;

	protected EaseChatInputMenu inputMenu;
	/** 评论 */
	public final static int COMMENT = 1;
	/** 评论删除 */
	public final static int COMMENT_DEL = 2;
	/** 评论点赞 */
	public final static int COMMENT_LAUD = 3;
	/** 评论点赞取消 */
	public final static int COMMENT_LAUD_CANCLE = 4;
	/** 回复--评论 */
	public final static int REPLY_COMMENT = 5;
	/** 回复--回复 */
	public final static int REPLY_REPLY = 6;
	/** 回复删除 */
	public final static int REPLY_DEL = 7;
	/** 回复点赞 */
	public final static int REPLY_LAUD = 8;
	/** 回复点赞取消 */
	public final static int REPLY_LAUD_CANCLE = 9;

	private int position;
	/** 回复ID */
	private String reply_id;
	/** 回复类型：1：回复-评论 2：回复-回复 */
	private String reply_type;
	/** 删除类型：1：删除-评论 2：删除-回复 */
	private String del_type;

	private DialogUtil dialogUtil;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_comment_detail_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_del = (TextView) findViewById(R.id.tv_del);
		z_action = (LinearLayout) findViewById(R.id.z_action);
		z_laud = (LinearLayout) findViewById(R.id.z_laud);
		tv_laud = (TextView) findViewById(R.id.tv_laud);
		iv_laud = (ImageView) findViewById(R.id.iv_laud);
		z_reply = (LinearLayout) findViewById(R.id.z_reply);
		StyleUtils.setTabBg(this, iv_laud, new int[] { R.drawable.cms_detail_1_icon_laud_down, R.drawable.cms_detail_1_icon_laud_up });

		lv_reply = (MyListView) findViewById(R.id.lv_reply);

		inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
		inputMenu.init(null);
		inputMenu.hideKeyboard();
		inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

			@Override
			public void onSendMessage(String content) {
				if ("1".equals(reply_type)) {
					sendTextCommentReply(content);
				} else if ("2".equals(reply_type)) {
					reply_type = "1";
					inputMenu.setHint("");
					replySendText(content);
				}
			}

			@Override
			public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
				return false;
			}

			@Override
			public void onBigExpressionClicked(EaseEmojicon emojicon) {
			}
		});
	}

	@Override
	protected void prepareData() {

		// 接收参数
		Intent intent = getIntent();
		id = intent.getStringExtra("id");

		reply_type = "1";

		dialogUtil = new DialogUtil();
		dialogUtil.setCallBack(this);

		adapter = new CmsCommentDetailAdapter(this, listReplyBean, commentBean, mHandler);
		lv_reply.setAdapter(adapter);

		gson = new Gson();
		Type beanTemp = new TypeToken<CmsCommentBean>() {
		}.getType();
		commentBean = gson.fromJson(ACache.get(this).getAsString("cms_comment_detail_" + id), beanTemp);
		setValue();

		getCommentDetail();

	}

	@Override
	protected void onMyClick(View v) {
	}

	/**
	 * 获取动态评论回复
	 */
	private void getCommentDetail() {
		String url = HttpUtil.getUrl("/dynamic/replyList");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		map.put("p", String.valueOf(1));
		map.put("num", "15");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.getCommentDetail(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
					commentBean = (CmsCommentBean) returnBean.getObject();
					setValue();
					// 数据缓存
					String json = gson.toJson(commentBean);
					ACache.get(CmsCommentDetailUI.this).put("cms_comment_detail_" + id, json);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 文字评论回复
	 */
	private void sendTextCommentReply(String content) {
		String url = HttpUtil.getUrl("/dynamic/reply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		map.put("content", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
					getCommentDetail();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 评论点赞
	 */
	private void commentLike() {
		String url = HttpUtil.getUrl("/dynamic/likeReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 评论取消点赞
	 */
	private void commentLikeCancle() {
		String url = HttpUtil.getUrl("/dynamic/cancelLikeReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 评论删除
	 */
	private void commentDel() {
		String url = HttpUtil.getUrl("/dynamic/delReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
					back();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 文字回复
	 */
	private void replySendText(String content) {
		String url = HttpUtil.getUrl("/dynamic/reply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", id);
		map.put("parent_reply_id", reply_id);
		map.put("content", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
					getCommentDetail();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 回复点赞
	 */
	private void replyLike(String reply_id) {
		String url = HttpUtil.getUrl("/dynamic/likeReply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("reply_id", reply_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 回复取消点赞
	 */
	private void replyLikeCancle(String reply_id) {
		String url = HttpUtil.getUrl("/dynamic/cancelLikeReply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("reply_id", reply_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 回复删除
	 */
	private void replyDel() {
		String url = HttpUtil.getUrl("/dynamic/delReply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("reply_id", reply_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsCommentDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsCommentDetailUI.this, returnBean.getCode())) {
					listReplyBean.remove(position);
					adapter.setData(listReplyBean, commentBean);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	private void setValue() {
		if (commentBean == null) {
			return;
		}

		BitmapHelp.loadImg(this, iv_avatar, commentBean.getAvatar(), R.drawable.default_avatar_angle);
		iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!commentBean.getUser_id().equals(MyConfig.getUserInfo(CmsCommentDetailUI.this).get("user_id"))) {
					Intent intent = new Intent(CmsCommentDetailUI.this, UserCardUI.class);
					intent.putExtra("user_id", commentBean.getUser_id());
					startActivity(intent);
				}
			}
		});
		String time = TimeUtil.getStandardDate(commentBean.getAdd_time());
		tv_nick.setText(commentBean.getNick());
		tv_time.setText(time);
		if (!Utils.isEmity(commentBean.getContent())) {
			Spannable span = SmileUtils.getSmiledText(this, commentBean.getContent());
			SpannableString spannableString = new SpannableString(span);
			tv_content.setText(spannableString);
			tv_content.setVisibility(View.VISIBLE);
		} else {
			tv_content.setVisibility(View.GONE);
		}

		// 评论是否点赞
		final String is_like = commentBean.getIs_like();
		if ("1".equals(is_like)) {
			z_laud.setSelected(true);
		} else {
			z_laud.setSelected(false);
		}
		tv_laud.setText(commentBean.getLike_num());

		if (commentBean.getUser_id().equals(MyConfig.getUserInfo(this).get("user_id"))) {
			// 自己的评论
			tv_del.setVisibility(View.VISIBLE);
			z_action.setVisibility(View.GONE);
			tv_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 删除评论
					Message msg = new Message();
					msg.what = CmsCommentDetailUI.COMMENT_DEL;
					mHandler.sendMessage(msg);
				}
			});
		} else {
			// 其他人的评论
			tv_del.setVisibility(View.GONE);
			z_action.setVisibility(View.VISIBLE);
			z_laud.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 点赞评论
					Message msg = new Message();
					if ("1".equals(is_like)) {
						msg.what = CmsCommentDetailUI.COMMENT_LAUD_CANCLE;
					} else {
						msg.what = CmsCommentDetailUI.COMMENT_LAUD;
					}
					mHandler.sendMessage(msg);
				}
			});
			z_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 回复评论
					Message msg = new Message();
					msg.what = CmsCommentDetailUI.REPLY_COMMENT;
					mHandler.sendMessage(msg);
				}
			});
		}

		listReplyBean = commentBean.getListReply();
		adapter.setData(listReplyBean, commentBean);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			CmsReplyBean replyBean;
			switch (msg.what) {
			case COMMENT_LAUD:
				// 评论点赞
				commentBean.setLike_num(String.valueOf(Integer.parseInt(commentBean.getLike_num()) + 1));
				commentBean.setIs_like("1");
				setValue();
				commentLike();
				break;
			case COMMENT_LAUD_CANCLE:
				// 评论取消点赞
				commentBean.setLike_num(String.valueOf(Integer.parseInt(commentBean.getLike_num()) - 1));
				commentBean.setIs_like("0");
				setValue();
				commentLikeCancle();
				break;
			case COMMENT_DEL:
				// 评论删除
				del_type = "1";
				position = msg.arg1;
				dialogUtil.setDialogWidth(Constants.width, false);
				dialogUtil.showTipDialog(CmsCommentDetailUI.this, "确定要删除此条评论吗？");
				break;
			case REPLY_COMMENT:
				// 回复评论
				reply_type = "1";
				inputMenu.setHint("回复" + commentBean.getNick() + ":");
				inputMenu.showKeyboard();
				break;
			case REPLY_LAUD:
				// 回复点赞
				replyBean = listReplyBean.get(msg.arg1);
				listReplyBean.get(msg.arg1).setLike_num(String.valueOf(Integer.parseInt(replyBean.getLike_num()) + 1));
				listReplyBean.get(msg.arg1).setIs_like("1");
				adapter.setData(listReplyBean, commentBean);
				reply_id = listReplyBean.get(msg.arg1).getReply_id();
				replyLike(reply_id);
				break;
			case REPLY_LAUD_CANCLE:
				// 回复取消点赞
				replyBean = listReplyBean.get(msg.arg1);
				listReplyBean.get(msg.arg1).setLike_num(String.valueOf(Integer.parseInt(replyBean.getLike_num()) - 1));
				listReplyBean.get(msg.arg1).setIs_like("0");
				adapter.setData(listReplyBean, commentBean);
				reply_id = listReplyBean.get(msg.arg1).getReply_id();
				replyLikeCancle(reply_id);
				break;
			case REPLY_DEL:
				// 回复删除
				del_type = "2";
				position = msg.arg1;
				reply_id = listReplyBean.get(position).getReply_id();
				dialogUtil.setDialogWidth(Constants.width, false);
				dialogUtil.showTipDialog(CmsCommentDetailUI.this, "确定要删除此条回复吗？");
				break;
			case REPLY_REPLY:
				// 回复回复
				reply_type = "2";
				position = msg.arg1;
				replyBean = listReplyBean.get(position);
				reply_id = replyBean.getReply_id();
				inputMenu.setHint("回复" + replyBean.getSend_nick() + ":");
				inputMenu.showKeyboard();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void callBack(View v) {
		dialogUtil.dismissDialog();
		switch (v.getId()) {
		case R.id.tv_dialog_tip_ok:
			// 删除确定
			if ("1".equals(del_type)) {
				commentDel();
			} else if ("2".equals(del_type)) {
				replyDel();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
