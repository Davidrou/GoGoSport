package com.geminno.entity;

public class Notice {
	private int user_id;// 
	private int userother_id;// 

	public Notice(int user_id, int userother_id) {
		super();
		this.user_id = user_id;
		this.userother_id = userother_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getUserother_id() {
		return userother_id;
	}

	public void setUserother_id(int userother_id) {
		this.userother_id = userother_id;
	}

}
