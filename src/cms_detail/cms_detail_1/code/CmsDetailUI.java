package cms_detail.cms_detail_1.code;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.CmsBean;
import bean.CmsCommentBean;
import bean.RequestReturnBean;
import yichat.util.ZUIUtil;
import yichat.util.ZUtil;

import com.baidu.mapapi.map.Polygon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.util.ZipUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Constants;
import com.shorigo.utils.DialogUtil;
import com.shorigo.utils.DialogUtil.DialogClickCallBack;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.UrlConstants;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyListView;
import com.shorigo.view.input.EaseChatInputMenu;
import com.shorigo.view.input.EaseChatInputMenu.ChatInputMenuListener;
import com.shorigo.view.input.model.EaseEmojicon;
import com.shorigo.view.jcvideoplayer.JCVideoPlayer;
import com.shorigo.yichat.R;

/**
 * 动态详情
 * 
 * @author peidongxu
 * 
 */
public class CmsDetailUI extends BaseUI implements DialogClickCallBack {
	private Gson gson;
	// 文章ID
	private String id;
	private CmsBean cmsBean;

	// 标题
	private TextView tv_title;
	// 来源
	private TextView tv_source;
	// 删除
	private TextView tv_del;
	// 时间
	private TextView tv_add_time;
	// 评论数
	private TextView tv_comment_num;

	private MyListView lv_img;
	private List<String> listImg;
	private CmsDetailImgAdapter imgAdapter;

	private JCVideoPlayer video_view;
	private WebView webview;

	private List<CmsCommentBean> listCommentBean;
	private MyListView lv_comment;
	private CmsDetailCommentAdapter adapter;

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
	private int position;
	/** 评论ID */
	private String review_id;
	/** 评论类型：1：评论 2：回复 */
	private String comment_type;

