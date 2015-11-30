package com.example.readnews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.readnews.PullDownView.OnPullDownListener;
import com.example.sportfun.R;
import com.geminno.entity.News;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @auther 廖汉文
 * @auther ShawnCoderLee
 */
public class ReadFragment extends Fragment implements OnPullDownListener,

OnItemClickListener {
	private static final String TAG = "read";
	
	
	RequestQueue requsetQueue;
	private static final String URl = "http://route.showapi.com/196-1";
	private static final String showapi_appid = "10599";
	private static final String showapi_sign = "b9dd79cbabbe4459a72be3fc4c3c0b2a";

	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;

	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;

	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE = 2;
	private int currentPage = 2;// append加载的页码

	private ListView mListView;

	private MyAdapter mAdapter;

	private PullDownView mPullDownView;

	//private List<String> mStrings = new ArrayList<String>();

	private ArrayList<News> newslist = new ArrayList<News>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Volley获取新闻的相关变量
		requsetQueue = Volley.newRequestQueue(getActivity());
		View readView = inflater.inflate(R.layout.read_list, container, false);
		/**
		 * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
		 */
		mPullDownView = (PullDownView) readView
				.findViewById(R.id.pull_down_view);

		// listview点击监听
		mListView = mPullDownView.getListView();
		mListView.setOnItemClickListener(this);
		// listview设置adapter
		// ??????????这个地方的上下文可能会出问题???????????
		mAdapter = new MyAdapter(newslist, getActivity());
		mListView.setAdapter(mAdapter);

		// ** 设置可以自动获取更多 ，滑到最后一个自动获取， 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		mPullDownView.setOnPullDownListener(this);

		// 隐藏 并禁用尾部
		mPullDownView.setHideFooter();

		// 显示并启用自动获取更多
		mPullDownView.setShowFooter();

		/** 隐藏并且禁用头部刷新 */
		mPullDownView.setHideHeader();

		/** 显示并且可以使用头部刷新 */
		mPullDownView.setShowHeader();

		/** 之前 网上很多代码 都会导致刷新事件 跟 上下文菜单同时弹出 这里做测试。。。已经解决 */
		mListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
					}
				});

		//mListView.setOnItemClickListener(this);

		// 加载数据 本类使用
		loadData();
		return readView;
	}

	private void loadData() {
		
		// ?showapi_appid=10599&
		// showapi_timestamp=20151014143605&num=10&page=1&showapi_sign=b9dd79cbabbe4459a72be3fc4c3c0b2a
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String loadstamp = sdf.format(new Date());
		String loadurl = URl
				+ "?showapi_appid="
				+ showapi_appid
				+ "&showapi_timestamp="
				+ loadstamp
				+ "&num=10&page=1&showapi_sign=b9dd79cbabbe4459a72be3fc4c3c0b2a";
		//Log.e("leenews2", loadurl);
		StringRequest sq_loadData = new StringRequest(loadurl,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						ArrayList<News> n = ParseResponse(response);
						//Log.e("leenews2", "size" + n.size());
						Log.e("leenews2", "收到新闻"+n.size()+"条");
						Message msg = mUIHandler
								.obtainMessage(WHAT_DID_LOAD_DATA);
						msg.obj = n;
						msg.sendToTarget();
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// Toast.makeText(context, text, duration);
					}
				});

		requsetQueue.add(sq_loadData);
	}

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 数据加载完毕
			case WHAT_DID_LOAD_DATA: {
				if (msg.obj != null) {
					ArrayList<News> initNews = (ArrayList<News>) msg.obj;
					if (!initNews.isEmpty()) {
						// mStrings是List<>类型
						newslist.addAll(initNews);
						mAdapter.notifyDataSetChanged();
					}
				}
				// 诉它数据加载完毕;
				break;
			}

			case WHAT_DID_REFRESH: {
				// 在这里添加新获取的数据，数据来自子线程，通过handler发过来
				// 获取方法
				ArrayList<News> body = (ArrayList<News>) msg.obj;
				// Log.i("leenews", "body: " + body + ".");
				// 在位置0添加body的内容
				newslist.clear();
				newslist.addAll(body);
				mAdapter.notifyDataSetChanged();
				Log.i(TAG, "收到刷新成功的msg");
				// 告诉它更新完毕
				break;

			}

			case WHAT_DID_MORE: {
				// 数据加载更多完毕
				ArrayList<News> body = (ArrayList<News>) msg.obj;
				newslist.addAll(body);
				mAdapter.notifyDataSetChanged();
				Log.i(TAG, "收到更多的msg");
				break;
			}
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// ??上下文???
		if(position<newslist.size()){
			
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra("news_url", newslist.get(position).getUrl());
			intent.putExtra("news_picUrl", newslist.get(position).getPicUrl());
			intent.putExtra("news_title", newslist.get(position).getTitle());
			startActivity(intent);
		}
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
	@Override
	public void onRefresh() {
		Log.i(TAG, "进入onRefresh");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String loadstamp = sdf.format(new Date());
		String loadurl = URl + "?showapi_appid=" + showapi_appid + "&showapi_timestamp=" + loadstamp
				+ "&num=10&page=1&showapi_sign=b9dd79cbabbe4459a72be3fc4c3c0b2a";
		StringRequest sq_refreshData = new StringRequest(loadurl, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
			//	Log.e("leenews2", response);
				ArrayList<News> n = ParseResponse(response);
				//Log.e("leenews2", "size" + n.size());
				// 刷新完成后通知主线程更新listview的
				Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
				msg.obj = n;
				/** 关闭 刷新完毕 ***/
				mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码
				// 这个地方有点疑问adapter
				msg.sendToTarget();
				Log.i(TAG, "收到刷新response的msg");
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				// Toast.makeText(context, text, duration);
			}
		});

		requsetQueue.add(sq_refreshData);

	}

	@Override
	public void onMore() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String loadstamp = sdf.format(new Date());
		String loadurl = URl + "?showapi_appid=" + showapi_appid
				+ "&showapi_timestamp=" + loadstamp + "&num=10&page="
				+ currentPage + "&showapi_sign=" + showapi_sign;
		//Log.e("leenews2", loadurl);
		StringRequest sq_moreData = new StringRequest(loadurl,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						//Log.e("leenews2", response);
						ArrayList<News> n = ParseResponse(response);
						//Log.e("leenews2", "size" + n.size());
						// 告诉它 获取更多 完毕 这个事线程安全的 可看源代码
						mPullDownView.notifyDidMore();
						Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
						msg.obj = n;
						msg.sendToTarget();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Toast.makeText(context, text, duration);
					}
				});
		currentPage++;
		requsetQueue.add(sq_moreData);

	}

	private ArrayList<News> ParseResponse(String res) {
		ArrayList<News> list = new ArrayList<News>();
		// Log.e("point", response);
		String title = null;
		String description = null;
		String picUrl = null;
		String url = null;
		JSONObject newsbody = null;
		// newslist = new ArrayList<News>();
		try {
			newsbody = new JSONObject(res).getJSONObject("showapi_res_body");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < res.length(); i++) {
				title = newsbody.getJSONObject(i + "").getString("title");
				description = newsbody.getJSONObject(i + "").getString("description");
				picUrl = newsbody.getJSONObject(i + "").getString("picUrl");
				url = newsbody.getJSONObject(i + "").getString("url");
				if (i == res.length() - 1) {
					News news = new News(title, description, picUrl, url);
					list.add(news);
				}
				if (newsbody.getJSONObject(i + "").getString("picUrl")
						.equals(newsbody.getJSONObject(i + 1 + "").getString("picUrl"))) {
					continue;
				} else {
					News news = new News(title, description, picUrl, url);
					list.add(news);
				}
			}
		} catch (Exception e) {
			// Log.e("leenews", e.getMessage() + "ErroeException");
		}
		// Log.e("leenews", list.size() + "size");
		return list;
	}
}
