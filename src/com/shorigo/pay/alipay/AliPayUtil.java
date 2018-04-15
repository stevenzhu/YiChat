package com.shorigo.pay.alipay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.shorigo.MyApplication;
import com.shorigo.utils.UrlConstants;

/**
 * 支付宝支付工具类
 * 
 * @author peidongxu
 * 
 */
public class AliPayUtil {
	// APP ID
	public static final String APPID = "";
	// 商户PID
	public static final String PARTNER = "";
	// 商户收款账号
	public static final String SELLER = "";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";
	// 商户私钥
	public static final String RSA_PRIVATE = "";
	// 商户私钥，pkcs8格式
	public static final String RSA2_PRIVATE = "";
	// 回调地址
	public static final String CALLBACK_URL = UrlConstants.SERVICE_HOST_URL + "/notify/index";

	private static final int SDK_PAY_FLAG = 1;

	private Activity activity;
	private AliPayCallBack aliPayCallBack;

	public AliPayUtil(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 设置回调函数
	 */
	public void setCallBack(AliPayCallBack aliPayCallBack) {
		this.aliPayCallBack = aliPayCallBack;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AliPayUtil.SDK_PAY_FLAG:
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					aliPayCallBack.alipayCallBack(true);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						aliPayCallBack.alipayCallBack(false);
						MyApplication.getInstance().showToast("支付结果确认中");
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						aliPayCallBack.alipayCallBack(false);
						MyApplication.getInstance().showToast("支付失败");
					}
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 调用SDK支付
	 * 
	 * @param name
	 *            商品名称
	 * @param out_trade_no
	 *            订单号
	 * @param pay_money
	 *            支付金额
	 * @param body
	 *            商品描述
	 * @param callback_url
	 *            服务器回调地址
	 */
	public void pay(final String orderInfo) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(activity);
				String decode = new String(Base64.decode(orderInfo));
				Map<String, String> result = alipay.payV2(decode, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 调用SDK支付
	 * 
	 * @param name
	 *            商品名称
	 * @param out_trade_no
	 *            订单号
	 * @param pay_money
	 *            支付金额
	 * @param body
	 *            商品描述
	 * @param callback_url
	 *            服务器回调地址
	 */
	public void pay(String name, String out_trade_no, String pay_money, String body) {
		String biz_content = getBiz_content(name, out_trade_no, pay_money, body);
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, biz_content, rsa2, CALLBACK_URL);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		final String orderInfo = orderParam + "&" + sign;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(activity);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getBiz_content(String subject, String out_trade_no, String total_amount, String body) {
		String orderInfo = "{";
		// 支付宝交易的超时时间
		orderInfo += "\"timeout_express\":" + "\"" + "30m" + "\"";

		// 销售产品码，商家和支付宝签约的产品码，为固定值
		orderInfo += ",\"product_code\":" + "\"" + "QUICK_MSECURITY_PAY" + "\"";

		// 商品金额
		orderInfo += ",\"total_amount\":" + "\"" + total_amount + "\"";

		// 商品名称
		orderInfo += ",\"subject\":" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += ",\"body\":" + "\"" + body + "\"";

		// 商户网站唯一订单号
		orderInfo += ",\"out_trade_no\":" + "\"" + out_trade_no + "\"";

		orderInfo += "}";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE, true);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA2\"";
	}

	public interface AliPayCallBack {
		public void alipayCallBack(boolean isSucess);
	}

}
