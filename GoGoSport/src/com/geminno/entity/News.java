package com.geminno.entity;

public class News {

	private String title;
	private String description;
	private String picUrl;
	private String url;
	private int pic; 
	public News(String title, String description, String picUrl, String url) {
		super();
		this.title = title;
		this.description = description;
		this.picUrl = picUrl;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}

	public News(String picUrl, int pic,String title) {
		super();
		this.picUrl = picUrl;
		this.pic = pic;
		this.title = title;
	}

}
