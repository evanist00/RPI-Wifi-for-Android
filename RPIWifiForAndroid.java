/*Copyright (c) 2010, HelpDesk Studios
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the HelpDesk Studios nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL HelpDesk Studios BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * 
 * 
 * This program was written by John Lee and Tom Alexander at RPI.
 * Special thanks to Professor Hollinger and xkcd comic about SQL Injection.
 * Special "fuck you" to everyone who thought this was impossible (myself included) lol
 * Honerable mention goes to a bottle of Kendall Jackson Cabernet Sauvignon
 * A quick note about why I made this program: It's true this feature should
 * be native to the Android's wifi class. The actual concept of injecting the missing
 * concepts is pretty ridiculous. 
 * 
 * Never the less, there was a need for Droid users to get onto the RPI wireless
 * to access secured sites only available on campus and I saw it fit to deliver.
 */
package hd.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RPIWifiForAndroid extends Activity {
	
	WifiConfiguration w;
	Handler progstate = new Handler();
	int ntwrkid;
	EditText rcsid, rcspwd;
	Toast t;
	Button connectbutton, closebutton;
	SharedPreferences myPrefs;
	SharedPreferences.Editor prefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Button connectbutton = (Button)findViewById(R.id.btn_connect);
		Button closebutton = (Button) findViewById(R.id.btn_close);
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		prefsEditor = myPrefs.edit();

		rcsid = (EditText) findViewById(R.id.rcs_id);
		rcspwd = (EditText) findViewById(R.id.rcs_pw);
		Log.i("SHARED PREFERENCESSSSSS " , myPrefs.getString("username", "whynousername"));
		//if login info saved 
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		if(!myPrefs.getString("username", "whynousername").equals("whynousername")) {
			rcsid.setText(myPrefs.getString("username", "smithj2"));
		
		}
		if(!myPrefs.getString("password", "whynopassword").equals("whynopassword")) {
			
			rcspwd.setText(myPrefs.getString("password", ""));
			
		}
		connectbutton.setOnClickListener(cfginjector);
		closebutton.setOnClickListener(appcloser);

	};
	
	private void WifiEnabler(WifiManager wifi) {
		Log.wtf("GGGGGGGGGGGGGGGGGGGGGG", "Enabling Wifi");
		//t = Toast.makeText(getApplication(), "Enabling Wifi", Toast.LENGTH_SHORT);
		wifi.setWifiEnabled(true);
		while(wifi.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
			//wait till enabled
		}
		//t.cancel();
		//t = Toast.makeText(getApplication(), "WIFI STATE now enabled", Toast.LENGTH_SHORT);
		//t.show();
	}
	
	private void WifiDisabler(WifiManager wifi) {
		//t = Toast.makeText(getApplication(), "Disabling Wifi", Toast.LENGTH_SHORT);
		wifi.setWifiEnabled(false);
		while(wifi.getWifiState()!=WifiManager.WIFI_STATE_DISABLED) {
			//wait till disabled
		}
		//t.cancel();
		//t = Toast.makeText(getApplication(), "WIFI STATE now Disabled", Toast.LENGTH_SHORT);
		//t.show();
	}
	
	
	private int getNetworkID(WifiManager wifi) {
		ntwrkid = 0;
		int size =  wifi.getConfiguredNetworks().size();
		for (int x = 0; x < size; x++) {
			w = wifi.getConfiguredNetworks().get(x);
			if (w.SSID.contains("\"r\""))
			{
				ntwrkid = x;
				break;
			}
		}
		return ntwrkid;
	}
	
	private int initSettings(WifiManager wifi) {
		w = new WifiConfiguration();
		
		wifi.setWifiEnabled(true);
		w.status = WifiConfiguration.Status.ENABLED;
		w.allowedProtocols.clear();
		//Authorized Algorithms
		w.allowedAuthAlgorithms.clear();
		w.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		w.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		//Key Management
		w.allowedKeyManagement.clear();
		w.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		//Group Ciphers
		w.allowedGroupCiphers.clear();
		//Pairwise Ciphers
		w.allowedPairwiseCiphers.clear();
		w.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
		//SSID
		w.SSID ="\"r\"";
		
		ntwrkid=wifi.addNetwork(w);
		if(ntwrkid==-1) {
			Log.i("XXXXXXXXXXXXXXXXXXXXX", "COULDNT GET THE FIRST PART WORKING\r\n");
			return -1;
		}
		else {
			Log.wtf("GGGGGGGGGGGGGGGGGGGGGG", "INIT settings configured successfully");
			//t = Toast.makeText(getApplication(), "Init settings added settings", Toast.LENGTH_SHORT);
			//t.show();
			wifi.saveConfiguration();
			return ntwrkid;
		}
	
	}
	
	private int peapSettings(WifiManager wifi, int ntwrkid) {
		w = wifi.getConfiguredNetworks().get(ntwrkid);
		Log.wtf("GGGGGGGGGGGGGGGGGGGGGG", "Grabbed network settings");
		w.SSID = wifi.getConfiguredNetworks().get(ntwrkid).SSID + "\neap=PEAP\n#\"\"";

		ntwrkid = wifi.updateNetwork(w);
		if(ntwrkid==-1) {
			Log.i("XXXXXXXXXXXXXXXXXXXXX", "COULDNT INJECT EAP SETTINGS (PEAP)");
			return -1;
		}
		else {
			Log.wtf("GGGGGGGGGGGGGGGGGGGGGG", "Peap settings enabled correctly");
			//t = Toast.makeText(getApplication(), "PEAP settings", Toast.LENGTH_SHORT);
			//t.show();
			wifi.saveConfiguration();
			return ntwrkid;
		}
		
	}
	
	private int phase2settings(WifiManager wifi, int ntwrkid) {
		w = wifi.getConfiguredNetworks().get(ntwrkid);
		w.SSID = wifi.getConfiguredNetworks().get(ntwrkid).SSID + "\nphase2=\"auth=MSCHAPV2\"";

		ntwrkid = wifi.updateNetwork(w);
		if(ntwrkid==-1) {
			Log.i("XXXXXXXXXXXXXXXXXXXXX", "PHASE2 SETTINGS DIDNT INJECT CORRECTLY");
			return -1;
		}
		else {
			wifi.saveConfiguration();
			//t = Toast.makeText(getApplication(), "PEAP settings", Toast.LENGTH_SHORT);
			//t.show();
			Log.i("GGGGGGGGGGGGGGGGGGGGGG", "PHASE 2 settings configured correctly");

			return ntwrkid;
		}
	}
	
	private int identity_settings(WifiManager wifi, int ntwrkid) {
	
	
		
		
		
		w = wifi.getConfiguredNetworks().get(ntwrkid);
		w.SSID=wifi.getConfiguredNetworks().get(ntwrkid).SSID + "\nidentity=\""+rcsid.getText().toString()+"\"";
		ntwrkid = wifi.updateNetwork(w);
		if(ntwrkid==-1) {
			Log.i("XXXXXXXXXXXXXXXXXXXXX", "IDENTITY INJECTION SUCKED");
			return -1;
		}
		else {
			//t = Toast.makeText(getApplication(), "identity settings applied", Toast.LENGTH_SHORT);
			//t.show();
			Log.i("GGGGGGGGGGGGGGGGGGGGGG", "IDENTITY INJECTED SUCCESSFULLY");
			wifi.saveConfiguration();

			return ntwrkid;
		}
		
	}
	
	private int password_settings(WifiManager wifi, int ntwrkid) {
		w = wifi.getConfiguredNetworks().get(ntwrkid);
		w.SSID="\"rpi_802.1x\"\npassword=\""+rcspwd.getText().toString()+"\"";
		
		

		ntwrkid = wifi.updateNetwork(w);
		if(ntwrkid==-1) {
			//Log.i("XXXXXXXXXXXXXXXXXXXXX", "PASSWORD INJECTION FAILED");
			return -1;
		}
		else {
			wifi.saveConfiguration();
			Log.i("GGGGGGGGGGGGGGGGGGGGGG", "PASSWORD AND SSID INJECTED SUCCESFULLY");
			//t = Toast.makeText(getApplication(), "password set with ssid", Toast.LENGTH_SHORT);
			//t.show();

			return ntwrkid;
		}
	}
	

	private OnClickListener cfginjector = new OnClickListener() {
	 	
		public void onClick(View v) { 
			
			class wificonfig extends AsyncTask {
				
				@Override
				protected void onPostExecute(Object result)	{
					
					
					//finish();
				}

				@Override
				protected Object doInBackground(Object... params) {
					// TODO Auto-generated method stub
					ProgressBar myProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
					
					int myProgress = 1;
					
			//		Toast t;
					
					Log.wtf("GGGGGGGGGGGGGGGGGGGGGG", "Doing in background");
					WifiManager wifi;
					int cur_ntwrk_id;
					wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					//enable wifi and initialize settings
					WifiEnabler(wifi);
					cur_ntwrk_id = initSettings(wifi);
					myProgressBar.setMax(6);	
					myProgressBar.setProgress(++myProgress);
	
					
					//off and on
					WifiDisabler(wifi);
					WifiEnabler(wifi);
					

					//PEAP injection
					cur_ntwrk_id = peapSettings(wifi,cur_ntwrk_id);
					
					myProgressBar.setProgress(++myProgress);
					
					
					//off and on
					WifiDisabler(wifi);
					WifiEnabler(wifi);		
					
					
					//mschapv2 injection
					cur_ntwrk_id = phase2settings(wifi,cur_ntwrk_id);
					
					myProgressBar.setProgress(++myProgress);
					
					//off and on
					WifiDisabler(wifi);
					WifiEnabler(wifi);
					
					//identity settings
					cur_ntwrk_id =  identity_settings(wifi,cur_ntwrk_id);
					
					myProgressBar.setProgress(++myProgress);
					
					//Save username for future login
					prefsEditor.putString("username",rcsid.getText().toString());
					prefsEditor.commit();
					
					//off and on
					WifiDisabler(wifi);
					WifiEnabler(wifi);
					
					//password settings
					cur_ntwrk_id = password_settings(wifi,cur_ntwrk_id);
					
					//Save password for future login
					prefsEditor.putString("password",rcspwd.getText().toString());
					prefsEditor.commit();
					
					
					
					myProgressBar.setProgress(++myProgress);
					
					Log.wtf("GGGGGGGGGGGGGGGGGGGg", "Final Progress is " + myProgress);
					
					//off and on one last time 
					WifiDisabler(wifi);
					WifiEnabler(wifi);
					
					wifi.enableNetwork(cur_ntwrk_id, false);
					return null;
				}
				
			}
		new wificonfig().execute(null);
		}
};
	private OnClickListener appcloser = new OnClickListener() {

		public void onClick(View v) {
			finish();
		}
	};

}