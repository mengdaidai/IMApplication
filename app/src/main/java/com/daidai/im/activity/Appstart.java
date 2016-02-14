package com.daidai.im.activity;



import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

import com.daidai.im.R;

public class Appstart extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.appstart);
		
	new Handler().postDelayed(new Runnable(){
		@Override
		public void run(){
			Intent intent = new Intent (Appstart.this,Welcome.class);
			startActivity(intent);			
			Appstart.this.finish();
		}
	}, 1000);
   }
}