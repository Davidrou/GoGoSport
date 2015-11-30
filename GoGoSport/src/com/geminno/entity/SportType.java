package com.geminno.entity;

//这个表其实可以不要,其充当数组的作用
public class SportType {
	private int sporttype_id;
	private String sport_name;
	
	public SportType(int sporttype_id, String sport_name) {
		super();
		this.sporttype_id = sporttype_id;
		this.sport_name = sport_name;
	}
	
	public int getSporttype_id() {
		return sporttype_id;
	}
	public void setSporttype_id(int sporttype_id) {
		this.sporttype_id = sporttype_id;
	}
	public String getSport_name() {
		return sport_name;
	}
	public void setSport_name(String sport_name) {
		this.sport_name = sport_name;
	}
	
}
