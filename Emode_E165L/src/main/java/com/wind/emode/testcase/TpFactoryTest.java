package com.wind.emode.testcase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;

public class TpFactoryTest extends BaseActivity{
	private static final String TAG="TpFactoryTest";
	private View btn_pass = null;
	private static final String TP_PATH = "/sys/devices/platform/HardwareInfo/01_ctp";//"/proc/ctp_info"
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_tp);
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.example.tptest","com.example.tptest.MainActivity"));
		startActivity(intent);
		
		
     /*   InputStream is;
        byte[] bytes = new byte[256];
        int count;
		registerReceiver();
		
    String tpType = getString(R.string.unknown);;
    try {
			is = new FileInputStream(TP_PATH);
			count = is.read(bytes);
			is.close();
			tpType = new String(bytes, 0, count);
			tpType = tpType.substring(tpType.indexOf(':') + 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
		Intent intent = new Intent();
		tpType=tpType.replace("\n","");
		if(tpType.equals("gt9xx")){
			intent.setComponent(new ComponentName("com.goodix.rawdata","com.goodix.rawdata.RawDataTest"));
			intent.putExtra("command", 1);
			intent.putExtra("frequences", 1);
			intent.putExtra("autofinish", true);
			intent.putExtra("successfinish", true);
			startActivity(intent);
			
		}else if(tpType.equals("fts")){
			intent.setComponent(new ComponentName("com.focaltech.ft_terminal_test","com.focaltech.ft_terminal_test.MainActivity"));
			intent.putExtra("command", 1);
			intent.putExtra("view", 1);
			intent.putExtra("autoFinish", 1);
			startActivity(intent);
		}	*/		
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 if(btn_pass == null){
	            View dv = getWindow().getDecorView();
	            View btn_bar = dv.findViewById(R.id.btn_bar);
	            btn_pass = dv.findViewById(R.id.btn_succ);
	            btn_pass.setVisibility(View.GONE);	
	    }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterReceiver();
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
			Log.d(TAG,"action="+action);
			
			if(action.equals("com.android.TPTEST")){
				 if(btn_pass != null){
	               	 	btn_pass.setVisibility(View.VISIBLE);	
	   		}
			}
			
           /* if (action.equals("android.intent.action.goodix")) {
            	int value = intent.getIntExtra("testResult", -1);
            		Log.d(TAG,"value="+value);
						 	 if((btn_pass != null) && (value == 0)){
				               	 	btn_pass.setVisibility(View.VISIBLE);	
				   		}
          }else if(action.equals("com.focaltech.ft_terminal_test")){
            	int value = intent.getIntExtra("testResult", -1);
            	Log.d(TAG,"value="+value);
						 	 if((btn_pass != null) && (value == 0)){
				               	 	btn_pass.setVisibility(View.VISIBLE);	
				   		 }
          }*/
            
        }
    };
    
    private void registerReceiver() {  
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.TPTEST");
       /* filter.addAction("android.intent.action.goodix");
        filter.addAction("com.focaltech.ft_terminal_test");  */     
        registerReceiver(mReceiver, filter);
	}  
  
	private void unRegisterReceiver() {  
	    unregisterReceiver(mReceiver);  
	}      

}
