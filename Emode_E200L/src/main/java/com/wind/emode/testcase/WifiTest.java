package com.wind.emode.testcase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WifiTest extends BaseActivity {
    private static final String LOG_TAG = "EMODE_WIFITEST";
    
    private static final int MSG_WIFI_ENABLED = 101;
    private static final int MSG_WIFI_SCAN_RESULTS_AVAILABLE = 102;
	private static final int MSG_WIFI_TEST_PASS = 103;
	private static final int MSG_WIFI_TEST_FAIL = 104;
    
    private static final int WIFI_SCAN_RESULTS_SIZE = 3;
    
    private TextView mWifiStatusText;
    private TextView mAddressText;
    private TextView mScanListText;	
    private boolean mOrigWiFiState = false;
    private WifiManager mWifiManager;
    private StringBuilder mDeviceList = new StringBuilder();  
    private boolean DBG = true;
    private final Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_WIFI_ENABLED:
				mWifiManager.startScan();
				WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
				mWifiStatusText.setText(getString(R.string.wifi_status) + getString(R.string.status_scan));
				mAddressText.setText(getString(R.string.wifi_mac) + wifiInfo.getMacAddress());
				break;
			case MSG_WIFI_SCAN_RESULTS_AVAILABLE:
				List<ScanResult> scanList = mWifiManager.getScanResults();
				for (ScanResult scanResult : scanList) {
					if (!mDeviceList.toString().contains(scanResult.SSID)) {
					    mDeviceList.append(scanResult.SSID);
            	        mDeviceList.append("\n");
					}
				}
				mScanListText.setText(mDeviceList.toString());
        	    if (scanList.size() >= WIFI_SCAN_RESULTS_SIZE) {
        	        mBtnPass.setVisibility(View.VISIBLE);
        	        mHandler.sendEmptyMessageDelayed(MSG_WIFI_TEST_PASS, 500);
        	    }
				break;
			case MSG_WIFI_TEST_PASS:
				finishTestcase(Constants.TEST_PASS);
				break;
			case MSG_WIFI_TEST_FAIL:
			    finishTestcase(Constants.TEST_FAIL);
				break;
			default:
				break;
			}
		}
    	
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				if (state == WifiManager.WIFI_STATE_ENABLED) {
					mHandler.sendEmptyMessage(MSG_WIFI_ENABLED);
				}
			} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {				
				mHandler.sendEmptyMessage(MSG_WIFI_SCAN_RESULTS_AVAILABLE);
			} 
		}
    	
    };

    protected void log(String msg) {
        if (DBG) {
            Log.e(LOG_TAG, msg);
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_wifi);
        mWifiStatusText = (TextView) findViewById(R.id.wifi_status);
        mAddressText = (TextView) findViewById(R.id.wifi_address);
        mScanListText = (TextView) findViewById(R.id.wifi_list);
        mAddressText.setText(getString(R.string.wifi_mac) + getString(R.string.unknown));
        mBtnPass.setVisibility(View.GONE);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, intentFilter);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiStatusText.setText(getString(R.string.wifi_status) + getString(R.string.status_off));
            mWifiManager.setWifiEnabled(true);          
        } else {
            mOrigWiFiState = true;
            mHandler.sendEmptyMessage(MSG_WIFI_ENABLED);
        }
    }

    
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (!mOrigWiFiState) {
		    Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_WIFI);
            newIntent.setPackage(getPackageName());
            startService(newIntent);    
		}
	}

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub              
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
