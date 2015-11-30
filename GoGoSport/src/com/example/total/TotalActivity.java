package com.example.total;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.InviteMessage;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.domain.InviteMessage.InviteMesageStatus;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.example.login.LoginActivity;
import com.example.maplocation.MapActivity;
import com.example.maplocation.PlaceFragment;
import com.example.readnews.ReadFragment;
import com.example.sportfun.AddSportActivity;
import com.example.sportfun.MatchActivity;
import com.example.sportfun.R;
import com.geminno.entity.UserInfo;
import com.gogosport.activity.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sport.chat.ChatActivity;
import com.sport.chat.ChatConstant;
import com.sport.chat.ChatHistoryFragment;
import com.sport.chat.DemoHXSDKHelper;
import com.sportfun.usercenter.MySportActivity;
import com.sportfun.usercenter.Information;
import com.sportfun.usercenter.NewsActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class TotalActivity extends BaseActivity implements OnClickListener,
		EMEventListener {
	Context context;
	private static final String TAG = "TotalActivity";
	
	// HX
	private MyConnectionListener connectionListener = null;
	private MyGroupChangeListener groupChangeListener = null;
	//private TextView unreadLabel;
	public boolean isConflict = false;// 账号在别处登录
	//private TextView unreadAddressLable;// 未读通讯录textview
	
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;

	// 侧滑
	private SlidingMenu slidingMenu;
	private Button btn_mInfo, btn_mSports, btn_mCollection;
	private ImageView iv_mPhoto;
	private TextView tv_mUserName;
	private String photoPath;

	//界面fragment
	private Fragment[] totalfrags;
	private HomeFragment homeFragment;
	private PlaceFragment placeFragment;
	private ChatHistoryFragment chatHistoryFragment;
	//private ContactlistFragment contactListFragment;
	private ReadFragment readFragment;
	private FragmentTransaction transFrag;
	private int index, currentTabIndex;
	
	//底部导航
	private RadioButton homePage, placePage, chatPage,
			readPage;
	private String[] titles;
	private TextView top_title;
	private TextView top_right;
	


	String addrStr;// 位置的拼接字符串

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(ChatConstant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			DemoHXSDKHelper.getInstance().logout(true, null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean(ChatConstant.ACCOUNT_CCNFLICT, false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.fragment_total);
		

		// 环信本地存储的Dao
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);

		// 初始化侧滑对象
		slidingMenu = new SlidingMenu(this);

		// 获取全局变量Location
		addrStr = getSharedPreferences("location", MODE_PRIVATE).getString(
				"address", "sdfsdf");
		// 分页tab的title
		titles = new String[] { "主页", "附近场地", "聊天", "体育新闻" };

		// 实例化cehua栏
		initSlidingMenu();

		// 实例化导航栏
		initView();

		// 实例化fragments
		initFragments();

		// 设置切换fragment的监听事件
		setListener();
		// 环信监听
		initHXListener();

		// 默认显示第一个
		transFrag = getSupportFragmentManager().beginTransaction();
		transFrag.add(R.id.content, homeFragment, "HomeFrag")
				.show(homeFragment).commit();
		currentTabIndex = 0;

		// 发送关闭progressdialog 消息
		Message message = mHandler.obtainMessage(EVENT_TIME_TO_CHANGE_IMAGE);
		mHandler.sendMessage(message);
	}

	private void initHXListener() {
		// TODO Auto-generated method stub
		// 注册一个监听连接状态的listener
		Log.i(TAG, "正在连接变化...");
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);

		// setContactListener监听联系人的变化等
		Log.i(TAG, "监听联系人变化");
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());

		Log.i(TAG, "监听群组变化中");
		groupChangeListener = new MyGroupChangeListener();
		EMGroupManager.getInstance()
				.addGroupChangeListener(groupChangeListener);
	}

	private void initView() {
		// TODO Auto-generated method stub

		// 顶部标题栏
		// top_left = (ImageButton) findViewById(R.id.top_left);
		top_right = (Button) findViewById(R.id.top_right);
		top_title = (TextView) findViewById(R.id.top_title);

		// tabs导航相关
		homePage = (RadioButton) findViewById(R.id.home);
		placePage = (RadioButton) findViewById(R.id.place);
		chatPage = (RadioButton) findViewById(R.id.chat);
		//chatContactPage = (RadioButton) findViewById(R.id.chatContact);
		readPage = (RadioButton) findViewById(R.id.read);

		// 侧滑菜单相关
		btn_mInfo = (Button) slidingMenu.findViewById(R.id.informationbutton);
		btn_mSports = (Button) slidingMenu.findViewById(R.id.sportsbutton);
		btn_mCollection = (Button) slidingMenu.findViewById(R.id.newsbutton);
		iv_mPhoto = (ImageView) slidingMenu.findViewById(R.id.photo);
		tv_mUserName = (TextView) slidingMenu.findViewById(R.id.userName);

		// 环信聊天
		//unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		//unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		
	}

	private void initFragments() {

		// 实例化Fragment
		homeFragment = new HomeFragment();
		placeFragment = new PlaceFragment();
		chatHistoryFragment = new ChatHistoryFragment();
		//contactListFragment = new ContactlistFragment();
		readFragment = new ReadFragment();
		// fragment放进数组中方便管理
		totalfrags = new Fragment[] { homeFragment, placeFragment,
				chatHistoryFragment, readFragment };
	}

	private void initSlidingMenu() {
		// 对SlideMenu进行配置
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(R.layout.slidingmenu);
	}

	private void setListener() {

		// 顶部标题栏
		// top_left.setOnClickListener(this);
		top_right.setOnClickListener(this);
		top_title.setOnClickListener(this);

		// tabsd导航
		homePage.setOnClickListener(this);
		placePage.setOnClickListener(this);
		chatPage.setOnClickListener(this);
		//chatContactPage.setOnClickListener(this);
		readPage.setOnClickListener(this);

		// 侧滑
		btn_mInfo.setOnClickListener(this);
		btn_mSports.setOnClickListener(this);
		btn_mCollection.setOnClickListener(this);
		iv_mPhoto.setOnClickListener(this);
		tv_mUserName.setOnClickListener(this);
		// btn_logout.setOnClickListener(this);
		// 设置当前用户头像、用户名
		// 1.初始化RequestQueue对象
		RequestQueue requestQueue = Volley
				.newRequestQueue(getApplicationContext());
		// 获取当前用户
		SharedPreferences sharedPreferences = getSharedPreferences("user",
				Activity.MODE_PRIVATE);
		String account = sharedPreferences.getString("account", "");
		System.out.println("用户账号" + account);

		// 设置每个控件的值
		Properties properties = URLPropertiesUtil.getProperties(this);
		String u = properties.getProperty("u");
		String getUserUrl = u + "AndroidQueryUserByAccount?account=" + account;
		// 2. 发送StringRequest请求（获取用户信息）
		StringRequest getRequsset = new StringRequest(Method.GET, getUserUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println(response);
						Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd")
								.create();
						UserInfo user = gson.fromJson(response, UserInfo.class);

						if (user.getUser_name() != null) {
							tv_mUserName
									.setText(user.getUser_name().toString());
						}
						if (user.getPhoto() != null) {

							photoPath = user.getPhoto().toString();
							// 设置图片控件内容
							downPic(iv_mPhoto, photoPath);
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println(error + "请求错误");

					}
				});

		// 3.将StringRequest加入到RequestQueue当中
		requestQueue.add(getRequsset);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.home:
			index = 0;
			top_right.setText("发起活动");
			top_right.setVisibility(View.VISIBLE);
			break;
		case R.id.place:
			index = 1;
			top_right.setText("查看地图");
			top_right.setVisibility(View.VISIBLE);
			break;
		case R.id.chat:
			// Toast.makeText(context, "憋点这里", Toast.LENGTH_SHORT).show();
			index = 2;
			top_right.setText("速配好友");
			top_right.setVisibility(View.VISIBLE);
			break;
