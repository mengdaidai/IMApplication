package com.daidai.im.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daidai.im.entity.ChatMsgEntity;
import com.daidai.im.ChatMsgViewAdapter;
import com.daidai.im.R;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FileEntity;
import com.daidai.im.entity.RecordEntity;
import com.daidai.im.util.FileUtils;
import com.daidai.im.util.MediaUtil;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;
import com.daidai.im.util.Util;
import com.daidai.im.view.RecordDialog;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * 
 * @author geniuseoe2012
 *  ��ྫ�ʣ����ע�ҵ�CSDN����http://blog.csdn.net/geniuseoe2012
 *  android��������Ⱥ��200102476
 */
public class ChatActivity extends Activity implements OnClickListener,View.OnTouchListener{
    /** Called when the activity is first created. */

	private Button mBtnSend;
	private Button mBtnBack;
    private Button mBtnPlus;
    private Button mBtnPlusPic;
    private Button mBtnPlusFile;
    private ImageButton mBtnChatMode;
	private EditText mEditTextContent;
    private LinearLayout mPlusLinearLayout;
	private ListView mListView;
    private Button mBtnRecord;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private MediaUtil mMediaUtil ;
    private RecordDialog mDialog;
    private boolean isRecordMode = false;
    private boolean recordCanceled = false;
    private boolean isRecording = false;
    private boolean isPlus = false;
    private String friend_name;
    private MyHandler mHandler;




	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        initView();
        initData();
    }
    
    
    public void initView()
    {
    	mListView = (ListView) findViewById(R.id.listview);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
        mBtnChatMode = (ImageButton)findViewById(R.id.chatmode);
        mBtnChatMode.setOnClickListener(this);
        mBtnRecord = (Button)findViewById(R.id.btn_sendrecord);
        mBtnRecord.setOnTouchListener(this);
        mBtnPlus = (Button)findViewById(R.id.btn_plus);
        mBtnPlus.setOnClickListener(this);
    	mBtnPlusPic = (Button)findViewById(R.id.plus_pic);
        mBtnPlusPic.setOnClickListener(this);
        mBtnPlusFile = (Button)findViewById(R.id.plus_file);
        mBtnPlusFile.setOnClickListener(this);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        mPlusLinearLayout = (LinearLayout)findViewById(R.id.ll_plus);

    }
    private final static int COUNT = 8;
    public void initData()
    {
		//从数据库取
    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
        mMediaUtil = new MediaUtil(ChatActivity.this,MyApplication.record_path);
        mHandler = new MyHandler();
        MyApplication.chat_handler = mHandler;

    }


	@Override
	public void onClick(View v) {
        Intent intent ;
		switch(v.getId())
		{
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
        case R.id.chatmode:
            /*XmlPullParser parser = ChatActivity.this.getResources().getLayout(R.layout.record_button);
            AttributeSet attributes = Xml.asAttributeSet(parser);
            int type;
            try{
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }

                if (type != XmlPullParser.START_TAG) {
                    Log.e("", "the xml file is error!\n");
                }
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("",""+parser.getAttributeCount());
            Button btn=new Button(ChatActivity.this,attributes);
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mMediaUtil.start_record();
                    return true;
                }
            });
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ChatActivity.this,attributes);
            mRelativeLayout .addView(btn,0,params);*/
            if(!isRecordMode){
                mEditTextContent.setVisibility(View.GONE);
                mBtnRecord.setVisibility(View.VISIBLE);
                isRecordMode = !isRecordMode;
            }else{
                mEditTextContent.setVisibility(View.VISIBLE);
                mBtnRecord.setVisibility(View.GONE);
                isRecordMode = !isRecordMode;
            }
            break;
        case R.id.btn_plus:
                if(isPlus){
                    mPlusLinearLayout.setVisibility(View.GONE);
                    isPlus = !isPlus;
                }else{
                    mPlusLinearLayout.setVisibility(View.VISIBLE);
                    isPlus = !isPlus;
                }
            break;
        case R.id.plus_file:
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), 0);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.plus_pic:
            intent = new Intent(ChatActivity.this,AllPicActivity.class);
            startActivityForResult(intent,1);
            break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    FileEntity e  = FileUtils.getFileEntity(this, uri);
                    FileEntity e1 = new FileEntity();
                    e1.setFile_name(e.getFile_name());
                    CommonMsg msg = new CommonMsg();
                    synchronized (this){
                        MyApplication.files.put(MyApplication.msg_id,e1);
                        msg.setMsg_id(MyApplication.msg_id);
                        MyApplication.msg_id++;
                    }
                    String file_name = e.getFile_name().substring(e.getFile_name().lastIndexOf('/') + 1);
                    e.setFile_name(file_name);
                    ChatMsgEntity cs = new ChatMsgEntity();
                    cs.setFile_name(e.getFile_name());
                    cs.setFile_length(e.getFile_length());
                    cs.setType(3);
                    cs.setMsgType(false);
                    cs.setName(MyApplication.user_name);
                    mDataArrays.add(cs);
                    mAdapter.notifyDataSetChanged();

                    msg.setFrom(MyApplication.user_id);
                    msg.setTo(MyApplication.current_to);
                    msg.setToken(MyApplication.token);
                    msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                    msg.setData(new Gson().toJson(e).getBytes());
                    msg.setType(Protocol.FILE_TYPE);
                    msg.setLength(msg.getData().length + Protocol.HEAD_LENGTH);
                    MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,msg);
                }
                break;
            case 1:
                List<String> pics = (List<String>)data.getSerializableExtra("dirs");
                if(pics!=null)
                for(String dir:pics){
                    ChatMsgEntity e = new ChatMsgEntity();
                    e.setData(dir.getBytes());
                    e.setName(MyApplication.user_name);
                    e.setType(2);
                    e.setMsgType(false);
                    mDataArrays.add(e);
                    mAdapter.notifyDataSetChanged();
                    CommonMsg msg = new CommonMsg();
                    msg.setFrom(MyApplication.user_id);
                    msg.setTo(MyApplication.current_to);
                    msg.setToken(MyApplication.token);
                    try{
                        FileInputStream fis = new FileInputStream(dir);
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        msg.setData(buffer);
                    }catch(FileNotFoundException e1){
                        e1.printStackTrace();
                    }catch(IOException e2){
                        e2.printStackTrace();
                    }
                    msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                    msg.setType(Protocol.PICTURE_TYPE);
                    msg.setLength(msg.getData().length + Protocol.HEAD_LENGTH);
                    MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,msg);

                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                mMediaUtil.start_record();
                mDialog = new RecordDialog(ChatActivity.this);
                mDialog.show();
                new Thread(new ObtainVolumeThread()).start();
                break;
            case MotionEvent.ACTION_MOVE:
                if(motionEvent.getY()<view.getTop()){
                    mDialog.showExit();
                    recordCanceled = true;
                }else{
                    mDialog.showRecord();
                    recordCanceled = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isRecording = false;
                //mDialog.cancel();
                if(recordCanceled){
                    mMediaUtil.cancel_record();
                }else{
                    RecordEntity re = mMediaUtil.stop_record();
                    ChatMsgEntity e = new ChatMsgEntity();
                    e.setDate(getDate());
                    e.setName(MyApplication.user_name);
                    e.setMsgType(false);
                    e.setType(1);
                    e.setRecord_time(re.getTime());
                    e.setRecord_path(re.getFile_path());
                    mDataArrays.add(e);
                    mAdapter.notifyDataSetChanged();
                    CommonMsg send_msg = new CommonMsg();
                    send_msg.setFrom(MyApplication.user_id);
                    send_msg.setTo(MyApplication.current_to);
                    try{
                        FileInputStream fis = new FileInputStream(new File(re.getFile_path()));
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        send_msg.setData(buffer);
                        send_msg.setTime(Util.longToBytes(System.currentTimeMillis()));
                        send_msg.setToken(MyApplication.token);
                        send_msg.setType(Protocol.VOICE_TYPE);
                        send_msg.setLength(send_msg.getData().length + Protocol.HEAD_LENGTH);
                        MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,send_msg);
                    }catch(FileNotFoundException exception){
                        exception.printStackTrace();
                    }catch(IOException exception){
                        exception.printStackTrace();
                    }

                }
                break;
        }
        return true;
    }



    private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
            entity.setName(MyApplication.user_name);
			entity.setMsgType(false);
			entity.setText(contString);
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
            CommonMsg send_msg = new CommonMsg();
            send_msg.setToken(MyApplication.token);
            send_msg.setFrom(MyApplication.user_id);
            send_msg.setTo(MyApplication.current_to);
            send_msg.setData(contString.getBytes());
            send_msg.setType(Protocol.TEXT_TYPE);
            send_msg.setLength(send_msg.getData().length + Protocol.HEAD_LENGTH);
            send_msg.setTime(Util.longToBytes(System.currentTimeMillis()));
            MyApplication.client_network.registerWriteChannel(MyApplication.socketChannel,send_msg);
		}
	}


	
    private String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
        						
        						
        return sbBuffer.toString();
    }
    
    
    public void head_xiaohei(View v) {
    	Intent intent = new Intent (ChatActivity.this,InfoXiaohei.class);
		startActivity(intent);	
      }

	private class MyHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){//文本信息
                CommonMsg commonMsg = (CommonMsg)msg.obj;
                ChatMsgEntity e = new ChatMsgEntity();
                e.setText(commonMsg.getData().toString());
                e.setDate(Long.toString(Util.bytesToLong(commonMsg.getTime())));
                e.setType(0);
                e.setMsgType(true);
                e.setName(friend_name);
                mDataArrays.add(e);
                mAdapter.notifyDataSetChanged();
			}else if(msg.what == 1){//语音消息
                CommonMsg commonMsg = (CommonMsg)msg.obj;
                ChatMsgEntity e = new ChatMsgEntity();
                e.setRecord_path(commonMsg.getData().toString());
                e.setRecord_time(0);//这个暂时还没有获取
                e.setDate(Long.toString(Util.bytesToLong(commonMsg.getTime())));
                e.setType(1);
                e.setMsgType(true);
                e.setName(friend_name);
                mDataArrays.add(e);
                mAdapter.notifyDataSetChanged();
			}else if(msg.what == 2){//图片消息
                CommonMsg commonMsg = (CommonMsg)msg.obj;
                ChatMsgEntity e = new ChatMsgEntity();
                e.setData(commonMsg.getData());
                e.setDate(Long.toString(Util.bytesToLong(commonMsg.getTime())));
                e.setType(2);
                e.setMsgType(true);
                e.setName(friend_name);
                mDataArrays.add(e);
                mAdapter.notifyDataSetChanged();
			}else if(msg.what == 3){//文件消息
                CommonMsg commonMsg = (CommonMsg)msg.obj;
                FileEntity entity = new Gson().fromJson(commonMsg.getData().toString(),FileEntity.class);
                ChatMsgEntity e = new ChatMsgEntity();
                e.setFile_length(entity.getFile_length());
                e.setFile_name(entity.getFile_name());
                e.setDate(Long.toString(Util.bytesToLong(commonMsg.getTime())));
                e.setType(3);
                e.setMsgType(true);
                e.setName(friend_name);
                e.setIs_offline(false);
                e.setCommon_msg_id(commonMsg.getMsg_id());
                mDataArrays.add(e);
                mAdapter.notifyDataSetChanged();
			}else if(msg.what == 4){//更新音量大小
                mDialog.updateViewer(msg.arg1);
            }
		}
	}

    private class ObtainVolumeThread implements Runnable{


        @Override
        public void run() {
            while(isRecording){
                try{
                    Thread.sleep(200);
                    int volumn = mMediaUtil.getVolumn();
                    if(volumn!=0){
                        Message msg = Message.obtain();
                        msg.what = 4;
                        msg.arg1 = volumn;
                        mHandler.sendMessage(msg);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }


            }
            mDialog.cancel();
        }
    }


}