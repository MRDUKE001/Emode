package com.wind.emode.testcase;

import android.app.Activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wind.emode.R;
import java.io.IOException;

public class Root extends Activity {
    private static final String RO_SECURE = "ro.secure";
    private static final String RO_ALLOW_MOCK_LOCATION = "ro.allow.mock.location";
    private static final String RO_DEBUG = "ro.debuggable";
    private static final String ADB_ENABLE = "persist.sys.usb.config";
    private static final String ATCI_USERMODE = "persist.service.atci.usermode";	
    private static final String RO_BUILD_TYPE = "ro.build.type";	
    TextView title = null;
    Button start = null;
    Button stop = null;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.em_permission);
        title = (TextView) findViewById(R.id.title);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        title.setText("Permission");

        start.setText("Root");
        start.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    try {
                        SystemProperties.set(ADB_ENABLE, "none");
                        //SystemClock.sleep(200);
                        SystemProperties.set(RO_SECURE, "0");
                        SystemProperties.set(RO_ALLOW_MOCK_LOCATION, "1");
                        SystemProperties.set(RO_DEBUG, "1");
                        //SystemClock.sleep(200);
                        SystemProperties.set(ADB_ENABLE, "mass_storage,adb,acm");
		                SystemProperties.set(ATCI_USERMODE, "1");				
        String type = SystemProperties.get(RO_BUILD_TYPE, "unknown");      
        if (!type.equals("eng")) {
            try {
                Process proc = Runtime.getRuntime().exec("start atcid-daemon-u");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }						
                        Toast.makeText(Root.this, "device root succeed.", Toast.LENGTH_LONG)
                             .show();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            });

        stop.setText("Unroot");
        stop.setEnabled(false);
    }
}
