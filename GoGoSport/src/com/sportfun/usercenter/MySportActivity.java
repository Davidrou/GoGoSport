package com.sportfun.usercenter;

import java.util.ArrayList;

import com.example.sportfun.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class MySportActivity extends FragmentActivity implements
		OnPageChangeListener, TabListener {

	private ViewPager mPager;
	private ArrayList<Fragment> mfragmentList;
	// �����б�
	ArrayList<String> titleList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_pager);

		// getActionBar().hide();
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View topbar = getLayoutInflater().inflate(R.layout.actionbar_top_view,
				null);
		getActionBar().setCustomView(topbar, lp);

		getActionBar().setDisplayShowHomeEnabled(false);// 去掉导航
		getActionBar().setDisplayShowTitleEnabled(false);// 去掉标题
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setDisplayShowCustomEnabled(true);

		initViewPager();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);

		mfragmentList = new ArrayList<Fragment>();
		mfragmentList.add(new Myhold());
		mfragmentList.add(new Myjoin());

		mPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(),
				mfragmentList));
		mPager.setCurrentItem(0);
		mPager.addOnPageChangeListener(this);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.title_bar_shape));
		//
		String[] tabName = null;
		String[] temTabName = { "我发起 ", "我加入" };
		tabName = temTabName;

		for (int i = 0; i < tabName.length; i++) {
			ActionBar.Tab tab = getActionBar().newTab();
			tab.setText(tabName[i]);
			tab.setTabListener(this);
			tab.setTag(i);
			getActionBar().addTab(tab);
		}
	}

	//
	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public MyViewPagerAdapter(FragmentManager fManager,
				ArrayList<Fragment> arrayList) {
			super(fManager);
			this.list = arrayList;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Fragment getItem(int arg0) {

			return list.get(arg0);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == ((Fragment) obj).getView();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// ����ViewPager��ʱ���������Ӧ��ActionBar Tab��ѡ��
		getActionBar().getTabAt(arg0).select();

	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1) {
		if (tab.getTag() == null)
			return;
		// ѡ��tab,����ѡ�
		int index = ((Integer) tab.getTag()).intValue();
		if (mPager != null && mPager.getChildCount() > 0
				&& mfragmentList.size() > index)
			mPager.setCurrentItem(index);
	}

	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	public void back(View view) {
		finish();
	}
}
