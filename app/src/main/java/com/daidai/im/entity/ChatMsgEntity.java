
package com.daidai.im.entity;

public class ChatMsgEntity {
    private static final String TAG = ChatMsgEntity.class.getSimpleName();

    private String name;

    private String date;

    private String text;

    private boolean isComMeg = true;

    private byte[] data;

    private int type = 0;

    private int record_time;

    private String record_path;

    private String file_name;

    private int file_length;

    private int file_off;

    private boolean is_offline;

    private byte common_msg_id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRecord_time() {
        return record_time;
    }

    public String getRecord_path() {
        return record_path;
    }

    public void setRecord_path(String record_path) {
        this.record_path = record_path;
    }

    public void setRecord_time(int record_time) {
        this.record_time = record_time;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFile_length() {
        return file_length;
    }

    public void setFile_length(int file_length) {
        this.file_length = file_length;
    }

    public int getFile_off() {
        return file_off;
    }

    public void setFile_off(int file_off) {
        this.file_off = file_off;
    }

    public boolean is_offline() {
        return is_offline;
    }

    public void setIs_offline(boolean is_offline) {
        this.is_offline = is_offline;
    }

    public byte getCommon_msg_id() {
        return common_msg_id;
    }

    public void setCommon_msg_id(byte common_msg_id) {
        this.common_msg_id = common_msg_id;
    }

    public ChatMsgEntity() {
    }


}
