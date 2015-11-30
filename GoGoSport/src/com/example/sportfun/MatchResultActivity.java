package com.example.sportfun;

import java.util.ArrayList;

import com.geminno.entity.UserInfo;
import com.sport.chat.ChatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MatchResultActivity extends Activity {

	ArrayList<UserInfo> userlist;
	MatchAdapter madapter;
	ListView listview;
	MatchBean matchbean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_result);
		userlist = new ArrayList<UserInfo>();

		listview = (ListView) this.findViewById(R.id.listView1);

		// 接收数据
		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();
		final ArrayList<UserInfo> userlist = (ArrayList<UserInfo>) b
				.getSerializable("user");
		Log.i("速配结果个数",userlist.size()+"");

		madapter = new MatchAdapter(userlist, this);
		listview.setAdapter(madapter);
		
		//临时的
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MatchResultActivity.this,FriendActivity.class);
				 intent.putExtra("user", userlist.get(position));
				startActivity(intent);
			}
		});;

	}
}
