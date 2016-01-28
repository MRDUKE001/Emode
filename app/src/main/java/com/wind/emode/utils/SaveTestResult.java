package com.wind.emode.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.wind.emode.data.TestCase;
import com.wind.emode.manager.MainManager;
import com.wind.emode.manager.ModuleManager;

import java.util.ArrayList;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class SaveTestResult {
    private final String TAG = this.getClass().getSimpleName();
    public static SaveTestResult mSaveTestResult;
    Context context;
    ModuleManager mModuleManager;
    MainManager mMainManager;

    public SaveTestResult(Context context){
        this.context=context;
        mModuleManager=ModuleManager.getInstance(context);
        mMainManager=MainManager.getInstance(context);
    }

    public static SaveTestResult getInstance(Context context){
        if (mSaveTestResult == null){
            synchronized (SaveTestResult.class){
                if (mSaveTestResult == null){
                    mSaveTestResult = new SaveTestResult(context);
                }
            }
        }
        return mSaveTestResult;
    }


    public void saveResult(int result,String Code) {

        Log.d(TAG,"result:"+result,"Code:"+Code);
        if (mModuleManager.mIsCustomizeErrorCode && Constants.TEST_FAILED == result) {
            result = mModuleManager.getErrorCode(Code);
        }
        if (mModuleManager.mTestcaseType == Constants.TESTCASE_TYPE_OTHER) {
            return;
        }
       // String keyName = mModuleManager.mTestcaseType == Constants.TESTCASE_TYPE_BOARD ? Constants.PREF_KEY_TEST_RESULTS_BOARD : Constants.PREF_KEY_TEST_RESULTS_PHONE;
       // int len = mModuleManager.mTestcaseType == Constants.TESTCASE_TYPE_BOARD ? mModuleManager.mBoardTestcases.size() : mModuleManager.mPhoneTestcases.size();
          String keyName = Constants.PREF_KEY_TEST_RESULTS_PHONE;
          int len = mModuleManager.mTestModule.size();

        SharedPreferences sp = context.getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);
        String oldResults = sp.getString(keyName, "");
       // String newResults = setValue(mModuleManager.getEnName(Code).replaceAll("\\s+", "_"), oldResults, result);
       // String newResults = setValue(Code.replaceAll("\\s+", "_"), oldResults, result);
        String newResults = setValue(Code, oldResults, result);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(keyName, newResults).commit();
      //  checkMMIResult(newResults, len);

        if (!mMainManager.mIsAutoTest && mModuleManager.mTestcaseType == Constants.TESTCASE_TYPE_PHONE && mMainManager.mIsSaveTestResultsToFile) {
            Intent intent = new Intent(Constants.ACTION_EMODE_UPDATE_RESULT_FILE);
            intent.setPackage(context.getPackageName());
            context.startService(intent);
        }

     //   stopService(new Intent(this, EmptyForegroundService.class));
     //   finish();
    }

    private String setValue(String name, String str, int value) {
        Log.d(TAG, "setValue, old results str: " + str);

        String newStr = "";

        if (TextUtils.isEmpty(str)) {
            newStr = name + "=" + value;
        } else if (!str.contains(",")) {
            String[] tp = str.split("=");

            if (tp[0].equals(name)) {
                newStr = name + "=" + value;
            } else {
                newStr = str + "," + name + "=" + value;
            }
        } else {
            String key = name + "=";

            if (str.startsWith(key)) {
                newStr = key + value + str.substring(str.indexOf(","));
            } else {
                int startIndex = str.indexOf(key);

                if (startIndex == -1) {
                    newStr = str + "," + key + value;
                } else {
                    int endIndex = str.indexOf(",", startIndex);
                    if (endIndex == -1) {
                        newStr = str.substring(0, startIndex) + key + value;
                    } else {
                        newStr = str.substring(0, startIndex) + key + value +
                                str.substring(endIndex);
                    }
                }
            }
        }

        Log.d(TAG,"setValue, new results str: " + newStr);
        return newStr;
    }


    private void checkMMIResult(String results, int len) {
        String[] pairs = results.split(",");

        if (pairs.length < len) {
        } else {
            int size = pairs.length;
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(pairs[i].split("=")[1]) != Constants.TEST_PASS) {
                    setAutoMMIResult(false);
                    return;
                }
            }
            setAutoMMIResult(true);
        }
    }

    private void setAutoMMIResult(boolean val) {
   /*     byte[] buff = NvRAMAgentUtils.readFile();//new byte[64];
        *//*
        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            int count = is.read(buff);
            is.close();

            if (count == 64) {
                int index = (EmodeApp.mTestCaseType == TestCaseType.BOARD) ? 59 : 58;
                buff[index] = (val ? "Y" : "N").getBytes()[0];

                FileOutputStream os = new FileOutputStream("/dev/pro_info");
                os.write(buff);
                os.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		*//*
        if(buff != null){
            int index = (ModuleManager.getInstance(context).mTestcaseType == Constants.TESTCASE_TYPE_BOARD) ? 59 : 58;
            buff[index] = (val?"Y":"N").getBytes()[0];
            NvRAMAgentUtils.writeFile(buff);
        }*/
    }


}
