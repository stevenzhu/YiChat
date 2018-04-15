package com.shorigo.zxing;

import android.content.Intent;
import android.view.View;
import android.webkit.WebSettings;

import com.shorigo.BaseUI;
import com.shorigo.utils.Utils;
import com.shorigo.view.ProgressWebView;
import com.shorigo.yichat.R;

public class CaptureResultUI extends BaseUI {

	private ProgressWebView webView;
	private String content;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.zxing_1_result);
	}

	@Override
	protected void findView_AddListener() {
		webView = (ProgressWebView) findViewById(R.id.webview);

	}

	@Override
	protected void prepareData() {
		setTitle("扫描结果");

		// 接收参数
		Intent intent = getIntent();
		content = intent.getStringExtra("content");
		setData();
	}

	private void setData() {
		if (!Utils.isEmity(content)) {
			// /------------加载URL
			// 设置WebView属性，能够执行Javascript脚本
			webView.getSettings().setJavaScriptEnabled(true);
			// 加载服务器上的页面
			webView.loadData(content, "text/html", null);
			// 得到webview设置
			WebSettings webSettings = webView.getSettings();
			// 允许使用javascript
			webSettings.setJavaScriptEnabled(true);
		}
	}

	@Override
	protected void back() {
		finish();
	}

	@Override
	protected void onMyClick(View v) {
		// TODO Auto-generated method stub

	}

}
