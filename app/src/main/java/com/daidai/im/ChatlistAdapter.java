package com.daidai.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daidai.im.entity.ChatlistEntity;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by songs on 2016/1/26.
 */
public class ChatlistAdapter extends BaseAdapter {
    Context mContext;
    List<ChatlistEntity> mListData;
    private LayoutInflater mInflater = null;

    public ChatlistAdapter(Context context,List<ChatlistEntity> mListData){
        this.mContext = context;
        this.mListData = mListData;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyHolder holder = null;
        if(view == null){
            holder = new MyHolder();
            view = mInflater.inflate(R.layout.chatlist_item,null);
            holder.head_image = (ImageView)view.findViewById(R.id.chatlist_head);
            holder.msg_text = (TextView)view.findViewById(R.id.chatlist_msg);
            holder.nickname_text = (TextView)view.findViewById(R.id.chatlist_nickname);
            holder.time_text = (TextView)view.findViewById(R.id.chatlist_time);
            view.setTag(holder);
        }else{
            holder = (MyHolder)view.getTag();
        }
        ChatlistEntity entity = mListData.get(i);
        holder.nickname_text.setText(entity.getNick_name());
        holder.time_text.setText(entity.getTime());
        holder.msg_text.setText(entity.getTime());
        File file = new File(entity.getHead_path());
        if(file.exists()){
            Bitmap bm = BitmapFactory.decodeFile(entity.getHead_path());
            holder.head_image.setImageBitmap(bm);
        }

        return view;
    }

    private class MyHolder{
        ImageView head_image;
        TextView time_text,msg_text,nickname_text;
    }
}
