package com.daidai.im.entity;


/*
	向服务器发送登录请求时用的，在CommonMsg里面
 */

public class LoginMessage {
	String password;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
