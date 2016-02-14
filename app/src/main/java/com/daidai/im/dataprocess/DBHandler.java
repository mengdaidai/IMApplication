package com.daidai.im.dataprocess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daidai.im.entity.ChatlistEntity;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FileEntity;
import com.daidai.im.entity.FriendMsg;
import com.daidai.im.util.Protocol;
import com.google.gson.Gson;

/**
 * Created by songs on 2015/12/31.
 */
public class DBHandler{
    DBHelper helper;
    public DBHandler(Context context){
        helper = new DBHelper(context);
    }

    public ChatlistEntity getFriendInfo(String friend_id){
        ChatlistEntity e = new ChatlistEntity();
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from person_info where user_id = ?";
        Cursor cursor = database.rawQuery(sql,new String[]{friend_id});
        cursor.moveToNext();
        e.setNick_name(cursor.getString(3));
        e.setHead_path(cursor.getString(2));
        return e;
    }

    public void insert_chat_msg(CommonMsg msg,int is_comming,String path,int file_state){//file_state 0代表还未接收，1代表正在接收，2代表拒绝,3代表接受完毕
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "";
        String aim = "";
        if(is_comming == 1)
            aim = msg.getFrom();
        else
            aim = msg.getTo();
        switch (msg.getType()){
            case Protocol.TEXT_TYPE:
                sql = "insert into chat(with,talk_text,time,is_comming,have_read,type,msg_state) values (?,?,?,?,?,?,?)";
                database.execSQL(sql,new Object[]{aim,msg.getData().toString(),msg.getTime(),is_comming,0,Protocol.TEXT_TYPE,0});
                break;
            case Protocol.VOICE_TYPE:
                sql = "insert into chat(with,voice,time,is_comming,have_read,type,msg_state) values (?,?,?,?,?,?,?)";
                database.execSQL(sql,new Object[]{aim,path,msg.getTime(),is_comming,0,Protocol.VOICE_TYPE,0});
                break;
            case Protocol.PICTURE_TYPE:
                sql = "insert into chat(with,picture,time,is_comming,have_read,type,msg_state) values (?,?,?,?,?,?,?)";
                database.execSQL(sql,new Object[]{aim,path,msg.getTime(),is_comming,0,Protocol.PICTURE_TYPE,0});
                break;
            case Protocol.FILE_TYPE:
                FileEntity e = new Gson().fromJson(msg.getData().toString(),FileEntity.class);
                sql = "insert into chat(with,file,file_length,file_suffix,file_off,file_state,time,is_comming,have_read,type,msg_state) values (?,?,?,?,?,?,?,?,?,?,?)";
                database.execSQL(sql,new Object[]{aim,path,e.getFile_length(),e.getFile_suffix(),0,file_state,msg.getTime(),is_comming,0,Protocol.FILE_TYPE,0});
                break;
        }
    }
    public void update_file_state(int state,int file_id){
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "update chat set file_state = ? where _id = ?";
        database.execSQL(sql,new Object[]{state,file_id});
    }

    public void update_file_off(int off,int file_id){
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "update chat set file_off = ? where _id = ?";
        database.execSQL(sql,new Object[]{off,file_id});
    }



    public void insert_friend(FriendMsg msg,String friend_id,int state,String head_path){//state 0代表接到请求未接收，1代表接到请求后接收，2代表发出请求后已接受
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql1 = "insert into friend (user_id,state,have_read) values (?,?,?)";
        String sql2 = "insert into person_info (user_id,head_image,user_name,sign) values (?,?,?,?)";
        database.execSQL(sql1,new Object[]{friend_id,state,0});
        database.execSQL(sql2,new Object[]{friend_id,head_path,msg.getFriend_name(),msg.getFriend_sign()});
    }





}
