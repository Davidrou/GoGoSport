package com.geminno.entity;

public class FastMatch {

	private int user_id;// 用户
	private String last_latitude;// 最后一次速配的时间
	private String last_longitude;// 最后一次速配的时间
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getLast_latitude() {
		return last_latitude;
	}
	public void setLast_latitude(String last_latitude) {
		this.last_latitude = last_latitude;
	}
	public String getLast_longitude() {
		return last_longitude;
	}
	public void setLast_longitude(String last_longitude) {
		this.last_longitude = last_longitude;
	}
	public long getLast_time() {
		return last_time;
	}
	public void setLast_time(long last_time) {
		this.last_time = last_time;
	}
	public int getSporttype_id() {
		return sporttype_id;
	}
	public void setSporttype_id(int sporttype_id) {
		this.sporttype_id = sporttype_id;
	}
	private long last_time;
	private int sporttype_id;// 选中的活动类型
	public FastMatch(int user_id, String last_latitude, String last_longitude,
			long last_time, int sporttype_id) {
		super();
		this.user_id = user_id;
		this.last_latitude = last_latitude;
		this.last_longitude = last_longitude;
		this.last_time = last_time;
		this.sporttype_id = sporttype_id;
	}
	
	

}
