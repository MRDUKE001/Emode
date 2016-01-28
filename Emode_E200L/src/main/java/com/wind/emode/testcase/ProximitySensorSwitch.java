package com.wind.emode.testcase;

import android.app.Activity;

import android.content.Context;

import android.os.Bundle;
import android.os.PowerManager;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;


public class ProximitySensorSwitch extends Activity implements OnClickListener {
    private Button btn0;
    private Button btn1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_switch_mode);
        ((TextView) findViewById(R.id.m0_title)).setText(R.string.title_disable_proximity);
        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        ((Button) findViewById(R.id.btn2)).setVisibility(View.GONE);
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn0.setText(R.string.disable_proximity);
        btn1.setText(R.string.enable_proximity);
    }

    @Override
    public void onClick(View v) {
        PowerManager powermanager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        switch (v.getId()) {
        case R.id.btn0:
            android.provider.Settings.System.putInt(getContentResolver(), "wind_disable_proximity",
                1);
            powermanager.reboot(null);

            break;

        case R.id.btn1:
            android.provider.Settings.System.putInt(getContentResolver(), "wind_disable_proximity",
                0);
            powermanager.reboot(null);

            break;
        }
    }
}
