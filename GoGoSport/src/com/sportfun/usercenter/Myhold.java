package com.sportfun.usercenter;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.example.total.SportDetailActivity;
import com.geminno.entity.HoldSport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.utils.Log;

//AndroidSportByUserId
public class Myhold extends Fragment {
	ListView lv;

	private List<HoldSport> holdsports = new ArrayList<HoldSport>();
	private LvAdapter adapter;
	private RequestQueue requestQueue;
	private List<HoldSport> hots = new ArrayList<HoldSport>();// 解析得到的对象集合

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.holdfragment, container,
				false);//
	
		
		
		lv = (ListView) rootView.findViewById(R.id.holdlistview);
		
		
		// 准备、设置数据源
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("user", Activity.MODE_PRIVATE);
		int user_id = sharedPreferences.getInt("user_id", 0);
		Log.i("用户的id:",user_id + "");
		Properties properties = URLPropertiesUtil.getProperties(getActivity());
		String u = properties.getProperty("u");
		String url = u + "AndroidSportByUserId?user_id=" + user_id;
		
		
		// 1.初始化RequestQueue对象
		requestQueue = Volley.newRequestQueue(getActivity());
		// 根据找到的活动ID 查出对应活动的内容（holdsport表）
		// 2. 发送StringRequest请求（加入的活动）
		StringRequest hsRequsset = new StringRequest(Method.GET, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {

						// geson解析集合对象
						Gson gson = new Gson();
						Type type = new TypeToken<List<HoldSport>>() {
						}.getType();
						hots = gson.fromJson(response, type);

						for (int i = 0; i < hots.size(); i++) {
							HoldSport hs = hots.get(i);
							// 获取所有字段
							String title = hs.getTitle();
							int user_id = hs.getUser_id();
							int sport_id = hs.getSport_id();
							int sporttype_id = hs.getSporttype_id();
							int user_limit = hs.getUser_limit();
							String sport_content1 = hs.getSport_content1();
							long hold_time = hs.getHold_time();
							long start_time = hs.getStart_time();
							long end_time = hs.getEnd_time();
							String place_longitude = hs.getPlace_longitude();
							String place_latitude = hs.getPlace_latitude();
							// 封装对象，放入集合
							HoldSport hoss = new HoldSport(sport_id,
									sporttype_id, title, sport_content1,
									hold_time, start_time, end_time,
									user_limit, user_id, place_longitude,
									place_latitude);
							holdsports.add(hoss);// 将解析得到的整个对象放入
						}
						adapter.notifyDataSetChanged();

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getActivity(), "null",
								Toast.LENGTH_SHORT).show();
					}
				});

		// 3.将StringRequest加入到RequestQueue当中
		requestQueue.add(hsRequsset);

		adapter = new LvAdapter(holdsports);
		lv.setAdapter(adapter);

		// 设置item点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 传对应item的用户对象查找出活动详情（三个头包括刷新）
				Intent intent = new Intent(getActivity(),
						SportDetailActivity.class); // 跳转页面
				intent.putExtra("hosp", holdsports.get(position));
				startActivity(intent);

			}
		});

		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	class LvAdapter extends BaseAdapter {
		
		private List<HoldSport> holdsports;
		LayoutInflater inflater;

		public LvAdapter(List<HoldSport> holdsports) {
			super();
			this.holdsports = holdsports;
			inflater = LayoutInflater.from(getActivity());
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
		ViewHolder vh=null;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
		vh	 = new ViewHolder();
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_hold, null);
				// 找控件
				vh.sporttype = (ImageView) convertView
						.findViewById(R.id.sporttype);
				vh.title = (TextView) convertView.findViewById(R.id.title);
				vh.holdtime = (TextView) convertView
						.findViewById(R.id.hold_time);
				vh.startEndtime = (TextView) convertView
						.findViewById(R.id.startEndtime);
				convertView.setTag(vh);

			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			// 设置数据
			vh.title.setText(holdsports.get(position).getTitle());
			// vh.joinnum.setText(holdsports.get(position).getSport_content1());
			// vh.limitnum.setText(holdsports.get(position).getUser_limit()+"");

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

			long holdTime = holdsports.get(position).getHold_time();
			long startTime = holdsports.get(position).getStart_time();
			long endTime = holdsports.get(position).getEnd_time();
			SimpleDateFormat time = new SimpleDateFormat(
					"yyyy.MM.dd.E HH:mm:ss");
			String h_t = time.format(holdTime);
			String s_t = time.format(startTime);
			String e_t = time.format(endTime);
			String hold_time = h_t.substring(0, 10);
			String start_time = s_t.substring(14, 19);
			String end_time = e_t.substring(14, 19);


			vh.holdtime.setText(hold_time);
			vh.startEndtime.setText(start_time + "--" + end_time);
			
			return convertView;
		}

		class ViewHolder {
			ImageView sporttype;
			TextView title;
			TextView splace;//
			TextView joinnum;
			TextView limitnum;
			TextView startEndtime;
			TextView holdtime;
		}

	}
}
