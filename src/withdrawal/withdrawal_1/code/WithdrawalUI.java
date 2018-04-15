package withdrawal.withdrawal_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 提现
 * 
 * @author peidongxu
 * 
 */
public class WithdrawalUI extends BaseUI implements OnItemClickListener {
	// 银行卡名称
	private TextView tv_card_name;
	private String card_name;
	private EditText et_money;
	// 提现金额
	private String money;

	private List<CardBean> listCardBean;
	private String card_id;
	private String type;// 提现类型 0普通 1加急
	private Gson gson;
	private Dialog mDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.withdrawal_1);
	}

	@Override
	protected void findView_AddListener() {
		RelativeLayout rl_card = (RelativeLayout) findViewById(R.id.z_card_name);
		rl_card.setOnClickListener(this);
		tv_card_name = (TextView) findViewById(R.id.tv_card_name);
		et_money = (EditText) findViewById(R.id.et_money);
		TextView tv_submit = (TextView) findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("提现");

		listCardBean = new ArrayList<CardBean>();
		gson = new Gson();
		type = "0";

		Type listBankMapType = new TypeToken<List<CardBean>>() {
		}.getType();
		listCardBean = gson.fromJson(ACache.get(this).getAsString("user_bank_list"), listBankMapType);

		// 选择银行卡
		getCardList();

	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_card_name:
			// 选择银行卡
			if (listCardBean != null && listCardBean.size() > 0) {
				showBankList(WithdrawalUI.this, WithdrawalUI.this);
			} else {
				MyApplication.getInstance().showToast("当前未绑定银行卡");
			}
			break;
		case R.id.tv_submit:
			// 提交
			money = et_money.getText().toString();

			if (Utils.isEmity(card_name)) {
				MyApplication.getInstance().showToast("请选择提现的银行卡");
				return;
			}

			if (Utils.isEmity(money)) {
				MyApplication.getInstance().showToast("请输入提现金额");
				return;
			}
			if (0 == Integer.parseInt(money)) {
				MyApplication.getInstance().showToast("提现金额不能为0");
				return;
			}

			submitWithdrawal();
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
				RequestReturnBean returnBean = WithdrawalJson.getUserBankList(response.toString());
				if (HttpUtil.isSuccess(WithdrawalUI.this, returnBean.getCode())) {
					listCardBean = returnBean.getListObject();
					// 数据缓存
					String json = gson.toJson(listCardBean);
					ACache.get(WithdrawalUI.this).put("user_bank_list", json);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 提现
	 */
	private void submitWithdrawal() {
		String url = HttpUtil.getUrl("/withdraw/apply");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("money", money);
		map.put("card_id", card_id);
		map.put("type", type);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = WithdrawalJson.analysis(response.toString());
				if (HttpUtil.isSuccess(WithdrawalUI.this, returnBean.getCode())) {
					MyApplication.getInstance().showToast("提现申请已提交");
					back();
				} else {
					MyApplication.getInstance().showToast(returnBean.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mDialog.dismiss();
		card_id = listCardBean.get(position).getId();
		card_name = listCardBean.get(position).getBankMap().get("name");
		tv_card_name.setText(card_name);
	}

	/**
	 * 选择银行显示框
	 * 
	 * @param context
	 * @return
	 */
	public void showBankList(Context context, OnItemClickListener onItemClickListener) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.withdrawal_1_dialog_bank, null);

		ListView lv_bank = (ListView) view.findViewById(R.id.lv_dialog_bank);
		lv_bank.setOnItemClickListener(onItemClickListener);

		WithdrawalDialogAdapter adapter = new WithdrawalDialogAdapter(context, listCardBean);
		lv_bank.setAdapter(adapter);

		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		int width = Constants.width - Constants.width / 10 * 2;
		int height = Constants.height - Constants.height / 10 * 3;
		LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.width = width;
		attributes.height = height;
		mDialog.getWindow().setAttributes(attributes);
	}

	@Override
	protected void back() {
		finish();
	}
}
