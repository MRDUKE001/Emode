package com.wind.emode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import com.wind.emode.data.Testcase;

import java.util.ArrayList;


public class AutoDetect extends Activity {
    private static final String LOG_TAG = "AutoDetect";
    private int mIndex = 0;
    private ArrayList<Testcase> mTestCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        if (EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_BOARD) {
            mTestCases = EmodeApp.mBoardTestcases;
        } else {
            mTestCases = EmodeApp.mPhoneTestcases;
        }
        EmodeApp.mIsAutoTest = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIndex < mTestCases.size()) {           
            Intent newIntent = new Intent();
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String className = "com.wind.emode.testcase." + mTestCases.get(mIndex).activity;
            newIntent.putExtra("testcase_code", mTestCases.get(mIndex).code);
            newIntent.setComponent(new ComponentName("com.wind.emode", className));
            startActivity(newIntent);
            if ("*983*679#".equals(mTestCases.get(mIndex).code) || "*983*473#".equals(mTestCases.get(mIndex).code)) {
                startService(new Intent(this, EmptyForegroundService.class));            
            }
            mIndex++;
        } else { 
            if (EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_PHONE && EmodeApp.mIsSaveTestResultsToFile) {
                Intent intent = new Intent(Constants.ACTION_EMODE_UPDATE_RESULT_FILE);
                intent.setPackage(getPackageName());
                startService(intent);
            }
            EmodeApp.mIsAutoTest = false;
            Intent newIntent = new Intent(Constants.ACTION_EMODE_CLOSE_WIFI_BT_GPS);
            newIntent.setPackage(getPackageName());
            startService(newIntent);             
            finish();
        }
    }

    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EmodeApp.mIsAutoTest = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putInt("auto_detect_index", mIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onRestoreInstanceState");
        mIndex = savedInstanceState.getInt("auto_detect_index");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
