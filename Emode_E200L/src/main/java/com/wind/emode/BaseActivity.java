package com.wind.emode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import com.wind.emode.data.Testcase;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Button;

public class BaseActivity extends Activity implements View.OnClickListener {
    private static final String LOG_TAG = "BaseActivity";
    protected static final boolean DBG = true;

    protected Button mBtnPass;
    protected Button mBtnFail;
    private Testcase mTestcase;
    protected AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        log("onCreate");
        mTestcase = EmodeApp.mTestcases.get(getIntent().getStringExtra("testcase_code"));
        mAudioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));

        View decorView = getWindow().getDecorView();

        if (decorView.findViewById(R.id.btn_bar) == null) {
            View v = LayoutInflater.from(this).inflate(R.layout.button_bar, (ViewGroup) decorView);
            mBtnPass = (Button) v.findViewById(R.id.btn_succ);
            mBtnFail = (Button) v.findViewById(R.id.btn_fail);
            mBtnPass.setOnClickListener(this);
            mBtnFail.setOnClickListener(this);
            mBtnPass.setTag(Constants.TEST_PASS);
            if (EmodeApp.mIsCustomizeErrorCode) {
                mBtnFail.setTag(mTestcase.errorCode);
            } else {
                mBtnFail.setTag(Constants.TEST_FAIL);
            }           
            
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

    /**
     * 将所有测试项的测试结果通过拼接存储到一个字符串中
     * @param name
     * @param str
     * @param value
     * @return
     */
    private String setValue(String name, String str, int value) {
    	log("setValue, old results str: " + str);

        String newStr = "";

        if (TextUtils.isEmpty(str)) {     //1.没有测试，直接添加
            newStr = name + "=" + value;
        } else if (!str.contains(",")) {
            String[] tp = str.split("=");

            if (tp[0].equals(name)) {     //2-1有一个测试结果，第一个匹配，更新
                newStr = name + "=" + value;
            } else {                      //2-2有一个测试结果，第一个没有匹配，后面添加
                newStr = str + "," + name + "=" + value;
            }
        } else {                          //3.多于一个测试结果
            String key = name + "=";

            if (str.startsWith(key)) {     //3-1多于一个测试结果，第一个匹配，更新
                newStr = key + value + str.substring(str.indexOf(","));
            } else {                       //3-2多于一个测试结果，第一个不匹配
                int startIndex = str.indexOf(key);

                if (startIndex == -1) {   //3-2-1多于一个测试结果，没有匹配结果，最后面添加
                    newStr = str + "," + key + value;
                } else {                  //3-2-2多于一个测试结果，找到匹配结果，更新
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

        log("setValue, new results str: " + newStr);

        return newStr;
    }

    @Override
    public void onClick(View v) {
        finishTestcase((Integer)(v.getTag()));
    }


    protected void finishTestcase(int result) {
        if (EmodeApp.mIsCustomizeErrorCode && Constants.TEST_FAIL == result) {
            result = mTestcase.errorCode;
        }       
        if (EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_OTHER) {
            finish();
            return;
        }
        String keyName = EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_BOARD ? Constants.PREF_KEY_TEST_RESULTS_BOARD : Constants.PREF_KEY_TEST_RESULTS_PHONE;
        int len = EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_BOARD ? EmodeApp.mBoardTestcases.size() : EmodeApp.mPhoneTestcases.size();

        SharedPreferences sp = getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);        
        String oldResults = sp.getString(keyName, "");        
        String newResults = setValue(mTestcase.enName.replaceAll("\\s+", "_"), oldResults, result);                       
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(keyName, newResults).commit();
        checkMMIResult(newResults, len);
        
        if (!EmodeApp.mIsAutoTest && EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_PHONE && EmodeApp.mIsSaveTestResultsToFile) {
            Intent intent = new Intent(Constants.ACTION_EMODE_UPDATE_RESULT_FILE);
            intent.setPackage(getPackageName());
            startService(intent);
        }
        
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
                if (Integer.parseInt(pairs[i].split("=")[1]) != Constants.TEST_PASS) {
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
            int index = (EmodeApp.mTestcaseType == Constants.TESTCASE_TYPE_BOARD) ? 59 : 58;
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
