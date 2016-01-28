package com.wind.emode.testcase;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import com.mediatek.telephony.TelephonyManagerEx;
import com.wind.emode.BaseActivity;

import android.telephony.TelephonyManager;
import com.wind.emode.R;

public class SimState extends BaseActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_sim_state);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("SIM");

        TelephonyManagerEx tm = TelephonyManagerEx.getDefault();
        
        int sim1State = tm.getSimState(0);
        int sim2State = tm.getSimState(1);

        String sim1StateStr = (sim1State == TelephonyManager.SIM_STATE_ABSENT || sim1State == TelephonyManager.SIM_STATE_UNKNOWN) ? "未插入" : "插入";
        String sim2StateStr = (sim2State == TelephonyManager.SIM_STATE_ABSENT || sim2State == TelephonyManager.SIM_STATE_UNKNOWN) ? "未插入" : "插入";

        TextView sim1 = (TextView) findViewById(R.id.sim1);
        sim1.setText("SIM 1: " + sim1StateStr);

        TextView sim2 = (TextView) findViewById(R.id.sim2);
        sim2.setText("SIM 2: " + sim2StateStr);

        TextView tvIMSI1 = (TextView) findViewById(R.id.imsi1);
        TextView tvIMSI2 = (TextView) findViewById(R.id.imsi2);
        tvIMSI1.setText("IMSI 1: " + tm.getSubscriberId(0));
        tvIMSI2.setText("IMSI 2: " + tm.getSubscriberId(1));
    }
}
