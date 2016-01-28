package com.wind.emode;

import com.wind.emode.testcase.BTTest;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.FileUtils;
import com.wind.emode.utils.Log;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

public class EmodeIntentService extends IntentService {
    private static final String TAG = "EmodeIntentService";
    private static final int MSG_RESTORE_WIFI_STATE = 100;
    private static final int MSG_RESTORE_BT_STATE = 101;
    private static final int MSG_RESTORE_GPS_STATE = 102;
    
    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
			Log.d(TAG, "handleMessage: " + msg.what);
            switch(msg.what){
            case MSG_RESTORE_WIFI_STATE:
                WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiMgr.setWifiEnabled(false);
                break;
            case MSG_RESTORE_BT_STATE:
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (btAdapter != null) {
                    btAdapter.disable(); 
                }
                break;
            case MSG_RESTORE_GPS_STATE:
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.LOCATION_MODE, msg.arg1);
                break;
            default:
                break;
            }
        }
    };
	public EmodeIntentService() {
		super("EmodeIntentService");
	}	

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	    Log.d(TAG, "onHandleIntent: " + intent.getAction());
        if (Constants.ACTION_EMODE_RESTORE_WIFI.equals(intent.getAction())) {   
            Message msg = mHandler.obtainMessage(MSG_RESTORE_WIFI_STATE);            
            mHandler.sendMessageDelayed(msg, 20000);
        } else if (Constants.ACTION_EMODE_OPEN_WIFI_BT_GPS.equals(intent.getAction())
                || Constants.ACTION_EMODE_CLOSE_WIFI_BT_GPS.equals(intent.getAction())) {  
            boolean isOpen = Constants.ACTION_EMODE_OPEN_WIFI_BT_GPS.equals(intent.getAction())? true : false;
            WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (isOpen) {
                if (!wifiMgr.isWifiEnabled()) {
                    wifiMgr.setWifiEnabled(true);
                }
            } else {
                if (wifiMgr.isWifiEnabled()) {
                    wifiMgr.setWifiEnabled(false);
                }
            }
            
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter != null) {
                if (isOpen) {
                    if (!btAdapter.isEnabled()) {
                        btAdapter.enable(); 
                    }
                } else {
                    if (btAdapter.isEnabled()) {
                        btAdapter.disable(); 
                    }
                }                 
            }
            
            if (isOpen) {
                if (Settings.Secure.getInt(getContentResolver(), 
                        Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF) < Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    Settings.Secure.putInt(getContentResolver(), 
                            Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            } else {
                if (Settings.Secure.getInt(getContentResolver(), 
                        Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF) > Settings.Secure.LOCATION_MODE_OFF) {
                    Settings.Secure.putInt(getContentResolver(), 
                            Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
                }
            }
        } else if (Constants.ACTION_EMODE_RESTORE_BT.equals(intent.getAction())) {
            Message msg = mHandler.obtainMessage(MSG_RESTORE_BT_STATE);
            mHandler.sendMessageDelayed(msg, 60000);            
        } else if (Constants.ACTION_EMODE_RESTORE_GPS.equals(intent.getAction())) {
            Message msg = mHandler.obtainMessage(MSG_RESTORE_GPS_STATE);
            msg.arg1 = intent.getIntExtra(Constants.ACTION_EMODE_RESTORE_GPS, Settings.Secure.LOCATION_MODE_OFF);
            mHandler.sendMessageDelayed(msg, 60000); 
        } else if (Constants.ACTION_EMODE_INIT_RESULT_FILE.equals(intent.getAction())) {
            FileUtils.initializeTestResultFile(this);
        } else if (Constants.ACTION_EMODE_UPDATE_RESULT_FILE.equals(intent.getAction())) {
            FileUtils.updateTestResultFile(this);
        }
	}

}
