package chat.chat_1.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.view.MyListView;
import com.shorigo.yichat.R;

/**
 * 红包详情
 * 
 * @author peidongxu
 * 
 */
public class RedPickageDetailUI extends BaseUI {
	private String order_id;
	private ImageView iv_avatar;
	private TextView tv_name;
	private TextView tv_pin;
	private TextView tv_remark;
	private TextView tv_money;
	private LinearLayout z_money;
	private TextView tv_tip;

	private List<Map<String, String>> listMap;
	private MyListView lv_list;
	private RedPickageDetailAdapter adapter;

	private RedPickageBean bean;
	private ProgressDialog progressDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.ease_activity_redpickage_detail);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_pin = (TextView) findViewById(R.id.tv_pin);
		tv_remark = (TextView) findViewById(R.id.tv_remark);
		z_money = (LinearLayout) findViewById(R.id.z_money);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_tip = (TextView) findViewById(R.id.tv_tip);

		lv_list = (MyListView) findViewById(R.id.lv_list);
	}

	@Override
	protected void prepareData() {
		setTitle("红包详情");

		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");

		bean = new RedPickageBean();

		adapter = new RedPickageDetailAdapter(this, listMap, bean.getMax_sn());
		lv_list.setAdapter(adapter);

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage("正在加载中...");
		progressDialog.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getDetail();
			}
		}, 1500);
	}

	@Override
	protected void onMyClick(View v) {
	}

	/**
	 * 获取红包详情
	 */
	private void getDetail() {
		String url = HttpUtil.getUrl("/redpacket/info");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("id", order_id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = RedPickageJson.getDetail(response.toString());
				if (HttpUtil.isSuccess(RedPickageDetailUI.this, returnBean.getCode())) {
					bean = (RedPickageBean) returnBean.getObject();
					setValue();
				}
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		});
	}

	/**
	 * 页面赋值
	 */
	private void setValue() {
		if (bean == null) {
			return;
		}

		BitmapHelp.loadImg(this, iv_avatar, bean.getAdd_useravatar());
		tv_name.setText(bean.getAdd_user_nick());
		tv_remark.setText(bean.getMessage());
		// 红包类型 1 普通红包 2 普通群红包 3 随机群红包
		String type = bean.getType();
		if ("3".equals(type)) {
			tv_pin.setVisibility(View.VISIBLE);
		} else {
			tv_pin.setVisibility(View.GONE);
		}
		// 本人领取状态 0未领取 1已领取
		String receive_status = bean.getReceive_status();
		if ("1".equals(receive_status)) {
			z_money.setVisibility(View.VISIBLE);
			tv_money.setText(bean.getReceive_money());
		} else {
			z_money.setVisibility(View.GONE);
		}
		tv_tip.setText(bean.getNum() + "个红包，共" + bean.getMoney() + "元");

		listMap = bean.getListReceive();
		adapter.setData(listMap, bean.getMax_sn());
	}

	@Override
	protected void back() {
		finish();
	}

}
