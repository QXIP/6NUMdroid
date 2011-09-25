/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.doubango.imsdroid.Screens;

import org.doubango.imsdroid.CustomDialog;
import org.doubango.imsdroid.Main;
import org.doubango.sixnum.R;
//import org.doubango.imsdroid.Screens.BaseScreen.SCREEN_TYPE;
//import org.doubango.imsdroid.Screens.ScreenTabDialer.PhoneInputType;
import org.doubango.imsdroid.Utils.DialerUtils;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.services.INgnConfigurationService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.pm.ActivityInfo;



public class ScreenHome extends BaseScreen {
	private static String TAG = ScreenHome.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	private static final int MENU_EXIT = 0;
	private static final int MENU_SETTINGS = 1;
	private static final int MENU_TABS = 2;
	private static final int MENU_MESSAGES = 3;

	
	private GridView mGridView;
	
	private final INgnSipService mSipService;
	
	private BroadcastReceiver mSipBroadCastRecv;
	
	public ScreenHome() {
		super(SCREEN_TYPE.HOME_T, TAG);
		
		mSipService = getEngine().getSipService();
		
		mInputType = PhoneInputType.Numbers;
		
		mConfigurationService = getEngine().getConfigurationService();
	}
	
	private EditText mEtNumber;
	private ImageButton mIbInputType;
	
	static enum PhoneInputType{
		Numbers,
		Text
	}

