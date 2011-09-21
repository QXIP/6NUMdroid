package org.doubango.imsdroid.Screens;

import org.doubango.imsdroid.Engine;
import org.doubango.imsdroid.IMSDroid;
//import org.doubango.imsdroid.Main;
import org.doubango.sixnum.R;
import org.doubango.imsdroid.Screens.BaseScreen;
//import org.doubango.imsdroid.Services.IScreenService;

//import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Handler;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
//import android.view.KeyEvent;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnCancelListener;
//import android.view.LayoutInflater;
import android.widget.TabHost.TabContentFactory;
import android.view.KeyEvent;
import android.view.Menu;

import org.doubango.imsdroid.Screens.BaseScreen.SCREEN_TYPE;



//import android.view.View;



public class ScreenTabs extends TabActivity {
//public class ScreenTabs extends BaseScreen{
	
	private static final String TAG = ScreenTabs.class.getCanonicalName();
	
	public ScreenTabs() {
		//super(SCREEN_TYPE.TAB_TABS, TAG);
		super();
	}
	
	//@Override
	//public boolean onKeyDown(int keyCode, KeyEvent event) {
	//    if (keyCode == KeyEvent.KEYCODE_BACK) {
	//        finish();
	//        return true;
	//    }
	//    return super.onKeyDown(keyCode, event);
	// }

	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {   
	  // Handling the back button
	  if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
		 // this.finish();
	    return true;
		  } 
	  this.finish();
	  return true;
	  //return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onPause() {
		 finish();
		 super.onPause();	
	}
	
	@Override
	protected void onStop() {
		 this.finish();
		 super.onStop();	
	}
	
	
	@Override
	public void onBackPressed() {
	// do something on back.
	return;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_home_tab);
		
		//TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		TabHost tabHost = getTabHost();
		//tabHost.setup();
		
	
		//tabHost.addTab(firstTabSpec.setContent(R.id.TabDialer));
		//tabHost.addTab(secondTabSpec);
		
		

		TabSpec firstTabSpec = tabHost.newTabSpec("ScreenTabDialer");
		TabSpec secondTabSpec = tabHost.newTabSpec("ScreenSettings");
		TabSpec thirdTabSpec = tabHost.newTabSpec("ScreenContacts");

		firstTabSpec.setIndicator("Dialer").setContent(new Intent(this,ScreenTabDialer.class));
		secondTabSpec.setIndicator("Settings").setContent(new Intent(this,ScreenSettings.class));
		thirdTabSpec.setIndicator("Contacts").setContent(new Intent(this,ScreenContacts.class));

		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(thirdTabSpec);
		//tabHost.addTab(firstTabSpec.setIndicator("Dialer").setContent(new Intent(this,ScreenTabDialer.class)));
		//tabHost.addTab(firstTabSpec.setIndicator("Dialer").setContent(R.id.screen_tab_contacts_listView));
		//tabHost.addTab(secondTabSpec.setIndicator("Settings").setContent(R.id.TabSettings));
		//tabHost.addTab(thirdTabSpec.setIndicator("History").setContent(R.id.TabHistory));
	
	}

	

	
	
}

