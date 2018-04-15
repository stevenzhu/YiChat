package feedback.feedback_1.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyListView;
import com.shorigo.yichat.R;

/**
 * 帮助与反馈
 * 
 * @author peidongxu
 * 
 */
public class FeedBackUI extends BaseUI implements OnItemClickListener {
	private List<Map<String, String>> listMap;
	private MyListView mListView;
	private FeedBackAdapter adapter;

	private String content;
	private EditText et_content;
	private String ids;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.feedback_1);
	}

	@Override
	protected void findView_AddListener() {
		mListView = (MyListView) findViewById(R.id.lv_feedback_cate);
		mListView.setOnItemClickListener(this);

		et_content = (EditText) findViewById(R.id.et_content);

		TextView tv_feedback = (TextView) findViewById(R.id.tv_feedback);
		tv_feedback.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("意见反馈");

		adapter = new FeedBackAdapter(this, listMap);
		mListView.setAdapter(adapter);

		getFeedBackCate();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_feedback:
			content = et_content.getText().toString();
			ids = "";
			if (listMap != null) {
				Map<String, String> map;
				StringBuffer buffer = new StringBuffer();
				boolean isFirst = true;
				for (int i = 0; i < listMap.size(); i++) {
					map = listMap.get(i);
					if ("1".equals(map.get("state"))) {
						if (!isFirst) {
							buffer.append(",");
						}
						buffer.append(map.get("id"));
						isFirst = false;
					}
				}
				ids = buffer.toString();
			}

			if (Utils.isEmity(content) && Utils.isEmity(ids)) {
				MyApplication.getInstance().showToast("请输入反馈内容");
				return;
			}

			submitFeedBack();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (listMap == null) {
			return;
		}
		String state = listMap.get(position).get("state");
		if ("0".equals(state)) {
			listMap.get(position).put("state", "1");
		} else if ("1".equals(state)) {
			listMap.get(position).put("state", "0");
		}
		adapter.setData(listMap);
	}

	/**
	 * 提交意见反馈
	 */
	private void submitFeedBack() {
		String url = HttpUtil.getUrl("/public/submitFeedBack");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("ids", ids);
		map.put("content", content);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = FeedBackJson.submitFeedBack(response.toString());
				if (HttpUtil.isSuccess(FeedBackUI.this, returnBean.getCode())) {
					MyApplication.getInstance().showToast("感谢您的反馈");
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
	 * 获取意见反馈分类
	 */
	private void getFeedBackCate() {
		String url = HttpUtil.getUrl("/public/getFeedBackCate");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = FeedBackJson.getFeedBackCate(response.toString());
				if (HttpUtil.isSuccess(FeedBackUI.this, returnBean.getCode())) {
					listMap = returnBean.getListObject();
					adapter.setData(listMap);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	protected void back() {
		finish();
	}

}
