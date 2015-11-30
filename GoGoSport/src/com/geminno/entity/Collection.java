package com.geminno.entity;

public class Collection {

	private int user_id;
	private String news_url;
	private long collect_time;
	public Collection() {
		super();
	}

	public Collection(int user_id, String news_url, long collect_time) {
		super();
		this.user_id = user_id;
		this.news_url = news_url;
		this.collect_time = collect_time;
	}
	public Collection(Integer _id, String news_url, String news_picUrl, String news_title) {
		super();
		this._id = _id;
		this.news_url = news_url;
		this.news_picUrl = news_picUrl;
		this.news_title = news_title;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getNews_url() {
		return news_url;
	}

	public void setNews_url(String news_url) {
		this.news_url = news_url;
	}

	public long getCollect_time() {
		return collect_time;
	}

	public void setCollect_time(long collect_time) {
		this.collect_time = collect_time;
	}

	private Integer _id;
	private String news_picUrl;
	private String news_title;

	public Collection(String news_url, Integer _id, String news_picUrl,
			String news_title) {
		super();
		this.news_url = news_url;
		this._id = _id;
		this.news_picUrl = news_picUrl;
		this.news_title = news_title;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getNews_picUrl() {
		return news_picUrl;
	}

	public void setNews_picUrl(String news_picUrl) {
		this.news_picUrl = news_picUrl;
	}

	public String getNews_title() {
		return news_title;
	}

	public void setNews_title(String news_title) {
		this.news_title = news_title;
	}

}
