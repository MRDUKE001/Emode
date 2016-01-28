package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.telephony.TelephonyManager;
import com.wind.emode.R;

public class IMSI extends BaseActivity {

	String ret = null;
	Button okButton;
	TextView mContent;
	TextView title;
	TelephonyManager tm;

	private static final String UNKNOWN = "unknown";

	protected static final String LOG_TAG = "EMODE_IMSI";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_imsi);

		title = (TextView) findViewById(R.id.title);
		title.setText("IMSI");

		mContent = (TextView) findViewById(R.id.imei_content);

		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		ret = tm.getSubscriberId();
		if (ret == null)
			ret = UNKNOWN;

		mContent.setText("IMSI:" + ret);
		okButton = (Button) findViewById(R.id.Button01);
		okButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
		okButton.setText("OK");
	}
}