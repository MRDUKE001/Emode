package com.wind.emode.testcase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneProxy;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.constants.TestcaseType;
import com.wind.emode.utils.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.wind.emode.NvRAMAgentUtils;
public class PhaseCheck extends Activity {
    private static final String TAG = "Emode.PhaseCheck";
    
    private static final String PREF_KEY_TEST_RESULTS_BOARD = "emode_board_results";
    private static final String PREF_KEY_TEST_RESULTS_PHONE = "emode_phone_results";
    
    private String[] mStationNames;
	private boolean mHasMsn;
    private boolean mGPSBTWiFiFlagInNV;
    private boolean mDisplayZTESalesCountStatus;
    private int mSNLength;

    private static String turn(char flag) {
        if ('Y' == flag) {
            return "PASS";
        } else if ('N' == flag) {
            return "FAIL";
        } else {
            return "NO TEST";
        }
    }

    //GPS, BT, WiFi flag store in NV
    private void getFlagListEx(StringBuffer content, String barcode) {
        byte[] buff = NvRAMAgentUtils.readFile();//new byte[64];
        //int count = 0;

        /*
        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            count = is.read(buff);
            Log.d(TAG, "getFlagListEx, count = " + count);
            is.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getFlagListEx: " + e);
        } catch (IOException e) {
            Log.e(TAG, "getFlagListEx: " + e);
        }
		*/

        for (int i = 51; i < 60; i++) {
            if ((i == 51) || (i == 52) || (i == 56) || ( i == 58) || (i == 59)) {
                //if (count == 64) {
                if(buff != null){
                    content.append("\n" + mStationNames[i - 51] + ": " + turn((char) buff[i]));
                } else {
                    content.append("\n" + mStationNames[i - 51] + ": " + turn('N'));
                }
            } else {
                content.append("\n" + mStationNames[i - 51] + ": " + turn(barcode.charAt(i)));
            }
        }

        String calibration = barcode.substring(60, 62);

        if ("10".equals(calibration)) {
            content.append("\n" + mStationNames[9] + ": PASS");
        } else if ("01".equals(calibration)) {
            content.append("\n" + mStationNames[9] + ": FAIL");
        } else {
            content.append("\n" + mStationNames[9] + ": NO TEST");
        }

        char ft = barcode.charAt(62);

        if ('P' == ft) {
            content.append("\n" + mStationNames[10] + ": PASS");
        } else if ('F' == ft) {
            content.append("\n" + mStationNames[10] + ": FAIL");
        } else {
            content.append("\n" + mStationNames[10] + ": NO TEST");
        }
		if (mDisplayZTESalesCountStatus) {
    		if (buff != null && buff.length > 125) {
    			if (buff[124] != 'Y') {
            	    content.append("\n" + mStationNames[11] + ": "+ getString(R.string.status_on));
    		    } else {
    			    content.append("\n" + mStationNames[11] + ": "+ getString(R.string.status_off));
    		    }
    		} else {
    			content.append("\n" + mStationNames[11] + ": ERROR");
    		}	
		}
				
    }

    //GPS, BT, WiFi flag store in barcode, which get from modem
    private void getFlagList(StringBuffer content, String barcode) {
        byte[] buff = NvRAMAgentUtils.readFile();//new byte[64];
        //int count = 0;

        /*
        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            count = is.read(buff);
            Log.d(TAG, "getFlagListEx, count = " + count);
            is.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getFlagListEx: " + e);
        } catch (IOException e) {
            Log.e(TAG, "getFlagListEx: " + e);
        }
		*/

        for(int i = 51; i < 60; i++) {
            if(i == 58 || i == 59){
                //if(count == 64){
                if(buff != null){
                    content.append("\n" + mStationNames[i - 51] + ": " + turn((char)buff[i]));
                }else{
                    content.append("\n" + mStationNames[i - 51] + ": " + turn('N'));
                }
            }else{
                content.append("\n" + mStationNames[i - 51] + ": " + turn(barcode.charAt(i)));
            }
        }
        String calibration = barcode.substring(60, 62);

        if ("10".equals(calibration)) {
            content.append("\n" + mStationNames[9] + ": PASS");
        } else if ("01".equals(calibration)) {
            content.append("\n" + mStationNames[9] + ": FAIL");
        } else {
            content.append("\n" + mStationNames[9] + ": NO TEST");
        }

        char ft = barcode.charAt(62);

        if ('P' == ft) {
            content.append("\n" + mStationNames[10] + ": PASS");
        } else if ('F' == ft) {
            content.append("\n" + mStationNames[10] + ": FAIL");
        } else {
            content.append("\n" + mStationNames[10] + ": NO TEST");
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle("PhaseCheck");
        mStationNames = getResources().getStringArray(com.wind.emode.R.array.product_station_names);
		mHasMsn = getResources().getBoolean(com.wind.emode.R.bool.config_display_msn_info);
        mGPSBTWiFiFlagInNV = getResources().getBoolean(com.wind.emode.R.bool.config_gps_bt_wifi_flag_store_in_nv);
        mSNLength = getResources().getInteger(com.wind.emode.R.integer.config_sn_length);
        mDisplayZTESalesCountStatus = getResources().getBoolean(com.wind.emode.R.bool.config_display_zte_sales_count_status);
        reCheckMMIResult();		
        String barcode = SystemProperties.get("gsm.serial");
        StringBuffer content = new StringBuffer();
        int len = barcode.length();
        if (TextUtils.isEmpty(barcode)) {
            barcode = "MT0123456789";
        } 
		
		while (len < 63) {
            barcode += " ";
            len = barcode.length();
        }
		      
        String sn = barcode.substring(0, mSNLength);
		
		content.append("\nSN: " + sn);
        if (mHasMsn) {
		    content.append("\nMSN: " + getMSN());
		}
        Log.d(TAG, "barcode---> " + barcode);

        if (mGPSBTWiFiFlagInNV) {
            getFlagListEx(content, barcode);
        } else {
            getFlagList(content, barcode);
        }

        TextView tv = new TextView(this);
        tv.setText(content.toString());
        setContentView(tv);
    }
	
    private String getMSN() {
        byte[] buff = new byte[1024];
        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            int count = is.read(buff);
            is.close();
            Log.i(TAG, "DevInfo-> getMSN() count: " + count);
            if(count < 13){
                return "";
            }
            String msn = new String(buff, 0, 13);
            Log.i(TAG, msn);
            return msn;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }	
    
    private void reCheckMMIResult() {
        String keyName = PREF_KEY_TEST_RESULTS_PHONE;    
        int len = EmodeApp.mPhoneTestcases.size();
        if (EmodeApp.mTestcaseType == TestcaseType.BOARD) {
            keyName = PREF_KEY_TEST_RESULTS_BOARD;
            len = EmodeApp.mBoardTestcases.size();
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);  
        String results = sp.getString(keyName, "");
        if (TextUtils.isEmpty(results)) {
            return;
        }
        String[] pairs = results.split(",");

        if (pairs.length < len) {
            setAutoMMIResult(false);
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
        if(buff != null){
            int index = (EmodeApp.mTestcaseType == TestcaseType.BOARD) ? 59 : 58;
            buff[index] = (val?"Y":"N").getBytes()[0];
            NvRAMAgentUtils.writeFile(buff);
        }       
    }    
}
