package net.sourceforge.simcpux;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.shorigo.utils.LogUtils;
import com.shorigo.utils.UrlConstants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * 微信支付工具类
 * 
 * @author peidongxu
 * 
 */
public class WXPayUtil {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	// APP_ID
	public static final String APP_ID = "";
	// 商户号
	public static final String MCH_ID = "";
	// API密钥，在商户平台设置
	public static final String API_KEY = "";
	// 回调地址
	public static final String CALLBACK_URL = UrlConstants.SERVICE_HOST_URL + "/notify/wxpay";

	private Activity activity;
	private String name;
	private String out_trade_no;
	private String pay_money;
	private String callback_url;
	private IWXAPI msgApi;

	public WXPayUtil(Activity activity, IWXAPI msgApi) {
		this.activity = activity;
		this.msgApi = msgApi;
	}

	public static WXPayCallBack wxPayCallBack;

	public void setWxPayCallBack(WXPayCallBack wxPayCallBack) {
		this.wxPayCallBack = wxPayCallBack;
	}

	PayReq req;
	TextView show;
	Map<String, String> resultunifiedorder;
	StringBuffer sb;

	@SuppressLint("DefaultLocale")
	public void pay(String name, String out_trade_no, String pay_money, String callback_url) {
		this.name = name;
		this.out_trade_no = out_trade_no;
		int p = (int) Double.parseDouble(pay_money) * 100;
		this.pay_money = String.valueOf(p);
		this.callback_url = CALLBACK_URL;
		req = new PayReq();
		sb = new StringBuffer();

		// 生成prepay_id
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		LogUtils.i("packageSign", packageSign);
	}

	/**
	 * 生成签名
	 */
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(activity, "提示", "正在获取预支付订单...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			Log.e("orion", content);
			Map<String, String> xml = decodeXml(content);

			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", APP_ID));
			packageParams.add(new BasicNameValuePair("body", name));
			packageParams.add(new BasicNameValuePair("mch_id", MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", callback_url));
			// packageParams.add(new
			// BasicNameValuePair("out_trade_no",genOutTradNo()));
			packageParams.add(new BasicNameValuePair("out_trade_no", out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			// packageParams.add(new BasicNameValuePair("total_fee", "1"));
			packageParams.add(new BasicNameValuePair("total_fee", pay_money));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	private void genPayReq() {

		req.appId = APP_ID;
		req.partnerId = MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		sendPayReq();

	}

	private void sendPayReq() {
		msgApi.registerApp(APP_ID);
		msgApi.sendReq(req);
	}

	public interface WXPayCallBack {
		public void wxPayCallBack(boolean isSuccess);
	}

}
