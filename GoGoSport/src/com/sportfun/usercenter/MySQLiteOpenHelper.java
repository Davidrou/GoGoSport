package com.sportfun.usercenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	final String CREATE_TABLE_SQL = "create table collection(_id integer primary key autoincrement, news_url varchar(255), news_picUrl varchar(255), news_title varchar(50))";

	public MySQLiteOpenHelper(Context context, String name) {
		super(context, "sportfun.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 第一次使用数据库时自动建表
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
