package com.wind.emode.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.support.v4.content.FileProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.wind.emode.EmodeApp;
import com.wind.emode.data.Testcase;

public class FileUtils {
    private static final String TAG = "FileUtils";
	private static final String PHOTO_DATE_FORMAT = "'IMG'_yyyyMMdd_HHmmss";
	
	public static Uri generateTempPhotoUri(Context context) {
		return FileProvider.getUriForFile(context, "com.wind.emode.files", 
				new File(pathForTempPhoto(context, generateTempPhotoFileName())));
	}
	
	private static String pathForTempPhoto(Context context, String fileName) {
		final File dir = context.getCacheDir();
		dir.mkdirs();
		final File f = new File(dir, fileName);
		return f.getAbsolutePath();
	}
	
	private static String generateTempPhotoFileName() {
		final Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT, Locale.US);
		return "Photo-" + dateFormat.format(date) + ".jpg";
	}
	
	
	public static void updateTestResultFile(Context context) {
	    long startTime = System.currentTimeMillis();
	    SharedPreferences sp = context.getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);  
	    String testResults = sp.getString(Constants.PREF_KEY_TEST_RESULTS_PHONE,"");
	    String[] testResultsPairs = testResults.split(",");
	    String[] testResultsItem; 
	    int[] testResultsValues = new int[EmodeApp.mPhoneTestcases.size()];
	    
	    final int size = EmodeApp.mPhoneTestcases.size();
	    final int len = testResultsPairs.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < len; j++) {
                testResultsItem = testResultsPairs[j].split("=");
                if (testResultsItem[0].equals(EmodeApp.mPhoneTestcases.get(i).enName.replaceAll("\\s+", "_"))) {
                    testResultsValues[i] = Integer.parseInt(testResultsItem[1]);
                }
            }
        }
	    
        StringBuilder fileSB = new StringBuilder();
        int testNum = 0;
        fileSB.append("[SMMI Test Result]\n");
        for (Testcase tc : EmodeApp.mPhoneTestcases) {
            fileSB.append(tc.enName);
            fileSB.append(",");
            fileSB.append(tc.errorCode);
            fileSB.append(",Initialize\n");
        }
        for (int i=0; i < size; i++) {
            if (testResultsValues[i] >= 0) {
                fileSB.append(EmodeApp.mPhoneTestcases.get(i).enName);
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
        long startTime = System.currentTimeMillis();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);  
        boolean testResultFileInited = sp.getBoolean(Constants.PREF_KEY_TEST_RESULT_FILE_INITIALIZED, false);
        if (!testResultFileInited && EmodeApp.mTestcaseReady) {            
            StringBuilder fileSB = new StringBuilder();
            StringBuilder prefSB = new StringBuilder();
            fileSB.append("[SMMI Test Result]\n");
            for (Testcase tc : EmodeApp.mPhoneTestcases) {
                fileSB.append(tc.enName);
                fileSB.append(",");
                fileSB.append(tc.errorCode);
                fileSB.append(",Initialize\n");
                
                prefSB.append(tc.enName.replaceAll("\\s+", "_"));
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
