package com.daidai.im.activity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.daidai.im.FriendAddAdapter;
import com.daidai.im.R;
import com.daidai.im.entity.FriendListEntity;

import java.util.List;

public class FriendAddActivity extends Activity {

    ListView friend_add_listview;
    FriendAddAdapter mAdapter;
    List<FriendListEntity> mListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        friend_add_listview = (ListView)findViewById(R.id.friend_add_list);
        mAdapter = new FriendAddAdapter(this,mListData);
        friend_add_listview.setAdapter(mAdapter);

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    mListData.add((FriendListEntity)msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

}
