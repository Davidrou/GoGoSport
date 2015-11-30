package com.example.total;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.geminno.entity.HoldSport;

//自定义比较器
public class MyComparator implements Comparator<HoldSport> {

	@Override
	public int compare(HoldSport arg0, HoldSport arg1) {//升序
		// TODO Auto-generated method stub
		long currentTime=System.currentTimeMillis();
		long a1=Math.abs(currentTime-arg0.getStart_time());
		long a2=Math.abs(currentTime-arg0.getStart_time());
		
		if(a1>a2){
			return -1;
		}else if(a1<a2){
			return 1;
		}

		return 0;
	}
	
	
}