	private DialogUtil dialogUtil;
	private LinearLayout grpTags;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_detail_1);
	}

	@Override
	protected void findView_AddListener() {
		inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
		inputMenu.init(null);
		inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

			@Override
			public void onSendMessage(String content) {
				if ("1".equals(comment_type)) {
					sendTextComment(content);
				} else if ("2".equals(comment_type)) {
					comment_type = "1";
					inputMenu.setHint("");
					sendTextCommentReply(content);
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

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_source = (TextView) findViewById(R.id.tv_source);
		tv_del = (TextView) findViewById(R.id.tv_del);
		tv_del.setOnClickListener(this);
		tv_add_time = (TextView) findViewById(R.id.tv_add_time);
		tv_comment_num = (TextView) findViewById(R.id.tv_comment_num);

		lv_img = (MyListView) findViewById(R.id.lv_img);
		video_view = (JCVideoPlayer) findViewById(R.id.video_view);
		webview = (WebView) findViewById(R.id.webview);
		lv_comment = (MyListView) findViewById(R.id.lv_comment);
		grpTags=(LinearLayout) findViewById(R.id.grpTags);

		//debug
		View vCt=findViewById(R.id.z_all);
		ZUtil.focusOnV(vCt);
	}

	@Override
	protected void prepareData() {

		// 接收参数
		Intent intent = getIntent();
		id = intent.getStringExtra("id");

		dialogUtil = new DialogUtil();
		dialogUtil.setCallBack(this);
		comment_type = "1";

		imgAdapter = new CmsDetailImgAdapter(this, listImg);
		lv_img.setAdapter(imgAdapter);

		gson = new Gson();
		Type beanTemp = new TypeToken<CmsBean>() {
		}.getType();
		cmsBean = gson.fromJson(ACache.get(this).getAsString("cms_detail_1_" + id), beanTemp);
		setValue();
		getCmsDetail();

		Type listTemp = new TypeToken<List<CmsCommentBean>>() {
		}.getType();
		listCommentBean = gson.fromJson(ACache.get(this).getAsString("cms_detail_1_comment_" + id), listTemp);
		adapter = new CmsDetailCommentAdapter(this, listCommentBean, mHandler);
		lv_comment.setAdapter(adapter);
		getCmsComment();

		seeCms();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_del:
			delCms();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取动态详情
	 */
	private void getCmsDetail() {
		String url = HttpUtil.getUrl("/dynamic/info");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.getCmsDetail(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
					cmsBean = (CmsBean) returnBean.getObject();
					setValue();
					// 数据缓存
					String json = gson.toJson(cmsBean);
					ACache.get(CmsDetailUI.this).put("cms_detail_1_" + id, json);
					//debug
					addTags(grpTags,cmsBean.getTags());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public static void addTags(ViewGroup grpTags,String[] tags){
		boolean hasTag=!ZUtil.isEmpty(tags);
		grpTags.setVisibility(hasTag?View.VISIBLE:View.GONE);
		if(!hasTag){return;}
		for(int i=0;i<tags.length;i++) {
			String tmpTag=tags[i];
//			if(TextUtils.isEmpty(tmpTag)||TextUtils.isEmpty(tmpTag.trim())){continue;}
			TextView tv = (TextView) LayoutInflater.from(grpTags.getContext()).inflate(R.layout.tag_item, grpTags, false);
			tv.setText(tmpTag);
			grpTags.addView(tv);
		}
	}

	/**
	 * 获取动态评论
	 */
	private void getCmsComment() {
		String url = HttpUtil.getUrl("/dynamic/reviewList");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", id);
		map.put("p", String.valueOf(1));
		map.put("num", "15");
		map.put("show_reply", "1");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.getComment(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
					listCommentBean = returnBean.getListObject();
					adapter.setData(listCommentBean);
					// 数据缓存
					String json = gson.toJson(listCommentBean);
					ACache.get(CmsDetailUI.this).put("cms_detail_1_comment_" + id, json);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 浏览动态
	 * 
	 * type 操作类型 1点赞 2取消点赞 3点击
	 */
	private void seeCms() {
		String url = HttpUtil.getUrl("/dynamic/operate");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", id);
		map.put("type", "3");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	/**
	 * 删除动态
	 */
	private void delCms() {
		String url = HttpUtil.getUrl("/dynamic/del");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
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
	 * 文字评论
	 */
	private void sendTextComment(String content) {
		String url = HttpUtil.getUrl("/dynamic/review");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", id);
		map.put("content", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
					getCmsComment();
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
		map.put("review_id", review_id);
		map.put("content", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
					getCmsComment();
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
	private void commentLike(String review_id) {
		String url = HttpUtil.getUrl("/dynamic/likeReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", review_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
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
	private void commentLikeCancle(String review_id) {
		String url = HttpUtil.getUrl("/dynamic/cancelLikeReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", review_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
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
	private void commentDel(String review_id) {
		String url = HttpUtil.getUrl("/dynamic/delReview");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("review_id", review_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsDetailJson.action(response.toString());
				if (HttpUtil.isSuccess(CmsDetailUI.this, returnBean.getCode())) {
					listCommentBean.remove(position);
					adapter.setData(listCommentBean);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 页面赋值
	 */
	private void setValue() {
		if (cmsBean == null) {
			return;
		}
		tv_title.setText(cmsBean.getTitle());
		tv_source.setText(cmsBean.getSource());
		if (!Utils.isEmity(cmsBean.getAdd_time())) {
			String add_time = TimeUtil.getStandardDate(cmsBean.getAdd_time());
			tv_add_time.setText(add_time);
		}
		if (Utils.isEmity(cmsBean.getComment_num())) {
			tv_comment_num.setText("0条评论");
		} else {
			tv_comment_num.setText(cmsBean.getComment_num() + "条评论");
		}
		if (!Utils.isEmity(cmsBean.getContent())) {
			setWebView(cmsBean.getContent());
			webview.setVisibility(View.VISIBLE);
		}
		// 设置图片、视频
		if ("1".equals(cmsBean.getType())) {
			List<String> listImg = cmsBean.getListImg();
			imgAdapter.setData(listImg);
			lv_img.setVisibility(View.VISIBLE);
		} else if ("2".equals(cmsBean.getType())) {
			List<String> listImg = cmsBean.getListImg();
			imgAdapter.setData(listImg);
			lv_img.setVisibility(View.VISIBLE);
		} else if ("3".equals(cmsBean.getType())) {
			List<String> listImg = cmsBean.getListImg();
			imgAdapter.setData(listImg);
			lv_img.setVisibility(View.VISIBLE);
		} else if ("4".equals(cmsBean.getType())) {
			video_view.setUp(cmsBean.getVideo(), "", cmsBean.getVideo_img());
			video_view.setVisibility(View.VISIBLE);
		}
		
		String user_id = MyConfig.getUserInfo(this).get("user_id");
		Map<String, String> userMap = cmsBean.getUserMap();
		if (userMap != null && user_id.equals(userMap.get("user_id"))) {
			tv_del.setVisibility(View.VISIBLE);
		} else {
			tv_del.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置WebView参数
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setWebView(String content) {
		webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webview.setVerticalScrollBarEnabled(false);
		webview.setVerticalScrollbarOverlay(false);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setHorizontalScrollbarOverlay(false);
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				handler.proceed(); // 接受所有网站的证书
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webview.getSettings().setDefaultTextEncodingName("utf-8");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
		} else {
			webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		}
		// 加载服务器上的页面
		webview.loadDataWithBaseURL(UrlConstants.SERVICE_HOST_URL, Utils.getHtmlData(content), "text/html", "utf-8", null);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			CmsCommentBean cmsCommentBean;
			String comment_id = "";
			switch (msg.what) {
			case COMMENT_LAUD:
				// 评论点赞
				cmsCommentBean = listCommentBean.get(msg.arg1);
				listCommentBean.get(msg.arg1).setLike_num(String.valueOf(Integer.parseInt(cmsCommentBean.getLike_num()) + 1));
				listCommentBean.get(msg.arg1).setIs_like("1");
				adapter.setData(listCommentBean);
				comment_id = listCommentBean.get(msg.arg1).getReview_id();
				commentLike(comment_id);
				break;
			case COMMENT_LAUD_CANCLE:
				// 评论取消点赞
				cmsCommentBean = listCommentBean.get(msg.arg1);
				listCommentBean.get(msg.arg1).setLike_num(String.valueOf(Integer.parseInt(cmsCommentBean.getLike_num()) - 1));
				listCommentBean.get(msg.arg1).setIs_like("0");
				adapter.setData(listCommentBean);
				comment_id = listCommentBean.get(msg.arg1).getReview_id();
				commentLikeCancle(comment_id);
				break;
			case COMMENT_DEL:
				// 评论删除
				position = msg.arg1;
				dialogUtil.setDialogWidth(Constants.width, false);
				dialogUtil.showTipDialog(CmsDetailUI.this, "确定要删除此条评论吗？");
				break;
			case REPLY_COMMENT:
				// 回复评论
				comment_type = "2";
				position = msg.arg1;
				cmsCommentBean = listCommentBean.get(position);
				review_id = cmsCommentBean.getReview_id();
				inputMenu.setHint("回复" + cmsCommentBean.getNick() + ":");
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
			String review_id = listCommentBean.get(position).getReview_id();
			commentDel(review_id);
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
