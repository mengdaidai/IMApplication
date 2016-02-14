package com.daidai.im.entity;

import java.io.Serializable;

/**
 * Created by songs on 2016/1/26.
 */
public class ChatlistEntity implements Serializable{
    String head_path;
    String user_id;
    String latest_msg;
    String nick_name;
    String time;

    public String getHead_path() {
        return head_path;
    }

    public void setHead_path(String head_path) {
        this.head_path = head_path;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getLatest_msg() {
        return latest_msg;
    }

    public void setLatest_msg(String latest_msg) {
        this.latest_msg = latest_msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
