package com.sportfun.application;

import java.util.List;

import com.baidu.mapapi.search.core.PoiInfo;
import com.easemob.EMCallBack;
import com.sport.chat.DemoHXSDKHelper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {

	public static final String TAG = "MyApplication";

	// app环境变量
	public static Context applicationContext;
	public static MyApplication appinstance;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	public List<PoiInfo> poilist =null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		applicationContext = this;
		appinstance = this;

		// //初始化环信
		// EMChat.getInstance().init(applicationContext);
		 Log.i(TAG, "环信初始化...");
		// EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		hxSDKHelper.onInit(applicationContext);
	}

	// 获取app单例
	public static MyApplication getInstance() {
		return appinstance;
	}


	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM, final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(isGCM, emCallBack);
	}

	public List<PoiInfo> getPoilist() {
		return poilist;
	}

	public void setPoilist(List<PoiInfo> poilist) {
		this.poilist = poilist;
	}

}
