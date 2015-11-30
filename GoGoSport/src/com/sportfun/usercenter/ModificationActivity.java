package com.sportfun.usercenter;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.geminno.entity.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 编辑资料
 */
public class ModificationActivity extends Activity {

	private EditText eUserName, eAddress, eBarthday, eSig;// 个性签名
	ProgressBar progressbar;
	RadioGroup groupSex;
	private RadioButton male;
	private RadioButton female;
	private RadioButton secret;
	// RadioButton buttonSex;
	private Button bDetermine;
	RequestQueue rq;
	StringRequest st;
	String url;
	UserInfo user;// 接收到的对象
	TextView show;
	String sex;

	// 获取一个日历对象
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
	// DateFormat fmtDate = DateFormat.getDateInstance();
	DateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// 修改日历控件的年，月，日
			// 这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			eBarthday.setText(fmtDate.format(dateAndTime.getTime()));

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modification);
		rq = Volley.newRequestQueue(getApplicationContext());

		Properties properties = URLPropertiesUtil.getProperties(this);
		url = properties.getProperty("u") + "AndroidUpdateUser";

		initView();
		// 接收个人中心传来的数据
		Intent intent = this.getIntent();
		user = (UserInfo) intent.getSerializableExtra("user");
		eUserName.setText(user.getUser_name());
		eAddress.setText(user.getUser_position());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(user.getBirthday());
		eBarthday.setText(s);
		eSig.setText(user.getSignature());

		if (TextUtils.isEmpty(user.getSex())) {
			secret.setChecked(true);
		} else if ("男".equals(user.getSex().toString().trim())) {
			male.setChecked(true);
		} else if ("女".equals(user.getSex().toString().trim())) {
			female.setChecked(true);
		} else {
			secret.setChecked(true);
		}

		// 设置出生日期点击事件 ///////////////////
		eBarthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(ModificationActivity.this, d, dateAndTime
						.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH),
						dateAndTime.get(Calendar.DAY_OF_MONTH)).show();

			}
		});
		// 性别点击事件 ///////////////////
		groupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radiobuttonId = group.getCheckedRadioButtonId();
				RadioButton buttonSex = (RadioButton) ModificationActivity.this
						.findViewById(radiobuttonId);
				sex = (String) buttonSex.getText();

			}
		});
		// 确认点击事件 ///////////////////
		bDetermine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressbar.setVisibility(View.VISIBLE);
				String user_name = eUserName.getText().toString();
				String signature = eSig.getText().toString();
				String user_position = eAddress.getText().toString();
				String birth = eBarthday.getText().toString();

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date birthday = null;
				try {
					birthday = new Date(df.parse(birth).getTime());

				} catch (ParseException e) {

					e.printStackTrace();
				}

				String account = user.getAccount();
				String pwd = user.getUserpwd();
				String picPath = user.getPhoto();

				// 封装用户编辑的信息对象 ////////////////
				UserInfo update = new UserInfo(account, pwd, picPath,
						user_name, sex, birthday, signature, user_position);
				// 解析
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd")
						.create();
				final String json = gson.toJson(update);
				System.out.println(json);
				Log.i("update请求对象", json);

				st = new StringRequest(Method.POST, url,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {
								progressbar.setVisibility(View.INVISIBLE);
								// 判断数据库是否更新成功，如果成功就将这边的数据传到infromation页面
								if (response.equals("1")) {
									Intent intent = new Intent();
									// eAddress,eBarthday,eSig
									intent.putExtra("name1", eUserName
											.getText().toString());
									intent.putExtra("name2", sex);
									intent.putExtra("name3", eAddress.getText()
											.toString());
									intent.putExtra("name4", eBarthday
											.getText().toString());
									intent.putExtra("name5", eSig.getText()
											.toString());
									setResult(4, intent);
									finish();
								} else {
									Toast.makeText(getApplicationContext(),
											"更新失败", Toast.LENGTH_SHORT).show();
								}

							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Log.i("update对象", "失败的结果" + error.toString());

							}
						}) {

					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("update", json);
						return map;
					}

				};
				rq.add(st);
				// /////////////////////////

			}
		});

	}

	private void initView() {

		bDetermine = (Button) this.findViewById(R.id.determine);
		eUserName = (EditText) this.findViewById(R.id.user_name);
		groupSex = (RadioGroup) this.findViewById(R.id.rg);

		male = (RadioButton) this.findViewById(R.id.male);
		female = (RadioButton) this.findViewById(R.id.female);
		secret = (RadioButton) this.findViewById(R.id.secret);

		eAddress = (EditText) this.findViewById(R.id.user_address);
		eBarthday = (EditText) this.findViewById(R.id.user_barthday);
		eSig = (EditText) this.findViewById(R.id.user_sig);
		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	public void back(View v) {
		finish();
	}

}
