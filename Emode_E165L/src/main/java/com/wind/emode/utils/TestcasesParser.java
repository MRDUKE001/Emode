package com.wind.emode.utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.wind.emode.R;
import com.wind.emode.EmodeApp;
import com.wind.emode.constants.TestcaseType;
import com.wind.emode.data.Testcase;

import android.content.Context;

public class TestcasesParser { 
	private static final String TAG = "TestcasesParser";

	public static void parseTestcases(Context context) {
		long startTime = System.currentTimeMillis();
		final String[] testcaseArray = context.getResources().getStringArray(R.array.testcases);
		final String excludeTestcases = Arrays.toString(context.getResources().getStringArray(R.array.exclude_testcases));
		EmodeApp.mTestcases.clear();
        EmodeApp.mPhoneTestcases.clear();
        EmodeApp.mBoardTestcases.clear();
        EmodeApp.mOtherTestcases.clear();
		Testcase testcase = null;
		String[] testcaseDetail;
		for (int i=0; i<testcaseArray.length; i++) {
            testcaseDetail = testcaseArray[i].split(":");
            //Log.d(TAG, "testcaseDetail: " + Arrays.toString(testcaseDetail));
            if (testcaseDetail != null && testcaseDetail.length == 5) { 
                if (excludeTestcases.contains(testcaseDetail[0])) {
                    continue;
                }
                testcase = new Testcase();
                testcase.code = testcaseDetail[0];              
                testcase.type = Integer.parseInt(testcaseDetail[1]);
                testcase.enName = testcaseDetail[2];
                testcase.cnName = testcaseDetail[3];
                testcase.activity = testcaseDetail[4];
                EmodeApp.mTestcases.put(testcase.code, testcase);
                if ((testcase.type & TestcaseType.PHONE) > 0) {
                    EmodeApp.mPhoneTestcases.add(testcase);
                }
                if ((testcase.type & TestcaseType.BOARD) > 0) {
                    EmodeApp.mBoardTestcases.add(testcase);
                }
                if ((testcase.type & TestcaseType.OTHER) > 0) {
                    EmodeApp.mOtherTestcases.add(testcase);
                }
                testcase = null;
            }
        }
        EmodeApp.mTestcaseReady = true;        
        Log.v(TAG, "parse testcases takes " + (System.currentTimeMillis() - startTime) + "ms");
	}	
	
}
