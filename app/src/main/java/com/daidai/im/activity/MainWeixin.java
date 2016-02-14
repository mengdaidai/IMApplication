package com.daidai.im.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.daidai.im.ChatlistAdapter;
import com.daidai.im.FriendListAdapter;
import com.daidai.im.R;
import com.daidai.im.entity.ChatlistEntity;
import com.daidai.im.entity.CommonMsg;
import com.daidai.im.entity.FriendListEntity;
import com.daidai.im.entity.TransmitionMessage;
import com.daidai.im.util.MyApplication;
import com.daidai.im.util.Protocol;

public class MainWeixin extends Activity {
	
	public static MainWeixin instance = null;
	 
	private ViewPager mTabPager;	
	private ImageView mTabImg;
	private ImageView mTab1,mTab2,mTab3,mTab4;
	private int zero = 0;
	private int currIndex = 0;
	private int one;
	private int two;
	private int three;
	private LinearLayout mClose;
    private LinearLayout mCloseBtn;
    private View layout;	
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	ListView chatlist_listview;
	ListView friends_listview;
	List<ChatlistEntity> mChatlistData;
	List<FriendListEntity> mFriendlistData;
	HashMap<String,Integer> chatlist_map;
	ChatlistAdapter chatlist_adapter;
	FriendListAdapter friendlist_adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_weixin);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        instance = this;
        
        mTabPager = (ViewPager)findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mTab1 = (ImageView) findViewById(R.id.img_weixin);
        mTab2 = (ImageView) findViewById(R.id.img_address);
        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_settings);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        Display currDisplay = getWindowManager().getDefaultDisplay();
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth/4;
        two = one*2;
        three = one*3;
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.main_tab_weixin, null);
        View view2 = mLi.inflate(R.layout.main_tab_address, null);
        View view3 = mLi.inflate(R.layout.main_tab_friends, null);
        View view4 = mLi.inflate(R.layout.main_tab_settings, null);

		RelativeLayout add_friend_rl = (RelativeLayout)view3.findViewById(R.id.rl_add_friend);
		RelativeLayout new_friend_rl = (RelativeLayout)view3.findViewById(R.id.rl_new_friend);
		add_friend_rl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addFriend();
			}
		});
		new_friend_rl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newFriend();
			}
		});

		Intent intent = getIntent();
		mChatlistData = (ArrayList<ChatlistEntity>)intent.getSerializableExtra("transmitionInfo");
		mFriendlistData = new ArrayList<FriendListEntity>();
		FriendListEntity e = new FriendListEntity();
		e.setFriend_name("dai");
		e.setFriend_id("85770267");
		e.setFriend_msg("enen");
		e.setAccept(true);
		mFriendlistData.add(e);
		chatlist_listview = (ListView)view1.findViewById(R.id.chatlist);
		friends_listview = (ListView)view3.findViewById(R.id.friendlist);
		chatlist_adapter = new ChatlistAdapter(this,mChatlistData);
		friendlist_adapter = new FriendListAdapter(this,mFriendlistData);
		chatlist_listview.setAdapter(chatlist_adapter);
		friends_listview.setAdapter(friendlist_adapter);

		friends_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				MyApplication.current_to = mFriendlistData.get(i).getFriend_id();
				Intent intent = new Intent(MainWeixin.this,ChatActivity.class);
				startActivity(intent);
			}
		});


        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}

			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mPagerAdapter);
    }
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
    		
        	if(menu_display){
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
        		Intent intent = new Intent();
            	intent.setClass(MainWeixin.this,Exit.class);
            	startActivity(intent);
        	}
    	}
    	
    	else if(keyCode == KeyEvent.KEYCODE_MENU){
			if(!menu_display){
				inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				layout = inflater.inflate(R.layout.main_menu, null);

				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
				

				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
			        	intent.setClass(MainWeixin.this,Exit.class);
			        	startActivity(intent);
			        	menuWindow.dismiss();
					}
				});				
				menu_display = true;				
			}else{
				menuWindow.dismiss();
				menu_display = false;
				}
			
			return false;
		}
    	return false;
    }
	public void btnmainright(View v) {  
		Intent intent = new Intent (MainWeixin.this,MainTopRightDialog.class);
		startActivity(intent);
      }  	
	public void startchat(View v) {
		Intent intent = new Intent (MainWeixin.this,ChatActivity.class);			
		startActivity(intent);
      }  
	public void exit_settings(View v) {
		Intent intent = new Intent (MainWeixin.this,ExitFromSettings.class);
		startActivity(intent);	
	 }
	public void btn_shake(View v) {
		Intent intent = new Intent (MainWeixin.this,ShakeActivity.class);			
		startActivity(intent);	
	}

	public void addFriend(){
		Intent intent = new Intent(this,AddFriendActivity.class);
		startActivity(intent);
	}

	public void newFriend(){
		Intent intent = new Intent(this,FriendAddActivity.class);
		startActivity(intent);
	}
	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				ChatlistEntity e = (ChatlistEntity)msg.obj;
				String from = e.getUser_id();
				Integer pos = chatlist_map.get(from);
				if(pos!=null){
					mChatlistData.remove(pos);
					mChatlistData.add(0,e);
				}else{
					mChatlistData.add(0,e);
				}
				chatlist_adapter.notifyDataSetChanged();
			}else if(msg.what == 1){
				
			}
		}
	}

}
    
    

