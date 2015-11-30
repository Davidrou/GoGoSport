package com.example.maplocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
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
import com.example.sportfun.RoutePlanActivity;
import com.umeng.socialize.utils.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceFragment extends Fragment implements OnItemSelectedListener,
		OnGetPoiSearchResultListener {

	public static final String tag = "PlaceFragment";

	public boolean isGetPoiDetail = false;
	ListView mListview;
	ListAdapter adapter;
	Context context;
	boolean flag = false;

	double mlatitude;
	double mlongitude;
	private LatLng mLatLng = null;
	private PoiSearch mPoiSearch = null;
	int count = 0;
	private List<PoiInfo> poilist;
	private int index = 0;
	// 视图组件
	Spinner spin_type, spin_distance;
	private String type = "篮球", distance = "5000";
	
	
	SharedPreferences share_location;

	Button showDetail;
	private List<Map<String, String>> datalist;

	// Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		SDKInitializer.initialize(context.getApplicationContext());
		Log.i(tag, "进入onCreateView");
		// 本地获取经纬度
		// 获取全局变量Location
		mlatitude = Double
				.parseDouble(getActivity().getSharedPreferences("location",
						Activity.MODE_PRIVATE).getString("latitude", "0.0"));
		mlongitude = Double.parseDouble(getActivity().getSharedPreferences(
				"location", Activity.MODE_PRIVATE)
				.getString("longitude", "0.0"));
		mLatLng = new LatLng(mlatitude, mlongitude);
		// POI搜索,发现周围场地
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		// fragment根视图获取
		View rootView = inflater.inflate(R.layout.fragment_place, container,
				false);
		// 实例化控件
		spin_type = (Spinner) rootView.findViewById(R.id.spinner1);
		spin_distance = (Spinner) rootView.findViewById(R.id.spinner2);

		mListview = (ListView) rootView.findViewById(R.id.listview);
		showDetail = (Button) rootView.findViewById(R.id.detail_tv);

		// 动态监听值
		spin_type.setOnItemSelectedListener(this);
		spin_distance.setOnItemSelectedListener(this);
		return rootView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// toast("被点击了");
		// spinner设置监听后立即执行onItemSelected,所以跳过,

		switch (parent.getId()) {
		case R.id.spinner1: {
			type = spin_type.getSelectedItem().toString();

			break;
		}
		case R.id.spinner2: {
			distance = spin_distance.getSelectedItem().toString();
			break;
		}

		}

		if (count < 1) {
			count++;
			return;
		}
		initPOI();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	// 加载POI
	public void initPOI() {
		// TODO Auto-generated method stub

		mPoiSearch
				.searchNearby(new PoiNearbySearchOption().keyword(type)
						.location(mLatLng).radius(Integer.parseInt(distance))
						.sortType(PoiSortType.distance_from_near_to_far)
						.pageNum(index));
		
		
		Log.e("loc", "开始搜索POI");
	}

	// 加载更多的POI
	// public void goToNextPage(View v) {
	// Log.e("loc", "加载更多");
	// index++;
	// initPOI();
	// }

	public void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

		shouPopWindow(result);
	}

	//显示地点详情
	private void shouPopWindow(PoiDetailResult result) {
		// TODO Auto-generated method stub

	    // 利用layoutInflater获得View
	    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.place_detail_layout, null);

	    // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
	    PopupWindow window = new PopupWindow(view,
	        WindowManager.LayoutParams.MATCH_PARENT,
	        WindowManager.LayoutParams.WRAP_CONTENT);

	    // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
	    window.setFocusable(true);


	    // 实例化一个ColorDrawable颜色为半透明
	    ColorDrawable dw = new ColorDrawable(0xb0000000);
	    window.setBackgroundDrawable(dw);

	    
	    // 设置popWindow的显示和消失动画
	    window.setAnimationStyle(R.style.mypopwindow_anim_style);
	    // 设置位置在在底部显示???第一个参数是什么意思
	    window.showAtLocation(getView(),Gravity.BOTTOM, 0, 0);

	    // 这里检验popWindow里的button是否可以点击
	    TextView placename = (TextView) view.findViewById(R.id.placename);
	    TextView placeaddress = (TextView) view.findViewById(R.id.placeaddress);
	    TextView favoritenum = (TextView) view.findViewById(R.id.favoritenum);
	    RatingBar placerating = (RatingBar) view.findViewById(R.id.placerating);
	    final TextView ratingnum = (TextView) view.findViewById(R.id.ratingnum);
	    Button phonenum = (Button) view.findViewById(R.id.phonenum);
	    
	    //设置数据
	    placename.setText(result.getName());
	    placeaddress.setText(result.getAddress());
	    if(result.getFavoriteNum()==0){
	    	
	    	favoritenum.setText("0");
	    }else{
	    	
	    	favoritenum.setText(result.getFavoriteNum());
	    }
	    placerating.setRating((float) result.getFacilityRating());
	    phonenum.setOnClickListener(new OnClickListener() {

	      @Override
	      public void onClick(View v) {

	        System.out.println("手机号被点击了");
	      }
	    });
	    
	    placerating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
				ratingnum.setText(rating+"");
			}
		});
	    //popWindow消失监听方法
	    window.setOnDismissListener(new OnDismissListener() {
	      
	      @Override
	      public void onDismiss() {
	        System.out.println("popWindow消失");
	      }
	    });

	  
		
		
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
result.getAllPoi();
		
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Log.e("loc", "未找到结果");
			Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		// 如果没有授权
		if (result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
			Log.e("loc", "授权未成功");
			Toast.makeText(context, "授权未成功", Toast.LENGTH_SHORT).show();
			return;
		}
		// 如果没有错误
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.e("loc", "收到POIList结果");
			// 得到POI结果后,添加到Adapter中
			poilist = result.getAllPoi();
			datalist = new ArrayList<Map<String, String>>();

			for (int i = 0; i < poilist.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				String name = poilist.get(i).name;
				String address = poilist.get(i).address;
				double lat = poilist.get(i).location.latitude;
				double lng = poilist.get(i).location.longitude;
				map.put("name", name);
				map.put("address", address);
				double a = MapTools.getDistanceFromXtoY(mLatLng.latitude,
						mLatLng.longitude, lat, lng);
				DecimalFormat df = new DecimalFormat("0.0");
				String f = "距离: " + df.format(a / 1000) + "千米";
				map.put("distance", f);
				datalist.add(map);
			}
			mListview.setAdapter(new PlaceAdapter(context));
			return;
		}
	}

	// 自定义的Adapter
	public class PlaceAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public PlaceAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datalist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if (vh == null) {
				vh = new ViewHolder();
				convertView = mInflater.inflate(R.layout.place_list_item, null);
				vh.name = (TextView) convertView.findViewById(R.id.name_tv);
				vh.distance = (TextView) convertView.findViewById(R.id.dis_tv);
				vh.address = (TextView) convertView.findViewById(R.id.addr_tv);
				vh.detail = (RelativeLayout) convertView
						.findViewById(R.id.detail_tv);
				vh.go = (RelativeLayout) convertView.findViewById(R.id.go_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.name.setText(datalist.get(position).get("name"));
			vh.address.setText(datalist.get(position).get("address"));
			vh.distance.setText(datalist.get(position).get("distance"));

			vh.detail.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						Log.i(position + "");
						// 显示地图详情
						mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poilist.get(position).uid));

						// new AlertDialog.Builder(context)
						//
						// .setTitle(poilist.get(position).name)
						// .setMessage(
						// datalist.get(position).get("address"))
						// .setIcon(R.drawable.ic_launcher).show();
					}

					return false;
				}

			});

			vh.go.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {

					case MotionEvent.ACTION_UP:
						Intent intent = new Intent();
						intent.setClass(context, RoutePlanActivity.class);
						intent.putExtra("fromX", mLatLng.latitude);
						intent.putExtra("fromY", mLatLng.longitude);
						intent.putExtra("toX",
								poilist.get(position).location.latitude);
						intent.putExtra("toY",
								poilist.get(position).location.longitude);
						startActivity(intent);
					}

					return false;
				}
			});
			return convertView;
		}

		class ViewHolder {
			public TextView name;
			public TextView distance;
			public TextView address;
			public RelativeLayout go;
			public RelativeLayout detail;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	
}
