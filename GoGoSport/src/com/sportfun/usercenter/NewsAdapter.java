package com.sportfun.usercenter;

import java.util.ArrayList;

import com.example.sportfun.R;
import com.geminno.entity.Collection;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	private ArrayList<Collection> collections;
	Context context;
	LayoutInflater inflater;
	ViewHolder holder;
	
	public NewsAdapter(ArrayList<Collection> collections, Context context) {
		this.collections = collections;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void onDateChange(ArrayList<Collection> collections) {
		this.collections = collections;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return collections.size();
	}

	@Override
	public Object getItem(int position) {
		return collections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_news, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.picUrl);
			holder.tv = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(collections.get(position).getNews_title());
		holder.iv.setTag(collections.get(position).getNews_picUrl());
		loadImagexutils(collections.get(position).getNews_picUrl(), holder.iv);
		return convertView;
	}
	
	private void loadImagexutils(String url, ImageView sellersmallimg) {
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(sellersmallimg, url);
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
