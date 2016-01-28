package com.wind.emode.testcase;

import com.wind.emode.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomVersionInfos extends Activity {
    protected static final String LOG_TAG = "CustomVersionInfos";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView txtTitle = new TextView(this);
        txtTitle.setTextSize(25);
        txtTitle.setGravity(Gravity.CENTER);
        ll.addView(txtTitle, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        if (true) {
            TextView hwVersion = new TextView(this);
            hwVersion.setText(getString(R.string.software_version)
                    + SystemProperties.get("ro.build.display.id", "v1.0"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 50;
            ll.addView(hwVersion, lp);
            setContentView(ll);
        } else {
            TextView hwVersion = new TextView(this);
            hwVersion.setText(getString(R.string.software_version)
                    + SystemProperties.get("ro.build.display.id", "v1.0"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 50;
            ll.addView(hwVersion, lp);
            TextView customVersion = new TextView(this);
            customVersion.setText(getString(R.string.external_hardware_version)
                    + SystemProperties.get("ro.product.hardware", "MBV1.0"));
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            lp0.topMargin = 20;
            ll.addView(customVersion, lp0);
            setContentView(ll);
        }
    }
}