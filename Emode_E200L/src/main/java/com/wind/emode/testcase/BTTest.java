package com.wind.emode.testcase;

import java.lang.reflect.Method;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



import com.wind.emode.utils.Constants;
//import android.server.BluetoothService;
import com.wind.emode.utils.Log;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.graphics.Color;
import android.app.Service;
import android.content.Context;
import android.provider.Settings;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.view.Window;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetooth;
import android.os.ServiceManager;
import android.os.RemoteException;

import com.wind.emode.R;

public class BTTest extends BaseActivity {
	private static final long MAX_SCAN_TIME = 30 * 1000;
	private static final int MSG_BT_ON = 100;
	private static final int MSG_BT_OFF = 101;
	private static final int MSG_BT_TEST_PASS = 102;
	private static final int MSG_BT_TEST_FAIL = 103;
	
	private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private Button mButton;
    private boolean mOrigBTState = false;
    private BluetoothAdapter mBtAdapter = null;  
    private StringBuilder mDeviceList = new StringBuilder();     
    private long mScanStartTime;
    
    private static final String LOG_TAG = "EMODE_BTTest";
    private boolean DBG = true;
    private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case MSG_BT_ON:
				mBtAdapter.startDiscovery();
	    		mText1.setText(getString(R.string.bt_status) + getString(R.string.status_on));
	    		mText2.setText(getString(R.string.bt_mac) + mBtAdapter.getAddress());
				break;
			case MSG_BT_TEST_PASS:
				finishTestcase(Constants.TEST_PASS);
				break;
			case MSG_BT_TEST_FAIL:
			    finishTestcase(Constants.TEST_FAIL);
				break;
			default:
				break;
			}
		}
    	
    };
    protected void log(String msg) {
        if(DBG) Log.e(LOG_TAG, msg);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
        	if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {        		
	            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
	            if (state == BluetoothAdapter.STATE_ON) {
	            	mHandler.sendEmptyMessage(MSG_BT_ON);
	            }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	if (!mDeviceList.toString().contains(device.getAddress())) {
            	    mDeviceList.append(device.getName());
            	    mDeviceList.append(":");
            	    mDeviceList.append(device.getAddress());
            	    mDeviceList.append("\n");
            	    mBtnPass.setVisibility(View.VISIBLE);
            	    mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_PASS, 500);
            	}
            	mText3.setText(mDeviceList);            	
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            	mScanStartTime = System.currentTimeMillis();
            	mText1.setText(getString(R.string.bt_status) + getString(R.string.status_scan));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	if (mDeviceList.length() == 0) {
            		mText1.setText(getString(R.string.no_bt_device));
            		mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_FAIL, 500);
            	}
            }
        }
    };
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.em_bt);
		
		mText1 = (TextView)findViewById(R.id.m0_t1);		
		mText2 = (TextView)findViewById(R.id.m0_t2);
		mText3 = (TextView)findViewById(R.id.m0_t3);
		mText2.setText(getString(R.string.bt_mac) + getString(R.string.unknown));
		mBtnPass.setVisibility(View.GONE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, intentFilter);
		
	    mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBtAdapter == null) {
	    	log("BluetoothAdapter is null");
	    	finish();
	    } else {            
            if (!mBtAdapter.isEnabled()) {  
                mText1.setText(getString(R.string.bt_status) + getString(R.string.status_off));
                mBtAdapter.enable();
            } else {
                mOrigBTState = true;
            }    
            if (mBtAdapter.isEnabled()) {
                mHandler.sendEmptyMessage(MSG_BT_ON);
            }      
        }
	}
  
    @Override
    protected void onStart() {
        super.onStart();       
    }

	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	protected void onStop(){
		super.onStop();
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
			if (!mOrigBTState) {
				Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_BT);
				newIntent.setPackage(getPackageName());
				startService(newIntent);
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}

