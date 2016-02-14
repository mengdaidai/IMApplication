package com.daidai.im.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daidai.im.R;
import com.daidai.im.dataprocess.RegisterNetWork;

/**
 * Created by songs on 2015/12/30.
 */
public class Register extends Activity {
    private EditText mUser; // 用户名编辑框
    private EditText mPassword; // 密码编辑框
    private TextView id_text;//显示注册后的id

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mUser = (EditText)findViewById(R.id.register_user_edit);
        mPassword = (EditText)findViewById(R.id.register_passwd_edit);
        id_text = (TextView)findViewById(R.id.register_id);

    }

    public void register_mainweixin(View v) {
        System.out.println("注册！");
        new Thread(new RegisterNetWork(mUser.getText().toString(),mPassword.getText().toString())).start();

    }
    public void register_back(View v) {     //标题栏 返回按钮
        this.finish();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                String response = (String)msg.obj;
                id_text.setText(response);
            }
        }
    }

}
