package com.daidai.im.util;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.daidai.im.dataprocess.ClientNetWork;
import com.daidai.im.dataprocess.DBHandler;
import com.daidai.im.dataprocess.DBHelper;
import com.daidai.im.dataprocess.FileSendThread;
import com.daidai.im.entity.ChatlistEntity;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FileEntity;
import com.daidai.im.entity.FriendMsg;
import com.daidai.im.entity.TransmitionMessage;

/**
 * Created by songs on 2015/12/30.
 */
public class MyApplication extends Application {
    static public String user_id;
    static public String user_name;
    static public SocketChannel socketChannel;
    static public String token = "00000000000000000000000000000000";
    static public DBHandler db_handler;
    static public Handler login_handler,register_handler,chatlist_handler,chat_handler,friend_add_handler;
    static public String current_to;
    static public CopyOnWriteArrayList<ChatlistEntity> update_chatlist_msg;
    static public CopyOnWriteArrayList<CommonMsg> update_friend_msg;
    static public String record_path,picture_path,file_path;
    static public Thread client_net_work_thread;
    static public Thread data_process_work_thread;
    static public String serverIP = "192.168.0.137";
    static public ConcurrentHashMap<String,FileSendThread> threads;
    static public ConcurrentHashMap<Byte,FileEntity> files;
    static public volatile boolean has_msg_to_send ;
    static public ClientNetWork client_network;
    static public int port = 8989;
    static public byte msg_id = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        db_handler = new DBHandler(this);
        update_chatlist_msg = new CopyOnWriteArrayList<ChatlistEntity>();
        update_friend_msg = new CopyOnWriteArrayList<CommonMsg>();
        record_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println("MyApplication"+record_path);
        record_path+="/IMApplication/Record";
        picture_path+="/IMApplication/Picture";
        file_path+="/IMApplication/File";
        has_msg_to_send = false;
        files = new ConcurrentHashMap<Byte, FileEntity>();
        threads = new ConcurrentHashMap<String, FileSendThread>();

    }

}
