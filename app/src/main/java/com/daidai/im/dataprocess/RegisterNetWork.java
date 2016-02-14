package com.daidai.im.dataprocess;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.RegisterMessage;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;

/**
 * Created by songs on 2015/12/30.
 */
public class RegisterNetWork implements Runnable {
    String username,password;

    public RegisterNetWork(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            Selector selector = Selector.open();
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            CommonMsg msg = null;
            socketChannel.connect(new InetSocketAddress("192.168.0.137",8989));
            socketChannel.register(selector, SelectionKey.OP_CONNECT| SelectionKey.OP_READ| SelectionKey.OP_WRITE,msg);
            while(true){
                //System.out.println("before select");
                int num = selector.select();
                System.out.println("after select"+num);
                if(num == 0) continue;
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    System.out.println("有事件准备！");
                    SelectionKey key = iter.next();
                    iter.remove();
                    socketChannel = (SocketChannel) key.channel();
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
                        System.out.println("读准备！");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int byteRead = socketChannel.read(buffer);
                        byte[] data = msg.getData();
                        int off = msg.getOff();
                        boolean getLength = false;
                        while(byteRead>0){
                            if(off+byteRead>data.length){
                                System.out.println("扩容");
                                data = Util.grow(data, 2048);
                                //System.out.println("扩容后："+data.length);
                            }
                            System.arraycopy(buffer.array(), 0, data, off, byteRead);
                            off+=byteRead;
                            if(!getLength){
                                if(off>=4){
                                    getLength = true;
                                    msg.setLength(Util.getLength(data));
                                }
                            }
                            if(off>=msg.getLength()&&getLength){
                                //System.out.println("off:"+off);
                                //System.out.println("length:"+msg.getLength());
                                System.out.println("数据包接受完成！");
                                byte[] dataa = new byte[off-Protocol.HEAD_LENGTH];
                                System.arraycopy(data, Protocol.HEAD_LENGTH, dataa, 0, off - Protocol.HEAD_LENGTH);
                                System.out.println("响应："+new String(dataa));
                            }

                            buffer.clear();
                            byteRead = socketChannel.read(buffer);

                        }
                        key.channel().close();
                        key.cancel();
                    }
                    else if(key.isWritable()){
                        System.out.println("写准备！");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        RegisterMessage register_msg = new RegisterMessage();
                        register_msg.setPassword(password);
                        register_msg.setUser_name(username);
                        String data = new Gson().toJson(register_msg);
                        // String data  = "";
                        System.out.println(data.getBytes().length);
                        byte message_id =1;
                        buffer.put(Util.intToBytes(data.getBytes().length + Protocol.HEAD_LENGTH));
                        System.out.println("position:" + buffer.position());
                        buffer.put(Util.intToByte(Protocol.REGISTER_TYPE));
                        System.out.println("position:" + buffer.position());
                        buffer.put(message_id);
                        System.out.println("position:" + buffer.position());
                        String from = "00000000";
                        String token = "00000000000000000000000000000000";
                        String to = "server00";
                        String time = "12312313";
                        buffer.put(from.getBytes());
                        System.out.println("position:" + buffer.position());
                        buffer.put(token.getBytes());
                        System.out.println("position:" + buffer.position());
                        buffer.put(to.getBytes());
                        System.out.println("position:" + buffer.position());
                        buffer.put(time.getBytes());
                        System.out.println("position:" + buffer.position());
                        buffer.put(data.getBytes());
                        System.out.println("position:" + buffer.position());
                        buffer.flip();
                        while(buffer.hasRemaining()){
                            int len = socketChannel.write(buffer);
                            System.out.println(len);
                        }
                        buffer.clear();
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
