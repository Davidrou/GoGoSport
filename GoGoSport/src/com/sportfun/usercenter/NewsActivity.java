package com.sportfun.usercenter;

import java.util.ArrayList;

import com.example.sportfun.R;
import com.geminno.entity.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class NewsActivity extends Activity implements OnItemLongClickListener {

	MySQLiteOpenHelper mysqlite;
	private ListView lv;
	private ArrayList<Collection> collections;
	private NewsAdapter adapter;
	private Collection collection;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		lv = (ListView) this.findViewById(R.id.listView);

		mysqlite = new MySQLiteOpenHelper(this, "sportfun.db");
		collections = new ArrayList<Collection>();
		cursor = mysqlite.getReadableDatabase().rawQuery("select * from collection", null);
		while (cursor.moveToNext()) {
			collection = new Collection();
			collection.set_id(cursor.getInt(0));
			collection.setNews_url(cursor.getString(1));
			collection.setNews_picUrl(cursor.getString(2));
			collection.setNews_title(cursor.getString(3));
			collections.add(collection);
		}
		adapter = new NewsAdapter(collections, this);
		lv.setAdapter(adapter);

		// listview的item点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			// 点击listview的item，跳转到对应的webview
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(NewsActivity.this, WebViewActivity.class);
				// 将news_url传入到webview中加载
				intent.putExtra("news_url", collections.get(position).getNews_url());
				startActivity(intent);
			}
		});

		// 设置listview的item长按事件
		lv.setOnItemLongClickListener(this);

	}

	// 定义删除数据的方法
	private void delete(int id) {
		mysqlite.getWritableDatabase().delete("collection", "_id = ? ", new String[] { id + "" });
		collections.remove(p);
		adapter.notifyDataSetChanged();
		System.out.println("adapter更新了");
	}

	int p;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
		// 定义一个对话框
		p = position;
		AlertDialog.Builder builder = new Builder(NewsActivity.this);
		// 设置对话框的属性
		builder.setTitle("删除").setMessage("确认删除吗？").setPositiveButton("确认", new OnClickListener() {

			// 设置积极按钮的点击事件
			@Override
			public void onClick(DialogInterface dialog, int which) {
				collection = collections.get(p);// 获取当前位置
				int id = collection.get_id();// 根据位置得到相应的id
				delete(id);
			}
		}).setNegativeButton("取消", new OnClickListener() {

			// 设置消极按钮的点击事件
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create().show();
		return false;
	}

	public void back(View v){
		finish();
	}
}
