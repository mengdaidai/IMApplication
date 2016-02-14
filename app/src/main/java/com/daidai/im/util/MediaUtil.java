package com.daidai.im.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.daidai.im.entity.RecordEntity;

import java.io.File;
import java.io.IOException;

/**
 * Created by songs on 2016/1/29.
 */
public class MediaUtil {

    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;
    Context context;
    File  mRecAudioFile;
    File record_dir;
    String record_path;
    long starttime;
    public MediaUtil(Context context,String record_path){
        this.context = context;
        this.record_path = record_path;
    }


    public void play_record(String file_path){
        mMediaPlayer = new MediaPlayer();
        try{
            mMediaPlayer.setDataSource(file_path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch(IOException e){
            e.printStackTrace();
        }


    }

    public void start_record(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            System.out.println(record_path);
            record_dir = new File(record_path);
            if(!record_dir.exists()){
                record_dir.mkdirs();
            }
            try{
                mRecAudioFile = File.createTempFile("record",".amr",record_dir);
                mMediaRecorder = new MediaRecorder();
            /* 设置录音来源为MIC */
                mMediaRecorder
                        .setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder
                        .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mMediaRecorder
                        .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mMediaRecorder.setOutputFile(mRecAudioFile
                        .getAbsolutePath());
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            }catch(IOException e){
                e.printStackTrace();
            }


        } else {
            Toast.makeText(context, "请插入SD卡", Toast.LENGTH_LONG);
        }

    }

    public RecordEntity stop_record(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        RecordEntity e = new RecordEntity();
        e.setTime((int)((System.currentTimeMillis()-starttime)/1000));
        e.setFile_path(mRecAudioFile.getAbsolutePath());
        return e;

    }

    public void cancel_record(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mRecAudioFile.delete();
    }

    public int getVolumn(){//这里不懂为啥什么
        int volumn = 9;
        if(mMediaRecorder!=null){
            volumn= mMediaRecorder.getMaxAmplitude();
            if(volumn!=0)
                volumn=(int) (10 * Math.log(volumn) / Math.log(10))/7;
        }
        return volumn;
    }
}
