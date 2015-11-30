package com.example.total;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.geminno.entity.HoldSport;
import com.geminno.entity.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sport.chat.ChatActivity;

/*
 * @auther 付丽娟
 * @auther ShawnLi
 */
public class SportDetailActivity extends Activity {



	Button joining;
	TextView myname;
	RequestQueue requestQueue;
	TextView myjnm;
	ImageView myphoto;
	LinearLayout layout;
	int limit;// 和加入活动人数拼接在控件myjnm中
	int joinnum;// 最初查找到的加入活动人数
	int userid;// 加入退出活动使用
	int sportid;// 加入退出活动使用
	UserInfo user = null;// 接收到的user
	Properties properties = URLPropertiesUtil.getProperties(this);
	String u = properties.getProperty("u");
	protected String nt;
	private HoldSport holdsport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sport_detail);
		// 接收intent数据
		Intent intent = getIntent();
		// 活动的属性(我发起的/我加入的/还是我没加入的,用于设置加入按钮的状态)
		holdsport = (HoldSport) intent.getSerializableExtra("hosp");
		String title = holdsport.getTitle();
		String descripe = holdsport.getSport_content1();
		long startTime = holdsport.getStart_time();
		long endTime = holdsport.getEnd_time();
		limit = holdsport.getUser_limit();

		// 找控件
		myname = (TextView) this.findViewById(R.id.myname);
		myphoto = (ImageView) this.findViewById(R.id.myphoto);
		TextView mytl = (TextView) this.findViewById(R.id.mytitle);
		TextView mydscr = (TextView) this.findViewById(R.id.mydescribe);
		TextView mystet = (TextView) this.findViewById(R.id.mystartend);
		myjnm = (TextView) this.findViewById(R.id.myjoinnum);
		Button chat = (Button) this.findViewById(R.id.startGroupChat);
		Button btn_join = (Button) findViewById(R.id.buttonj);
	
