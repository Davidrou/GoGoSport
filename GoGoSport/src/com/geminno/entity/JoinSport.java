package com.geminno.entity;

public class JoinSport {

	private int user_id;
	private int sport_id;

	public JoinSport(int user_id, int sport_id) {
		super();
		this.user_id = user_id;
		this.sport_id = sport_id;
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

	public void setSprot_id(int sprot_id) {
		this.sport_id = sprot_id;
	}

}
