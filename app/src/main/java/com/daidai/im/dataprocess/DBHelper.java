package com.daidai.im.dataprocess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by songs on 2015/12/30.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "IMClient.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table account(user_id char PRIMARY KEY , password char, "
                + "flag integer)");
        db.execSQL("create table char_list(user_id char PRIMARY KEY )");
        db.execSQL("create table person_info(user_id char PRIMARY KEY , head_image char, "
                + "user_name char, sign char)");
        db.execSQL("create table chat(_id integer PRIMARY KEY AUTOINCREMENT, with char, "
                + "talk_text char, time datetime, picture char,voice char,file char,file_name char," +
                "is_coming integer,have_read integer,type integer,file_off int,msg_state int,file_state int," +
                "file_length int,file_suffix char)");//msg_state代表是否发送成功
        db.execSQL("create table friend(user_id char PRIMARY KEY,state int,have_read int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
