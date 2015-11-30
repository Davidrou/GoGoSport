package com.geminno.entity;

import java.io.Serializable;

//发起活动 实例
public class HoldSport implements Serializable{

	private int sport_id;
	private int sporttype_id;// 活动类型id
	private String title;
	private String sport_content1;
	private long hold_time;//发贴时间
	private long start_time;//活动开始时间
	private long end_time;//活动结束时间
	private int user_limit;
	private int user_id;// 发起人id
	private String place_longitude;// 场地经度
	private String place_latitude;// 场地纬度
	public int getSport_id() {
		return sport_id;
	}
	public void setSport_id(int sport_id) {
		this.sport_id = sport_id;
	}
	public int getSporttype_id() {
		return sporttype_id;
	}
	public void setSporttype_id(int sporttype_id) {
		this.sporttype_id = sporttype_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSport_content1() {
		return sport_content1;
	}
	public void setSport_content1(String sport_content1) {
		this.sport_content1 = sport_content1;
	}
	public long getHold_time() {
		return hold_time;
	}
	public void setHold_time(long hold_time) {
		this.hold_time = hold_time;
	}
	public long getStart_time() {
		return start_time;
	}
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}
	public long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}
	public int getUser_limit() {
		return user_limit;
	}
	public void setUser_limit(int user_limit) {
		this.user_limit = user_limit;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getPlace_longitude() {
		return place_longitude;
	}
	public void setPlace_longitude(String place_longitude) {
		this.place_longitude = place_longitude;
	}
	public String getPlace_latitude() {
		return place_latitude;
	}
	public void setPlace_latitude(String place_latitude) {
		this.place_latitude = place_latitude;
	}

	public HoldSport(int sport_id,int user_id,String sport_content1,String title, int user_limit,int sporttype_id,long start_time,long end_time,String place_longitude,String place_latitude) {
		super();
		this.sport_id = sport_id;
		this.user_id = user_id;
		this.sport_content1 = sport_content1;
		this.title = title;
		this.user_limit = user_limit;
		this.sporttype_id=sporttype_id;
		this.start_time = start_time;
		this.end_time = end_time;
		this.place_longitude = place_longitude;
		this.place_latitude = place_latitude;
	}
	public HoldSport() {
		super();
	}
	
	
	@Override
	public String toString() {
		return "HoldSport [sport_id=" + sport_id + ", sporttype_id="
				+ sporttype_id + ", title=" + title + ", sport_content1="
				+ sport_content1 + ", hold_time=" + hold_time + ", start_time="
				+ start_time + ", end_time=" + end_time + ", user_limit="
				+ user_limit + ", user_id=" + user_id + ", place_longitude="
				+ place_longitude + ", place_latitude=" + place_latitude + "]";
	}
	public HoldSport(int sporttype_id, String title,
			String sport_content1, long hold_time, long start_time,
			long end_time, int user_limit, int user_id, String place_longitude,
			String place_latitude) {
		super();
	
		this.sporttype_id = sporttype_id;
		this.title = title;
		this.sport_content1 = sport_content1;
		this.hold_time = hold_time;
		this.start_time = start_time;
		this.end_time = end_time;
		this.user_limit = user_limit;
		this.user_id = user_id;
		this.place_longitude = place_longitude;
		this.place_latitude = place_latitude;
	}
	
	public HoldSport(int sport_id, int sporttype_id, String title,
			String sport_content1, long hold_time, long start_time,
			long end_time, int user_limit, int user_id, String place_longitude,
			String place_latitude) {
		super();
		this.sport_id = sport_id;
		this.sporttype_id = sporttype_id;
		this.title = title;
		this.sport_content1 = sport_content1;
		this.hold_time = hold_time;
		this.start_time = start_time;
		this.end_time = end_time;
		this.user_limit = user_limit;
		this.user_id = user_id;
		this.place_longitude = place_longitude;
		this.place_latitude = place_latitude;
	}
	
	
}
