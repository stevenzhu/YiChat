package card_add.card_add_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
 * 银行卡添加
 * 
 * @author peidongxu
 * 
 */
public class CardAddUI extends BaseUI implements OnItemClickListener {
	// 开户人名称，开户人身份证号，卡名称，卡号
	private EditText et_card_khr_name, et_card_khr_num, et_card_num;
	private TextView tv_card_name;
	private String card_khr_name, card_khr_num, bank_id, card_num;

	private List<Map<String, String>> listCardMap;
	private static final char kongge = ' ';
	private Dialog mDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.card_add_1);
	}

	@Override
	protected void findView_AddListener() {
		et_card_khr_name = (EditText) findViewById(R.id.et_card_khr_name);
		et_card_khr_num = (EditText) findViewById(R.id.et_card_khr_num);
		RelativeLayout z_card_name = (RelativeLayout) findViewById(R.id.z_card_name);
		z_card_name.setOnClickListener(this);
		tv_card_name = (TextView) findViewById(R.id.tv_card_name);
		tv_card_name.setOnClickListener(this);
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_card_num.addTextChangedListener(firstTextWatcher);

		TextView tv_submit = (TextView) findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);

	}

	@Override
	protected void prepareData() {
		setTitle("绑定银行卡");

		listCardMap = new ArrayList<Map<String, String>>();
		gson = new Gson();

		Type listCardBeanType = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		listCardMap = gson.fromJson(ACache.get(this).getAsString("bank_list"), listCardBeanType);

		getBankList();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_card_name:
		case R.id.z_card_name:
			// 选择银行卡
			if (listCardMap != null && listCardMap.size() > 0) {
				showBankList(CardAddUI.this, CardAddUI.this);
			}
			break;
		case R.id.tv_submit:
			// 提交
			card_khr_name = et_card_khr_name.getText().toString();
			card_khr_num = et_card_khr_num.getText().toString();
			String card_name = tv_card_name.getText().toString();
			card_num = et_card_num.getText().toString();

			if (Utils.isEmity(card_khr_name)) {
				MyApplication.getInstance().showToast("请输入真实姓名");
				return;
			}
			if (Utils.isEmity(card_khr_num)) {
				MyApplication.getInstance().showToast("请输入身份证号");
				return;
			}
			if (Utils.isEmity(card_name) || "选择银行".equals(card_name)) {
				MyApplication.getInstance().showToast("请选择开户银行");
				return;
			}
			if (Utils.isEmity(card_num)) {
				MyApplication.getInstance().showToast("请输入银行卡号");
				return;
			}
			if (card_num.indexOf(kongge) != -1) {
				card_num = card_num.replaceAll(String.valueOf(kongge), "");
			}
			addCard();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取银行
	 */
	private void getBankList() {
		String url = HttpUtil.getUrl("/withdraw/getBankList");
		Map<String, String> params = new HashMap<String, String>();
		HttpUtil.post(this, url, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CardAddJson.getBankList(response.toString());
				if (HttpUtil.isSuccess(CardAddUI.this, returnBean.getCode())) {
					listCardMap = returnBean.getListObject();
					// 数据缓存
					String json = gson.toJson(listCardMap);
					ACache.get(CardAddUI.this).put("bank_list", json);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 添加银行卡
	 */
	private void addCard() {
		String url = HttpUtil.getUrl("/withdraw/addBank");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("bank_id", bank_id);
		map.put("bank_num", card_num);
		map.put("user_name", card_khr_name);
		map.put("identity_card", card_khr_num);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CardAddJson.analysis(response.toString());
				if (HttpUtil.isSuccess(CardAddUI.this, returnBean.getCode())) {
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
		bank_id = listCardMap.get(position).get("id");
		tv_card_name.setText(listCardMap.get(position).get("name"));
	}

	/**
	 * 选择银行显示框
	 * 
	 * @param context
	 * @return
	 */
	public void showBankList(Context context, OnItemClickListener onItemClickListener) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.card_add_1_dialog_bank, null);

		ListView lv_bank = (ListView) view.findViewById(R.id.lv_dialog_bank);
		lv_bank.setOnItemClickListener(onItemClickListener);

		CardAddDialogAdapter adapter = new CardAddDialogAdapter(context, listCardMap);
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

	TextWatcher firstTextWatcher = new TextWatcher() {
		// 改变之前text长度
		private int beforeTextLength = 0;
		// 改变之前的文字
		private CharSequence beforeChar;
		// 改变之后text长度
		private int onTextLength = 0;
		// 是否改变空格或光标
		private boolean isChanged = false;
		// 记录光标的位置
		private int location = 0;
		private char[] tempChar;
		private StringBuffer buffer = new StringBuffer();
		// 已有空格数量
		private int konggeNumberB = 0;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onTextLength = s.length();
			buffer.append(s.toString());
			if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
				isChanged = false;
				return;
			}
			isChanged = true;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			beforeTextLength = s.length();
			if (buffer.length() > 0) {
				buffer.delete(0, buffer.length());
			}
			konggeNumberB = 0;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ' ') {
					konggeNumberB++;
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (isChanged) {
				location = et_card_num.getSelectionEnd();
				int index = 0;
				while (index < buffer.length()) {
					if (buffer.charAt(index) == kongge) {
						buffer.deleteCharAt(index);
					} else {
						index++;
					}
				}
				index = 0;
				int konggeNumberC = 0;
				while (index < buffer.length()) {
					if ((index == 4 || index == 9 || index == 14 || index == 19)) {
						buffer.insert(index, kongge);
						konggeNumberC++;
					}
					index++;
				}

				if (konggeNumberC > konggeNumberB) {
					location += (konggeNumberC - konggeNumberB);
				}

				tempChar = new char[buffer.length()];
				buffer.getChars(0, buffer.length(), tempChar, 0);
				String str = buffer.toString();
				if (location > str.length()) {
					location = str.length();
				} else if (location < 0) {
					location = 0;
				}

				et_card_num.setText(str);
				Editable etable = et_card_num.getText();
				Selection.setSelection(etable, location);
				isChanged = false;
			}
		}
	};
	private Gson gson;
}
