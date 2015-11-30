package com.example.total;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportfun.R;
import com.geminno.entity.News;


class VpAdapter extends PagerAdapter {

	private List<News> news;
	private Context c;
	LayoutInflater inflater;
	
	public VpAdapter(List<News> news, Context c) {
		this.news = news;
		this.c = c;
		inflater = LayoutInflater.from(c);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2000;//
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == (View)arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		View view = inflater.inflate(R.layout.vpitem, null);
		ImageView iv = (ImageView) view.findViewById(R.id.picUrl);
		TextView tv=(TextView) view.findViewById(R.id.title);
		tv.setText(news.get(position%news.size()).getTitle());
		iv.setBackgroundResource(news.get(position%news.size()).getPic());

		container.addView(view);
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				Intent intent = new Intent(c, com.sportfun.usercenter.WebViewActivity.class);
				intent.putExtra("news_url",news.get(position%news.size()).getPicUrl());
				c.startActivity(intent);
			}
		});
		
		return view;
	}
	


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}


}