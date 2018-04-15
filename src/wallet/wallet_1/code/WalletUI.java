package wallet.wallet_1.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import withdrawal.withdrawal_1.code.WithdrawalUI;
import withdrawal_list.withdrawal_list_2.code.WithdrawalListUI;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bean.CardBean;
import bean.RequestReturnBean;
import card_list.card_list_1.code.CardListUI;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 钱包管理
 * 
 * @author peidongxu
 * 
 */
public class WalletUI extends BaseUI {
	private ImageView iv_avatar;// 头像
	private TextView tv_money;// 余额
	private TextView tv_card;// 银行卡数

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.wallet_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_card = (TextView) findViewById(R.id.tv_card);

		RelativeLayout z_withdrawal = (RelativeLayout) findViewById(R.id.z_withdrawal);
		z_withdrawal.setOnClickListener(this);
		RelativeLayout z_card = (RelativeLayout) findViewById(R.id.z_card);
		z_card.setOnClickListener(this);
		RelativeLayout z_withdrawal_list = (RelativeLayout) findViewById(R.id.z_withdrawal_list);
		z_withdrawal_list.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("我的钱包");

		setUserInfo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getAccount();
		getCardList();
	}

	/**
	 * 获取我的账户
	 */
	private void getAccount() {
		String url = HttpUtil.getUrl("/pay/account");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = WalletJson.getAccount(response.toString());
				if (HttpUtil.isSuccess(WalletUI.this, returnBean.getCode())) {
					// 成功
					Map<String, String> map = (Map<String, String>) returnBean.getObject();
					setAccount(map);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
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
				RequestReturnBean returnBean = WalletJson.getUserBankList(response.toString());
				if (HttpUtil.isSuccess(WalletUI.this, returnBean.getCode())) {
					List<CardBean> listBank = returnBean.getListObject();
					if (listBank != null && listBank.size() > 0) {
						tv_card.setText(String.valueOf(listBank.size()) + "张");
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 设置账户信息
	 */
	private void setAccount(Map<String, String> map) {
		if (map == null) {
			return;
		}
		if (!Utils.isEmity(map.get("money"))) {
			tv_money.setText("￥" + map.get("money"));
		}
	}

	private void setUserInfo() {
		Map<String, String> userInfo = MyConfig.getUserInfo(this);
		if (userInfo != null) {
			BitmapHelp.loadImg(this, iv_avatar, userInfo.get("avatar"), R.drawable.default_avatar_angle);
		}
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_withdrawal:
			startActivity(new Intent(this, WithdrawalUI.class));
			break;
		case R.id.z_card:
			startActivity(new Intent(this, CardListUI.class));
			break;
		case R.id.z_withdrawal_list:
			startActivity(new Intent(this, WithdrawalListUI.class));
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
