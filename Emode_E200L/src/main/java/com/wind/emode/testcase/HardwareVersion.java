package com.wind.emode.testcase;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import com.wind.emode.utils.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.SystemProperties;
import com.wind.emode.R;

public class HardwareVersion extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView txtTitle = new TextView(this);
        txtTitle.setTextSize(25);
        txtTitle.setGravity(Gravity.CENTER);
        ll.addView(txtTitle, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView txtVersion = new TextView(this);
        txtVersion.setText(getString(R.string.hardware_version) + SystemProperties.get("ro.product.hardware", "MBV1.0"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 50;
        ll.addView(txtVersion, lp);
        
        setContentView(ll);
    }

}
