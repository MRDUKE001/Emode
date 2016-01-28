package com.wind.emode.testcase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.mediatek.telephony.TelephonyManagerEx;
import com.wind.emode.R;
import java.util.List;
import android.os.SystemProperties;
import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;
import com.mediatek.internal.telephony.ltedc.svlte.SvltePhoneProxy;
import com.android.internal.telephony.PhoneBase;

/*
 * create by lizusheng@wind-mobi.com 20151216
 */
public class ImeiWrite extends Activity implements OnClickListener {

	private static final String TAG = ImeiWrite.class.getSimpleName();
    private static final int EVENT_WRITE_IMEI = 7;
    private static final int EVENT_WRITE_MEID = 8;
    private static final boolean MTK_GEMINI_SUPPORT =
            "1".equals(SystemProperties.get("ro.mtk_gemini_support"));
    private Phone mPhone = null;
    private AlertDialog mAlertDialog;
    private EditText mEditImeiValue;
    private Button mBtnSim1;
    private Button mBtnSim2;
    private Button mBtnMeid;
    private Button mBtnReboot;
	private SvltePhoneProxy mSvltePhoneProxy;
	protected PhoneBase mLtePhone;
	protected PhoneBase mNLtePhone;

    private Handler mResponseHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }

            AsyncResult ar;
            switch (msg.what) {
                case EVENT_WRITE_IMEI:
                    ar = (AsyncResult) msg.obj;
                    if (ar.exception == null) {
                        mAlertDialog.setTitle("IMEI WRITE");
                        mAlertDialog.setMessage("The IMEI is writen successfully.");
                        mAlertDialog.show();
                    } else {
                        mAlertDialog.setTitle("IMEI WRITE");
                        mAlertDialog.setMessage("Fail to write IMEI due to radio unavailable or something else.");
                        mAlertDialog.show();
                    }
                    break;
                case EVENT_WRITE_MEID:
                	ar = (AsyncResult) msg.obj;
                	if (ar.exception == null) {
                	 mAlertDialog.setTitle("MEID WRITE");
                     mAlertDialog.setMessage("The MEID is writen successfully.");
                     mAlertDialog.show();
                	}else{
                     mAlertDialog.setTitle("MEID WRITE");
                     mAlertDialog.setMessage("Fail to write MEID due to radio unavailable or something else.");
                     mAlertDialog.show();
                	}
                default:
                    break;
            }
        }
    };

    private void writeImei(String imei, int simId){
        String imeiString[] = { "AT+EGMR=1,", "+EGMR" };
            if (simId == PhoneConstants.SIM_ID_1) {
                imeiString[0] = "AT+EGMR=1,7,\"" + imei
                        + "\"";
            } else if (simId == PhoneConstants.SIM_ID_2) {
                imeiString[0] = "AT+EGMR=1,10,\""
                        + imei + "\"";
            }
        Log.d(TAG, "IMEI Command:"+imeiString[0]);
        mLtePhone.invokeOemRilRequestStrings(imeiString, mResponseHander
                .obtainMessage(EVENT_WRITE_IMEI));
/*        mPhone.invokeOemRilRequestStrings(imeiString, mResponseHander
        		.obtainMessage(EVENT_WRITE_IMEI));
*/    }
    
    private void writeMeid(String meid){
      //String imeiString1[] = { "at+auso=9,", "" };
        String imeiString[] = { "at+vmobid=0,", "" };
     // imeiString1[0] = "at+auso=9";
        imeiString[0] = "at+vmobid=0,\"7268324842763108\",2,"
                       +"\""+meid + "\"";
        Log.d(TAG, "MEID Command:"+imeiString[0]+"\n"+imeiString[1]);
     /*   mPhone.invokeOemRilRequestStrings(imeiString1, mResponseHander
                .obtainMessage(EVENT_WRITE_MEID));*/
        mPhone.invokeOemRilRequestStrings(imeiString, mResponseHander
        		.obtainMessage(EVENT_WRITE_MEID));
    }
    

	private int checkImei(String imei) {
		int len = 14;
		int v = 0;
		int sum = 0;
		int e = 0;
		for(int i = 0; i < len; i ++){
			v = Integer.parseInt(imei.substring(i, i + 1));
			if(i % 2 != 0){
				e = v * 2;
				sum += e / 10 + e % 10;
			}else{
				sum += v;
			}
		}
		int m = sum % 10;
                int cb = 0;
		if(m != 0){
			cb = (10 - m);
		}
                if(Integer.parseInt(imei.substring(14)) != cb){
                    return cb;
                }
		return -1;
	}

    @Override
    public void onClick(View arg0) {
        String imei = mEditImeiValue.getText().toString().trim();
        Log.d(TAG, "value:"+imei);
      if(arg0.getId()==mBtnMeid.getId()){  
        if("".equals(imei)){
             Toast.makeText(this, "MEID must not be null!", Toast.LENGTH_LONG).show();
             return;
             }else if(imei.length() != 14){
             Toast.makeText(this, "MEID length must be 14!", Toast.LENGTH_LONG).show();
             return;
             }
      }else if(arg0.getId()==mBtnSim1.getId() || arg0.getId()==mBtnSim2.getId()){
        if("".equals(imei)){
            Toast.makeText(this, "IMEI must not be null!", Toast.LENGTH_LONG).show();
            return;
        }else if(imei.length() != 15){
            Toast.makeText(this, "IMEI length must be 15!", Toast.LENGTH_LONG).show();
            return;
        }else{
            int cb = checkImei(imei);
            if(cb != -1){
                Toast.makeText(this, "IMEI error! end number may be " + cb + " !", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
        if (arg0.getId() == mBtnSim1.getId()) {
            writeImei(imei, PhoneConstants.SIM_ID_1);
        }else if (arg0.getId() == mBtnSim2.getId()) {
            writeImei(imei, PhoneConstants.SIM_ID_2);
        }else if (arg0.getId() == mBtnMeid.getId()) {
            writeMeid(imei);
        }else if (arg0.getId() == mBtnReboot.getId()) {
            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.putExtra("nowait", 1);
            intent.putExtra("interval", 1);
            intent.putExtra("window", 0);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imei_write);
        mBtnSim1 = (Button)findViewById(R.id.btn_sim1);
        mBtnSim2 = (Button)findViewById(R.id.btn_sim2);
        mBtnMeid = (Button)findViewById(R.id.btn_meid);
        mBtnReboot = (Button)findViewById(R.id.btn_reboot);
      //String imei1 = TelephonyManagerEx.getDefault().getDeviceId(PhoneConstants.SIM_ID_1);//PhoneFactory.getPhone(PhoneConstants.SIM_ID_1).getDeviceId();
        String imei1 = SystemProperties.get("ro.wind_imei");
        Log.d(TAG, "MEID1:"+imei1);
        if (imei1 != null && !"".equals(imei1) && !"00000000".equals(imei1)) {
            mBtnSim1.setEnabled(false);
            ((TextView)findViewById(R.id.txt_imei1)).setText("1. IMEI1: " + imei1);
        }
        if (!MTK_GEMINI_SUPPORT) {
            mBtnSim2.setEnabled(false);
        }else{
        //String imei2 = TelephonyManagerEx.getDefault().getDeviceId(PhoneConstants.SIM_ID_2);//PhoneFactory.getPhone(PhoneConstants.SIM_ID_2).getDeviceId();
          String imei2 = SystemProperties.get("ro.wind_imei2");
          Log.d(TAG, "MEID2:"+imei2);
            if(imei2 != null && !"".equals(imei2)){
                mBtnSim2.setEnabled(false);
                ((TextView)findViewById(R.id.txt_imei2)).setText("2. IMEI2: " + imei2);
            }
        }
        String meid = SystemProperties.get("ro.wind_meid");
        Log.d(TAG, "MEID:"+meid);
        if(null!=meid && !"".equals(meid)&& !"00000000000000".equals(meid)){
        	 mBtnMeid.setEnabled(false);
        	 ((TextView)findViewById(R.id.txt_meid)).setText("3. MEID: " + meid);
        }
        mBtnSim1.setOnClickListener(this);
        mBtnSim2.setOnClickListener(this);
        mBtnMeid.setOnClickListener(this);
        mBtnReboot.setOnClickListener(this);
        mEditImeiValue = (EditText)findViewById(R.id.et_imei);
     
		mSvltePhoneProxy=(SvltePhoneProxy)PhoneFactory.getDefaultPhone();
		mLtePhone=mSvltePhoneProxy.getLtePhone();
	    mPhone = PhoneFactory.getDefaultPhone();
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mAlertDialog = builder.create();
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK" , (DialogInterface.OnClickListener)null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
