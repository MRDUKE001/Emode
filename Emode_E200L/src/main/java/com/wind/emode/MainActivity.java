package com.wind.emode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.utils.TestcasesParser;
import com.wind.emode.utils.Log;
import com.wind.emode.utils.Constants;


public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private Button mBtnManual = null;
	private Button mBtnAuto = null;
	private Button mBtnResult = null;
	private Button mFactoryReset = null;
	private TextView mTitle = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.start);
        
        Log.d(TAG,"isFromDialerPad:"+getIntent().getBooleanExtra("isFromDialerPad",false));      
        if(!getIntent().getBooleanExtra("isFromDialerPad",false)){
        	 Log.d(TAG,"isTestcaseParsed:"+EmodeApp.mTestcaseReady);
        	  if(!EmodeApp.mTestcaseReady){      	
             	 TestcasesParser.parseTestcases(this);
             	 Log.d(TAG,"isTestcaseParsed:"+EmodeApp.mTestcaseReady);
             }
    	     EmodeApp.mTestcaseType = Constants.TESTCASE_TYPE_PHONE;
    	     Log.d(TAG,"set the mTestcaseType to PHONE");  
        }
        
        mBtnManual = (Button) findViewById(R.id.manual);
        mBtnAuto = (Button) findViewById(R.id.auto);
        mBtnResult = (Button) findViewById(R.id.result);
        mFactoryReset = (Button) findViewById(R.id.factory_reset);

        if (EmodeApp.mTestcaseType != Constants.TESTCASE_TYPE_PHONE) {
            mFactoryReset.setVisibility(View.GONE);
        }

        mTitle = (TextView) findViewById(R.id.start_title);
        mTitle.setText(R.string.select_mode);

        mBtnManual.setText(R.string.btn_manual);
        mBtnResult.setText(R.string.btn_result);

        OnClickListener listener = new OnClickListener() {
                public void onClick(View arg0) {
                	Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.wind.emode",
                            "com.wind.emode.EmodeSubmenu"));
                    startActivity(intent);
                }
            };

        mBtnManual.setOnClickListener(listener);
        mBtnResult.setOnClickListener(listener);

        mBtnAuto.setText(R.string.btn_auto);
        mBtnAuto.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                	Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.wind.emode",
                            "com.wind.emode.AutoDetect"));
                    startActivity(intent);
                    Intent newIntent = new Intent(Constants.ACTION_EMODE_OPEN_WIFI_BT_GPS);
                    newIntent.setPackage(getPackageName());
                    startService(newIntent);  
                }
            });

        mFactoryReset.setText(R.string.btn_factory_reset);
        mFactoryReset.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (true) {
                    	Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.wind.emode",
                                "com.wind.emode.MasterClean"));
                        startActivity(intent);
                    } else {
                    	Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$PrivacySettingsActivity"));
                        intent.putExtra("emode_dialer", "emode_dialer");
                        startActivity(intent);
                    }
                }
            });
    }
}
