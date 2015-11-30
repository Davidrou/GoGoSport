package com.example.readnews;

import java.util.ArrayList;





import com.example.sportfun.R;
import com.geminno.entity.News;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author 廖汉文
 * @author 李印晓
 * 
 */

public class MyAdapter extends BaseAdapter {
	
	// listView的item个数
	ArrayList<News> newslist;// 新闻集合数据，传到adapter
	Context context;
	LayoutInflater inflater;
	ViewHolder holder;

	public MyAdapter(ArrayList<News> newslist, Context context) {
		this.newslist = newslist;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void onDateChange(ArrayList<News> newslist) {
		this.newslist = newslist;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return newslist.size();
	}

	@Override
	public Object getItem(int position) {
		return newslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.picUrl);
			holder.tv = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(newslist.get(position).getTitle());
		loadImagexutils(newslist.get(position).getPicUrl(), holder.iv);
		return convertView;
	}
	
	private void loadImagexutils(String url, ImageView sellersmallimg) {
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(sellersmallimg, url);
	}

	/*
	 * 将item里的控件封装在ViewHolder类中
	 */
	public static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
