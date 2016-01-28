package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;

import android.os.Bundle;
import android.os.SystemProperties;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.telephony.TelephonyManager;
import android.util.Config;
import com.wind.emode.R;

public class IMEI extends BaseActivity {

	String ret = null;
	Button okButton;
	TextView mContent;
	TextView title;
	TelephonyManager tm;

	private static final String UNKNOWN = "unknown";

	protected static final String LOG_TAG = "EMODE_IMEI";

    static private String getIMEIFromModem(String imei){
        char[] nums = imei.toCharArray();
        StringBuffer res = new StringBuffer();
        int val = Integer.parseInt(nums[4] + "");
        int sum = val;
        res.append(val);
        int i = 6;
        while(i < 18){
            val = Integer.parseInt(nums[i + 1] + "");
            sum += (val / 5) + (val * 2) % 10;
            res.append(val);
            val = Integer.parseInt(nums[i] + "");
            sum += val;
            res.append(val);
            i += 2;
        }
        val = Integer.parseInt(nums[19] + "");
        sum += (val / 5) + (val * 2) % 10;
        res.append(val);
        int en = sum - sum / 10 * 10;
        if(en == 0){
            res.append(0);
        }else{
            res.append(10 - en);
        }
        return res.toString();
    }

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_imei);

		title = (TextView) findViewById(R.id.title);
		title.setText("IMEI");

		mContent = (TextView) findViewById(R.id.imei_content);

        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        ret = tm.getDeviceId();
		if (ret == null)
			ret = UNKNOWN;

		mContent.setText("IMEI:" + ret);

		okButton = (Button) findViewById(R.id.Button01);
		okButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
		okButton.setText("OK");
	}
}