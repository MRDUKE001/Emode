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

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    
    
}
