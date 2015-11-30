package com.example.sportfun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.easemob.exceptions.EaseMobException;
import com.geminno.entity.HoldSport;
import com.geminno.entity.UserInfo;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gogosport.activity.BaseActivity;
import com.google.gson.Gson;
import com.sportfun.usercenter.MySportActivity;
import com.umeng.socialize.utils.Log;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddSportActivity extends BaseActivity {

	RequestQueue rq;
	private ProgressDialog progressDialog;
	LinearLayout btn_pickPlace;
	Button submit;// 发起
	private ImageButton back;
	EditText sportName, sportContent;// 活动标题，内容
	TextView tv_address;// 地址
	private Spinner spinner;// 活动类型下拉列表
	TextView sportNum;// 活动人数
	Context context;
	// PostListView postListView;
	String targetServlet = "AndroidHoldSport";
	UserInfo user;// 发起人（用户）
	// 经纬度
	private String latitude;
	private String longitude;
	private SlideDateTimeListener listener;

	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private LinearLayout etStartTime; // 活动开始时间容器
	private Button btStartTime;// 活动开始时间
	private LinearLayout etEndTime;//活动结束时间容器
	private Button btEndTime;// 活动结束时间
	String stStartTime;
	String stEndTime;

	public String arr[] = { "选择类型", "足球", "篮球", "网球", "羽毛球", "乒乒球", "台球" };
	int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_sport);
		rq = Volley.newRequestQueue(getApplicationContext());
		initView();
		setListener();

	}

	public void setListener() {
		// 开始时间点击事件
		etStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.et_start) {
					listener = new SlideDateTimeListener() {

						@Override
						public void onDateTimeSet(Date date) {
							stStartTime = formater.format(date);
							btStartTime.setText(stStartTime);
						}

						@Override
						public void onDateTimeCancel() {
						}

					};

					new SlideDateTimePicker.Builder(getSupportFragmentManager())
							.setListener(listener).setInitialDate(new Date())
							.setIs24HourTime(true).build().show();
				}

			}
		});
		// 结束时间点击事件
		etEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.et_end) {
					listener = new SlideDateTimeListener() {

						@Override
						public void onDateTimeSet(Date date) {
							stEndTime = formater.format(date);
							btEndTime.setText(stEndTime);
						}

						@Override
						public void onDateTimeCancel() {
						}
					};

					new SlideDateTimePicker.Builder(getSupportFragmentManager())
							.setListener(listener).setInitialDate(new Date())
							.setIs24HourTime(true).build().show();
				}

			}
		});
		// 地点点击事件
		btn_pickPlace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 根据活动类型搜索附近场地
				if (!"选择类型".equals((String) spinner.getSelectedItem())) {
					Intent intent = new Intent(AddSportActivity.this,
							PickPlaceActivity.class);
					intent.putExtra("sporttype",
							(String) spinner.getSelectedItem());
					startActivityForResult(intent, 0);
				} else {
					Toast.makeText(context, "活动类型不能为空", Toast.LENGTH_LONG)
							.show();
					return;
				}
			}
		});
		// 发起活动点击事件
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (("").equals(sportName.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("输入标题").setMessage("活动标题不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				}
				if (("").equals(sportContent.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("输入内容").setMessage("活动内容不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				}
				if (("").equals(btStartTime.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("开始时间").setMessage("开始时间不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				}
				if (("").equals(btEndTime.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("结束时间").setMessage("结束时间不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				}
				if (("").equals(sportNum.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("限制人数").setMessage("限制人数不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				}
				if (("").equals(tv_address.getText().toString())) {
					new AlertDialog.Builder(AddSportActivity.this)
							.setTitle("输入地址").setMessage("地址不能为空")
							.setPositiveButton("确认", null).create().show();
					return;
				} else {

					// 活动类型的转换
					count = spinner.getSelectedItemPosition();

					// 发帖时间
					long l_date = System.currentTimeMillis();

					// 开始和结束时间
					long l_starttime = 0;
					long l_endtime = 0;
					try {
						l_starttime = formater.parse(
								btStartTime.getText().toString()).getTime();
						l_endtime = formater.parse(
								btEndTime.getText().toString()).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 活动标题
					String title = sportName.getText().toString();
					// 活动内容
					String content = sportContent.getText().toString();
					// 场地经纬度
					SharedPreferences share = getSharedPreferences("location",
							MODE_PRIVATE);
					latitude = share.getString("latitude", "0.0");
					longitude = share.getString("longitude", "0.0");
					// 人数限制
					int number = Integer.parseInt((sportNum.getText())
							.toString());
					// 用户id
					int user_id = getSharedPreferences("user", MODE_PRIVATE)
							.getInt("user_id", 0);
					// int userId = getSharedPreferences("user", mode)
					HoldSport sport = new HoldSport(count, title, content,
							l_date, l_starttime, l_endtime, number, user_id,
							latitude, longitude);
					// gson解析
					Gson gson = new Gson();
					final String json = gson.toJson(sport);

					Log.i("sport对象", json);

					// 获取url
					String urlCommen = URLPropertiesUtil.getProperties(context)
							.getProperty("u") + targetServlet;
					StringRequest sr = new StringRequest(Method.POST,
							urlCommen, new Listener<String>() {

								@Override
								public void onResponse(String response) {
									// TODO Auto-generated method stub
									Log.i("发起活动", "返回的 结果" + response);
									Toast.makeText(AddSportActivity.this,
											"发起成功,正在创建群聊", Toast.LENGTH_SHORT)
											.show();

									createPublicGroupChat();

								}

							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub
									Log.i("发起活动", "失败的 结果" + error.toString());
								}
							}) {
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();
							map.put("sport", json);
							return map;
						}
					};
					rq.add(sr);

				}
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	/*
	 * 控件获取以及点击事件
	 */
	private void initView() {

		context = this;
		submit = (Button) findViewById(R.id.top_right);
		back = (ImageButton) findViewById(R.id.top_left);
		sportName = (EditText) findViewById(R.id.sport_name);

		etStartTime = (LinearLayout) this.findViewById(R.id.et_start);
		btStartTime = (Button) this.findViewById(R.id.et_start_time);
		etEndTime = (LinearLayout) this.findViewById(R.id.et_end);
		btEndTime = (Button) this.findViewById(R.id.et_end_time);
		// etStartTime.setOnTouchListener(this);

		spinner = (Spinner) findViewById(R.id.sport_type);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		spinner.setAdapter(arrayAdapter);
		sportNum = (TextView) findViewById(R.id.sport_num);
		tv_address = (TextView) this.findViewById(R.id.sport_address);
		// iv=(ImageView) findViewById(R.id.di_tu);
		btn_pickPlace = (LinearLayout) findViewById(R.id.pickplace);

		sportContent = (EditText) findViewById(R.id.sport_content);
		// 发表点击事件
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0 && requestCode == 0) {
			Bundle b = data.getExtras();
			latitude = b.getString("latitude");
			longitude = b.getString("longitude");
			tv_address.setText(b.getString("address"));
			Log.i("返回结果", latitude + longitude + "");
		}
	}

	private void createPublicGroupChat() {

		// 新建群组
		String st1 = "程序员正在创建群组,请稍候...";
		final String st2 = "程序员们生病了,暂时无法创建群聊";
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String groupName = sportName.getText().toString().trim();
				String desc = sportContent.getText().toString();
				String[] members = new String[] {};
				int maxNumber = Integer.parseInt((sportNum.getText())
						.toString());
				Log.i("待创建群信息", groupName + desc + members.length);
				try {
					// 创建公开群，此种方式创建的群，可以自由加入
					EMGroupManager.getInstance().createPublicGroup(groupName,
							desc, members, false, maxNumber);

					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							finish();
							startActivity(new Intent(AddSportActivity.this,
									MySportActivity.class));
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(AddSportActivity.this,
									st2 + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}

			}
		}).start();
	}
}
