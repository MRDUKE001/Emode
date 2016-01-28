package com.wind.emode.testcase;

import android.app.Activity;
import android.os.Bundle;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import java.util.Iterator;

import android.location.GpsSatellite;
import android.location.GpsStatus;

import java.util.NoSuchElementException;

import com.wind.emode.BaseActivity;

import com.wind.emode.EmodeApp;

//import android.os.Power;
import android.provider.Settings;

import com.wind.emode.R;

public class GPSTest extends BaseActivity {

	TextView mtitle;
	TextView mtext1;
	TextView mtext2;
	TextView mtext3;
	TextView mtext4;
	Button mbutton;
    View btn_pass = null;
	private Iterator<GpsSatellite> mSatellites;
	private GpsSatellite msatellite;
	private GpsStatus gs;
	String svStatus = "";

    private int mOrigLocationMode;

	private LocationManager locationManager;
	
	private Svlistener mSvStatusListener;
	private GPSlocationlistener mLocationlistener;


	private static final String LOG_TAG = "EMODE_GPSTest";
	private boolean DBG = true;
	protected void log(String msg) {
		if (DBG)
			Log.e(LOG_TAG, msg);
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_gps);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mOrigLocationMode = Settings.Secure.getInt(getContentResolver(), 
		        Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
		if (mOrigLocationMode < Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
			Settings.Secure.putInt(getContentResolver(),
			        Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
		}
		mLocationlistener = new GPSlocationlistener();
		mSvStatusListener = new Svlistener();

		mtext1 = (TextView) findViewById(R.id.m0_t1);
		mtext2 = (TextView) findViewById(R.id.m0_t2);
		mtext3 = (TextView) findViewById(R.id.m0_t3);
		mtext4 = (TextView) findViewById(R.id.m0_t4);

		mtext1.setText(getString(R.string.gps_tip));
		mtext2.setText("");
		mtext3.setText(getString(R.string.gps_total) + "0");
		
		mbutton = (Button) findViewById(R.id.m0_b);
		mbutton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
		mbutton.setText("OK");
	}

	@Override
	protected void onResume() {
		 if(btn_pass == null){
             View dv = getWindow().getDecorView();
             View btn_bar = dv.findViewById(R.id.btn_bar);
             btn_pass = dv.findViewById(R.id.btn_succ);
             btn_pass.setVisibility(View.GONE);
         }
		super.onResume();

		//Power.acquireWakeLock(2, "PowerManagerService");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, mLocationlistener);
		locationManager.addGpsStatusListener(mSvStatusListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mOrigLocationMode < Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
		    Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_GPS);
            newIntent.setPackage(getPackageName());
            newIntent.putExtra(Constants.EXTRA_GPS_ORIG_STATE, mOrigLocationMode);
            startService(newIntent);    
		}
		locationManager.removeUpdates(mLocationlistener);
		locationManager.removeGpsStatusListener(mSvStatusListener);
		//Power.releaseWakeLock("PowerManagerService");
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub	       
		super.onDestroy();				
	}
	
	class Svlistener implements GpsStatus.Listener {
		public void onGpsStatusChanged(int event) {
			gs = locationManager.getGpsStatus(null);

			mSatellites = gs.getSatellites().iterator();
			int n = 0;

			svStatus = "";

			for (int i = 0; i < 256; i++) {
				try {
					msatellite = mSatellites.next();
					n++;
					svStatus = svStatus + "(" + msatellite.getPrn() + ";"
							+ msatellite.getSnr() + ";"
							+ msatellite.getElevation() + ";"
							+ msatellite.getAzimuth() + ") ";
				} catch (NoSuchElementException e) {
					break;
				}
			}
			mtext2.setText(svStatus);
			mtext3.setText(getString(R.string.gps_total) + n);
			if(n > 7){
            	btn_pass.setVisibility(View.VISIBLE);
            }else{
                btn_pass.setVisibility(View.GONE);
            }
		}
	}

	class GPSlocationlistener implements LocationListener {
		public void onLocationChanged(Location location) {
			StringBuilder str = new StringBuilder(getString(R.string.gps_latitude_longitude));
			str.append(location.getLatitude()).append(',').append(
					location.getLongitude());
			mtext4.setText(str.toString());
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		
		}

		public void onProviderEnabled(String provider) {
		
		}

		public void onProviderDisabled(String provider) {

		}
	}

}
