/* As of January 6, 2010 this code does not work
 * On the motorola droid (1st gen) the tab manager
 * will open properly but an activity within it will
 * cause a crash.
 * I suggest using the android logcat tool to debug and find the source of the problem*/
package hd.wifi;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;

import android.view.Menu;

import android.widget.ArrayAdapter;


import org.json.JSONArray;
import org.json.JSONException;

public class JobList extends ListActivity{
	
	String output;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		
		StringBuilder builder = new StringBuilder();
		String jobs = "";
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://leet.arc.rpi.edu/printqueue/data/vcc.json");
		
		JSONObject tgt;
		JSONArray printerlist;
		String[] plist;
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					jobs+=line;
				}

				try {
					tgt = new JSONObject(jobs).getJSONObject("printers");
					printerlist = tgt.getJSONArray("printer");
					plist = new String[printerlist.length()];
					for(int i=0;i<printerlist.length();i++) {
						plist[i] = printerlist.getJSONObject(i).getString("name") + " (";
						if(printerlist.getJSONObject(i).getString("queue").equals("empty")) {
							plist[i] += printerlist.getJSONObject(i).getString("queue");
						}
						else {
							JSONObject temp = new JSONObject(printerlist.getJSONObject(i).getString("queue"));
							plist[i] += temp.getJSONArray("job").length();
						}
						plist[i] += ")" ;
					}
					
					this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,R.id.label, plist));		
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		
			}
			else {
				plist = new String[] {"Could not access printer queue. Please check to see if you are connected to rpi_802.1x"};
				this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,R.id.label, plist));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		
		return true;
	}

	
	
}