package com.wind.emode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.wind.emode.data.TestCase;
import com.wind.emode.manager.MainManager;
import com.wind.emode.manager.ModuleManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lizusheng on 2015/12/28.
 */
public class FileUtils {
    public static final String TAG = "FileUtils";
    public static final String NOT_EXISTS = "NOT_EXISTS";
    public static final String IO_ERROR = "IO_ERROR";

    public static void byte2image(byte[] data,String path){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean prepareFile(String path) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        File destDir = new File(path).getParentFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
            //  此处不可使用打印到本地 会造成回环
            //Lo.gV("dir made");
        }
        return true;
    }

    public static String getText(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return NOT_EXISTS;
        }

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return IO_ERROR;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void updateTestResultFile(Context context) {
        MainManager mMainManager = MainManager.getInstance(context);
        ModuleManager mModuleManager = ModuleManager.getInstance(context);
        long startTime = System.currentTimeMillis();
        SharedPreferences sp = context.getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);
        String testResults = sp.getString(Constants.PREF_KEY_TEST_RESULTS_PHONE,"");
        String[] testResultsPairs = testResults.split(",");
        String[] testResultsItem;
        int[] testResultsValues = new int[mModuleManager.mPhoneTestcases.size()];

        final int size = mModuleManager.mPhoneTestcases.size();
        final int len = testResultsPairs.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < len; j++) {
                testResultsItem = testResultsPairs[j].split("=");
                if (testResultsItem[0].equals(mModuleManager.mPhoneTestcases.get(i).getEnName().replaceAll("\\s+", "_"))) {
                    testResultsValues[i] = Integer.parseInt(testResultsItem[1]);
                }
            }
        }

        StringBuilder fileSB = new StringBuilder();
        int testNum = 0;
        fileSB.append("[SMMI Test Result]\n");
        for (TestCase tc : mModuleManager.mPhoneTestcases) {
            fileSB.append(tc.getEnName());
            fileSB.append(",");
            fileSB.append(tc.getErrorCode());
            fileSB.append(",Initialize\n");
        }
        for (int i=0; i < size; i++) {
            if (testResultsValues[i] >= 0) {
                fileSB.append(mModuleManager.mPhoneTestcases.get(i).getEnName());
                fileSB.append(",");
                fileSB.append(testResultsValues[i]);
                if (testResultsValues[i] == Constants.TEST_PASS) {
                    fileSB.append(",PASS\n");
                    testNum++;
                } else {
                    testNum++;
                    fileSB.append(",FAIL\n");
                }
            }
        }
        if (testNum == size) {
            fileSB.append("[SMMI All Test Done]\n");
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), Constants.TEST_RESULT_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(fileSB.toString().getBytes());
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "updateTestResultToFile", e);
        } catch (IOException e) {
            Log.e(TAG, "updateTestResultToFile", e);
        } finally {
            try {
                bos.close();
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, "updateTestResultToFile", e);
            }
        }
        Log.v(TAG, "update result file takes " + (System.currentTimeMillis() - startTime) + "ms");
    }


    public static void initializeTestResultFile(Context context) {
        MainManager mMainManager = MainManager.getInstance(context);
        ModuleManager mModuleManager = ModuleManager.getInstance(context);
        long startTime = System.currentTimeMillis();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean testResultFileInited = sp.getBoolean(Constants.PREF_KEY_TEST_RESULT_FILE_INITIALIZED, false);
        if (!testResultFileInited && mModuleManager.mXmlParsed) {
            StringBuilder fileSB = new StringBuilder();
            StringBuilder prefSB = new StringBuilder();
            fileSB.append("[SMMI Test Result]\n");
            for (TestCase tc : mModuleManager.mPhoneTestcases) {
                fileSB.append(tc.getEnName());
                fileSB.append(",");
                fileSB.append(tc.getErrorCode());
                fileSB.append(",Initialize\n");

                prefSB.append(tc.getEnName().replaceAll("\\s+", "_"));
                prefSB.append("=");
                prefSB.append(Constants.NOT_TEST);
                prefSB.append(",");
            }
            prefSB.deleteCharAt(prefSB.length() - 1);
            SharedPreferences testResultsSP = context.getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor testResultsEditor = testResultsSP.edit();
            testResultsEditor.putString(Constants.PREF_KEY_TEST_RESULTS_PHONE, prefSB.toString());
            testResultsEditor.commit();

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), Constants.TEST_RESULT_FILE_NAME);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(fileSB.toString().getBytes());
                bos.flush();
                bos.close();
                fos.close();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(Constants.PREF_KEY_TEST_RESULT_FILE_INITIALIZED, true);
                editor.commit();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "initializeTestResultFile", e);
            } catch (IOException e) {
                Log.e(TAG, "initializeTestResultFile", e);
            } finally {
                try {
                    bos.close();
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "initializeTestResultFile", e);
                }
            }
            Log.v(TAG, "initialize result file takes " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }
}
