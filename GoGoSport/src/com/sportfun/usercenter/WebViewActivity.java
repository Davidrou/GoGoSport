package com.sportfun.usercenter;

import com.example.sportfun.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

/**
 * 
 * @author 廖汉文
 *
 */

public class WebViewActivity extends Activity {

	private String news_url = null;
	private WebView wv;
	private ProgressDialog dialog;
	private ImageButton ib;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_no_collect);
		ib = (ImageButton) this.findViewById(R.id.top_left);
		news_url = (String) getIntent().getExtras().get("news_url");
		init();
	}

	private void init() {
		news_url = (String) getIntent().getExtras().get("news_url");
		wv = (WebView) this.findViewById(R.id.webView);
		// WebView加载web资源
		wv.loadUrl(news_url);
		// WebViewClient帮助WebView去处理一些页面控制和请求通知
		wv.setWebViewClient(new WebViewClient() {

			// 覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebVIew中打开
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// 返回值是true的时候控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器去打开
				view.loadUrl(url);
				return true;
			}
		});

		// 启用支持JavaScript
		WebSettings settings = wv.getSettings();
		settings.setJavaScriptEnabled(true);
		// WebView加载页面优先使用缓存加载
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		wv.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				// newProgress 1-100之间的整数
				if (newProgress == 100) {
					// 网页加载完毕，关闭ProgressDialog
					closeDialog();
				} else {
					// 网页正在加载,打开ProgressDialog
					openDialog(newProgress);
				}
			}

			private void closeDialog() {
				// TODO Auto-generated method stub
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}
			}

			private void openDialog(int newProgress) {
				// TODO Auto-generated method stub
				if (dialog == null) {
					dialog = new ProgressDialog(WebViewActivity.this);
					dialog.setTitle("正在加载");
					dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					dialog.setProgress(newProgress);
					dialog.show();

				} else {
					dialog.setProgress(newProgress);
				}

			}
		});
		
	}

	// 改写物理按键返回的逻辑
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (wv.canGoBack()) {
	// wv.goBack();// 返回上一页面
	// return true;
	// } else {
	// System.exit(0);// 退出程序
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	public void back(View v) {
		finish();
	}

}
