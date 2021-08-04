package com.yc.bean;

import java.io.Serializable;

public class Resuser implements Serializable {

	private Integer userid;
	private String username;
	private String pwd;
	private String email;
	private String phone;
	private Double integral;   //积分

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Double getIntegral() {
		return integral;
	}

	public void setIntegral(Double integral) {
		this.integral = integral;
	}

	@Override
	public String toString() {
		return "Resuser{" +
				"userid=" + userid +
				", username='" + username + '\'' +
				", pwd='" + pwd + '\'' +
				", email='" + email + '\'' +
				", phone=" + phone +
				", integral=" + integral +
				'}';
	}
}
