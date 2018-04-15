package com.shorigo.yichat.wxapi;

import net.sourceforge.simcpux.WXPayUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shorigo.MyApplication;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信支付回调
 * 
 * @author peidongxu
 * 
 */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, WXPayUtil.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (0 == resp.errCode) {
			WXPayUtil.wxPayCallBack.wxPayCallBack(true);
			finish();
		} else if (-1 == resp.errCode) {
			MyApplication.getInstance().showToast("支付失败");
			finish();
		} else if (-2 == resp.errCode) {
			MyApplication.getInstance().showToast("支付已取消");
			finish();
		}

		// if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.app_tip);
		// builder.setMessage(getString(R.string.pay_result_callback_msg,
		// resp.errStr +";code=" + String.valueOf(resp.errCode)));
		// builder.show();
		// }
	}

}