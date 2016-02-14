
package com.daidai.im;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.daidai.im.entity.ChatMsgEntity;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FileEntity;
import com.daidai.im.entity.ServerFileResponse;
import com.daidai.im.util.MediaUtil;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    MediaUtil util ;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
        util = new MediaUtil(context,null);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    

	
	
    public View getView(int position, View convertView, ViewGroup parent) {

		ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		MyViewHolder viewHolder = null;

			viewHolder = new MyViewHolder();
			if (isComMsg)
			{
				if(entity.getType() == 0){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
					viewHolder.tvContent.setText(entity.getText());
				}else if(entity.getType() == 1){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_voice_left, null);
					viewHolder.recordTime = (TextView)convertView.findViewById(R.id.tv_time);
                    viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
					viewHolder.recordTime.setText(entity.getRecord_time()+"''");
					viewHolder.tvContent.setTag(entity.getRecord_path());
					viewHolder.tvContent.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							String path = (String)view.getTag();
							util.play_record(path);
						}
					});
				}else if(entity.getType() == 2){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_image_left, null);
					viewHolder.ivContent = (ImageView)convertView.findViewById(R.id.iv_chatcontent);
					Bitmap bitmap = BitmapFactory.decodeFile(new String (entity.getData()));
					viewHolder.ivContent.setImageBitmap(bitmap);
				}else if(entity.getType() == 3){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_image_left, null );
					viewHolder.btnAccept = (Button)convertView.findViewById(R.id.btn_accept);
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
				}
			}else{
				if(entity.getType() == 0){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
					viewHolder.tvContent.setText(entity.getText());
				}else if(entity.getType() == 1){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_voice_right, null);
					viewHolder.recordTime = (TextView)convertView.findViewById(R.id.tv_time);
                    viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
					viewHolder.recordTime.setText(entity.getRecord_time()+"''");
					viewHolder.tvContent.setTag(entity.getRecord_path());
					viewHolder.tvContent.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							String path = (String)view.getTag();
							util.play_record(path);
						}
					});
				}else if(entity.getType() == 2){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_image_right, null);
					viewHolder.ivContent = (ImageView)convertView.findViewById(R.id.iv_chatcontent);
					Bitmap bitmap = BitmapFactory.decodeFile(new String (entity.getData()));
					viewHolder.ivContent.setImageBitmap(bitmap);
				}else if(entity.getType() == 3){
					convertView = mInflater.inflate(R.layout.chatting_item_msg_file_right, null );
					viewHolder.btnAccept = (Button)convertView.findViewById(R.id.btn_accept);
					viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_chatcontent);
					viewHolder.btnAccept.setVisibility(View.GONE);
					ServerFileResponse response = new ServerFileResponse();
					synchronized (this) {
						response.setFile_id(MyApplication.msg_id);
						FileEntity file_entity = new FileEntity();
						file_entity.setFile_name(entity.getFile_name());
						file_entity.setFile_length(entity.getFile_length());
						MyApplication.files.put(MyApplication.msg_id,file_entity);
						MyApplication.msg_id++;

					}
					response.setPort(MyApplication.port);
					response.setYes(true);
					CommonMsg commonMsg = new CommonMsg();
					commonMsg.setMsg_id(entity.getCommon_msg_id());
					if(entity.is_offline())
						commonMsg.setType(Protocol.TRANSMITION_RESPONSE_TYPE);
					else
						commonMsg.setType(Protocol.FILE_RESPONSE_TYPE);
					commonMsg.setData(new Gson().toJson(response).getBytes());
					viewHolder.btnAccept.setTag(commonMsg);
					viewHolder.tvContent.setText(entity.getFile_name() + "\n" + entity.getFile_off() + "/" + entity.getFile_length());
					viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							CommonMsg msg = (CommonMsg) view.getTag();
							msg.setFrom(MyApplication.user_id);
							msg.setTo(MyApplication.current_to);
							msg.setLength(msg.getData().length + Protocol.HEAD_LENGTH);
							msg.setToken(MyApplication.token);
							msg.setTime(Util.longToBytes(System.currentTimeMillis()));
							MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel, msg);

						}
					});
				}
			}

			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.isComMsg = isComMsg;
			convertView.setTag(viewHolder);




		viewHolder.tvSendTime.setText(entity.getDate());
		viewHolder.tvUserName.setText(entity.getName());


		return convertView;
    }



	private class MyViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public ImageView ivContent;
		public TextView recordTime;
		public boolean isComMsg = true;
		public Button btnAccept;
	}

}
