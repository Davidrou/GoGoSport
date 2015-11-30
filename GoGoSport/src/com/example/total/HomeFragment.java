package com.example.total;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.geminno.entity.HoldSport;
import com.geminno.entity.News;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 
 * @author 付丽娟
 * 
 */
public class HomeFragment extends Fragment{

	private List<News> newslist = new ArrayList<News>();
	private ViewPager vp;
	private VpAdapter vpAdapter;
	
	SharedPreferences sharedPreferences;

	private View view, header, header1;
	private PullToRefreshListView refreshView;
	private ListView lv;;
	private List<HoldSport> holdsports = new ArrayList<HoldSport>();//最初显示的
	private LvAdapter adapter;
	RequestQueue requestQueue;
	List<HoldSport> hots;// 解析得到的对象集合

	// listview
	Properties properties = URLPropertiesUtil.getProperties(getActivity());
	String u = properties.getProperty("u");
	String listUrl = u + "AndroidQuerySportByPage?pageNo=1&pageSize=4";//自定义一页显示4条数据

	// viewpager
	// 处理viewpager消息
		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				vp.setCurrentItem(vp.getCurrentItem() + 1);
				handler.sendEmptyMessageDelayed(0, 4000);// 轮播间隔为4s
			};
		};
	int recodeSport;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	

		// 1.初始化RequestQueue对象
		requestQueue = Volley.newRequestQueue(getActivity());
		// 类库相关
		view = inflater.inflate(R.layout.myfragment, null);
		refreshView = (PullToRefreshListView) view
				.findViewById(R.id.refreshListview);
		refreshView.setMode(Mode.BOTH);// 两端刷新

		refreshView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		refreshView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("加载中");
		refreshView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放加载");

		refreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});

		// 加头！！！
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.MATCH_PARENT);//
		header = inflater.inflate(R.layout.viewpager, refreshView, false);
		header1 = inflater.inflate(R.layout.listtitle, refreshView, false);

		vp = (ViewPager) header.findViewById(R.id.viewpager);
		initData();// viewpager数据填充

		header.setLayoutParams(layoutParams);
		header1.setLayoutParams(layoutParams);
		lv = refreshView.getRefreshableView();
		lv.addHeaderView(header);// 放vp
		lv.addHeaderView(header1);// 放“活动列表”

		// 准备lv数据源

		// 2. 发送StringRequest请求（分页显示活动列表）
		StringRequest listRequsset = new StringRequest(Method.GET, listUrl,
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
							// 获取字段
							String title = hs.getTitle();
							int user_id = hs.getUser_id();// 给查询用户名、头像使用
							String descripe = hs.getSport_content1();// 活动详情页面显示
							int sport_id = hs.getSport_id();// 查找加入活动的人数
							int limitnum = hs.getUser_limit();
							int sporttype_id = hs.getSporttype_id();
							long start_time = hs.getStart_time();
							long end_time = hs.getEnd_time();
							long current_time= System.currentTimeMillis();
							String place_longitude = hs.getPlace_longitude();
							String place_latitude = hs.getPlace_latitude();
							System.out.println(current_time+"活动状态"+end_time);
							if(current_time < end_time){
							// 封装对象，放入集合
							HoldSport hos = new HoldSport(sport_id, user_id,
									descripe, title, limitnum, sporttype_id,
									start_time, end_time, place_longitude,
									place_latitude);
							holdsports.add(hos);// 将解析得到的对象放入	
							}
						}											
						adapter.setHoldsports(holdsports);
						adapter.notifyDataSetChanged();

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
					}
				});

		// 3.将StringRequest加入到RequestQueue当中
		requestQueue.add(listRequsset);
		// 设置lv数据源
		sharedPreferences = getActivity().getSharedPreferences("location",Activity.MODE_PRIVATE);
		//将获取到的集合数据进行排序（按时间）
		Collections.sort(holdsports,new MyComparator());
		adapter = new LvAdapter(holdsports, getActivity(),sharedPreferences);
		refreshView.setAdapter(adapter);

		// 设置item点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HoldSport hosp = new HoldSport();
				hosp = holdsports.get(position - 3);// 传对应item的用户对象查找出活动详情（三个头包括刷新）
				Intent intent = new Intent(getActivity(),
						SportDetailActivity.class); // 跳转页面
				intent.putExtra("hosp", hosp);
				startActivity(intent);

			}
		});	
		
