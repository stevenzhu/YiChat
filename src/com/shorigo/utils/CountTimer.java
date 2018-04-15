package com.shorigo.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 验证码计时器
 * 
 * @author peidongxu
 * 
 */
public class CountTimer extends CountDownTimer {
	TextView tv_get_code;

	/**
	 * 
	 * @param tv_get_code
	 * @param millisInFuture
	 *            总的时间
	 * @param countDownInterval
	 *            间隔时间
	 */
	public CountTimer(TextView tv_get_code, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		this.tv_get_code = tv_get_code;
	}

	@Override
	public void onFinish() {
		tv_get_code.setEnabled(true);
		tv_get_code.setText("发送验证码");
	}

	@Override
	public void onTick(long millisUntilFinished) {
		tv_get_code.setEnabled(false);
		tv_get_code.setText("(" + millisUntilFinished / 1000 + ")秒");
	}
}
