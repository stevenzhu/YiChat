package chat.chat_1.code;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.simcpux.WXPayUtil;
import net.sourceforge.simcpux.WXPayUtil.WXPayCallBack;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.RedPacketConstant;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.pay.alipay.AliPayUtil;
import com.shorigo.pay.alipay.AliPayUtil.AliPayCallBack;
import com.shorigo.utils.Constants;
import com.shorigo.utils.DialogUtil;
import com.shorigo.utils.DialogUtil.DialogClickCallBack;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 发红包
 * 
 * @author peidongxu
 * 
 */
public class RedPickageUI extends BaseUI implements DialogClickCallBack, AliPayCallBack, WXPayCallBack {
	private String id;
	private int chatType;

	private double money;
	private String totol_money;
	private TextView tv_tip;// 提示
	private LinearLayout z_chat_group;// 群组红包布局
	private TextView tv_group_money_pin;// 拼 标识
	private TextView tv_group_money;
	private EditText et_group_money;// 群红包 金额
	private TextView tv_type_desc;
	private TextView tv_type;
	private String type;// 红包类型 1 普通红包 2 普通群红包 3 随机群红包
	private EditText et_group_num;// 群红包 人数
	private String group_num;
	private TextView tv_group_num;// 群内人数

	private LinearLayout z_chat_single;// 个人红包布局
	private EditText et_money;

	private EditText et_remark;// 留言
	private String remark;
	private TextView tv_money;// 总金额
	private TextView tv_send;//

	private String order_id;

