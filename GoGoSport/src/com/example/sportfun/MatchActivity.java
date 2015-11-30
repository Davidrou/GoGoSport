package com.example.sportfun;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.geminno.entity.FastMatch;
import com.geminno.entity.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.utils.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MatchActivity extends Activity implements OnClickListener {
	// 足球 ，篮球 ， 网球,羽毛球,乒乓,,桌球
	private LinearLayout football, basketball, tennis, badminton, pingpong,
			zhuoqiu;
	private RequestQueue rq;
	private Context context;
	private String last_longitudu = "0.0";
	private String last_latitude = "0.0";

	private long l_time = System.currentTimeMillis();
	private int sporttype_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_match);

		rq = Volley.newRequestQueue(getApplicationContext());
		initView();
		setListener();

	}

	private void initView() {
		badminton = (LinearLayout) this.findViewById(R.id.i_yumaoqiu);
		football = (LinearLayout) this.findViewById(R.id.i_zuqiu);
		basketball = (LinearLayout) this.findViewById(R.id.i_lanqiu);
		tennis = (LinearLayout) this.findViewById(R.id.i_wangqiu);
		zhuoqiu = (LinearLayout) findViewById(R.id.i_zhuoqiu);
		pingpong = (LinearLayout) findViewById(R.id.i_pingpongqiu);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		badminton.setOnClickListener(this);
		football.setOnClickListener(this);
		basketball.setOnClickListener(this);
		zhuoqiu.setOnClickListener(this);
		tennis.setOnClickListener(this);
		pingpong.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.i_yumaoqiu:
			sporttype_id = 4;
			break;
		case R.id.i_zuqiu:
			sporttype_id = 1;
			break;
		case R.id.i_lanqiu:
			sporttype_id = 2;
			break;
		case R.id.i_wangqiu:
			sporttype_id = 3;
			break;
		case R.id.i_zhuoqiu:
			sporttype_id = 6;
			break;
		case R.id.i_pingpongqiu:
			sporttype_id = 4;
			break;
		}
		sendFastMatch();

	}

	private void sendFastMatch() {
		int user_id = getSharedPreferences("user", MODE_PRIVATE).getInt(
				"user_id", 0);

		// 封装对象
		FastMatch fmatch = new FastMatch(user_id, last_longitudu,
				last_latitude, l_time, sporttype_id);
		// gson解析
		final Gson gson = new Gson();
		String json = gson.toJson(fmatch);
		Log.i("json", json);
		// 发出一条HTTP请求，创建一个StringRequest对象

		String url = URLPropertiesUtil.getProperties(context).getProperty("u")
				+ "AndroidMatch";
		Log.i("速配", "正在连接!" + url);

		StringRequest sr = new StringRequest(url + "?fmatch=" + json,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("UserInfo对象", "返回的 结果" + response);
						Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
						Type type = new TypeToken<ArrayList<UserInfo>>() {
						}.getType();
						ArrayList<UserInfo> userlist = g.fromJson(response,
								type);
						Intent intent = new Intent(MatchActivity.this,
								MatchResultActivity.class);
						intent.putExtra("user", userlist);
						startActivity(intent);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("UserInfo对象", "失败的 结果" + error.toString());
					}
				});
		// 发送请求，
		rq.add(sr);
	}

	public void back(View v){
		finish();
	}
}
