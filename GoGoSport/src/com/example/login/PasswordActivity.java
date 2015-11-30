package com.example.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.example.total.TotalActivity;

public class PasswordActivity extends Activity {

	EditText pwd1;
	EditText pwd2;
	Button logButton;
	Properties properties = URLPropertiesUtil.getProperties(this);
	String u = properties.getProperty("u");
	String url = u+"AndroidTelLogin";
	String tel ;
	String pwd;//密码
	Date birthday;
	String position2=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_password);

		pwd1=(EditText) this.findViewById(R.id.etMima);
		pwd2=(EditText) this.findViewById(R.id.etMima2);
		logButton=(Button) this.findViewById(R.id.denglu);
		logButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			pwd=pwd1.getEditableText().toString();
			String s2=pwd2.getEditableText().toString();
			birthday=new Date(System.currentTimeMillis());
			SharedPreferences sharedPreferences = getSharedPreferences("userInfo", 
					Activity.MODE_PRIVATE); 
					tel = sharedPreferences.getString("phone", ""); 
		
		String position=getSharedPreferences("location", MODE_PRIVATE).getString("address", "默認位置");
		
		try {
			position2=URLEncoder.encode(position,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			if(pwd.equals(s2)){
				
	//将账号和密码存入数据库（在此之前已避免用户已存在问题）
				// 1.初始化RequestQueue对象
				RequestQueue requestQueue = Volley
						.newRequestQueue(PasswordActivity.this);
				// 2. 发送StringRequest请求
			
				StringRequest stringRequsset = new StringRequest(Method.POST,url,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {

								
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {

							
							}
						}){

							@Override
							protected Map<String, String> getParams()
									throws AuthFailureError {
								Map<String, String> map=new HashMap<String, String>();
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
						        String  birthday2= df.format(birthday);
						        String picPath="http://localhost:8080/db/images/default_avator.png";
								map.put("tel", tel);
								map.put("pwd", pwd);
								map.put("picPath", picPath);
								map.put("birthday", birthday2);
								map.put("position", position2);
								return map;
							}
					
				};
				// 3.将StringRequest加入到RequestQueue当中
				requestQueue.add(stringRequsset);
			
				startActivity(new Intent(PasswordActivity.this,LoginActivity.class));
				finish();	
				
			}else{
				pwd2.setText("");
				Toast toast = Toast.makeText(getApplicationContext(),
						"密码确认错误，请重新输入", Toast.LENGTH_LONG);
					   toast.setGravity(Gravity.CENTER, 0, 0);
					   toast.show();
			}
			}
		});
		
	}


}
