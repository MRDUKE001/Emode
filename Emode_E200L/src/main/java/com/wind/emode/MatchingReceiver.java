package com.wind.emode;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Config;
import android.widget.Toast;

import com.wind.emode.data.Testcase;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import com.wind.emode.utils.TestcasesParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;


public class MatchingReceiver extends BroadcastReceiver {
	
	// A=*,B=#
	enum EMODE_CODE {
		A983A0B, //*983*0#
		A983A1B, //*983*1#
		ABAB9463A1BABA, //*#*#9463*1#*#*
		ABAB9463A7BABA, //*#*#9463*7#*#*
		A983A7B, //*983*7#
		A983A5B, //*983*5#
		ABAB1705B, //*#*#1705#
		AB1923B, //*#1923#
		AB458B, //*#458#
		A983A57B,  //*983*57#
		A983A233B,  //*983*233#
		A983A27274B,  //*983*27274#
		A983A3640B,  //*983*3640#
		A983A8888B,  //*983*8888#
		A983A9999B,  //*983*9999#
		A9643A240B,  //*9643*240#
		A983A246B,    /* *983*246# add by lizusheng@wind-mobi.com 20151217 */
		UNKNOWN
	}

	
	private boolean mFromDialerPad;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        Log.v(this, "onReceive:" + action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
        } else if (action.equals(Constants.ACTION_EMODE)) {
            String code = intent.getStringExtra(Constants.EXTRA_EMODE_CODE);
            Log.v(this, "onReceive, code = " + code);            
            if (!EmodeApp.mTestcaseReady) {
                TestcasesParser.parseTestcases(context);
            }
	        mFromDialerPad = true;
            startActivityByCode(context, code);
        } else if (action.equals(Constants.ACTION_EMODE_MENU)) {
            String code = intent.getStringExtra(Constants.EXTRA_EMODE_CODE);
            Testcase testcase = EmodeApp.mTestcases.get(code);
            if (testcase != null) {
                final Intent newIntent = new Intent();
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String className = "com.wind.emode.testcase." + testcase.activity;
                newIntent.putExtra("testcase_code", testcase.code);
                newIntent.setComponent(new ComponentName("com.wind.emode", className));
                context.startActivity(newIntent);
            } else {
                Log.w(this, "TestCase not found: " + code);
            }
        } else if (action.equals(Constants.ACTION_EMODE_STOP)) {
            context.stopService(new Intent(context, EmptyForegroundService.class));
        }
    }
    
    private void startActivityByCode(Context context, String code) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Testcase testcase = EmodeApp.mTestcases.get(code);
        if ("*983*679#".equals(code) || "*983*473#".equals(code)) {
            context.startService(new Intent(context, EmptyForegroundService.class));            
        }
        if (testcase != null) {
    	    if (mFromDialerPad) {
    		    clearDialerPad(context);
    		    mFromDialerPad = false;
    	    }					
            String className = "com.wind.emode.testcase." + testcase.activity;
            intent.putExtra("testcase_code", testcase.code);
            intent.setComponent(new ComponentName("com.wind.emode", className));
            context.startActivity(intent);
        } else {
        	EMODE_CODE eCode = EMODE_CODE.UNKNOWN;
        	try {
        	    eCode = EMODE_CODE.valueOf(code.replace('*','A').replace('#','B'));
        	} catch (IllegalArgumentException e) {
        		
        	}
        	if (eCode != EMODE_CODE.UNKNOWN && mFromDialerPad) {
        		clearDialerPad(context);
        		mFromDialerPad = false;
        	}
        	switch (eCode) {
        	case A983A0B:
        		intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.MainActivity"));
        		intent.putExtra("isFromDialerPad",true);
                EmodeApp.mTestcaseType = Constants.TESTCASE_TYPE_BOARD;
                context.startActivity(intent);
        		break;
        	case A983A1B:
        	case ABAB1705B:
        	case AB1923B:
        	case ABAB9463A1BABA:
        		intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.MainActivity"));
        		intent.putExtra("isFromDialerPad",true);
                EmodeApp.mTestcaseType = Constants.TESTCASE_TYPE_PHONE;
                context.startActivity(intent);
        		break;
        	case A983A7B:
        	case AB458B:
        		intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.EmodeSubmenu"));
                EmodeApp.mTestcaseType = Constants.TESTCASE_TYPE_OTHER;
                context.startActivity(intent);
        		break;
        	case A983A5B:
        	    if (context.getResources().getBoolean(com.wind.emode.R.bool.config_display_zte_sales_count_status)) {
        		    intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.testcase.SalesCountStatus"));
                    context.startActivity(intent);
        	    }
        		break;
        	case A983A3640B:
        		intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.RadioInfo"));
                context.startActivity(intent);
        		break;
        	case A983A57B:
        		intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$PrivacySettingsActivity"));
                context.startActivity(intent);
        		break;
        	case A983A233B:
        		intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.ApnSettings"));
                context.startActivity(intent);
        		break;
        	case A983A27274B:
        		PowerManager powermanager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                powermanager.reboot(null);
        		break;
        	case A983A8888B:
        		intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.AddChinaApn"));
                context.startActivity(intent);
        		break;				
        	case A983A9999B:
        		intent.setComponent(new ComponentName("com.android.updatesystem", "com.android.updatesystem.UpdateSystemT"));
        		if (context.getPackageManager().resolveActivity(intent, 0) != null) {
        		    context.startActivity(intent);
        		}	
        		break;	
        	case A9643A240B:
        	    intent.setComponent(new ComponentName("com.wind.benqtestapp",
                        "com.wind.emode.benq.MainActivity"));
        		if (context.getPackageManager().resolveActivity(intent, 0) != null) {
        		    context.startActivity(intent);
	            }
        		break;
            case ABAB9463A7BABA:
                intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.testcase.CustomVersionInfos"));
                context.startActivity(intent);
                break;
          //add by lizusheng@wind-mobi.com 20151217 start
            case A983A246B:
            	intent.setComponent(new ComponentName("com.wind.emode", "com.wind.emode.testcase.ImeiWrite"));
            	context.startActivity(intent);
            	break;
          //add by lizusheng@wind-mobi.com 20151217 end
        	case UNKNOWN:
			    Log.w(this, "TestCase not found: " + code);
        		break;
        	}
        }
    }

	private void clearDialerPad(Context context) {
		Intent i = new Intent("com.wind.intent.action.CLEAR_DIALER_PAD");
		context.sendBroadcast(i);
	}
}
