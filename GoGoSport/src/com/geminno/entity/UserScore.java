package com.geminno.entity;

public class UserScore {

	private int user_id;// 用户id
	private int sporttype_id;// 活动类型id
	private int score;

	public UserScore(int user_id, int sporttype_id, int score) {
		super();
		this.user_id = user_id;
		this.sporttype_id = sporttype_id;
		this.score = score;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getSporttype_id() {
		return sporttype_id;
	}

	public void setSporttype_id(int sporttype_id) {
		this.sporttype_id = sporttype_id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
