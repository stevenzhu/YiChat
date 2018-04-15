package withdrawal_list.withdrawal_list_2.code;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.view.View;
import bean.RequestReturnBean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.MyConfig;
import com.shorigo.view.MyListView;
import com.shorigo.view.refresh.PullRefreshUtil;
import com.shorigo.view.refresh.PullRefreshView;
import com.shorigo.view.refresh.PullRefreshView.OnPullListener;
import com.shorigo.yichat.R;

/**
 * 提现记录
 * 
 * @author peidongxu
 * 
 */
public class WithdrawalListUI extends BaseUI implements OnPullListener {
	private PullRefreshView refresh_view;
	// 当前页数
	private int currentPage = 1;
	private boolean isRef = true;
	private MyListView mListView;
	private WithdrawalListAdapter adapter;
	private List<Map<String, String>> listMap;
	private Gson gson;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.withdrawal_list_2);
	}

	@Override
	protected void findView_AddListener() {
		refresh_view = (PullRefreshView) findViewById(R.id.refresh_view);
		refresh_view.setOnPullListener(this);
		PullRefreshUtil.setRefresh(refresh_view, true, false);
		mListView = (MyListView) findViewById(R.id.lv_withdrawal_list);
	}

	@Override
	protected void prepareData() {
		setTitle("提现列表");

		currentPage = 1;
		isRef = true;

		// 获取缓存对象
		gson = new Gson();
		Type listTemp = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		listMap = gson.fromJson(ACache.get(this).getAsString("withdrawal_list"), listTemp);
		adapter = new WithdrawalListAdapter(this, listMap);
		mListView.setAdapter(adapter);

		getWithdrawalList();
	}

	@Override
	protected void onMyClick(View v) {

	}

	/**
	 * 获取提现记录
	 */
	private void getWithdrawalList() {
		String url = HttpUtil.getUrl("/withdraw/list");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("p", String.valueOf(currentPage));
		map.put("num", "15");
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = WithdrawalListJson.getWithdrawalList(response.toString());
				if (HttpUtil.isSuccess(WithdrawalListUI.this, returnBean.getCode())) {
					List<Map<String, String>> listTempMap = returnBean.getListObject();
					if (listTempMap != null && listTempMap.size() == 15) {
						PullRefreshUtil.setRefresh(refresh_view, true, true);
					} else {
						PullRefreshUtil.setRefresh(refresh_view, true, false);
					}
					if (isRef) {
						listMap = listTempMap;
						// 数据缓存
						String json = gson.toJson(listMap);
						ACache.get(WithdrawalListUI.this).put("withdrawal_list", json);
					} else {
						listMap.addAll(listTempMap);
					}
					adapter.setData(listMap);
					if (refresh_view != null) {
						refresh_view.refreshFinish();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				if (refresh_view != null) {
					refresh_view.refreshFinish();
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		currentPage = 1;
		isRef = true;
		getWithdrawalList();
	}

	@Override
	public void onLoad() {
		currentPage++;
		isRef = false;
		getWithdrawalList();
	}

	@Override
	protected void back() {
		finish();
	}
}