chat.setVisibility(View.GONE);
		joining = (Button) this.findViewById(R.id.buttonj);
		// 判断如果是自己发起的活动,把加入Button隐藏

		// 设置数据
		mytl.setText(title);
		mydscr.setText(descripe);

		SimpleDateFormat time = new SimpleDateFormat("MM.dd HH:mm");
		String start_time = time.format(startTime);
		String end_time = time.format(endTime);
		

		mystet.setText(start_time + "至" + end_time );

		// 获取用户id,找到对应的名称，再设置控件myname内容
		userid = holdsport.getUser_id();
		String userUrl = u + "AndroidQueryUserById?userId=" + userid;
		// 1.初始化RequestQueue对象
		requestQueue = Volley.newRequestQueue(this);
		// 2. 发送StringRequest请求（获取用户名 和 account 和头像url）
		StringRequest userRequsset = new StringRequest(Method.GET, userUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {

						Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd")
								.create();
						user = gson.fromJson(response, UserInfo.class);
						// 获取字段,设置控件myname值
						if (TextUtils.isEmpty(user.getUser_name())
								&& TextUtils.isEmpty(user.getAccount())
								&& TextUtils.isEmpty(user.getPhoto())) {

							myname.setText(user.getUser_name().toString());
							downPic(myphoto, user.getPhoto().toString());
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getApplicationContext(), "null",
								Toast.LENGTH_SHORT).show();
					}
				});

		// 3.将StringRequest加入到RequestQueue当中（获取用户名）
		requestQueue.add(userRequsset);

		// 获取加入活动的用户信息
		sportid = holdsport.getSport_id();
		String joinPicUrl = u + "AndroidQueryJoinUserBySportId";
		StringRequest joinpicRequsset = new StringRequest(Method.POST,
				joinPicUrl, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("response:" + response);

						Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd")
								.create();
						Type type = new TypeToken<ArrayList<UserInfo>>() {
						}.getType();
						// 将获取到的加入总人数和限制人数拼接在控件myjnm中显示
						ArrayList<UserInfo> userinfo = gson.fromJson(response,
								type);
						joinnum = userinfo.size();
						System.out.println(joinnum + "参加活动的人数");
						myjnm.setText(joinnum + "/" + limit + "");

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getApplicationContext(), "null",
								Toast.LENGTH_SHORT).show();
						System.out.println(error + "请求错误");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// post 提交 重写参数 ，将自动 提交参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("sportid", sportid + "");
				return map;
			}
		};

		// 3.将StringRequest加入到RequestQueue当中（获取加入活动的总人数）
		requestQueue.add(joinpicRequsset);

		// 设置点击事件

		myphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (user != null) {
					Intent intent = new Intent(SportDetailActivity.this,
							com.example.sportfun.FriendActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}
			}
		});
		chat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String groupId = null;

				// 通过循环找出群聊ID
				List<EMGroup> grouplist = EMGroupManager.getInstance()
						.getAllGroups();
				for (EMGroup item : grouplist) {
					if (holdsport.getTitle().equals(item.getGroupName())) {
						groupId = item.getGroupId();
						break;
					}
				}

				// 进入群聊
				Intent intent = new Intent(SportDetailActivity.this,
						ChatActivity.class);
				// it is group chat
				intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				intent.putExtra("groupId", groupId);
				startActivityForResult(intent, 0);
			}
		});

		joining.setOnClickListener(new OnClickListener() {
			String delidUrl = u + "AndroidExitSport?userid=" + userid
					+ "&sportid=" + sportid;
			String addidUrl = u + "AndroidJoinSport?userid=" + userid
					+ "&sportid=" + sportid;

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 加入活动（点击之后button文字变为退出）
				String btTx = joining.getText().toString();
				joining.setText("加入");
				if (btTx == "退出") {
					// 2. 发送StringRequest请求（退出活动）
					StringRequest exitRequsset = new StringRequest(Method.GET,
							delidUrl, new Listener<String>() {

								@Override
								public void onResponse(String response) {

									Toast.makeText(getApplicationContext(),
											response, Toast.LENGTH_SHORT)
											.show();
								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {

									Toast.makeText(getApplicationContext(),
											"null", Toast.LENGTH_SHORT).show();
								}
							});
					requestQueue.add(exitRequsset);
					// 每点击一次退出活动人数减一
					myjnm.setText((joinnum - 1) + "/" + limit + "");
					joinnum = joinnum - 1;
					joining.setText("加入");

				} else {
					if (joinnum < limit || limit == 0) {
						// 2. 发送StringRequest请求（加入活动）
						StringRequest joinRequsset = new StringRequest(
								Method.GET, addidUrl, new Listener<String>() {

									@Override
									public void onResponse(String response) {

										Toast.makeText(getApplicationContext(),
												response, Toast.LENGTH_SHORT)
												.show();

										if (response.equals("加入成功")) {
											if (limit == 0) {
												myjnm.setText(joinnum + 1
														+ "/无上限");
												joinnum = joinnum + 1;
											} else {
												// 每点击一次加入活动人数加一
												myjnm.setText((joinnum + 1)
														+ "/" + limit + "");
												joinnum = joinnum + 1;
											}
										}
									}
								}, new ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {

										Toast.makeText(getApplicationContext(),
												"null", Toast.LENGTH_SHORT)
												.show();
									}
								});
						requestQueue.add(joinRequsset);

						joining.setText("退出");
					} else {

						Toast.makeText(getApplicationContext(), "人数已满加入不了咯",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
			Bitmap bitmap = (Bitmap) map.get("Bitmap");
			ImageView iView = (ImageView) map.get("Image");
			iView.setImageBitmap(bitmap);

		};

	};

	public void downPic(final ImageView imageView, final String picUrl) {
		new Thread() {
			private ImageView iv;

			@Override
			public void run() {
				iv = imageView;
				try {
					if (picUrl == null) {

						System.out.println("空的url");
					}
					URL url = new URL(picUrl);// ???
					// ???
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);//
					connection.setReadTimeout(5000);//
					InputStream is = connection.getInputStream();
					//
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					//
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("Image", iv);
					map.put("Bitmap", bitmap);
					Message message = Message.obtain();
					message.obj = map;
					handler.sendMessage(message);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("捕获了异常");
					e.printStackTrace();
				}
			}

		}.start();
	}

}
