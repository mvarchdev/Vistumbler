package com.eiri.wifidb_uploader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class WiFiDemo extends Activity implements OnClickListener {
	private static final String TAG = "WiFiDB_Demo";
	Switch ScanSwitch;
	TextView textStatus;
	Button buttonScan;
	MyResultReceiver resultReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	       .detectNetwork() // or .detectAll() for all detectable problems
	       .penaltyDialog()  //show a dialog
	       .permitNetwork() //permit Network access 
	       .build());

		// Setup UI
		resultReceiver = new MyResultReceiver(null);
		ScanSwitch = (Switch) findViewById(R.id.ScanSwitch);
		ScanSwitch.setOnClickListener(this);

		//Setup GPS
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
		}else{
			showGPSDisabledAlertToUser();
		}			
	}
	
	private void showGPSDisabledAlertToUser(){
		Log.d(TAG, "showGPSDisabledAlertToUser");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
		.setCancelable(false)
		.setPositiveButton("Goto Settings Page To Enable GPS",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(callGPSSettingIntent);
					}
				}
		);
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				}
		);
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, QuickPrefsActivity.class));
                return true;
        }
        return false;
    }
    
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		    case R.id.ScanSwitch:
		    	Log.d(TAG, "ScanSwitch Pressed");
		      	ScanSwitch = (Switch) findViewById(R.id.ScanSwitch);
		      	if (ScanSwitch.isChecked()){
		      		Log.d(TAG, "Start Scan");
		      		startService(new Intent(this, ScanService.class));
		      		ScanSwitch.setChecked(true);
		      	} else {
		      		Log.d(TAG, "Stop Scan");
		      		stopService(new Intent(this, ScanService.class));
		      		ScanSwitch.setChecked(false);
		        }
		      	break;
	    }

	}
	class MyResultReceiver extends ResultReceiver
	{
		public MyResultReceiver(Handler handler) {
			super(handler);
		}
		
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) 
		{
			Log.d(TAG, resultData.toString());
			if(resultCode == 100){
				textStatus.append("\n\nWiFi Status: " + resultData.toString());
			}
		}	
	}
}
