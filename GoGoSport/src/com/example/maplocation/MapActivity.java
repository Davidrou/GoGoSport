package com.example.maplocation;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.example.sportfun.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends Activity implements
		OnGetPoiSearchResultListener {

	private static final String TAG = "LifeCycleActivity";
	private static final String tag = "location";

	// 基础地图相关
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private PoiSearch mPoiSearch = null;
	// 覆盖物相关
	// Marker marker;

	// 定位相关
	LatLng mLatLng = null;
	MapStatusUpdate mLocStatusUpdate = null;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	public MyLocationListenner myListener;
	Button requestLocButton;
	LocationMode mCurrentMode;
	// 当前的位置信息
	public BDLocation mCurrentLocation = null;
	private String distance;
	private String type;
	private PoiSearch poiSearch;

	// Activity创建时被调用
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate called.");
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		// poilist = new List<PoiInfo>();
		// poiList = MyApplication.getInstance().getPoilist();
		// Log.i("收到的覆盖物个数", poiList.size()+"");
		distance = getIntent().getStringExtra("distance");
		type = getIntent().getStringExtra("type");
		// System.out.println(type);

		initView();
		// 初始化地图控件,包括显示地图&选择地图模式&开启定位图层
		initMap();
		setListener();
		// 定位到我的位置
		initMyLocation();

	}

	private void setPOI() {
		// TODO Auto-generated method stub
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(this);
		poiSearch.searchNearby(new PoiNearbySearchOption().keyword(type)
				.location(mLatLng).radius(Integer.parseInt(distance))
				.sortType(PoiSortType.distance_from_near_to_far).pageNum(1));
	}

	private void initView() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.bmapView);
		requestLocButton = (Button) findViewById(R.id.requestLocButton);
		BitmapDescriptorFactory.fromResource(R.drawable.overlay);
	}

	private void initMap() {
		// 获取地图控件引用
		mBaiduMap = mMapView.getMap();
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mLocStatusUpdate = MapStatusUpdateFactory.zoomTo(20);
		mBaiduMap.animateMapStatus(mLocStatusUpdate);

	}

	private void setListener() {
		// TODO Auto-generated method stub
		// 点击则定位到我的位置
		requestLocButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLatLng != null) {
					mLocStatusUpdate = MapStatusUpdateFactory
							.newLatLng(mLatLng);
				}
				if (mLocStatusUpdate != null) {
					// Log.i("info", "定位到我的位置");
					mBaiduMap.animateMapStatus(mLocStatusUpdate);
				} else {
					Toast.makeText(MapActivity.this, "暂未获取到定位数据,请稍等...",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initMyLocation() {
		Log.i(tag, "进入initloc");
		mCurrentMode = LocationMode.NORMAL;
		// 开启定位图层
		if (mBaiduMap == null) {
			Log.i(tag, "mBaiduMap为null");
		}
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
		option.setScanSpan(10000);
		option.setIsNeedAddress(true);

		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume called.");
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	// Activity被覆盖到下面或者锁屏时被调用
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause called.");
		// 有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据

		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	// 退出当前Activity时被调用,调用之后Activity就结束了
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestory called.");

		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	// 定义位置监听器(内部类)
	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

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
							MapActivity.this,
							location.getProvince() + location.getCity()
									+ location.getAddrStr(), Toast.LENGTH_SHORT)
							.show();
					mBaiduMap.animateMapStatus(mLocStatusUpdate);
				} else {
					Toast.makeText(MapActivity.this, "暂未获取到定位数据,请稍等...",
							Toast.LENGTH_SHORT).show();
				}
			}
			// 开始POI搜索
			setPOI();
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub

		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}

		// System.out.println("poi结果:" + result.getAllPoi().size());
		if (mLocClient.isStarted()) {
			mLocClient.stop();
			mLocClient = null;
			System.out.println("定位已关闭");
		}
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()
					.poiUid(poi.uid)));

			return true;
		}
	}
}