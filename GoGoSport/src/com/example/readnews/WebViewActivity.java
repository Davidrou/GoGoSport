package com.example.readnews;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sportfun.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.geminno.entity.Collection;

/**
 * 
 * @author 廖汉文
 *
 */

public class WebViewActivity extends Activity implements OnClickListener {

	MySQLiteOpenHelper mysqlite;
	String URL = "http://192.168.191.1:8080/db/servlet/AndroidCollect";
	String json;
	Button btn;
	ImageButton ib;
	private String news_url = null;
	private String news_picUrl = null;
	private String news_title = null;
	private WebView wv;
	private ProgressDialog dialog;
	private String news_URL;
	int flag = 0;// 定义标记变量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		// getActionBar().show();
		news_url = (String) getIntent().getExtras().get("news_url");
		news_picUrl = (String) getIntent().getExtras().get("news_picUrl");
		news_title = (String) getIntent().getExtras().get("news_title");
		mysqlite = new MySQLiteOpenHelper(this, "sportfun.db");

		btn = (Button) this.findViewById(R.id.collect);
		btn.setOnClickListener(this);
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
				if (newProgress == 89) {
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

	private void insertData(SQLiteDatabase db, String news_url, String news_picUrl, String news_title) {
		db.execSQL("insert into collection values(null, ?, ?, ?)", new String[] { news_url, news_picUrl, news_title });
	}

	private void deleteData(SQLiteDatabase db, String news_url) {
		db.execSQL("delete from collection where news_url=?", new String[] { news_url });
	}

	@Override
	public void onClick(View arg0) {
		// 获取前一页面传过来的值
		news_url = (String) getIntent().getExtras().get("news_url");
		news_picUrl = (String) getIntent().getExtras().get("news_picUrl");
		news_title = (String) getIntent().getExtras().get("news_title");
		// 用游标查询本地SQLite的新闻
		final Cursor cursor = mysqlite.getReadableDatabase().rawQuery("select * from collection", null);
		while (cursor.moveToNext()) {
			news_URL = cursor.getString(1);
		}
		// 点击一次收藏至本地
		if (flag == 0) {
			// 存到本地SQLite
			btn.setBackgroundResource(R.drawable.collect1);
			insertData(mysqlite.getReadableDatabase(), news_url, news_picUrl, news_title);
		} else if (flag == 1) { // 再次点击删除收藏
			btn.setBackgroundResource(R.drawable.collect0);
			deleteData(mysqlite.getWritableDatabase(), news_url);
		}
		flag = (flag + 1) % 2;// 循环按钮的两个点击事件
		// 定义收藏时间为系统当前时间(毫秒)
		Long collect_time = System.currentTimeMillis();
		Collection collection = new Collection(2, news_url, collect_time);
		Gson gson = new Gson();
		json = gson.toJson(collection);
		// 收藏新闻的url到服务器
		RequestQueue rq = Volley.newRequestQueue(WebViewActivity.this);
		StringRequest sr = new StringRequest(URL + "?a=" + json, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Toast.makeText(WebViewActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
//				Toast.makeText(WebViewActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
		rq.add(sr);
	}

	public void back(View v) {
		finish();
	}

}
