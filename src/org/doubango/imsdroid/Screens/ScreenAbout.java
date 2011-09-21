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

import org.doubango.imsdroid.IMSDroid;
import org.doubango.sixnum.R;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
//import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
//import android.preference.PreferenceManager;





public class ScreenAbout extends BaseScreen {
	private static final String TAG = ScreenAbout.class.getCanonicalName();
	private final INgnConfigurationService mConfigurationService;
	
	WebView webview;
	
	public ScreenAbout() {
		super(SCREEN_TYPE.ABOUT_T, TAG);
		
		mConfigurationService = getEngine().getConfigurationService();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_about);
                
        TextView textView = (TextView)this.findViewById(R.id.screen_about_textView_copyright);
        String copyright = this.getString(R.string.copyright);
		textView.setText(String.format(copyright,
				IMSDroid.getVersionName(), this.getString(R.string.doubango_revision)));
		
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		// WebAPP Android Hook Publish
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		
		
		final Activity activity = this;
		
		 webview.setWebChromeClient(new WebChromeClient() {
		   public void onProgressChanged(WebView view, int progress) {
		     // Activities and WebViews measure progress with different scales.
		     // The progress meter will automatically disappear when we reach 100%
		     activity.setProgress(progress * 1000);
		   }
		 });
		 webview.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		 });
		
		// CLEAR CACHE
		webview.clearCache(true);
		
		//webview.setBackgroundColor(0);
		//webview.clearView();
		webview.loadUrl("http://www.qxip.net/openid/public/user/login");
		webview.setWebViewClient(new HelloWebViewClient());
		
		webview.requestFocus(View.FOCUS_DOWN);
		
		

	}
	
	// WEBPROV START
	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	
		}
	
	
	// WebAPP Hook func
	public class JavaScriptInterface {
	    Context mContext;
	    
	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c) {
	        mContext = c;
	    }
	
	    
	    public void getProvisioning(String username, String password, String domain, String proxy) {
	    	// do something with them (IMSDROID VERSION)
	    	
	    	mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, 
					username.toString().trim());
	    	mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, 
					"sip:"+username+"@"+domain.toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, 
					username.toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, 
					password.toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, 
					domain.toString().trim());
			mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST,
					domain.toString().trim());
			// Compute & commit
						if(!mConfigurationService.commit()){
							Log.e(TAG, "Failed to Commit() configuration");
							Toast.makeText(mContext, "Provisioning Glitch", Toast.LENGTH_SHORT).show();
						} else { 
							Toast.makeText(mContext, "INUM: "+username, Toast.LENGTH_SHORT).show(); 
							}
			
			// Switch to Settings Page for Auto-Calibration & Exit Activity
			//webview.destroy();
			//mScreenService.show(ScreenHome.class);
			finish();
			 
	    }
	}


	// WEBPROV END
	

	
	

}
