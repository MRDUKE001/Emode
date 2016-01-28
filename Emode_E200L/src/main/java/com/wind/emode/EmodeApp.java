package com.wind.emode;

import android.app.Application;

import com.wind.emode.data.Testcase;

import java.util.ArrayList;
import java.util.HashMap;


public class EmodeApp extends Application {
    public static HashMap<String, Testcase> mTestcases = new HashMap<String, Testcase>();
    public static ArrayList<Testcase> mPhoneTestcases = new ArrayList<Testcase>();
    public static ArrayList<Testcase> mBoardTestcases = new ArrayList<Testcase>();
    public static ArrayList<Testcase> mOtherTestcases = new ArrayList<Testcase>();
    public static int mTestcaseType = 0;
    public static boolean mTestcaseReady = false;
    public static boolean mIsAutoTest = false;
    public static boolean mIsCustomizeErrorCode = false;
    public static boolean mIsSaveTestResultsToFile = false;
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mIsCustomizeErrorCode = getResources().getBoolean(com.wind.emode.R.bool.config_asus_customize_test_error_code);
        mIsSaveTestResultsToFile = getResources().getBoolean(com.wind.emode.R.bool.config_asus_save_test_results_to_file);
    }
    
    
}
