package com.example.sportfun;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.geminno.entity.FastMatch;
import com.geminno.entity.UserInfo;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Telephony.Sms.Conversations;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchAdapter extends BaseAdapter {

	ArrayList<UserInfo> userlist;
	Context context;
	LayoutInflater inflater;
	ViewHolder holder = null;

	public MatchAdapter(ArrayList<UserInfo> userlist, Context context) {
		super();
		this.userlist = userlist;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userlist.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return userlist.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int position, View covertView, ViewGroup vg) {

		if (covertView == null) {
			holder = new ViewHolder();
			// 加载用于Adapter布局的资源即item项View
			covertView = inflater.inflate(R.layout.match_buju, null);
			// 分别获得item布局中的控件，并存放在“盒子中”，以便重复使用
			holder.head = (ImageView) covertView.findViewById(R.id._head);
			holder.sex = (ImageView) covertView.findViewById(R.id._sex);
			holder.name = (TextView) covertView.findViewById(R.id._name);
			holder.weizhi = (TextView) covertView.findViewById(R.id._weizhi);
			holder.age = (TextView) covertView.findViewById(R.id._age);
			holder.sign = (TextView) covertView.findViewById(R.id._sign);
			holder.distance = (TextView) covertView
					.findViewById(R.id._distance);

			// 将“盒子”存放在v中，用于重复循环使用，重构新的item显示项
			covertView.setTag(holder);
		} else {
			// v不为空时，直接使用
			holder = (ViewHolder) covertView.getTag();
		}

		holder.name.setText(userlist.get(position).getUser_name());
		int age = getAgeByBirthday(userlist.get(position).getBirthday());
		holder.age.setText(age + "岁");
		if ("1".equals(userlist.get(position).getSex())) {
			holder.sex.setBackgroundResource((R.drawable.sex1));
		} else {
			holder.sex.setBackgroundResource((R.drawable.sex0));
		}
		holder.sign.setText(userlist.get(position).getSignature());
		holder.weizhi.setText(userlist.get(position).getUser_position());
		//使用百度地图地理编码
		//新建函数,新开子线程,
		holder.distance.setText("暂未实现距离");
		
		loadImagexutils(holder.head,userlist.get(position).getPhoto());
		//Log.i("头像Url",userlist.get(position).getPhoto());
		
		return covertView;

	}

	private void loadImagexutils(ImageView head, String photourl) {
		// TODO Auto-generated method stub
		BitmapUtils util = new BitmapUtils(context);
		util.display(head ,photourl);
	}

	/**
	 * 根据用户生日计算年龄
	 */
	public int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthday)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		}
		return age;
	}

	public static class ViewHolder {
		ImageView head, sex;
		TextView name, weizhi, sign, age, distance;
	}
}
