package com.daidai.im.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.daidai.im.R;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FriendMsg;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;

public class AddFriendActivity extends Activity {

    Button add_friend_btn;
    EditText friend_id_edit,friend_msg_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        add_friend_btn = (Button)findViewById(R.id.btn_add_friend);
        friend_msg_edit = (EditText)findViewById(R.id.friend_msg);
        friend_id_edit = (EditText)findViewById(R.id.friend_id);
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMsg msg = new CommonMsg();
                msg.setFrom(MyApplication.user_id);
                msg.setTo(friend_id_edit.getText().toString());
                msg.setToken(MyApplication.token);
                msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                msg.setType(Protocol.FRIEND_TYPE);
                msg.setData(friend_msg_edit.getText().toString().getBytes());
                msg.setLength(Protocol.HEAD_LENGTH+msg.getData().length);
                MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,msg);

            }
        });
    }

}