	private DialogUtil dialogUtil;
	private String pay_type; // 购买方式 2: 支付宝 3：微信
	private String order_sn;// 购买单号
	private AliPayUtil aliPayUtil;
	private WXPayUtil wxPayUtil;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.ease_activity_redpickage);
	}

	@Override
	protected void findView_AddListener() {
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		z_chat_group = (LinearLayout) findViewById(R.id.z_chat_group);
		tv_group_money_pin = (TextView) findViewById(R.id.tv_group_money_pin);
		tv_group_money = (TextView) findViewById(R.id.tv_group_money);
		et_group_money = (EditText) findViewById(R.id.et_group_money);
		et_group_money.addTextChangedListener(new MyTextWatcher(et_group_money));
		tv_type_desc = (TextView) findViewById(R.id.tv_type_desc);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_type.setOnClickListener(this);
		et_group_num = (EditText) findViewById(R.id.et_group_num);
		et_group_num.addTextChangedListener(new MyTextWatcher(et_group_num));
		tv_group_num = (TextView) findViewById(R.id.tv_group_num);

		z_chat_single = (LinearLayout) findViewById(R.id.z_chat_single);
		et_money = (EditText) findViewById(R.id.et_money);
		et_money.addTextChangedListener(new MyTextWatcher(et_money));

		et_remark = (EditText) findViewById(R.id.et_remark);

		tv_money = (TextView) findViewById(R.id.tv_money);

		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		tv_send.getBackground().setAlpha(100);
	}

	@Override
	protected void prepareData() {
		setTitle("发红包");

		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		chatType = intent.getIntExtra("chatType", 0);

		group_num = "1";

		// 初始化对象
		dialogUtil = new DialogUtil();
		dialogUtil.setCallBack(this);
		dialogUtil.setDialogWidth(Constants.width, true);
		aliPayUtil = new AliPayUtil(this);
		aliPayUtil.setCallBack(this);
		wxPayUtil = new WXPayUtil(this, msgApi);
		wxPayUtil.setWxPayCallBack(this);

		if (chatType == EaseConstant.CHATTYPE_GROUP) {
			z_chat_group.setVisibility(View.VISIBLE);
			z_chat_single.setVisibility(View.GONE);
			type = "3";
			tv_type_desc.setText("每人抽到的金额随机，");
			tv_type.setText("改为普通红包");
			tv_group_money_pin.setVisibility(View.VISIBLE);
			tv_group_money.setText("总金额");
			EMGroup group = EMClient.getInstance().groupManager().getGroup(id);
			if (group != null) {
				tv_group_num.setText("" + group.getMemberCount());
			}
		} else {
			z_chat_group.setVisibility(View.GONE);
			z_chat_single.setVisibility(View.VISIBLE);
			type = "1";
		}
		tv_tip.setVisibility(View.GONE);
		tv_send.setEnabled(false);
		tv_send.getBackground().setAlpha(100);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_type:
			// 改变类型
			if ("2".equals(type)) {
				type = "3";
				tv_type_desc.setText("每人抽到的金额随机，");
				tv_type.setText("改为普通红包");
				tv_group_money_pin.setVisibility(View.VISIBLE);
				tv_group_money.setText("总金额");
			} else if ("3".equals(type)) {
				type = "2";
				tv_type_desc.setText("每人抽到的金额固定，");
				tv_type.setText("改为拼手气红包");
				tv_group_money_pin.setVisibility(View.GONE);
				tv_group_money.setText("单个金额");
			}
			break;
		case R.id.tv_send:
			// 发红包
			remark = et_remark.getText().toString();
			if (Utils.isEmity(remark)) {
				remark = "恭喜发财，大吉大利";
			}
			dialogUtil.showPayDialog(this);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取红包订单号
	 */
	private void getOrderSn() {
		String url = HttpUtil.getUrl("/redpacket/send");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("type", type);
		map.put("num", group_num);
		map.put("money", totol_money);
		if (id.contains(Constants.PREFIX)) {
			id = id.replace(Constants.PREFIX, "");
		}
		map.put("to_user_id", id);
		map.put("message", remark);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = RedPickageJson.getOrderSn(response.toString());
				if (HttpUtil.isSuccess(RedPickageUI.this, returnBean.getCode())) {
					if (returnBean.getObject() != null) {
						order_sn = returnBean.getObject().toString();
						if ("2".equals(pay_type)) {
							aliPayUtil.pay("发红包", order_sn, totol_money, "");
						} else if ("3".equals(pay_type)) {
							wxPayUtil.pay("发红包", order_sn, totol_money, "");
						}
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
	 * 获取红包订单ID
	 */
	private void getOrderId() {
		String url = HttpUtil.getUrl("/redpacket/sendHandle");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("type", pay_type);
		map.put("out_trade_no", order_sn);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = RedPickageJson.getOrderSn(response.toString());
				if (HttpUtil.isSuccess(RedPickageUI.this, returnBean.getCode())) {
					if (returnBean.getObject() != null) {
						order_id = returnBean.getObject().toString();
						Intent intent = new Intent();
						intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_TYPE, type);
						intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_ID, order_id);
						intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID, id);
						intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_GREETING, remark);
						setResult(RESULT_OK, intent);
						back();
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
	 * 内部类实现TextWatcher公用
	 */
	private class MyTextWatcher implements TextWatcher {

		private EditText editText;

		private MyTextWatcher(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (chatType == EaseConstant.CHATTYPE_GROUP) {
				// 群组
				if (!Utils.isEmity(et_group_money.getText().toString())) {
					money = Double.parseDouble(et_group_money.getText().toString());
				} else {
					money = 0;
				}
				group_num = et_group_num.getText().toString();

				tv_tip.setVisibility(View.GONE);
				if ("2".equals(type)) {
					// 普通
					if (money < 1) {
						tv_tip.setText("单个红包金额不可低于1元");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if (money > 200) {
						tv_tip.setText("单个红包金额不可超过200元");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if (Utils.isEmity(group_num)) {
						totol_money = "0.00";
						tv_money.setText("￥0.00");
						tv_tip.setVisibility(View.GONE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if ("0".equals(group_num)) {
						tv_tip.setText("至少需要设置1个红包");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					totol_money = String.format("%.2f", money * Integer.parseInt(group_num));
					tv_money.setText("￥" + totol_money);
					tv_tip.setVisibility(View.GONE);
					tv_send.setEnabled(true);
					tv_send.getBackground().setAlpha(255);
				} else if ("3".equals(type)) {
					// 拼手气
					totol_money = String.format("%.2f", money);
					tv_money.setText("￥" + totol_money);
					if (money > 20000) {
						tv_tip.setText("单次支付金额不可超过20000元");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if (Utils.isEmity(group_num)) {
						tv_tip.setVisibility(View.GONE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if ("0".equals(group_num)) {
						tv_tip.setText("至少需要设置1个红包");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if (Integer.parseInt(group_num) > 100) {
						tv_tip.setText("一次最多可发100个红包");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					if (money < 1 * Integer.parseInt(group_num)) {
						tv_tip.setText("单个红包金额不可低于1元");
						tv_tip.setVisibility(View.VISIBLE);
						tv_send.setEnabled(false);
						tv_send.getBackground().setAlpha(100);
						return;
					}
					tv_tip.setVisibility(View.GONE);
					tv_send.setEnabled(true);
					tv_send.getBackground().setAlpha(255);
				}
			} else {
				// 个人
				if (!Utils.isEmity(et_money.getText().toString())) {
					money = Double.parseDouble(et_money.getText().toString());
				} else {
					money = 0;
				}
				if (money == 0) {
					tv_money.setText("￥0.00");
					tv_tip.setVisibility(View.GONE);
					tv_send.setEnabled(false);
					tv_send.getBackground().setAlpha(100);
					return;
				}
				totol_money = String.format("%.2f", money);
				tv_money.setText("￥" + totol_money);
				if (money < 1) {
					tv_tip.setText("单个红包金额不可低于1元");
					tv_tip.setVisibility(View.VISIBLE);
					tv_send.setEnabled(false);
					tv_send.getBackground().setAlpha(100);
					return;
				} 
				if (money > 200) {
					tv_tip.setText("单个红包金额不可超过200元");
					tv_tip.setVisibility(View.VISIBLE);
					tv_send.setEnabled(false);
					tv_send.getBackground().setAlpha(100);
				} else {
					tv_tip.setVisibility(View.GONE);
					tv_send.setEnabled(true);
					tv_send.getBackground().setAlpha(255);
				}
			}
		}
	}

	public void callBack(View v) {
		switch (v.getId()) {
		case R.id.z_pay_alipay:
			// 支付宝支付
			pay_type = "2";
			dialogUtil.dismissDialog();
			getOrderSn();
			break;
		case R.id.z_pay_wxpay:
			// 微信支付
			pay_type = "3";
			dialogUtil.dismissDialog();
			getOrderSn();
			break;
		default:
			break;
		}
	};

	@Override
	public void alipayCallBack(boolean isSuccess) {
		if (isSuccess) {
			getOrderId();
		}
	}

	@Override
	public void wxPayCallBack(boolean isSuccess) {
		if (isSuccess) {
			getOrderId();
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
