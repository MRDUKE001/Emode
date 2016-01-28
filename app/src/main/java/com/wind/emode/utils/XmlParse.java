package com.wind.emode.utils;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;

import com.wind.emode.R;
import com.wind.emode.data.TestCase;
import com.wind.emode.manager.MainManager;
import com.wind.emode.manager.ModuleManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class XmlParse {
    private static final String TAG = "XmlParse";
    public static ArrayList<TestCase> mTestModule = new ArrayList<TestCase>();
    public static void parseXml(Context context){
        Log.d(TAG,"Now start to parse Xml ....");
        ModuleManager mModuleManager=ModuleManager.getInstance(context);

        long startTime = System.currentTimeMillis();
        final String[] testcaseArray = context.getResources().getStringArray(R.array.testcases);
        final String excludeTestcases = Arrays.toString(context.getResources().getStringArray(R.array.exclude_testcases));

        TestCase testcase = null;
        String[] testcaseDetail;
        for (int i=0; i<testcaseArray.length; i++) {
            testcaseDetail = testcaseArray[i].split(":");
            Log.d(TAG, "testcaseDetail: " + Arrays.toString(testcaseDetail));
            if (testcaseDetail != null && testcaseDetail.length == 6) {
                if (excludeTestcases.contains(testcaseDetail[0])) {
                    continue;
                }
                Log.d(TAG, "testcaseDetail enName:" + testcaseDetail[2]);
                testcase = new TestCase();
                testcase.setCode(testcaseDetail[0]);
                testcase.setType(Integer.parseInt(testcaseDetail[1]));
                testcase.setEnName(testcaseDetail[2]);
                testcase.setCnName(testcaseDetail[3]);
                testcase.setErrorCode(Integer.parseInt(testcaseDetail[4]));
                testcase.setActivity(testcaseDetail[5]);

                mModuleManager.mTestModule.add(testcase);

                if ((testcase.getType() & Constants.TESTCASE_TYPE_PHONE) > 0) {
                    mModuleManager.mPhoneTestcases.add(testcase);
                    Log.d(TAG,"add to PhoneTestCases");
                }
                if ((testcase.getType() & Constants.TESTCASE_TYPE_BOARD) > 0) {
                    mModuleManager.mBoardTestcases.add(testcase);
                    Log.d(TAG, "add to mBoardTestcases");
                }
                if ((testcase.getType() & Constants.TESTCASE_TYPE_OTHER) > 0) {
                    mModuleManager.mOtherTestcases.add(testcase);
                    Log.d(TAG, "add to mOtherTestcases");
                }
                testcase = null;
            }
        }
        ModuleManager.getInstance(context).mXmlParsed = true;

        Log.d(TAG, "XML Parse finished ,takes " + (System.currentTimeMillis() - startTime) + "ms");
       /* if ( ModuleManager.getInstance(context).mIsSaveTestResultsToFile) {
            Intent newIntent = new Intent(Constants.ACTION_EMODE_INIT_RESULT_FILE);
            newIntent.setPackage(context.getPackageName());
            context.startService(newIntent);
        }*/
    }
}
