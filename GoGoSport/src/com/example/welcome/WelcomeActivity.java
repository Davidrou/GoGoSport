package com.example.welcome;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.example.login.LoginActivity;
import com.example.sportfun.R;
import com.sport.chat.ChatConstant;
import com.sport.chat.DemoHXSDKHelper;
import com.umeng.socialize.utils.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 开屏页
 *
 */
public class WelcomeActivity extends Activity {

	Context context;
	private static final String TAG = "WelcomeActivity";
	private static final int IS_FIRST_HANDLER_MESSAGE = 0x123;

	private BDLocation mLocation;
	private LocationClient mLocClient;
	private static Handler handler;
	private MyLocationListenner myListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		context = getApplicationContext();
		SDKInitializer.initialize(context);

		setContentView(R.layout.fragment_welcome);
		Log.i(TAG, "welcome");

		initLocation();

		//
		final SharedPreferences sharedPreferences = getSharedPreferences(
				"config", Context.MODE_PRIVATE);

		// handler
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Log.i(TAG, "进入Handler");
				// 加载环信本地数据
				if (msg.what == IS_FIRST_HANDLER_MESSAGE) {
					boolean isFirst = sharedPreferences.getBoolean(
							ChatConstant.IS_FIRST_LOGIN, true);
					if (isFirst) {
						Log.i(TAG, "首次进入app");
						// 跳转到导航页
						startActivity(new Intent(WelcomeActivity.this,
								GuideActivity.class));
						// 如果是 第一次登录，修改登录状态为非第一次登录
						sharedPreferences.edit()
								.putBoolean(ChatConstant.IS_FIRST_LOGIN, false)
								.commit();
						finish();
					} else {
						// ** 免登陆情况 加载所有本地群和会话
						// 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
						// 加上的话保证进了主页面会话和群组都已经load完毕
						if (DemoHXSDKHelper.getInstance().isLogined()) {
							EMGroupManager.getInstance().loadAllGroups();
							EMChatManager.getInstance().loadAllConversations();
							Log.i(TAG, "不是首次进入app,加载本地环信数据ok");
						}
						// 否则，跳转到登录界面
						startActivity(new Intent(WelcomeActivity.this,
								LoginActivity.class));
						finish();
					}

				}
			}

		};

	}

	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null) {
				// 获取不到位置就继续监听
				Toast.makeText(context, "获取位置失败", Toast.LENGTH_SHORT).show();
				return;
			} else {
				// 更新当前位置
				// mLocation = location;
				getSharedPreferences("location", MODE_PRIVATE).edit()
						.putString("latitude", location.getLatitude() + "")
						.putString("longitude", location.getLongitude() + "")
						.putString("address", location.getAddrStr()).commit();
				Toast.makeText(context, location.getCity(), Toast.LENGTH_SHORT)
						.show();
				// 获取到位置后关闭定位,关闭定位
				Log.i(TAG, "定位成功");
				closeLocation();

				// 接下来才执行环信相关函数
				// 通知Handler
				handler.sendEmptyMessageDelayed(IS_FIRST_HANDLER_MESSAGE, 2000);
			}
		}
	}

	private void initLocation() {
		mLocClient = new LocationClient(this);
		// 注册监听器,获得LocationData
		myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		// 获取定位设置
		LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30 * 60 * 1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void closeLocation() {
		// TODO Auto-generated method stub
		// 退出时销毁定位
		if (mLocClient.isStarted()) {

			mLocClient.stop();
		}
		Log.i(TAG, "定位关闭");
		if (mLocation != null) {
			// mLocClient.stop();
			mLocClient = null;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 退出时销毁定位
		if (mLocClient.isStarted()) {

			mLocClient.stop();
		}
		Log.i(TAG, "定位关闭");
		if (mLocation != null) {
			// mLocClient.stop();
			mLocClient = null;
		}
	}

}
