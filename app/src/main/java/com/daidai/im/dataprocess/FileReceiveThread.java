package com.daidai.im.dataprocess;

import com.daidai.im.entity.FileEntity;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

/**
 * Created by songs on 2016/2/2.
 */
public class FileReceiveThread implements Runnable {
    Socket s ;
    InputStream is;
    DataInputStream dis;
    public FileReceiveThread(Socket s){
        this.s = s;
        try {
            is = s.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            int byte_read = 0;
            byte[] buffer = new byte[5*1024];
            byte[] data = new byte[4];
            int off = 0;
            int data_off =0;
            int file_length = 0;
            int file_id;
            int start_pos = 0;
            boolean startOfFile = true;
            File file_dir = new File(MyApplication.file_path);
            if(!file_dir.exists()){
                file_dir.mkdirs();
            }
            File file;
            FileOutputStream fos = null;
            //RandomAccessFile raf = new RandomAccessFile(file,"rw");
            int i =0;
            while(true){
                if((byte_read = dis.read(buffer))!=-1){
                    i++;
                    System.out.println("得到一些数据"+byte_read+"      "+i);
                    //raf.seek(off);
                    off+=byte_read;
                    System.out.println("off:"+off/1024);
                    if(off>file_length&&file_length!=0){
                        System.out.println("开始接受第二个文件");
                        fos.write(buffer,0,byte_read-off+file_length);
                        fos.flush();
                        off = off-file_length;
                        file_length = 0;
                        data_off = 0;
                        System.out.println(off/1024);
                        startOfFile = true;
                        start_pos = byte_read-off;
                    }
                    else if(off == file_length){
                        System.out.println("开始接受第二个文件1");
                        fos.write(buffer,0,byte_read);
                        fos.flush();
                        file_length = 0;
                        data_off = 0;
                        off = 0;
                        startOfFile = true;
                        start_pos = 0;
                        continue;
                    }
                    if(off>=4&&startOfFile){
                        System.arraycopy(buffer, start_pos, data, data_off, 4-data_off);
                        file_id = Util.getLength(data);
                        FileEntity file_entity = MyApplication.files.get(file_id);
                        file_length = file_entity.getFile_length();
                        file_length+=4;
                        System.out.println("length:"+file_length/1024);
                        startOfFile = false;
                        file = new File(MyApplication.file_path+"/"+file_entity.getFile_name());
                        fos = new FileOutputStream(file);
                        fos.write(buffer,4-data_off,byte_read-4+data_off);
                    }else if(!startOfFile){
                        fos.write(buffer,0,byte_read);
                        System.out.println("写入文件  "+byte_read);
                    }else if(off<4){
                        System.arraycopy(buffer, start_pos, data, data_off, byte_read);
                        data_off+=byte_read;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
