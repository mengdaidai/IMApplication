package com.daidai.im.activity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daidai.im.R;
import com.daidai.im.dataprocess.ClientNetWork;
import com.daidai.im.dataprocess.DataProcessWork;
import com.daidai.im.entity.ChatlistEntity;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.LoginMessage;
import com.daidai.im.entity.TransmitionMessage;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
    MyHandler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        mHandler = new MyHandler();
        MyApplication.login_handler = mHandler;
        
    }

    public void login_mainweixin(View v) {
        /*Intent startIntent = new Intent(this, DataProcessWork.class);
        startIntent.putExtra("id",mUser.getText().toString());
        startIntent.putExtra("password",mPassword.getText().toString());
        //出现正在登录progressbar
        startService(startIntent);*/
        MyApplication.user_id = mUser.getText().toString();
        LoginMessage login_msg = new LoginMessage();
        login_msg.setPassword(mPassword.getText().toString());
        CommonMsg msg = new CommonMsg();
        msg.setFrom(mUser.getText().toString());
        msg.setTo("服务00");
        msg.setType(Protocol.LOGIN_TYPE);
        msg.setTime(Util.longToBytes(System.currentTimeMillis()));
        msg.setToken("00000000000000000000000000000000");
        msg.setData(new Gson().toJson(login_msg).getBytes());
        msg.setLength(Protocol.HEAD_LENGTH + msg.getData().length);
        MyApplication.client_network = new ClientNetWork(getApplicationContext(),msg);
        MyApplication.client_net_work_thread = new Thread(MyApplication.client_network);
        MyApplication.client_net_work_thread.start();
//        MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,msg);


      }
    public void login_back(View v) {     //标题栏 返回按钮
      	this.finish();
      }  
    public void login_pw(View v) {     //忘记密码按钮
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
      }


    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    new AlertDialog.Builder(Login.this)
                            .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                            .setTitle("登录失败")
                            .setMessage("微信帐号或者密码不正确，\n请检查后重新输入！")
                            .create().show();

                    break;
                case 1:
                    ArrayList<ChatlistEntity> msgs = (ArrayList<ChatlistEntity>)msg.obj;
                    int friend_add_num = msg.arg1;
                    Intent intent = new Intent(Login.this,MainWeixin.class);
                    intent.putExtra("transmitionInfo",(Serializable)msgs);
                    intent.putExtra("friend",friend_add_num);
                    startActivity(intent);
                    break;
            }
        }
    }


}
