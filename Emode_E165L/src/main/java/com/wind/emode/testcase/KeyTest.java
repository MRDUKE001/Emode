package com.wind.emode.testcase;

import android.content.res.Resources;

import android.os.Bundle;

import android.provider.Settings;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;


public class KeyTest extends BaseActivity {
    protected static final String LOG_TAG = "EMODE_KeyTest";
    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView t5;
    TextView t6;
    View btn_pass = null;
    int up = 1;
    int down = 1;
    int h = 0;
    int m = 0;
    int b = 0;
    int s = 0;
    int call = 0;
    int end = 0;
    Button backbutton;
	boolean hasRecentAppsKey;
	boolean disableVitualKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.em_key);
        hasRecentAppsKey =  getResources().getBoolean(R.bool.config_has_recent_apps_key);
        disableVitualKey = getResources().getBoolean(R.bool.config_disable_vitual_key);

        t1 = (TextView) findViewById(R.id.t_1);
        t2 = (TextView) findViewById(R.id.t_2);
        t3 = (TextView) findViewById(R.id.t_3);

        h = 1;
        m = 1;
        b = 1;

        t1 = (TextView) findViewById(R.id.t_1);
        t1.setText("HOME");

        t2 = (TextView) findViewById(R.id.t_2);
		if (hasRecentAppsKey) {
			t2.setText("RECENT");
		} else {
            t2.setText("MENU");
		}

        t3 = (TextView) findViewById(R.id.t_3);
        t3.setText("BACK");

        t4 = (TextView) findViewById(R.id.t_4);
        t4.setText("VOLUME UP");

        t5 = (TextView) findViewById(R.id.t_5);
        t5.setText("VOLUME DOWN");

        t6 = (TextView) findViewById(R.id.t_6);
        backbutton = (Button) findViewById(R.id.b_3);
        backbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        backbutton.setText("OK");

        if (disableVitualKey){
            h = 0;
            m = 0;
            b = 0;
            t1.setText("");
            t2.setText("");
            t3.setText("");
        }
    }

    @Override
    protected void onResume() {
        if (btn_pass == null) {
            btn_pass = getWindow().getDecorView().findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log("------ key code:" + keyCode);

        switch (keyCode) {
        case KeyEvent.KEYCODE_HOME:
            t1.setText("");
            h = 0;

            break;

        case KeyEvent.KEYCODE_MENU:
            if (!hasRecentAppsKey) {					
                t2.setText("");
                m = 0;
            }

            break;

        case KeyEvent.KEYCODE_BACK:
            t3.setText("");
            b = 0;

            break;

        case KeyEvent.KEYCODE_VOLUME_UP:
            t4.setText("");
            up = 0;

            break;

        case KeyEvent.KEYCODE_VOLUME_DOWN:
            t5.setText("");
            down = 0;

            break;

        case KeyEvent.KEYCODE_CALL:
            t2.setText("");
            call = 0;

            break;

        case KeyEvent.KEYCODE_ENDCALL:
            t3.setText("");
            end = 0;

            break;

        case KeyEvent.KEYCODE_SEARCH:
            t6.setText("");
            s = 0;

            break;

        case KeyEvent.KEYCODE_APP_SWITCH:
			if (hasRecentAppsKey) {
                t2.setText("");
                m = 0;
			} else {
	            t6.setText("");
	            s = 0;			
			}

            break;

        default:
            break;
        }

        if ((up == 0) && (down == 0)) {
        	if ((h == 0) && (b == 0) && (m == 0)) {
                btn_pass.callOnClick();

            }
        }

        return true;
    }
}
