package card_list.card_list_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import bean.CardBean;
import bean.RequestReturnBean;

import card_add.card_add_1.code.CardAddUI;
import card_list_del.card_list_del_1.code.CardDelListUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyListView;
import com.shorigo.yichat.R;

public class CardListUI extends BaseUI {
	private List<CardBean> listCardBean;
	private MyListView mListView;
	private CardListAdapter adapter;
	private TextView tv_msg;
	private Gson gson;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.card_list_1);
	}

	@Override
	protected void findView_AddListener() {
		mListView = (MyListView) findViewById(R.id.lv_card_list);
		tv_msg = (TextView) findViewById(R.id.tv_card_list_msg);
		TextView tv_bing = (TextView) findViewById(R.id.tv_bing);
		tv_bing.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("我的银行卡");
		setRightButton("解绑");

		listCardBean = new ArrayList<CardBean>();
		gson = new Gson();

		Type listCardBeanType = new TypeToken<List<CardBean>>() {
		}.getType();
		listCardBean = gson.fromJson(ACache.get(this).getAsString("user_bank_list"), listCardBeanType);

		adapter = new CardListAdapter(this, listCardBean);
		mListView.setAdapter(adapter);

		if (!Utils.isNetworkConnected(this) && listCardBean == null) {
			MyApplication.getInstance().showToast("请检查网络！");
			tv_msg.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getCardList();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_bing:
			// 绑定
			startActivity(new Intent(this, CardAddUI.class));
			break;
		case R.id.tv_right:
			// 解绑
			startActivity(new Intent(this, CardDelListUI.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 获取银行卡列表
	 */
	private void getCardList() {
		String url = HttpUtil.getUrl("/withdraw/getUserBankList");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CardListJson.getUserBankList(response.toString());
				if (HttpUtil.isSuccess(CardListUI.this, returnBean.getCode())) {
					listCardBean = returnBean.getListObject();
					adapter.setData(listCardBean);
					// 数据缓存
					String json = gson.toJson(listCardBean);
					ACache.get(CardListUI.this).put("user_bank_list", json);
				}

				if (listCardBean != null && listCardBean.size() > 0) {
					mListView.setVisibility(View.VISIBLE);
					tv_msg.setVisibility(View.GONE);
				} else {
					mListView.setVisibility(View.GONE);
					tv_msg.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);

				if (listCardBean != null && listCardBean.size() > 0) {
					mListView.setVisibility(View.VISIBLE);
					tv_msg.setVisibility(View.GONE);
				} else {
					mListView.setVisibility(View.GONE);
					tv_msg.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	protected void back() {
		finish();
	}
}
