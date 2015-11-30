package com.geminno.entity;

public class SportComment {

	private int user_id;// 
	private int sport_id;// 
	private String comment_text;
	private String comment_pic;
	private long comment_time;

	public SportComment(int user_id, int sport_id, String comment_text,
			String comment_pic, long comment_time) {
		super();
		this.user_id = user_id;
		this.sport_id = sport_id;
		this.comment_text = comment_text;
		this.comment_pic = comment_pic;
		this.comment_time = comment_time;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getSport_id() {
		return sport_id;
	}

	public void setSport_id(int sport_id) {
		this.sport_id = sport_id;
	}

	public String getComment_text() {
		return comment_text;
	}

	public void setComment_text(String comment_text) {
		this.comment_text = comment_text;
	}

	public String getComment_pic() {
		return comment_pic;
	}

	public void setComment_pic(String comment_pic) {
		this.comment_pic = comment_pic;
	}

	public long getComment_time() {
		return comment_time;
	}

	public void setComment_time(long comment_time) {
		this.comment_time = comment_time;
	}

}
