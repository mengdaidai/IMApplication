package com.daidai.im.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
	服务器发回的登录成功的消息，在CommonMsg的里面
 */

public class LoginResponseMessage implements Serializable{
	String token = "";
	ArrayList<TransmitionMessage> msgs;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public ArrayList<TransmitionMessage> getMsgs() {
		return msgs;
	}
	public void setMsgs(ArrayList<TransmitionMessage> msgs) {
		this.msgs = msgs;
	}
}
