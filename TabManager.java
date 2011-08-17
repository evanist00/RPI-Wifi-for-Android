package hd.wifi;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class TabManager extends TabActivity {
	  void switchTab() {
		    final Intent intent = new Intent(getBaseContext(), Disclaimer.class);
		    intent.setAction("Switch to tab 1, please");
		    startActivity(intent);
	  }
	@Override //Called when the activity is first created
	public void onCreate(Bundle savedInstanceState) {
		Log.w("\nRPIWIFI","onCREATE OF TABMANAGER\n");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabHost tabHost = getTabHost();

	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // primary and default tab which looks up printers. ONLY DISPLAYS PRINTER NAMES, statuses, and number of jobs
	    intent = new Intent().setClass(this, Disclaimer.class);
	    spec = tabHost.newTabSpec("Disclaimer").setIndicator("Disclaimer",null).setContent(intent);
	    tabHost.addTab(spec);
	    
	    // secondary tab shows jobs on each printer.
	    intent = new Intent().setClass(this, RPIWifiForAndroid.class);
	    spec = tabHost.newTabSpec("Login").setIndicator("Login to RPI Wifi",null).setContent(intent);
	    tabHost.addTab(spec);
	 
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, JobList.class);
	    spec = tabHost.newTabSpec("Printers").setIndicator("Printers",null).setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(1);
		
	}
	
}