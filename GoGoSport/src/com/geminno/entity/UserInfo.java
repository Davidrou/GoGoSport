package com.geminno.entity;

import java.io.Serializable;
import java.sql.Date;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int user_id;
	private String account;
	private String userpwd;
	private String photo;
	private String user_name;
	private String sex;
	private Date birthday;
	private String signature;
	private String user_position;
	
	public UserInfo(String account, String userpwd,String user_position) {
		super();
		this.account = account;
		this.userpwd = userpwd;
		this.user_position = user_position;
	}
	
	
	public UserInfo(String account, String userpwd, String photo,
			String user_name, String sex, String user_position) {
		super();
		this.account = account;
		this.userpwd = userpwd;
		this.photo = photo;
		this.user_name = user_name;
		this.sex = sex;
		this.user_position = user_position;
	}


		//修改用户资料
		public UserInfo(int user_id, String user_name, String sex, Date birthday, String signature, String user_position) {
			super();
			this.user_id = user_id;
			this.user_name = user_name;
			this.sex = sex;
			this.birthday = birthday;
			this.signature = signature;
			this.user_position = user_position;
		}

	public UserInfo(int user_id, String account, String userpwd, String photo,
			String user_name, String sex, Date birthday, String signature,
			String user_position) {
		super();
		this.user_id = user_id;
		this.account = account;
		this.userpwd = userpwd;
		this.photo = photo;
		this.user_name = user_name;
		this.sex = sex;
		this.birthday = birthday;
		this.signature = signature;
		this.user_position = user_position;
	}

	public UserInfo(String account, String userpwd, String photo,
			String user_name, String sex, Date birthday, String signature,
			String user_position) {
		super();
		this.account = account;
		this.userpwd = userpwd;
		this.photo = photo;
		this.user_name = user_name;
		this.sex = sex;
		this.birthday = birthday;
		this.signature = signature;
		this.user_position = user_position;
	}

	public int getUser_id() {
		return user_id;
	}


	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUser_position() {
		return user_position;
	}

	public void setUser_position(String user_position) {
		this.user_position = user_position;
	}


	@Override
	public String toString() {
		return "UserInfo [user_id=" + user_id + ", account=" + account
				+ ", userpwd=" + userpwd + ", photo=" + photo + ", user_name="
				+ user_name + ", sex=" + sex + ", birthday=" + birthday
				+ ", signature=" + signature + ", user_position="
				+ user_position + "]";
	}

}
