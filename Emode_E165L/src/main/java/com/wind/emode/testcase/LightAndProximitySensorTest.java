package com.wind.emode.testcase;

import android.graphics.Color;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;


//mark off common SensorTest,because phone hasn't light and proxy sensor. 
public class LightAndProximitySensorTest extends BaseActivity implements SensorEventListener {
    protected static final String LOG_TAG = "LightAndProximitySensorTest";
	private static final float DIFF = 0.0001f;
	private static final int VALUE_CHANGE_TIMES = 3;
    TextView mtext7;
    TextView mtext8;
    TextView mGsensorResult;
    TextView mCompassResult;
    TextView mLightResult;
    TextView mProximityResult;
    Button mbutton;
    SensorManager sm;
    Sensor mSensor2;
    Sensor mSensor3;
    private boolean mFlag;
	private float mLastLightValue = 0.0f;
	private float mLastProximityValue = 0.0f;
    private int mLightUploadTime;
    private int mProximityUploadTime;
    private View btn_pass = null;
    private boolean resultFlags[] = {false, false};

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_sensor_light_prox);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor2 = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensor3 = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        mtext7 = (TextView) findViewById(R.id.m0_t7);
        mtext8 = (TextView) findViewById(R.id.m0_t8);

        mtext8.setText(getString(R.string.proximity) + " X : ");

        mtext7.setText(getString(R.string.light) + " X:  ");

        mLightResult = (TextView) findViewById(R.id.light_result);
        mProximityResult = (TextView) findViewById(R.id.proximity_result);
        mProximityResult.setText(getString(R.string.proximity) + getString(R.string.test_fail));
        mProximityResult.setTextColor(Color.RED);
        mLightResult.setText(getString(R.string.light) + getString(R.string.test_fail));
        mLightResult.setTextColor(Color.RED);

        mbutton = (Button) findViewById(R.id.m0_b);
        mbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                    case R.id.m0_b: {
                        finish();

                        return;
                    }

                    default:
                        break;
                    }
                }
            });
        mbutton.setText("OK");

        //		if (EMflag == AutoDetect) {
        //			EMtimer.schedule(EMtask, 15000);
        //		}
    }

    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        Sensor sensor = event.sensor;

        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                mtext7.setText(getString(R.string.light) + " X:  " + values[0]);
				Log.d(LOG_TAG,  "Light, X:  " + values[0]);
				if (Math.abs(mLastLightValue - values[0]) > DIFF ) {
                    mLightUploadTime++;
					mLastLightValue = values[0];
				}

                if (mLightUploadTime >= VALUE_CHANGE_TIMES) {
                    mLightResult.setText(getString(R.string.light) + getString(R.string.test_pass));
                    mLightResult.setTextColor(Color.GREEN);
                    testPass(0);
                }
            }

            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                mtext8.setText(getString(R.string.proximity) + " X:  " + values[0]);
				Log.d(LOG_TAG,  "Proximity, X:  " + values[0]);
				if (Math.abs(mLastProximityValue - values[0]) > DIFF ) {
                    mProximityUploadTime++;
					mLastProximityValue = values[0];
				}

                if (mProximityUploadTime >= VALUE_CHANGE_TIMES) {
                    mProximityResult.setText(getString(R.string.proximity) +
                        getString(R.string.test_pass));
                    mProximityResult.setTextColor(Color.GREEN);
                    testPass(1);
                }
            }
        }
    }

    @Override
    protected void onResume() {
    	if(btn_pass == null){
            View dv = getWindow().getDecorView();
            btn_pass = dv.findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
        }
        super.onResume();
        sm.registerListener(this, mSensor2, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, mSensor3, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }

    // ��Ȼû��ʵ�֣�������ɾ��
    // @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
    
    public void testPass(int index){
  	  resultFlags[index] = true;
        if(resultFlags[0] && resultFlags[1]){
            btn_pass.setVisibility(View.VISIBLE);
            finishTestcase(TAG_PASS);
        }
  }
}
