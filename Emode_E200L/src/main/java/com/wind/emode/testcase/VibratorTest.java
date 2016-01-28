package com.wind.emode.testcase;

import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.app.Service;

import android.provider.Settings;

import android.provider.Settings.SettingNotFoundException;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class VibratorTest extends BaseActivity {
	private static final int VIBRATE_ONCE = 1;
	private static final int STOP_VIBRATE = 2;		
    protected static final String LOG_TAG = "EMODE_VibratorTest";
	private Vibrator mVibrator;
    private IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    public Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
				case VIBRATE_ONCE:
					mVibrator.vibrate(new long[]{1000,1000}, 0);
					break;

				case STOP_VIBRATE:
					removeMessages(VIBRATE_ONCE);
					mVibrator.cancel();
					break;

                default:
                    break;
                }
            }
        };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_vibrator);

		mVibrator =  (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);		

    }

    @Override
    protected void onResume() {
        super.onResume();

		handler.sendEmptyMessageDelayed(VIBRATE_ONCE, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();

		handler.sendEmptyMessage(STOP_VIBRATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
