package com.daidai.im.dataprocess;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Message;

import com.daidai.im.entity.ChatlistEntity;
import com.daidai.im.entity.FileEntity;
import com.daidai.im.entity.FriendListEntity;
import com.daidai.im.entity.FriendMsg;
import com.daidai.im.entity.LoginResponseMessage;
import com.daidai.im.entity.ServerFileResponse;
import com.daidai.im.entity.TransmitionMessage;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.daidai.im.entity.CommonMsg;
import com.daidai.im.util.Protocol;

/**
 * Created by songs on 2015/12/30.
 */
public class DataProcessWork implements Runnable {
    ThreadPoolExecutor executor ;
    DBHandler handler;
    Context context;
    volatile boolean is_off = false;
    volatile boolean stop = false;


    public DataProcessWork(Context context){
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        handler = new DBHandler(context);
        this.context = context;
    }
    @Override
    public void run() {
        //此处发送心跳
        while(!stop){
            if(MyApplication.has_msg_to_send){
                try{
                    Thread.sleep(50000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

            }
            else{
                CommonMsg msg = new CommonMsg();
                msg.setFrom(MyApplication.user_id);
                msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                msg.setType(Protocol.HEARTBEAT_TYPE);
                msg.setToken(MyApplication.token);
                msg.setTo("服务00");
                msg.setLength(Protocol.HEAD_LENGTH);
                MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel, msg);
                is_off = true ;
                try{
                    Thread.sleep(5000);
                    if(is_off){
                        //重新连接
                    }else{
                        Thread.sleep(45000);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }


            }
        }
    }

    public void handleMsg(CommonMsg msg){
        executor.execute(new HandleThread(msg));
    }

    public class HandleThread implements Runnable {
        CommonMsg msg;

        public HandleThread(CommonMsg msg){
            this.msg = msg;
        }

        @Override
        public void run() {
            System.out.println("处理头！");
            handleHead(msg);
                switch(msg.getType()){
                    case Protocol.REGISTER_RESPONSE_TYPE://注册消息
                        handleRegisterResponse(msg);
                        break;
                    case Protocol.LOGIN_RESPONSE_TYPE://登录信息
                        handleLoginResponse(msg);
                        break;
                    case Protocol.TEXT_TYPE:
                        handleText(msg);
                        break;
                    case Protocol.VOICE_TYPE:
                        handleVoice(msg);
                        break;
                    case Protocol.PICTURE_TYPE:
                        handlePicture(msg);
                        break;
                    case Protocol.FILE_TYPE://别人发过来的文件发送请求
                        handleFile(msg);
                        break;
                    case Protocol.FILE_RESPONSE_TYPE://请求发送文件的回复
                        handleFileResponse(msg);
                        break;
                    case Protocol.FRIEND_TYPE:
                        handleFriend(msg);
                        break;
                    case Protocol.FRIEND_RESPONSE_TYPE:
                        handleFileResponse(msg);
                        break;
                    case Protocol.HEARTBEAT_RESPONSE_TYPE:
                        handleHeartBeatResponse(msg);
                        break;

                }
        }

        private void handleHead(CommonMsg msg){
            byte[] data = msg.getData();
            byte type_byte = data[Protocol.TYPE_START];
            msg.setType(type_byte);
            byte[] msg_id_byte = Arrays.copyOfRange(data, Protocol.MESSAGE_ID_START, Protocol.FROM_START);
            byte msg_id = msg_id_byte[0];
            msg.setMsg_id(msg_id);
            byte[] from_byte = Arrays.copyOfRange(data, Protocol.FROM_START, Protocol.TOKEN_START);
            String from = new String(from_byte);
            msg.setFrom(from);
            byte[] token_byte = Arrays.copyOfRange(data, Protocol.TOKEN_START, Protocol.TO_START);
            String token = new String(token_byte);
            msg.setToken(token);
            byte[] to_byte = Arrays.copyOfRange(data, Protocol.TO_START, Protocol.TIME_START);
            String to = new String(to_byte);
            msg.setTo(to);
            byte[] time_byte = Arrays.copyOfRange(data, Protocol.TIME_START, Protocol.HEAD_LENGTH);
            msg.setTime(time_byte);
            msg.setData(Arrays.copyOfRange(data, Protocol.HEAD_LENGTH, msg.getLength()));
            int length = msg.getData().length;
            String json_string = new String(msg.getData());
        }

        private void handleLoginResponse(CommonMsg msg){
            Message handler_msg = Message.obtain();
            if(msg.getData().toString().equals("用户名或密码错误！")){
                handler_msg.what = 0;
                MyApplication.login_handler.sendMessage(handler_msg);
                stopConnection();
            }else{
                handler_msg.what = 1;
                String json_string = new String(msg.getData());
                LoginResponseMessage lrm = new Gson().fromJson(json_string, LoginResponseMessage.class);
                ArrayList<TransmitionMessage> msgs = lrm.getMsgs();
                ArrayList<ChatlistEntity> chat_es = new ArrayList<ChatlistEntity>();
                int friend_add_num = 0;
                for(TransmitionMessage m:msgs){
                    ChatlistEntity e = null;
                    switch (m.getType()){
                        case Protocol.TEXT_TYPE:
                            e = MyApplication.db_handler.getFriendInfo(m.getFrom());
                            e.setTime(m.getTime());
                            e.setLatest_msg(m.getData().toString());
                            e.setUser_id(m.getFrom());
                            chat_es.add(e);
                            break;
                        case Protocol.VOICE_TYPE:
                            e = MyApplication.db_handler.getFriendInfo(m.getFrom());
                            e.setLatest_msg("[语音]");
                            e.setTime(m.getTime());
                            e.setUser_id(m.getFrom());
                            chat_es.add(e);
                            break;
                        case Protocol.PICTURE_TYPE:
                            e = MyApplication.db_handler.getFriendInfo(m.getFrom());
                            e.setLatest_msg("[图片]");
                            e.setTime(m.getTime());
                            e.setUser_id(m.getFrom());
                            chat_es.add(e);
                            break;
                        case Protocol.FRIEND_TYPE:
                            FriendMsg f_msg = new Gson().fromJson(new String(m.getData()),FriendMsg.class);
                            /*File dir = new File(MyApplication.picture_path);
                            if(!dir.exists()) dir.mkdirs();
                            File file = null;
                            try{
                                file = File.createTempFile("head",".jpg",dir);
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(f_msg.getHead_picture());
                            }catch(IOException e1){
                                e1.printStackTrace();
                            }
                            String path = file.getAbsolutePath();
                            MyApplication.db_handler.insert_friend(f_msg,m.getFrom(),0,path);*/
                            friend_add_num++;
                            break;
                    }
                }
                MyApplication.token = lrm.getToken();
                handler_msg.obj = chat_es;
                handler_msg.arg1 = friend_add_num;
                MyApplication.login_handler.sendMessage(handler_msg);
            }
        }

        private void handleRegisterResponse(CommonMsg msg){
            Message handler_msg = Message.obtain();
            handler_msg.what = 0;
            handler_msg.obj = msg.getData().toString();
            MyApplication.register_handler.sendMessage(handler_msg);

            stopConnection();
        }

        private void handleText(CommonMsg msg){
            MyApplication.db_handler.insert_chat_msg(msg,1,null,0);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:"+top_activity);
            Message handler_msg = Message.obtain();
            //此处从数据库获得friend相关的信息
            ChatlistEntity e  = MyApplication.db_handler.getFriendInfo(msg.getFrom());
            e.setTime(Long.toString(Util.bytesToLong(msg.getTime())));
            e.setLatest_msg(msg.getData().toString());
            e.setUser_id(msg.getFrom());
            if(top_activity.equals("ChatActivity")){
                if(MyApplication.current_to.equals(msg.getFrom())){
                    handler_msg.what = 0;
                    handler_msg.obj = msg;
                    MyApplication.chat_handler.sendMessage(handler_msg);
                }else{
                    MyApplication.update_chatlist_msg.add(e);
                }

            }else if(top_activity.equals("MainWeixin")){
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_chatlist_msg.add(e);
            }
        }

        private void handleVoice(CommonMsg msg){
            File dir = new File(MyApplication.record_path);
            if(!dir.exists()) dir.mkdirs();
            File file = null;
            try{
                file = File.createTempFile("record",".amr",dir);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(msg.getData());
            }catch(IOException e){
                e.printStackTrace();
            }
            String path = file.getAbsolutePath();//这里要存一下
            MyApplication.db_handler.insert_chat_msg(msg,1,path,0);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:"+top_activity);
            Message handler_msg = Message.obtain();
            //此处从数据库获得friend相关的信息
            ChatlistEntity e  = MyApplication.db_handler.getFriendInfo(msg.getFrom());
            e.setTime(Long.toString(Util.bytesToLong(msg.getTime())));
            e.setLatest_msg("[语音]");
            e.setUser_id(msg.getFrom());
            if(top_activity.equals("ChatActivity")){
                if(MyApplication.current_to.equals(msg.getFrom())){
                    handler_msg.what = 1;
                    File record_dir = new File(MyApplication.record_path);
                    if(!record_dir.exists()){
                        record_dir.mkdirs();
                    }
                    try{
                        File mRecAudioFile = File.createTempFile("record",".amr",record_dir);
                        FileOutputStream fos = new FileOutputStream(mRecAudioFile,true);
                        fos.write(msg.getData());
                        msg.setData(mRecAudioFile.getAbsolutePath().getBytes());
                    }catch(IOException exception){
                        exception.printStackTrace();
                    }
                    handler_msg.obj = msg;
                    MyApplication.chat_handler.sendMessage(handler_msg);
                }else{
                    MyApplication.update_chatlist_msg.add(e);
                }

            }else if(top_activity.equals("MainWeixin")){
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_chatlist_msg.add(e);
            }
        }


        private void handlePicture(CommonMsg msg){
            File dir = new File(MyApplication.picture_path);
            if(!dir.exists()) dir.mkdirs();
            File file = null;
            try{
                file = File.createTempFile("pic",".jpg",dir);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(msg.getData());
            }catch(IOException e){
                e.printStackTrace();
            }
            String path = file.getAbsolutePath();//这里要存一下
            MyApplication.db_handler.insert_chat_msg(msg,1,path,0);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:"+top_activity);
            Message handler_msg = Message.obtain();
            //此处从数据库获得friend相关的信息
            ChatlistEntity e  = MyApplication.db_handler.getFriendInfo(msg.getFrom());
            e.setTime(Long.toString(Util.bytesToLong(msg.getTime())));
            e.setLatest_msg("[图片]");
            e.setUser_id(msg.getFrom());
            if(top_activity.equals("ChatActivity")){
                if(MyApplication.current_to.equals(msg.getFrom())){
                    handler_msg.what = 2;
                    msg.setData(path.getBytes());
                    handler_msg.obj = msg;
                    MyApplication.chat_handler.sendMessage(handler_msg);
                }else{
                    MyApplication.update_chatlist_msg.add(e);
                }

            }else if(top_activity.equals("MainWeixin")){
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_chatlist_msg.add(e);
            }
        }



        private void handleHeartBeatResponse(CommonMsg msg){
            is_off = false;
        }


        private void handleFile(CommonMsg msg){
            MyApplication.db_handler.insert_chat_msg(msg,1,null,0);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:"+top_activity);
            Message handler_msg = Message.obtain();
            //此处从数据库获得friend相关的信息
            ChatlistEntity e  = MyApplication.db_handler.getFriendInfo(msg.getFrom());
            e.setTime(Long.toString(Util.bytesToLong(msg.getTime())));
            e.setLatest_msg("[文件]");
            e.setUser_id(msg.getFrom());
            if(top_activity.equals("ChatActivity")){
                if(MyApplication.current_to.equals(msg.getFrom())){
                    handler_msg.what = 3;
                    handler_msg.obj = msg;
                    MyApplication.chat_handler.sendMessage(handler_msg);
                }else{
                    MyApplication.update_chatlist_msg.add(e);
                }

            }else if(top_activity.equals("MainWeixin")){
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_chatlist_msg.add(e);
            }

        }

        private void handleFileResponse(CommonMsg msg){
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:"+top_activity);
            ServerFileResponse response = new Gson().fromJson(new String(msg.getData()),ServerFileResponse.class);
            FileEntity file_entity = MyApplication.files.get(msg.getMsg_id());
            String file_path = file_entity.getFile_name();
            if(response.isIs_off()){
                    synchronized (DataProcessWork.this){
                        if(MyApplication.threads.get(MyApplication.serverIP)==null){
                            List<String> files = new ArrayList<String>();
                            List<Byte> ids = new ArrayList<Byte>();
                            ids.add(response.getFile_id());
                            files.add(file_path);
                            Socket s = new Socket();
                            try {
                                s.connect(new InetSocketAddress(MyApplication.serverIP,response.getPort()));
                                FileSendThread thread = new FileSendThread(s, files, ids);
                                MyApplication.threads.put(MyApplication.serverIP, thread);
                                new Thread(thread).start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            MyApplication.threads.get(MyApplication.serverIP).files.add(file_path);
                            MyApplication.threads.get(MyApplication.serverIP).msg_ids.add(response.getFile_id());
                        }
                    }

            }else {
                if(response.isYes()){
                    synchronized (DataProcessWork.this){
                        if(MyApplication.threads.get(msg.getTo())==null){
                            List<String> files = new ArrayList<String>();
                            List<Byte> ids = new ArrayList<Byte>();
                            ids.add(response.getFile_id());
                            files.add(file_path);
                            Socket s = new Socket();
                            try {
                                s.connect(new InetSocketAddress(msg.getTo(),response.getPort()));
                                FileSendThread thread = new FileSendThread(s, files, ids);
                                MyApplication.threads.put(msg.getTo(), thread);
                                new Thread(thread).start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            MyApplication.threads.get(msg.getTo()).files.add(file_path);
                            MyApplication.threads.get(msg.getTo()).msg_ids.add(response.getFile_id());
                        }
                    }
                }else{
                    //通知页面更新
                }
                
            }
            Message handler_msg = Message.obtain();
            //此处从数据库获得friend相关的信息
            ChatlistEntity e = new ChatlistEntity();
            e.setTime(Long.toString(Util.bytesToLong(msg.getTime())));
            e.setLatest_msg("[文件]");
            if(top_activity.equals("ChatActivity")){
                if(MyApplication.current_to.equals(msg.getFrom())){
                    handler_msg.what = 4;
                    handler_msg.obj = msg;
                    MyApplication.chat_handler.sendMessage(handler_msg);
                }else{
                    MyApplication.update_chatlist_msg.add(e);
                }

            }else if(top_activity.equals("MainWeixin")){
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_chatlist_msg.add(e);
            }
        }


        private void handleGroupMsg(CommonMsg msg){

        }

        private void handleCreateGroup(CommonMsg msg){

        }

        private void handleFriend(CommonMsg msg){
            FriendMsg friend_msg = new Gson().fromJson(msg.getData().toString(),FriendMsg.class);
            File dir = new File(MyApplication.picture_path);
            if(!dir.exists()) dir.mkdirs();
            File file = null;
            try{
                file = File.createTempFile("head",".jpg",dir);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(msg.getData());
            }catch(IOException e){
                e.printStackTrace();
            }
            String path = file.getAbsolutePath();
            MyApplication.db_handler.insert_friend(friend_msg,msg.getFrom(),0,path);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:" + top_activity);
            Message handler_msg = Message.obtain();
            if(top_activity.equals("MainWeixin")){
                handler_msg.what = 1;
                handler_msg.obj = msg;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else if(top_activity.equals("FriendAddActivity")){

                FriendListEntity e = new FriendListEntity();
                e.setAccept(false);
                e.setFriend_id(msg.getFrom());
                e.setHead_path(path);
                e.setFriend_name(friend_msg.getFriend_name());
                e.setFriend_msg(friend_msg.getFriend_msg());
                handler_msg.what = 0;
                handler_msg.obj = e;
                MyApplication.friend_add_handler.sendMessage(handler_msg);
            }



        }

        private void handleFriendResponse(CommonMsg msg){
            FriendMsg friend_msg = new Gson().fromJson(msg.getData().toString(),FriendMsg.class);
            File dir = new File(MyApplication.picture_path);
            if(!dir.exists()) dir.mkdirs();
            File file = null;
            try{
                file = File.createTempFile("head",".jpg",dir);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(msg.getData());
            }catch(IOException e){
                e.printStackTrace();
            }
            String path = file.getAbsolutePath();
            MyApplication.db_handler.insert_friend(friend_msg,msg.getFrom(),2,path);
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            String top_activity = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            System.out.println("top_activity:" + top_activity);
            Message handler_msg = Message.obtain();
            if(top_activity.equals("MainWeixin")){
                FriendListEntity e = new FriendListEntity();
                e.setAccept(false);
                e.setFriend_id(msg.getFrom());
                e.setHead_path(path);
                e.setFriend_name(friend_msg.getFriend_name());
                handler_msg.what = 2;
                handler_msg.obj = e;
                MyApplication.chatlist_handler.sendMessage(handler_msg);
            }else{
                MyApplication.update_friend_msg.add(msg);
            }
        }

        private void stopConnection(){
            stop = true;
            MyApplication.data_process_work_thread.interrupt();
            MyApplication.data_process_work_thread = null;
            MyApplication.client_network.stop = true;
            MyApplication.client_net_work_thread.interrupt();
            MyApplication.client_net_work_thread = null;
        }
    }
}
