package com.daidai.im.dataprocess;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

import com.daidai.im.entity.CommonMsg;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;

/**
 * Created by songs on 2015/12/20.
 */
public class ClientNetWork implements Runnable {
    public static final int BUFFER_SIZE = 1024;//缓冲区大小须再议
    DataProcessWork dataProcessWork ;
    Selector selector ;
    Context context;
    volatile boolean stop = false;
    CommonMsg login_msg = null;

    public ClientNetWork(Context context,CommonMsg login_msg){
        this.context = context;
        dataProcessWork = new DataProcessWork(context);
        this.login_msg = login_msg;
    }
    @Override
    public void run() {
        try {

            selector = Selector.open();
            MyApplication.socketChannel = SocketChannel.open();
            MyApplication.socketChannel.configureBlocking(false);
            CommonMsg msg = null;
            MyApplication.socketChannel.connect(new InetSocketAddress(MyApplication.serverIP, 8989));
            MyApplication.socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE, msg);
            //MyApplication.socketChannel.register(selector,SelectionKey.OP_WRITE,login_msg);
            //MyApplication.data_process_work_thread = new Thread(dataProcessWork);
            //MyApplication.data_process_work_thread.start();
            MyApplication.has_msg_to_send = true;
            while(!stop){

                System.out.println("before select");
                int num = 0;
                synchronized (this){
                    num = selector.select();
                }
                if(num == 0) continue;
                MyApplication.has_msg_to_send = true;
                System.out.println("after select"+num);
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    System.out.println("有事件准备！");
                    SelectionKey key = iter.next();
                    iter.remove();
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    msg = (CommonMsg) key.attachment();
                    if(msg == null){
                        msg = new CommonMsg();
                    }
                    if(key.isConnectable() && !socketChannel.isConnected()){
                        System.out.println("可连接");
                        boolean success = socketChannel.finishConnect();
                        if(!success)
                            System.out.println("连接失败");
                        else{
                            System.out.println("链接成功！");
                        }

                    }
                    else if(key.isReadable()){
                        //System.out.println("readable");
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        int byteRead = socketChannel.read(buffer);
                        msg = new CommonMsg();
                        /*msg = (CommonMsg) key.attachment();
                        if(msg == null){
                            msg = new CommonMsg();
                        }*/
                        byte[] data = new byte[1024];
                        msg.setData(data);
                        int off = msg.getOff();
                        boolean getLength = false;
                        while(byteRead > 0){
                            //System.out.println("key:"+key);
                            //System.out.println("data.length:"+data.length);
                            ///System.out.println("off+byteRead:"+(off+byteRead));
                            if(off+byteRead>data.length){
                                //System.out.println("扩容");
                                data = Util.grow(data,BUFFER_SIZE*2);
                                //System.out.println("扩容后："+data.length);
                            }
                            System.arraycopy(buffer.array(), 0, data, off, byteRead);
                            off+=byteRead;
                            if(off>=msg.getLength()){
                                System.out.println("off:"+off);
                                System.out.println("length:"+msg.getLength());
                                System.out.println("数据包接受完成！");
                            }
                            if(!getLength){
                                if(off>=Protocol.LENGTH_BYTE){
                                    getLength = true;
                                    msg.setLength(Util.getLength(data));
                                }
                            }
                            buffer.clear();
                            //System.out.println("temp:"+new String(getData(data,off)));
                            String temp = new String(Arrays.copyOfRange(msg.getData(), Protocol.HEAD_LENGTH, off));
                            System.out.println("temp:"+new String(Arrays.copyOfRange(data, Protocol.HEAD_LENGTH, off)));
                            byteRead = socketChannel.read(buffer);
                        }
                        //System.out.println("出循环！");
                        msg.setOff(off);
                        msg.setData(data);
                        String temp = new String(Arrays.copyOfRange(msg.getData(), Protocol.HEAD_LENGTH, off));
                        msg.setChannel(socketChannel);
                        //System.out.println(msg.getOff());
                        System.out.println(msg.getLength());
                        if(msg.getOff() == msg.getLength()){
                            String json_string = msg.getData().toString();
                            dataProcessWork.handleMsg(msg);
                        }
                    }
                    else if(key.isWritable()){
                        System.out.println("写准备！");

                        if(login_msg == null)
                            msg = (CommonMsg)key.attachment();
                        else
                            msg = login_msg;
                        byte message_id =1;
                        ByteBuffer buffer = ByteBuffer.allocate(msg.getLength());
                        buffer.put(Util.intToBytes(msg.getLength()));
                        buffer.put(Util.intToByte(msg.getType()));
                        buffer.put(msg.getMsg_id());
                        buffer.put(MyApplication.user_id.getBytes());
                        buffer.put(MyApplication.token.getBytes());
                        buffer.put(msg.getTo().getBytes());
                        buffer.put(msg.getTime());
                        buffer.put(msg.getData());
                       /* int hasWrite = 0;
                        int remaining = 1024-Protocol.HEAD_LENGTH;
                        if(msg.getData()!=null)
                        while(hasWrite<msg.getData().length){
                            if(msg.getData().length-hasWrite>1024){
                                buffer.put(msg.getData(),hasWrite,remaining);
                                hasWrite+=remaining;
                            }else{
                                buffer.put(msg.getData(),hasWrite,msg.getData().length-hasWrite);
                                hasWrite+=msg.getData().length-hasWrite;
                            }
                            remaining = 1024;
                            buffer.flip();
                            while(buffer.hasRemaining()){
                                int len = socketChannel.write(buffer);
                                System.out.println(len);
                            }
                            buffer.clear();
                        }*/
                        buffer.flip();
                        while(buffer.hasRemaining()){
                            int len = socketChannel.write(buffer);
                            System.out.println(len);
                        }
                        buffer.clear();
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                        login_msg = null;
                    }
                    MyApplication.has_msg_to_send = false;

                }

            }
            MyApplication.socketChannel.close();

        } catch (ClosedByInterruptException e) {
            try{
                MyApplication.socketChannel.close();
            }catch(IOException e1){
                e1.printStackTrace();
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public void registerWriteChannel(SocketChannel channel,CommonMsg msg){
        try{
            channel.configureBlocking(false);
            //selector.wakeup();
            channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ |SelectionKey.OP_WRITE,msg);
        }catch(IOException e){
            e.printStackTrace();
        }

    }





}
