package com.wind.emode;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;


import android.text.TextUtils;

//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
import com.wind.emode.utils.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;

import com.wind.emode.constants.TestcaseType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.wind.emode.NvRAMAgentUtils;

public class BaseActivity extends Activity implements View.OnClickListener {
    private static final String LOG_TAG = "BaseActivity";
    private static final String PREF_KEY_TEST_RESULTS_BOARD = "emode_board_results";
    private static final String PREF_KEY_TEST_RESULTS_PHONE = "emode_phone_results";
    protected static final boolean DBG = true;
    protected static final String TAG_PASS = "1";
    protected static final String TAG_FAIL = "2";

    protected Button mBtnPass;
    protected Button mBtnFail;
    private String mTestCaseCode;
    protected AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        log("onCreate");
        mTestCaseCode = getIntent().getStringExtra("test_code");
        mAudioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));

        View decorView = getWindow().getDecorView();

        if (decorView.findViewById(R.id.btn_bar) == null) {
            View v = LayoutInflater.from(this).inflate(R.layout.button_bar, (ViewGroup) decorView);
            mBtnPass = (Button) v.findViewById(R.id.btn_succ);
            mBtnFail = (Button) v.findViewById(R.id.btn_fail);
            mBtnPass.setOnClickListener(this);
            mBtnFail.setOnClickListener(this);
            mBtnPass.setTag(TAG_PASS);
            mBtnFail.setTag(TAG_FAIL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_RING,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        log("onResume");

    }

    @Override
    protected void onStart() {
        super.onStart();

        log("onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAudioManager.abandonAudioFocus(null);
        log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    private String setValue(String code, String str, String value) {
    	log("setValue, old results str: " + str);

        String newStr = "";

        if (TextUtils.isEmpty(str)) {
            newStr = code + "=" + value;
        } else if (!str.contains(",")) {
            String[] tp = str.split("=");

            if (tp[0].equals(code)) {
                newStr = code + "=" + value;
            } else {
                newStr = str + "," + code + "=" + value;
            }
        } else {
            String key = code + "=";

            if (str.startsWith(key)) {
                newStr = key + value + str.substring(key.length() + 1);
            } else {
                int index = str.indexOf("," + key);

                if (index == -1) {
                    newStr = str + "," + key + value;
                } else {
                    newStr = str.substring(0, index + 1) + key + value +
                        str.substring(index + key.length() + 2);
                }
            }
        }

        log("setValue, new results str: " + newStr);

        return newStr;
    }

    @Override
    public void onClick(View v) {
        finishTestcase(v.getTag().toString());
    }

    protected void finishTestcase(String result) {
        String keyName = PREF_KEY_TEST_RESULTS_PHONE;
        int len = EmodeApp.mPhoneTestcases.size();

        if (EmodeApp.mTestcaseType == TestcaseType.BOARD) {
            keyName = PREF_KEY_TEST_RESULTS_BOARD;
            len = EmodeApp.mBoardTestcases.size();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);        
        String oldResults = sp.getString(keyName, "");        
        String newResults = setValue(mTestCaseCode, oldResults, result);                       
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(keyName, newResults).commit();
        checkMMIResult(newResults, len);
        stopService(new Intent(this, EmptyForegroundService.class));
        finish();
    }

    private void checkMMIResult(String results, int len) {
        String[] pairs = results.split(",");

        if (pairs.length < len) {
            //setAutoMMIResult(false);
        } else {
            int size = pairs.length;
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(pairs[i].split("=")[1]) == 2) {
                    setAutoMMIResult(false);
                    return;
                }
            }
            setAutoMMIResult(true);
        }
    }

    private void setAutoMMIResult(boolean val) {
        byte[] buff = NvRAMAgentUtils.readFile();//new byte[64];
        /*
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
		*/
        if(buff != null){
            int index = (EmodeApp.mTestcaseType == TestcaseType.BOARD) ? 59 : 58;
            buff[index] = (val?"Y":"N").getBytes()[0];
            NvRAMAgentUtils.writeFile(buff);
		}		
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    protected void log(String msg) {
        if (DBG) {
            Log.d(this, msg);
        }
    }
}