	private PhoneInputType mInputType;
	private InputMethodManager mInputMethodManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_home);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mEtNumber = (EditText)findViewById(R.id.screen_tab_dialer_editText_number);
		mIbInputType = (ImageButton) findViewById(R.id.screen_tab_dialer_imageButton_input_type);
		
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_0, "0", "+", DialerUtils.TAG_0, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_1, "1", "", DialerUtils.TAG_1, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_2, "2", "ABC", DialerUtils.TAG_2, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_3, "3", "DEF", DialerUtils.TAG_3, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_4, "4", "GHI", DialerUtils.TAG_4, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_5, "5", "JKL", DialerUtils.TAG_5, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_6, "6", "MNO", DialerUtils.TAG_6, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_7, "7", "PQRS", DialerUtils.TAG_7, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_8, "8", "TUV", DialerUtils.TAG_8, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_9, "9", "WXYZ", DialerUtils.TAG_9, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_star, "*", "", DialerUtils.TAG_STAR, mOnDialerClick);
		DialerUtils.setDialerTextButton(this, R.id.screen_tab_dialer_button_sharp, "#", "", DialerUtils.TAG_SHARP, mOnDialerClick);
		
		DialerUtils.setDialerImageButton(this, R.id.screen_tab_dialer_button_audio, R.drawable.voice_call_48, DialerUtils.TAG_AUDIO_CALL, mOnDialerClick);
		DialerUtils.setDialerImageButton(this, R.id.screen_tab_dialer_button_video, R.drawable.visio_call_48, DialerUtils.TAG_VIDEO_CALL, mOnDialerClick);
		DialerUtils.setDialerImageButton(this, R.id.screen_tab_dialer_button_del, R.drawable.ic_input_delete_48, DialerUtils.TAG_DELETE, mOnDialerClick);
		

		
		mEtNumber.setInputType(InputType.TYPE_NULL);
		mEtNumber.setFocusable(false);
		mEtNumber.setFocusableInTouchMode(false);
		mEtNumber.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mInputType == PhoneInputType.Numbers){
					final boolean bShowCaret = mEtNumber.getText().toString().length() > 0;
					mEtNumber.setFocusableInTouchMode(bShowCaret);
					mEtNumber.setFocusable(bShowCaret);
				}
			}
        });
		
		findViewById(R.id.screen_tab_dialer_button_0).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				appendText("+");
				return true;
			}
		});
		
		mIbInputType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(mInputType){
					case Numbers:
						mInputType = PhoneInputType.Text;
						mIbInputType.setImageResource(R.drawable.input_text);
						mEtNumber.setInputType(InputType.TYPE_CLASS_TEXT);
						
						mEtNumber.setFocusableInTouchMode(true);
						mEtNumber.setFocusable(true);
						mInputMethodManager.showSoftInput(mEtNumber, 0);
						break;
						
					case Text:
					default:
						mInputType = PhoneInputType.Numbers;
						mIbInputType.setImageResource(R.drawable.input_numbers);
						mEtNumber.setInputType(InputType.TYPE_NULL);
						
						final boolean bShowCaret = mEtNumber.getText().toString().length() > 0;
						mEtNumber.setFocusableInTouchMode(bShowCaret);
						mEtNumber.setFocusable(bShowCaret);
						mInputMethodManager.hideSoftInputFromWindow(mEtNumber.getWindowToken(), 0);
						break;
				}
			}
		});
		
		// INUMDROID: auto registration
		// If username and password are set attempt registration
		if (mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD) != null ) {
				if(mSipService.isRegistered()) {
					// do nothing
					
					}  else { mSipService.register(ScreenHome.this);  }
		} else {
			// if not, hand over to provisioning module
			CustomDialog.show(
					ScreenHome.this,
					R.drawable.about_48,
					null,
					"INUM Not Configured \nProvision a New or Existing Account?",
					"Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Alex added an iNum check
							boolean inum = mConfigurationService.getBoolean(NgnConfigurationEntry.GENERAL_INUM_PROV,NgnConfigurationEntry.DEFAULT_GENERAL_INUM_PROV);

							if  (inum)
								mScreenService.show(ScreenAbout.class);
							else
								dialog.cancel();
						}
					}, "No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			
		 } 
		
		// end of transmission
		
		
		
		mGridView = (GridView) findViewById(R.id.screen_home_gridview);
		mGridView.setAdapter(new ScreenHomeAdapter(this));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ScreenHomeItem item = (ScreenHomeItem)parent.getItemAtPosition(position);
				if (item != null) {
					if(position == ScreenHomeItem.ITEM_SIGNIN_SIGNOUT_POS){
						if(mSipService.getRegistrationState() == ConnectionState.CONNECTING || mSipService.getRegistrationState() == ConnectionState.TERMINATING){
							mSipService.stopStack();
						}
						else if(mSipService.isRegistered()){
							mSipService.unRegister();
						} 
						else{
							mSipService.register(ScreenHome.this);
						}
					}
					else if(position == ScreenHomeItem.ITEM_EXIT_POS){
						CustomDialog.show(
								ScreenHome.this,
								R.drawable.exit_48,
								null,
								"Are you sure you want to exit?",
								"Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										((Main)(getEngine().getMainActivity())).exit();
									}
								}, "No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
					}
					else{					
						mScreenService.show(item.mClass, item.mClass.getCanonicalName());
					}
				}
				
				
				
			}
		});
		
		mSipBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				
				// Registration Event
				if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
					NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
					if(args == null){
						Log.e(TAG, "Invalid event args");
						return;
					}
					switch(args.getEventType()){
						case REGISTRATION_NOK:
						case UNREGISTRATION_OK:
						case REGISTRATION_OK:
						case REGISTRATION_INPROGRESS:
						case UNREGISTRATION_INPROGRESS:
						case UNREGISTRATION_NOK:
						default:
							((ScreenHomeAdapter)mGridView.getAdapter()).refresh();
							break;
					}
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
	    registerReceiver(mSipBroadCastRecv, intentFilter);
	}

	
	private final View.OnClickListener mOnDialerClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = Integer.parseInt(v.getTag().toString());
			final String number = mEtNumber.getText().toString();
			if(tag == DialerUtils.TAG_CHAT){
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(number)){
					// ScreenChat.startChat(number);
					mEtNumber.setText(NgnStringUtils.emptyValue());
				}
			}
			else if(tag == DialerUtils.TAG_DELETE){
				final int selStart = mEtNumber.getSelectionStart();
				if(selStart >0){
					final StringBuffer sb = new StringBuffer(number);
					sb.delete(selStart-1, selStart);
					mEtNumber.setText(sb.toString());
					mEtNumber.setSelection(selStart-1);
				}
			}
			else if(tag == DialerUtils.TAG_AUDIO_CALL){
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(number)){
					ScreenAV.makeCall(number, NgnMediaType.Audio);
					mEtNumber.setText(NgnStringUtils.emptyValue());
				}
			}
			else if(tag == DialerUtils.TAG_VIDEO_CALL){
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(number)){
					ScreenAV.makeCall(number, NgnMediaType.AudioVideo);
					mEtNumber.setText(NgnStringUtils.emptyValue());
				}
			}
			else{
				final String textToAppend = tag == DialerUtils.TAG_STAR ? "*" : (tag == DialerUtils.TAG_SHARP ? "#" : Integer.toString(tag));
				appendText(textToAppend);
			}
		}
	};
	
	private void appendText(String textToAppend){
		final int selStart = mEtNumber.getSelectionStart();
		final StringBuffer sb = new StringBuffer(mEtNumber.getText().toString());
		sb.insert(selStart, textToAppend);
		mEtNumber.setText(sb.toString());
		mEtNumber.setSelection(selStart+1);
	}
	
	@Override
	protected void onDestroy() {
       if(mSipBroadCastRecv != null){
    	   unregisterReceiver(mSipBroadCastRecv);
    	   mSipBroadCastRecv = null;
       }
        
       super.onDestroy();
	}
	
	@Override
	public boolean hasMenu() {
		return true;
	}
	
	@Override
	public boolean createOptionsMenu(Menu menu) {
		menu.add(0, ScreenHome.MENU_SETTINGS, 0, "Contacts");
		// Alex added an iNum check
		boolean inum = mConfigurationService.getBoolean(NgnConfigurationEntry.GENERAL_INUM_PROV,NgnConfigurationEntry.DEFAULT_GENERAL_INUM_PROV);
		if  (inum)
			menu.add(0, ScreenHome.MENU_TABS, 0, "Provision");
		menu.add(0, ScreenHome.MENU_MESSAGES, 0, "Messages");
		/*MenuItem itemExit =*/ menu.add(0, ScreenHome.MENU_EXIT, 0, "Exit");
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case ScreenHome.MENU_EXIT:
				((Main)getEngine().getMainActivity()).exit();
				break;
			case ScreenHome.MENU_SETTINGS:
				// temp change to Contacts for testing
				mScreenService.show(ScreenTabContacts.class);
				break;
			case ScreenHome.MENU_MESSAGES:
				mScreenService.show(ScreenTabMessages.class);
				break;
			case ScreenHome.MENU_TABS:
				//mScreenService.show(ScreenTabs.class);
				mScreenService.show(ScreenAbout.class);
				break;
		}
		return true;
	}
	
	
	/**
	 * ScreenHomeItem
	 */
	static class ScreenHomeItem {
		static final int ITEM_SIGNIN_SIGNOUT_POS = 0;
		static final int ITEM_EXIT_POS = 4;
		final int mIconResId;
		final String mText;
		final Class<? extends Activity> mClass;

		private ScreenHomeItem(int iconResId, String text, Class<? extends Activity> _class) {
			mIconResId = iconResId;
			mText = text;
			mClass = _class;
		}
	}
	
	/**
	 * ScreenHomeAdapter
	 */
	static class ScreenHomeAdapter extends BaseAdapter{
		static final int ALWAYS_VISIBLE_ITEMS_COUNT = 3;
		static final ScreenHomeItem[] sItems =  new ScreenHomeItem[]{
			// always visible
    		new ScreenHomeItem(R.drawable.sign_in_48, "Sign In", null),
    		//new ScreenHomeItem(R.drawable.exit_48, "Exit/Quit", null),
    		new ScreenHomeItem(R.drawable.options_48, "Options", ScreenSettings.class),
    		//new ScreenHomeItem(R.drawable.about_48, "Provision", ScreenAbout.class),
    		// visible only if connected
    		//new ScreenHomeItem(R.drawable.dialer_48, "Dialer", ScreenTabDialer.class),
    		//new ScreenHomeItem(R.drawable.eab2_48, "Address Book", ScreenTabContacts.class),
    		new ScreenHomeItem(R.drawable.history_48, "History", ScreenTabHistory.class),
    		//new ScreenHomeItem(R.drawable.chat_48, "Messages", ScreenTabMessages.class),
		};
		
		private final LayoutInflater mInflater;
		private final ScreenHome mBaseScreen;
		
		ScreenHomeAdapter(ScreenHome baseScreen){
			mInflater = LayoutInflater.from(baseScreen);
			mBaseScreen = baseScreen;
		}
		
		void refresh(){
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mBaseScreen.mSipService.isRegistered() ? sItems.length : ALWAYS_VISIBLE_ITEMS_COUNT;
		}

		@Override
		public Object getItem(int position) {
			return sItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ScreenHomeItem item = (ScreenHomeItem)getItem(position);
			
			if(item == null){
				return null;
			}

			if (view == null) {
				view = mInflater.inflate(R.layout.screen_home_item, null);
			}
			
			if(position == ScreenHomeItem.ITEM_SIGNIN_SIGNOUT_POS){
				if(mBaseScreen.mSipService.getRegistrationState() == ConnectionState.CONNECTING || mBaseScreen.mSipService.getRegistrationState() == ConnectionState.TERMINATING){
					((TextView) view.findViewById(R.id.screen_home_item_text)).setText("Cancel");
					((ImageView) view .findViewById(R.id.screen_home_item_icon)).setImageResource(R.drawable.sign_inprogress_48);
				}
				else{
					if(mBaseScreen.mSipService.isRegistered()){
						((TextView) view.findViewById(R.id.screen_home_item_text)).setText("Log Out");
						((ImageView) view .findViewById(R.id.screen_home_item_icon)).setImageResource(R.drawable.sign_out_48);
					}
					else{
						((TextView) view.findViewById(R.id.screen_home_item_text)).setText("Log In");
						((ImageView) view .findViewById(R.id.screen_home_item_icon)).setImageResource(R.drawable.sign_in_48);
					}
				}
			}
			else{				
				((TextView) view.findViewById(R.id.screen_home_item_text)).setText(item.mText);
				((ImageView) view .findViewById(R.id.screen_home_item_icon)).setImageResource(item.mIconResId);
			}
			
			return view;
		}
		
		
		public boolean hasBack(){
			return true;
		}
		
		
	}
	
}
