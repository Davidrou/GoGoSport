package com.example.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.example.total.TotalActivity;
import com.geminno.entity.UserInfo;
import com.gogosport.activity.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.sport.chat.ChatConstant;
import com.sport.chat.DemoHXSDKHelper;
import com.sportfun.application.MyApplication;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.soexample.commons.Constants;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "longin destory");
		super.onDestroy();
	}

	private static final String TAG = "LoginActivity";
	private Context context;
	// qq登陆
	private static final String appId = "100424468";
	private static final String appKey = "c7394704798a158208a74ab60104f0ba";
	// 短信发验证码
	private String APPKEY = "ad713eb7d90e";
	private String APPSECRET = "24422bdefbb4c4badf561b6ccde3514e";
	private Button registBtn;
	private Button loadBtn;

	// account登陆
	private EditText et_account;
	private EditText et_password;
	private String account, password;
	private UserInfo me;
	private Editor edit;
	private SharedPreferences share;

	RequestQueue requestQueue;
	String phone;
	// 第三方登陆相关
	private Button loginQQ;

	// 第三方登录：
	// 整个平台的Controller,负责管理整个SDK的配置、操作等处理
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);

	Properties properties = URLPropertiesUtil.getProperties(this);
	String u = properties.getProperty("u");
	String login_url = u + "AndroidLogin";
	String regist_url = u + "AndroidRegister";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 如果用户处于未退出状态，直接进入主页面
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			startActivity(new Intent(LoginActivity.this, TotalActivity.class));
			return;
		}

		setContentView(R.layout.fragment_main);

		// 验证码相关：
		SMSSDK.initSDK(this, APPKEY, APPSECRET);// 获取KEY和SECRET，使得项目与第三方后台相连接
		context = this;

		requestQueue = Volley.newRequestQueue(context);

		initView();

		initListener();

		// 配置需要qq的相关平台
		configPlatforms();

	}

	private void initView() {
		// TODO Auto-generated method stub
		// account登陆实例化
		et_account = (EditText) findViewById(R.id.account);
		et_password = (EditText) findViewById(R.id.password);
		registBtn = (Button) this.findViewById(R.id.regist);// 设置注册按钮监听事件
		loadBtn = (Button) this.findViewById(R.id.loader);
		loginQQ = (Button) this.findViewById(R.id.btn_qq_login);

	}

	private void initListener() {
		// TODO Auto-generated method stub
		registBtn.setOnClickListener(this);
		loadBtn.setOnClickListener(this);
		loginQQ.setOnClickListener(this);
	}

	// 第三方登录相关函数：

	// 三方登录按钮监听事件：

	@Override
	public void onClick(View v) {

		// 首先监测网络是否可用
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
		case R.id.regist:// zzhuce
			onRegistButtonClicked();
			break;
		case R.id.loader:
			onLoginButtonClicked();
			break;
		case R.id.btn_qq_login: // qq登录
			login(SHARE_MEDIA.QQ);
			break;
		default:
			break;
		}
	}

	/*
	 * 登陆自有服务器
	 */
	private void onLoginButtonClicked() {
		// c测试代码
	//	et_account.setText("13156059721");
	//	et_password.setText("9721");
//		et_account.setText("18279408903");
//		et_password.setText("8903");

		account = et_account.getText().toString();
		password = et_password.getText().toString();
		// Textutils是对所有Text进行处理/判断等操作的工具类
		if (TextUtils.isEmpty(account)) {
			Toast.makeText(this, "账户不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, login_url + "?userpwd=" + password + "&"
				+ "account=" + account, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e("自有服务器", "登陆失败,服务器失败");
				//Toast.makeText(this, "服务器错误,程序员可能忘了打开服务器", Toast.LENGTH_SHORT).show();
				return;
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub

				if ("0".equals(arg0.result)) {
					Toast.makeText(context, "登录失败,请检查账号密码", Toast.LENGTH_SHORT)
							.show();
				} else {

					Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd")
							.create();
					// String json = gson.toJson(user);
					me = gson.fromJson(arg0.result, UserInfo.class);
					share = getSharedPreferences("user", MODE_PRIVATE);
					edit = share.edit();
					edit.putInt("user_id", me.getUser_id());
					System.out.println(me.getUser_id());
					edit.putString("account", me.getAccount());
					edit.putString("user_position", me.getUser_position());
					edit.putString("userpwd", me.getUserpwd());
					edit.commit();
					if (me.getPhoto() != null) {
						Log.e("自有服务器", me.getPhoto());
						edit.putString("photo", me.getPhoto());
						//edit.putString("",null);
					}else{
						Log.i("自有服务器", "存入的头像url为空");
						edit.putString("photo", null);
					}
					if (me.getUser_name() != null) {
						edit.putString("user_name", me.getUser_name());
					}
					if (me.getSex() != null) {
						edit.putString("sex", me.getSex());
					}
					if (me.getBirthday().getTime() != 0) {
						edit.putString("birthday", me.getBirthday().getTime()
								+ "");
					}
					if (me.getSignature() != null) {
						edit.putString("signature", me.getSignature());
					}
					Log.e("sportfun", "用户信息已经保存到share中");

					// 登陆环信
					loadOnHX();

				}
			}

		});
	}

	/*
	 * 登陆环信 如果登陆成功,进入totalActivity 若失败,进入不了App
	 */
	private void loadOnHX() {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(account, password, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "登陆环信成功");
				// Toast.makeText(context,
				// 登陆成功，保存用户名密码
				MyApplication.getInstance().setUserName(account);
				MyApplication.getInstance().setPassword(password);

				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// 处理好友和群组
					initializeContacts();
					// 跳转到主页
					Intent intent = new Intent(getApplicationContext(),
							TotalActivity.class);
					Log.i(TAG, "跳转到主页啦啦");
					startActivity(intent);
					finish();
					Log.i(TAG, "loginac finish");
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					Log.e(TAG, "加载本地回话失败");
					Toast.makeText(context, "加载本地回话失败", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				Log.e(TAG, "登陆环信失败");
				// Toast.makeText(context, "登陆环信失败", Toast.LENGTH_SHORT).show();
			}
		});

	}

	/*
	 * 注册到自有服务器
	 */
	private void onRegistButtonClicked() {
		// TODO Auto-generated method stub
		// 点击注册按钮之后跳转到注册界面，这里调用集成好的注册界面
		RegisterPage registerPage = new RegisterPage();

		// 设置注册回调事件
		registerPage.setRegisterCallback(new EventHandler() {
			// 匿名类重写事件（验证）执行完才执行的方法
			@Override
			public void afterEvent(int event, int result, Object data) {
				// TODO Auto-generated method stub
				// 如果验证成功，说明用户注册成功，跳转到填写密码界面
				if (result == SMSSDK.RESULT_COMPLETE) {

					Intent intent = new Intent(LoginActivity.this,
							PasswordActivity.class);
					startActivity(intent);
					finish();
				}
			}

		});

		// 2、显示注册界面
		registerPage.show(getApplicationContext());
	}

	/**
	 * 配置分享平台参数
	 */
	private void configPlatforms() {
		// 添加QQ、QZone平台
		addQQQZonePlatform();
	}

	private void addQQQZonePlatform() {

		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this,
				appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();
	}

	/**
	 * 授权。如果授权成功，则获取用户信息
	 * 
	 * @param platform
	 */

	String uid;// 作为用户账号存入数据库
	String sex;
	String username;
	String photourl;
	String pwd;
	Date birthday;
	String position;

	private void login(final SHARE_MEDIA platform) {
		mController.doOauthVerify(LoginActivity.this, platform,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权开始",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e,
							SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权失败",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						// 获取uid
						uid = value.getString("uid");
						if (!TextUtils.isEmpty(uid)) {
							// uid不为空，获取用户信息
							// 把扣扣信息存到SharedPreferences里
							share = getSharedPreferences("user", MODE_PRIVATE);
							edit = share.edit();
							edit.putString("account", uid);
							edit.commit();
							System.out.println("扣扣信息已经保存在user文件中");
							getUserInfo(platform);
						} else {
							Toast.makeText(LoginActivity.this, "授权失败...",
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权取消",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * 第三方登陆(QQ) 获取用户信息
	 * 
	 * @param platform
	 */
	private void getUserInfo(SHARE_MEDIA platform) {
		mController.getPlatformInfo(LoginActivity.this, platform,
				new UMDataListener() {

					@Override
					public void onStart() {
						myDialog = ProgressDialog.show(LoginActivity.this,
								"正在登录运动乐..", "登录中,请稍后..", true, true);
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {

						if (info != null) {
							sex = info.get("gender").toString();// 性别
							username = info.get("screen_name").toString();// 昵称

							photourl = info.get("profile_image_url").toString();// 头像
							// 给第三方登录用户默认一个密码、生日
							pwd = "12345";
							birthday = new Date(System.currentTimeMillis());

							// System.out.println(info.get("city")+"5555555555");
							// System.out.println(info.get("province")+"5555555555");
							// 获取用户当前位置
							position = getSharedPreferences("location",
									MODE_PRIVATE).getString("address", "默認位置");

							Properties properties = URLPropertiesUtil
									.getProperties(getApplicationContext());
							String u = properties.getProperty("u");
							String url = u + "AndroidQQLogin";
							// 有可能会发生用户已存在，存入不了的情况（由数据库内部处理）
							// 1.初始化RequestQueue对象
							RequestQueue requestQueue = Volley
									.newRequestQueue(LoginActivity.this);
							// 2. 发送StringRequest请求 URLEncoder.encode(json,
							// "utf-8")
							StringRequest stringRequsset = new StringRequest(
									Method.POST, url, new Listener<String>() {

										@Override
										public void onResponse(String response) {

										}
									}, new ErrorListener() {

										@Override
										public void onErrorResponse(
												VolleyError error) {

										}
									}) {
								@Override
								protected Map<String, String> getParams()
										throws AuthFailureError {
									// post 提交 重写参数 ，将自动 提交参数
									Map<String, String> map = new HashMap<String, String>();
									String uid2 = null;
									String username2 = null;
									String sex2 = null;
									String position2 = null;
									String birthday2 = null;
									try {
										uid2 = URLEncoder.encode(uid, "utf-8");
										username2 = URLEncoder.encode(username,
												"utf-8");
										sex2 = URLEncoder.encode(sex, "utf-8");
										position2 = URLEncoder.encode(position,
												"utf-8");
										// 将日期转换为字符串
										SimpleDateFormat df = new SimpleDateFormat(
												"yyyy-MM-dd");
										birthday2 = df.format(birthday);
										System.out.println("生日" + birthday2);
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									map.put("uid", uid2);
									map.put("pwd", pwd);
									map.put("photourl", photourl);
									map.put("username", username2);
									map.put("sex", sex2);
									map.put("birthday", birthday2);
									map.put("position", position2);
									return map;
								}
							};
							// 3.将StringRequest加入到RequestQueue当中
							requestQueue.add(stringRequsset);

							// // 跳转到首页
							Handler handler = new Handler();
							Runnable mTasks = new Runnable() {
								public void run() {
									startActivity(new Intent(
											LoginActivity.this,
											TotalActivity.class));
									finish();
								}
							};

							handler.post(mTasks);

						}
					}
				});
	}

	static ProgressDialog myDialog;

	// 一个关闭progressdialog的方法
	public static void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (myDialog != null) {
			myDialog.dismiss();
		}
	}

	/*
	 * 将收到的 联系人存储到本地
	 */
	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(ChatConstant.NEW_FRIENDS_USERNAME);
		userlist.put(ChatConstant.NEW_FRIENDS_USERNAME, newFriends);

		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(ChatConstant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(ChatConstant.GROUP_USERNAME, groupUser);

		// 存入内存
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

}
