package card_list_del.card_list_del_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import android.widget.TextView;
import bean.CardBean;
import bean.RequestReturnBean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyListView;
import com.shorigo.yichat.R;

/**
 * 银行卡删除列表
 * 
 * @author peidongxu
 * 
 */
public class CardDelListUI extends BaseUI {

	private List<CardBean> listCardBean;
	private MyListView mListView;
	private CardDelListAdapter adapter;
	private TextView tv_msg;
	private Gson gson;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.card_list_del_1);
	}

	@Override
	protected void findView_AddListener() {
		mListView = (MyListView) findViewById(R.id.lv_card_del_list);
		tv_msg = (TextView) findViewById(R.id.tv_card_del_list_msg);
		TextView tv_nobing = (TextView) findViewById(R.id.tv_nobing);
		tv_nobing.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("解绑");
		gson = new Gson();
		listCardBean = new ArrayList<CardBean>();

		Type listCardBeanType = new TypeToken<List<CardBean>>() {
		}.getType();
		listCardBean = gson.fromJson(ACache.get(this).getAsString("user_bank_list"), listCardBeanType);

		adapter = new CardDelListAdapter(this, listCardBean);
		mListView.setAdapter(adapter);
		getCardList();

		if (!Utils.isNetworkConnected(this) && listCardBean == null) {
			MyApplication.getInstance().showToast("请检查网络！");
			tv_msg.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_nobing:
			// 解绑
			String id_array = "";
			if (listCardBean != null && listCardBean.size() > 0) {
				for (int i = 0; i < listCardBean.size(); i++) {
					if (listCardBean.get(i).isCheck()) {
						if (Utils.isEmity(id_array)) {
							id_array = listCardBean.get(i).getId();
						} else {
							id_array = id_array + "," + listCardBean.get(i).getId();
						}
					}
				}
			}
			if (Utils.isEmity(id_array)) {
				MyApplication.getInstance().showToast("未选中银行卡");
				return;
			}
			delCardList(id_array);
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
				RequestReturnBean returnBean = CardDelListJson.getUserBankList(response.toString());
				if (HttpUtil.isSuccess(CardDelListUI.this, returnBean.getCode())) {
					listCardBean = returnBean.getListObject();
					adapter.setData(listCardBean);
					// 数据缓存
					String json = gson.toJson(listCardBean);
					ACache.get(CardDelListUI.this).put("user_bank_list", json);
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

	/**
	 * 批量解绑的银行卡
	 */
	private void delCardList(String id_array) {
		String url = HttpUtil.getUrl("/withdraw/delBank");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id_array", id_array);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CardDelListJson.analysis(response.toString());
				if (HttpUtil.isSuccess(CardDelListUI.this, returnBean.getCode())) {
					getCardList();
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
