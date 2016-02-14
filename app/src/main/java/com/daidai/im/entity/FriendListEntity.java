package com.daidai.im.entity;

/**
 * Created by songs on 2016/2/3.
 */
public class FriendListEntity {
    String friend_name;
    String friend_id;
    String head_path;
    String friend_msg;
    boolean accept;


    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getHead_path() {
        return head_path;
    }

    public void setHead_path(String head_path) {
        this.head_path = head_path;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getFriend_msg() {
        return friend_msg;
    }

    public void setFriend_msg(String friend_msg) {
        this.friend_msg = friend_msg;
    }
}
