package com.example.total;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.maplocation.MapTools;
import com.example.sportfun.R;
import com.geminno.entity.HoldSport;

/**
 * @author Administrator implements OnGetGeoCoderResultListener
 */
public class LvAdapter extends BaseAdapter {
	// 定义几个不同布局类型
	private static final int TYPE_LV = 0;// 大部分类型
	private static final int TYPE_LV1 = 1;// 无描述类型
	private int currentType;// 当前item类型

	private List<HoldSport> holdsports;
	private Context c;
	LayoutInflater inflater;
	SharedPreferences share;
	GeoCoder mSearch = null;

	// GeoCoder mSearch ;
	// LatLng mLatLng;

	public LvAdapter(List<HoldSport> holdsports, Context c,
			SharedPreferences share) {
		super();
		this.holdsports = holdsports;
		this.c = c;
		this.share = share;
		inflater = LayoutInflater.from(c);
		 // 搜索模块，也可去掉地图模块独立使用

		 mSearch = GeoCoder.newInstance();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return holdsports.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 3;// 自定义了三种布局
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		// 没有活动描述，lv1;有描述没有活动类型，lv2;大部分都显示，lvadapter
		if (holdsports.get(position).getSport_content1() == null) {
			return TYPE_LV1;
		} else {
			return TYPE_LV;
		}

	}

	ViewHolder vh;
	ViewHolder1 vh1;
	int limit;// 和加入活动人数进行拼接
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View lvView = null;
		View lv1View = null;
		currentType = getItemViewType(position);
		System.out.println(currentType == TYPE_LV);
		Volley.newRequestQueue(c);
		if (currentType == TYPE_LV) {
			vh = new ViewHolder();
			if (convertView == null) {
				lvView = inflater.inflate(R.layout.lvadapter, null);
				// 找控件
				vh.sporttype = (ImageView) lvView.findViewById(R.id.sporttype);
				vh.title = (TextView) lvView.findViewById(R.id.sporttitle);
				vh.splace = (TextView) lvView.findViewById(R.id.sportplace);
				vh.distance = (TextView) lvView.findViewById(R.id.distance);
				vh.limitnum = (TextView) lvView.findViewById(R.id.limitnum);
				vh.sporystatus = (TextView) lvView
						.findViewById(R.id.sportstatus);
				vh.startEndtime = (TextView) lvView
						.findViewById(R.id.startEndtime);

				lvView.setTag(vh);
				convertView = lvView;
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			// 设置数据
			vh.title.setText(holdsports.get(position).getTitle());


			switch (holdsports.get(position).getSporttype_id()) {
			case 1:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_05);
				break;
			case 2:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_32);
				break;
			case 3:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_07);
				break;
			case 4:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_15);
				break;
			case 5:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_16);
				break;
			case 6:
				vh.sporttype.setBackgroundResource(R.drawable.shaixuan1_09);
				break;
			}

			long currentTime = System.currentTimeMillis();
			long startTime = holdsports.get(position).getStart_time();
			long endTime = holdsports.get(position).getEnd_time();
			if (currentTime < startTime) {
				vh.sporystatus.setText("待开始");
			} else if (currentTime > startTime  && currentTime < endTime ) {
				vh.sporystatus.setText("进行中");
			} else {
				vh.sporystatus.setText("已结束");
			}

			System.out.println("当前时间"+currentTime);
			System.out.println("开始时间"+startTime);
			System.out.println("结束时间"+endTime);
			
			SimpleDateFormat time = new SimpleDateFormat(
					"yyyy.MM.dd.E HH:mm:ss");
			String s_t = time.format(startTime);
			String e_t = time.format(endTime);
			String start_time = s_t.substring(14, 19);
			String end_time = e_t.substring(14, 19);

			vh.startEndtime.setText(start_time + "--" + end_time);

			double mylat = Double.parseDouble(share.getString("latitude",
					"erer"));
			double mylng = Double.parseDouble(share.getString("longitude",
					"erer"));
			double plat = Double.parseDouble(holdsports.get(position)
					.getPlace_latitude());
			System.out.println(plat+"地点");
			double plng = Double.parseDouble(holdsports.get(position)
					.getPlace_longitude());
			double a = MapTools.getDistanceFromXtoY(mylat, mylng, plat, plng);
			DecimalFormat df = new DecimalFormat("0.0");
			String f = "距离" + df.format(a / 1000) + "km";
			vh.distance.setText(f);
			
			Log.i("场地位置", plat+","+plng);
			System.out.println("dfgsergesgrsfasidfuhaiouygfaiuf");
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(plat,plng)));
			MyRunable mRunable = new MyRunable();
			
			new Thread(mRunable).start();
			

			 
			// });
			limit = holdsports.get(position).getUser_limit();
			if (limit == 0) {
				vh.limitnum.setText("不限人数");
			} else {
				vh.limitnum.setText("限" + limit + "人");
			}
			

		} else if (currentType == TYPE_LV1) {
			vh1 = new ViewHolder1();
			if (convertView == null) {
				lv1View = inflater.inflate(R.layout.lv1, null);
				// 找控件
				vh1.title = (TextView) lv1View.findViewById(R.id.onetitle);
				vh1.limitnum = (TextView) lv1View.findViewById(R.id.onelimit);
				vh1.sporystatus = (TextView) lv1View
						.findViewById(R.id.onestatus);
				vh1.startEndtime = (TextView) lv1View
						.findViewById(R.id.onestartend);

				lv1View.setTag(vh1);
				convertView = lv1View;

			} else {
				vh1 = (ViewHolder1) convertView.getTag();
			}
			// 设置数据
			vh1.title.setText(holdsports.get(position).getTitle());

			long currentTime = System.currentTimeMillis();
			long startTime = holdsports.get(position).getStart_time();
			long endTime = holdsports.get(position).getEnd_time();
			if (currentTime < startTime ) {
				vh1.sporystatus.setText("待开始");
			} else if (currentTime > startTime && currentTime < endTime ) {
				vh1.sporystatus.setText("进行中");
			} else {
				vh1.sporystatus.setText("已结束");
			}

			SimpleDateFormat time = new SimpleDateFormat(
					"yyyy.MM.dd.E HH:mm:ss");
			String s_t = time.format(startTime);
			String e_t = time.format(endTime);
			String start_time = s_t.substring(14, 19);
			String end_time = e_t.substring(14, 19);

			vh1.startEndtime.setText(start_time + "--" + end_time);
			
			limit=holdsports.get(position).getUser_limit();
			if (limit == 0) {
				vh1.limitnum.setText("不限人数");
			} else {
				vh1.limitnum.setText("限" + limit + "人");
			}

		}
		return convertView;
	}

	class ViewHolder {// 大部分类型
		ImageView sporttype;
		TextView sporystatus;//
		TextView title;
		TextView splace;//
		TextView distance;// 根据经纬度计算出用户位置和球场位置的距离
		TextView joinnum;
		TextView limitnum;
		TextView startEndtime;

	}

	class ViewHolder1 {// 无描述类型
		TextView sporystatus;//
		TextView title;
		TextView limitnum;
		TextView startEndtime;

	}


	public List<HoldSport> getHoldsports() {
		return holdsports;
	}

	public void setHoldsports(List<HoldSport> holdsports) {
		this.holdsports = holdsports;
	}


class MyRunable implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				// TODO Auto-generated method stub
				Log.i("场地位置",arg0.getAddress());
				vh.splace.setText(arg0.getAddress());
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
}

}
