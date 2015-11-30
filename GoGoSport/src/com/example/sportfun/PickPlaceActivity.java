package com.example.sportfun;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PickPlaceActivity extends Activity implements OnClickListener,
		OnGetPoiSearchResultListener {
	private Button btn_me;
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	Intent intent;
	Intent backIntent;
	String sporttype;

	// 定位相关
	private int load_Index = 0;
	int radius = 3000;// 定位半径3000m
	LatLng mLatLng = null;
	MapStatusUpdate mLocStatusUpdate = null;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	public MyLocationListenner myListener;
	Button requestLocButton;
	LocationMode mCurrentMode;
	// POI 搜索相关
	private PoiSearch mPoiSearch = null;
	// 当前的位置信息
	public BDLocation mCurrentLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(savedInstanceState);
		// 获取addsportActivity 的运动类型
		intent = getIntent();
		Bundle b = intent.getExtras();
		sporttype = b.getString("sporttype");

		// 显示地图
		setContentView(R.layout.activity_pick_place);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 定位到当前
		initLoction();

		// 聚焦到我的位置
		btn_me = (Button) findViewById(R.id.me);
		btn_me.setText(sporttype);
		btn_me.setOnClickListener(this);
	}

	private void initLoction() {
		mCurrentMode = LocationMode.NORMAL;
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位设置
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		// 定位初始化(包括获取位置和定位设置)
		mLocClient = new LocationClient(this);
		// 注册监听器,获得LocationData
		myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		// 获取定位设置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(3000);
		option.setIsNeedAddress(true);

		mLocClient.setLocOption(option);
		mLocClient.start();

		// poi监听器
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

	}

	// Button点击监听器
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.me: {
			if (mLatLng != null) {
				mLocStatusUpdate = MapStatusUpdateFactory.newLatLng(mLatLng);
			}
			if (mLocStatusUpdate != null) {
				// Log.i("info", "定位到我的位置");
				mBaiduMap.animateMapStatus(mLocStatusUpdate);
			} else {
				Toast.makeText(PickPlaceActivity.this, "暂未获取到定位数据,请稍等...",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		}

	}

	/*
	 * 重写地图生命周期
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		// 关闭定位图层
		if (mBaiduMap.isMyLocationEnabled()) {
			mBaiduMap.setMyLocationEnabled(false);
		}
		// activity 销毁时同时销毁地图控件
		if (mMapView.isActivated()) {
			mMapView.onDestroy();
		}

	}

	// 定义位置监听器(内部类)
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 更新当前位置
			mCurrentLocation = location;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				Log.i("info", "第一次进入");
				isFirstLoc = false;
				mLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				mLocStatusUpdate = MapStatusUpdateFactory.newLatLng(mLatLng);

				if (mLocStatusUpdate != null) {
					// Log.i("info", "定位到我的位置");
					// toast我的 位置信息
					Toast.makeText(
							PickPlaceActivity.this,
							location.getCity() + location.getStreet() + "附近的"
									+ sporttype + "地点",
							Toast.LENGTH_SHORT).show();
					// ????????调用poinear搜索,是否需要手动聚焦位置??
					mBaiduMap.animateMapStatus(mLocStatusUpdate);
					// 调用POI搜索功能
					PIONearSearch();
				} else {
					Toast.makeText(PickPlaceActivity.this, "暂未获取到定位数据,请稍等...",
							Toast.LENGTH_SHORT).show();
				}
			}

		}// ///////

		// POINearSearch
		private void PIONearSearch() {
			// TODO Auto-generated method stub
			mPoiSearch.searchNearby(new PoiNearbySearchOption()//
					.location(mLatLng)//
					.keyword(sporttype)//
					.radius(radius)//
					.pageNum(load_Index));

		}
	}// ////////
	/*
	 * 定义Handler
	 */

	// Handler mHandler = new Handler(

	// POI覆盖物,需要继承,
	// 可在这里设置覆盖物的点击事件
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
		
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PickPlaceActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
			Intent intentback = new Intent().putExtra("latitude", 0.0)//
					.putExtra("longitude", 0.0)// .
					.putExtra("address", "未找到结果");
			PickPlaceActivity.this.setResult(0, intentback);
			PickPlaceActivity.this.finish();

			// ///////////############
		} else {
			intent.putExtra("latitude", result.getLocation().latitude)//
					.putExtra("longitude", result.getLocation().longitude)// .
					.putExtra("address", result.getAddress());
			PickPlaceActivity.this.setResult(0, intent);
			PickPlaceActivity.this.finish();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(PickPlaceActivity.this, "未找到结果,需要自己添加活动场地",
					Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			//
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			// 可以在这里实现修改类型
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到类似结果";
			Toast.makeText(PickPlaceActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if( keyCode == KeyEvent.KEYCODE_BACK){
			Intent intentback = new Intent().putExtra("latitude", 0.0)//
					.putExtra("longitude", 0.0)// .
					.putExtra("address", "未找到结果");
			PickPlaceActivity.this.setResult(0, intentback);
			PickPlaceActivity.this.finish();
		}
		return super.onKeyUp(keyCode, event);
	}

}// ////
