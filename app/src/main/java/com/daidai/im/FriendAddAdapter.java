package com.daidai.im;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FriendListEntity;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by songs on 2016/2/3.
 */
public class FriendAddAdapter extends BaseAdapter {

    Context context;
    List<FriendListEntity> mList;
    private LayoutInflater mInflater;

    public FriendAddAdapter(Context context,List<FriendListEntity> mList){
        this.context = context;
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder = null;
        if(view == null){
            view = mInflater.inflate(R.layout.friend_add_item,null);
            viewholder = new ViewHolder();
            viewholder.head_pic = (ImageView)view.findViewById(R.id.friend_add_head);
            viewholder.name_text = (TextView)view.findViewById(R.id.friend_add_name);
            viewholder.msg_text = (TextView)view.findViewById(R.id.friend_add_msg);
            viewholder.accept_button = (Button)view.findViewById(R.id.friend_add_accept);
            viewholder.accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button button = (Button)view;
                    int position = (Integer)button.getTag();
                    button.setClickable(false);
                    button.setText("已接受");
                    CommonMsg msg = new CommonMsg();
                    msg.setType(Protocol.FRIEND_RESPONSE_TYPE);
                    msg.setToken(MyApplication.token);
                    msg.setTo(mList.get(position).getFriend_id());
                    msg.setFrom(MyApplication.user_id);
                    msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                    msg.setLength(Protocol.HEAD_LENGTH);
                    MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,msg);
                }
            });
            view.setTag(viewholder);
        }else{
            viewholder = (ViewHolder)view.getTag();
        }

        //还没设置头像
        viewholder.accept_button.setTag(i);
        viewholder.msg_text.setText(mList.get(i).getFriend_msg());
        viewholder.name_text.setText(mList.get(i).getFriend_name());
        if(mList.get(i).isAccept()){
            viewholder.accept_button.setBackgroundColor(Color.WHITE);
            viewholder.accept_button.setClickable(false);
            viewholder.accept_button.setText("已接受");
        }else{
            viewholder.accept_button.setBackgroundColor(Color.GREEN);
            viewholder.accept_button.setClickable(true);
            viewholder.accept_button.setText("接受");
        }

        return view;
    }

    private class ViewHolder {
        ImageView head_pic;
        TextView name_text,msg_text;
        Button accept_button;
    }

}