//发送请求，查出数据库活动的总数
		String recordUrl=u+"AndroidCountSport";
		
		// 2. 发送StringRequest请求（分页显示活动列表）
		StringRequest recordRequsset = new StringRequest(Method.GET, recordUrl,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						
						recodeSport=Integer.parseInt(response);
						System.out.println(recodeSport+"xxxxxxxxxxx");
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
					}
				});

		// 3.将StringRequest加入到RequestQueue当中
		requestQueue.add(recordRequsset);
		
		return view;	
		
	}


	// 上拉下拉处理
	List<HoldSport> holds;// 接收刷新的数据
	String testUrl = u + "AndroidQuerySportByPage?";
	String Url;// 拼接而来的发送请求URL
	int pageNo=2 ;//加载时发送的pageNo

	private class GetDataTask extends AsyncTask<Void, Void, List<HoldSport>> {// 定义返回值的类型

		// 后台处理部分
		@Override
		protected List<HoldSport> doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			// String str="Added after refresh...I add";//设置刷新后第一条的内容
			holds = new ArrayList<HoldSport>();

			if (refreshView.isHeaderShown()) {// 如果下拉就刷新
			
				Url = testUrl + "pageNo=1&pageSize=4";// 总是显示第一页内容（一页三条）
				pageNo=2;
			} else if (refreshView.isFooterShown()) {// 上拉就加载
				
				Url = testUrl + "pageNo="+pageNo+"&pageSize=4" ;
				pageNo++;
			}
			
			// 2. 发送StringRequest请求（分页显示活动列表）
			StringRequest testRequsset = new StringRequest(Method.GET, Url,
					new Listener<String>() {

						@Override
						public void onResponse(String response) {

							// geson解析集合对象
							Gson gson = new Gson();
							Type type = new TypeToken<List<HoldSport>>() {
							}.getType();
							List<HoldSport> hotss = gson.fromJson(response,
									type);

							for (int i = 0; i < hotss.size(); i++) {
								HoldSport hs = hotss.get(i);
								// 获取字段
								String title = hs.getTitle();
								int user_id = hs.getUser_id();// 用来查询用户名
								String descripe = hs.getSport_content1();// 活动详情页面显示
								int sport_id = hs.getSport_id();// 查找加入活动的人数
								int limitnum = hs.getUser_limit();
								int sporttype_id = hs.getSporttype_id();
								long start_time = hs.getStart_time();
								long end_time = hs.getEnd_time();
								long current_time= System.currentTimeMillis();
								String place_longitude = hs
										.getPlace_longitude();
								String place_latitude = hs.getPlace_latitude();
								if(current_time < end_time){
								// 封装对象，放入集合
								HoldSport hoss = new HoldSport(sport_id,
										user_id, descripe, title, limitnum,
										sporttype_id, start_time, end_time,
										place_longitude, place_latitude);
												
								if(pageNo>2){
									
									holdsports.add(hoss);// 将解析得到的加到原来的后面
									
								}else{
									holds.add(hoss);//将刷新获取到的放入一个新的集合	
									
									adapter.setHoldsports(holds);
									adapter.notifyDataSetChanged();
									holdsports=holds;//刷新后再加载，集合只存刷新后的数据
								}
								}
							}
							
						if((pageNo-1)*4>=recodeSport){
							Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
							refreshView.getRefreshableView().setSelection((pageNo-1)*4);//指定跳转的item位置
						
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

							Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
						}
					});

			requestQueue.add(testRequsset);

			if(pageNo>2){
				Collections.sort(holdsports,new MyComparator());
				return holdsports;
			}else{
				Collections.sort(holdsports,new MyComparator());
				return holds;
			}
		}

		// 这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		// 根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值 ֵ
		@Override
		protected void onPostExecute(List<HoldSport> result) {

			// 重新设置item点击事件
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					HoldSport hosp = new HoldSport();
					// 列表的个数也要顾及到加载增加的（集合为加载后设置的）
					hosp = holdsports.get(position - 3);// 传对应item的用户对象查找出活动详情（三个头包括刷新）
					Intent intent = new Intent(getActivity(),
							SportDetailActivity.class); // 跳转页面
					intent.putExtra("hosp", hosp);
					startActivity(intent);

				}
			});

			// 通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
			adapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();

			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
		}
	}

	// 初始化vp数据源
	public void initData() {
		newslist.add(new News("http://sports.ifeng.com/a/20151030/46049265_0.shtml",R.drawable.first_lunbo,"叶振南质疑裁判打分结果 晒数据暗示中国受不公待遇"));
		newslist.add(new News("http://sports.ifeng.com/a/20151030/46049920_0.shtml",R.drawable.second_lunbo,"拜尔斯罕见失误连连仍夺全能冠军 美国包揽前两名"));
		newslist.add(new News("http://sports.ifeng.com/a/20151028/46022481_0.shtml",R.drawable.thrid_lunbo,"河南主帅：朱婷是我们培养的 为家乡效力义不容辞"));
		vpAdapter=new VpAdapter(newslist, getContext());
		vp.setAdapter(vpAdapter);
		int centervalue = 1000;// 实际上，给它默认了2000的中间值
		int value = centervalue % newslist.size();
		vp.setCurrentItem(centervalue - value);// 使得当前页始终为第一个
		handler.sendEmptyMessageDelayed(0, 3000);// 发送空消息
	}
	
}

