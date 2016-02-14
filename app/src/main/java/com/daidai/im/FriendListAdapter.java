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

import java.util.List;

/**
 * Created by songs on 2016/2/5.
 */
public class FriendListAdapter extends BaseAdapter {

    Context context;
    List<FriendListEntity> mList;
    private LayoutInflater mInflater;

    public FriendListAdapter(Context context,List<FriendListEntity> mList) {
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
            view = mInflater.inflate(R.layout.friend_item,null);
            viewholder = new ViewHolder();
            viewholder.head_pic = (ImageView)view.findViewById(R.id.friend_add_head);
            viewholder.name_text = (TextView)view.findViewById(R.id.friend_add_name);

            view.setTag(viewholder);
        }else{
            viewholder = (ViewHolder)view.getTag();
        }

        //还没设置头像

        viewholder.name_text.setText(mList.get(i).getFriend_name());


        return view;
    }

    private class ViewHolder {
        ImageView head_pic;
        TextView name_text;
    }
}
