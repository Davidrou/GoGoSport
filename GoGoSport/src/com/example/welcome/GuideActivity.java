package com.example.welcome;

import java.util.ArrayList;
import java.util.List;

import com.example.login.LoginActivity;
import com.example.sportfun.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class GuideActivity extends Activity {

	List<ImageView> arraylist;
	int a;//标记最后一页
	ViewPager vp;
	Button bt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_guide);
		
		vp=(ViewPager) this.findViewById(R.id.viewpager);
		bt=(Button) this.findViewById(R.id.tiyan);
		vp.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				a=arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				if(a==2){//（从0开始，第三页就是2）
					bt.setVisibility(View.VISIBLE);  
					bt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							startActivity(new Intent(GuideActivity.this,LoginActivity.class));
							finish();
						}
					});
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
//viewpager
		arraylist=new ArrayList<ImageView>();
		
		ImageView iv=new ImageView(getApplicationContext());//鍒涘缓瀵硅薄
		iv.setBackgroundResource(R.drawable.start_1);//鎵嬪姩娣诲姞ImageView
		ImageView iv2=new ImageView(getApplicationContext());
		iv2.setBackgroundResource(R.drawable.start_2);
		ImageView iv3=new ImageView(getApplicationContext());
		iv3.setBackgroundResource(R.drawable.start_3);
//		
		arraylist.add(iv);
		arraylist.add(iv2);
		arraylist.add(iv3);
		//Button bt=(Button) this.findViewById(R.id.tiyan);
		
		vp.setAdapter(new PicAdapter());
	}

	class PicAdapter extends PagerAdapter{

		
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arraylist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
//
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			((ViewPager)container).addView(arraylist.get(position));
			 return arraylist.get(position);
			
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager)container).removeView((View)object);
			
		}
	}
	
}