//		case R.id.chatContact:
//			// Toast.makeText(context, "憋点这里", Toast.LENGTH_SHORT).show();
//			index = 3;
//			top_right.setVisibility(View.GONE);
//
//			break;
		case R.id.read:
			index = 3;
			top_right.setText("我的收藏");
			top_right.setVisibility(View.VISIBLE);
			break;

		case R.id.informationbutton:
			Intent intent1 = new Intent(this, Information.class);
			startActivity(intent1);
			// slidingMenu.toggle();// 显示/隐藏
			break;
		case R.id.sportsbutton:
			Intent intent2 = new Intent(this, MySportActivity.class);
			startActivity(intent2);
			// slidingMenu.toggle();// 显示/隐藏
			break;
		case R.id.newsbutton:
			Intent intent3 = new Intent(this, NewsActivity.class);
			startActivity(intent3);
			// slidingMenu.toggle();// 显示/隐藏
			break;
		case R.id.photo:
			Intent intent4 = new Intent(this, Information.class);
			startActivity(intent4);
			break;

		case R.id.top_right:
			onTopRightClicked(currentTabIndex);
			break;

		}

		if (currentTabIndex != index) {
			// 更改标题
			top_title.setText(titles[index]);
			// 不同的fragment切换动画
			// 切换Fragment
			transFrag = getSupportFragmentManager().beginTransaction();
			if (currentTabIndex > index) {

				// 设置fragment切换动画(从左进入)
				transFrag.setCustomAnimations(R.anim.slide_in_from_left,
						R.anim.slide_out_to_right, R.anim.slide_in_from_left,
						R.anim.slide_out_to_right);

			} else {
				// 设置fragment切换动画(从右进入)
				transFrag.setCustomAnimations(R.anim.slide_in_from_right,
						R.anim.slide_out_to_left, R.anim.slide_in_from_right,
						R.anim.slide_out_to_left);
			}
			// 防止fragment重复新建
			if (!totalfrags[index].isAdded()) {
				transFrag.add(R.id.content, totalfrags[index]);
			}
			transFrag.hide(totalfrags[currentTabIndex]).show(totalfrags[index])
					.commit();
		}
		currentTabIndex = index;
	}

	/*
	 * 标题栏右边的按钮被点击 会根据fragment的index跳转道不同的Activity
	 */
	private void onTopRightClicked(int currentTabIndex2) {
		// TODO Auto-generated method stub
		// 2没有Button可以点击
		switch (currentTabIndex2) {
		case 0:
			startActivity(new Intent(this, AddSportActivity.class));
			break;
		case 1:
			String type = placeFragment.getType();
			String distance = placeFragment.getDistance();
			System.out.println(type + ":" + distance);

			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("type", type);
			intent.putExtra("distance", distance);
			startActivity(intent);

			break;
		case 2:
			startActivity(new Intent(this, MatchActivity.class));
			break;
		case 4:
			startActivity(new Intent(this, NewsActivity.class));
			break;

		}
	}

	// progressDialog
	private static final int EVENT_TIME_TO_CHANGE_IMAGE = 100;
	private static final int UPLOAD_USER_PHOTO = 200;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_TIME_TO_CHANGE_IMAGE:
				LoginActivity.closeProgressDialog();
				break;
			}
			switch (msg.what) {
			case UPLOAD_USER_PHOTO:
				Bitmap bitmap = (Bitmap) msg.obj;
				iv_mPhoto.setImageBitmap(bitmap);
				break;
			}

		}
	};

	// 加载用户头像
	public void downPic(ImageView iv_mPhoto, final String picUrl) {
		this.iv_mPhoto = iv_mPhoto;
		new Thread() {

			@Override
			public void run() {
				try {
					if (picUrl == null) {
						System.out.println("头像为空");
						return;
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
					Message message = mHandler.obtainMessage(UPLOAD_USER_PHOTO);
					message.obj = bitmap;
					mHandler.sendMessage(message);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e);
					e.printStackTrace();
				}
			}

		}.start();
	}

	/**
	 * 连接监听listener
	 * 
	 * 如果网络正常,同步联系人和Group到SQLite
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			boolean groupSynced = HXSDKHelper.getInstance()
					.isGroupsSyncedWithServer();
			boolean contactSynced = HXSDKHelper.getInstance()
					.isContactsSyncedWithServer();

			// in case group and contact were already synced, we supposed to
			// notify sdk we are ready to receive the events
			if (groupSynced && contactSynced) {
				new Thread() {
					@Override
					public void run() {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			} else {
				if (!groupSynced) {
					asyncFetchGroupsFromServer();
				}

				if (!contactSynced) {
					asyncFetchContactsFromServer();
				}

			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// chatHistoryFragment.getView().findViewById(id)
					// 如果连接不上网络,则显示无法连接网络提示
					if (currentTabIndex == 2) {

						chatHistoryFragment.errorItem.setVisibility(View.GONE);
					}
					// Log.e(TAG, chatHistoryFragment.toString());
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = "无法连接到服务器";
			final String st2 = "当前网络不可用,请检查";
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						if (currentTabIndex == 2) {

							chatHistoryFragment.errorItem
									.setVisibility(View.VISIBLE);

							if (NetUtils.hasNetwork(TotalActivity.this)) {

								chatHistoryFragment.errorText.setText(st1);
								Toast.makeText(context, st1, Toast.LENGTH_SHORT)
										.show();
							} else {

								chatHistoryFragment.errorText.setText(st2);
								Toast.makeText(context, st1, Toast.LENGTH_SHORT)
										.show();
							}
						}
					}
				}

			});
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHXSDKHelper.getInstance().logout(false, null);
		String st = "Logoff_notification";
		if (!TotalActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							TotalActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage("账户在别处登陆");
				conflictBuilder.setPositiveButton("ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								finish();
								startActivity(new Intent(TotalActivity.this,
										LoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG,
						"---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHXSDKHelper.getInstance().logout(true, null);
		String st5 = "账号已被移除";
		if (!TotalActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							TotalActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage("em_user_remove");
				accountRemovedBuilder.setPositiveButton("ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(TotalActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG,
						"---------color userRemovedBuilder error"
								+ e.getMessage());
			}

		}

	}

	// 从服务器端同步群组
	public static void asyncFetchGroupsFromServer() {
		HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

			@Override
			public void onSuccess() {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

				if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
					HXSDKHelper.getInstance().notifyForRecevingEvents();
				}
			}

			@Override
			public void onError(int code, String message) {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

		});
	}

	// 从服务器端同步联系人
	static void asyncFetchContactsFromServer() {
		HXSDKHelper.getInstance().asyncFetchContactsFromServer(
				new EMValueCallBack<List<String>>() {

					@Override
					public void onSuccess(List<String> usernames) {
						Context context = HXSDKHelper.getInstance()
								.getAppContext();

						System.out.println("----------------"
								+ usernames.toString());
						EMLog.d("roster", "contacts size: " + usernames.size());
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							//setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"申请与通知"
						User newFriends = new User();
						newFriends
								.setUsername(ChatConstant.NEW_FRIENDS_USERNAME);
						String strChat = context
								.getString(R.string.Application_and_notify);
						newFriends.setNick(strChat);

						userlist.put(ChatConstant.NEW_FRIENDS_USERNAME,
								newFriends);
						// 添加"群聊"
						User groupUser = new User();
						String strGroup = context
								.getString(R.string.group_chat);
						groupUser.setUsername(ChatConstant.GROUP_USERNAME);
						groupUser.setNick(strGroup);
						groupUser.setHeader("");
						userlist.put(ChatConstant.GROUP_USERNAME, groupUser);

						// 存入内存
						((DemoHXSDKHelper) HXSDKHelper.getInstance())
								.setContactList(userlist);
						// 存入db
						UserDao dao = new UserDao(context);
						List<User> users = new ArrayList<User>(userlist
								.values());
						dao.saveContactList(users);

						HXSDKHelper.getInstance().notifyContactsSyncListener(
								true);

						if (HXSDKHelper.getInstance()
								.isGroupsSyncedWithServer()) {
							HXSDKHelper.getInstance().notifyForRecevingEvents();
						}

						((DemoHXSDKHelper) HXSDKHelper.getInstance())
								.getUserProfileManager()
								.asyncFetchContactInfosFromServer(usernames,
										new EMValueCallBack<List<User>>() {

											@Override
											public void onSuccess(
													List<User> uList) {
												((DemoHXSDKHelper) HXSDKHelper
														.getInstance())
														.updateContactList(uList);
												((DemoHXSDKHelper) HXSDKHelper
														.getInstance())
														.getUserProfileManager()
														.notifyContactInfosSyncListener(
																true);
											}

											@Override
											public void onError(int error,
													String errorMsg) {
											}
										});
					}

					@Override
					public void onError(int error, String errorMsg) {
						HXSDKHelper.getInstance().notifyContactsSyncListener(
								false);
					}

				});
	}


	/***
	 * 好友变化listener
	 * 
	 */
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	// private InviteMessgeDao inviteMessgeDao;

	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			//获取本地的List
			Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper
					.getInstance()).getContactList();
			//生成新的List
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = new User(username);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
//			if (currentTabIndex == 3)
//				contactListFragment.refresh();

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper
					.getInstance()).getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = "已把你从他好友列表里移除";
					if (ChatActivity.activityInstance != null
							&& usernameList
									.contains(ChatActivity.activityInstance
											.getToChatUsername())) {
						Toast.makeText(
								TotalActivity.this,
								ChatActivity.activityInstance
										.getToChatUsername() + st10, 1).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
					// 刷新ui
					//contactListFragment.refresh();
					chatHistoryFragment.refresh();
				}
			});

		}

		@Override
		public void onContactInvited(String username, String reason) {

			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null
						&& inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {

			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
//	User setUserHead(String username) {
//		User user = new User();
//		user.setUsername(username);
//		String headerName = null;
//		if (!TextUtils.isEmpty(user.getNick())) {
//			headerName = user.getNick();
//		} else {
//			headerName = user.getUsername();
//		}
//		if (username.equals(ChatConstant.NEW_FRIENDS_USERNAME)) {
//			user.setHeader("");
//		} else if (Character.isDigit(headerName.charAt(0))) {
//			user.setHeader("#");
//		} else {
//			user.setHeader(HanziToPinyin.getInstance()
//					.get(headerName.substring(0, 1)).get(0).target.substring(0,
//					1).toUpperCase());
//			char header = user.getHeader().toLowerCase().charAt(0);
//			if (header < 'a' || header > 'z') {
//				user.setHeader("#");
//			}
//		}
//		return user;
//	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = EMChatManager.getInstance().getUnreadMsgsCount();
		if (count > 0) {
			//unreadLabel.setText(String.valueOf(count));
			//unreadLabel.setVisibility(View.VISIBLE);
		} else {
			//unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	// 群组状态监听
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {

			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			String st3 = getResources().getString(
					R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + " " + st3));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 2)
						chatHistoryFragment.refresh();
					// if
					// (CommonUtils.getTopActivity(TotalActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (currentTabIndex == 2)
							chatHistoryFragment.refresh();
						// if (CommonUtils.getTopActivity(TotalActivity.this)
						// .equals(GroupsActivity.class.getName())) {
						// GroupsActivity.instance.onResume();
						// }
					} catch (Exception e) {
						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {

			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (currentTabIndex == 2)
						chatHistoryFragment.refresh();
					// if (CommonUtils.getTopActivity(MainActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {

			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {

			String st4 = getResources().getString(
					R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + " " + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 2)
						chatHistoryFragment.refresh();
					Toast.makeText(context, "GroupActivity为实现",
							Toast.LENGTH_SHORT).show();
					// GroupActivity为实现
					// if
					// (CommonUtils.getTopActivity(TotalActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	}

	/**
	 * include 群组监听 保存and提示新消息
	 * 
	 * @param
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
//		if (currentTabIndex == 3)
//			contactListFragment.refresh();

	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance())
				.getContactList().get(ChatConstant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					// unreadAddressLable.setText(String.valueOf(count));
					//unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					//unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(
				ChatConstant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = ((DemoHXSDKHelper) HXSDKHelper
					.getInstance()).getContactList()
					.get(ChatConstant.NEW_FRIENDS_USERNAME).getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (currentTabIndex == 2) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (chatHistoryFragment != null) {
						chatHistoryFragment.refresh();
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

		if (connectionListener != null) {
			EMChatManager.getInstance().removeConnectionListener(
					connectionListener);
		}

		if (groupChangeListener != null) {
			EMGroupManager.getInstance().removeGroupChangeListener(
					groupChangeListener);
		}

		try {
			unregisterReceiver(internalDebugReceiver);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper
				.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	/**
	 * 监听消息事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

			refreshUI();
			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}

		default:
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(ChatConstant.ACCOUNT_REMOVED,
				isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "onNewIntent");
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(ChatConstant.ACCOUNT_REMOVED,
				false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// getMenuInflater().inflate(R.menu.context_tab_contact, menu);
	}

	public void logout(View v) {
		DemoHXSDKHelper.getInstance().logout(true, new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						// 重新显示登陆页面
						finish();
						startActivity(new Intent(TotalActivity.this,
								LoginActivity.class));

					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(int code, String message) {
			}
		});
	}

	/**
	 * 检查当前用户是否被删除
	 */

	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	public void showSlidmenu(View v){
		slidingMenu.toggle();
	}
}
