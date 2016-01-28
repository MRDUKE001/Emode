package com.wind.emode.testcase;

import android.app.Activity;

import android.content.Context;

import android.graphics.Color;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.os.PowerManager;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;


public class ProximitySensorStressTest extends BaseActivity implements SensorEventListener,
    View.OnClickListener {
    private static final String TAG = "ProximitySensorStressTest";
    private static final int TOTAL = 10000;
    private TextView pSupport = null;
    private TextView pValue = null;
    private TextView pTotal = null;
    private TextView pCurrent = null;
    private Button pOperate = null;
    private boolean mExistProximity;
    private float mCurrentValue = 0.0f;
    private int mCurrentCount = 0;
    private SensorManager mSensorMgr = null;
    private Sensor mSensor = null;
    private PowerManager.WakeLock mProximityWakeLock;
    private PowerManager mPowerManager;
    private boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_proximity);

        initWidget();

        if (mExistProximity) {
            mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

            // Wake lock used to control proximity sensor behavior.
            if (mPowerManager.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {
                mProximityWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                        TAG);
            }

            Log.d(TAG, "onCreate: mProximityWakeLock: " + mProximityWakeLock);
        }
    }

    private void initWidget() {
        pSupport = (TextView) findViewById(R.id.p_support);
        pValue = (TextView) findViewById(R.id.p_value);
        pTotal = (TextView) findViewById(R.id.p_total);
        pCurrent = (TextView) findViewById(R.id.p_current);

        pOperate = (Button) findViewById(R.id.p_operate);
        pOperate.setOnClickListener(this);

        pValue.setText(getString(R.string.proximity_timer_value, mCurrentValue + ""));
        pTotal.setText(getString(R.string.proximity_timer_total, TOTAL));
        pCurrent.setText(getString(R.string.proximity_timer_current, mCurrentCount));

        int SensorTypes = 0;
        for (Sensor sensor:mSensorMgr.getSensorList(Sensor.TYPE_ALL)) {
        	SensorTypes |= sensor.getType();
        }
        mExistProximity = (SensorTypes & Sensor.TYPE_PROXIMITY) > 0;

        if (!mExistProximity) {
            pSupport.setVisibility(View.VISIBLE);
            pOperate.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //		if (mExistProximity) {
        //			mSm.unregisterListener(this);
        //		}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        Sensor sensor = event.sensor;

        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                pValue.setText(getString(R.string.proximity_timer_value, values[0] + ""));
                updateProximitySensorMode(values[0]);
            }
        }
    }

    private void updateProximitySensorMode(float proximityX) {
        synchronized (mProximityWakeLock) {
            if (proximityX == 1f) {
                if (!mProximityWakeLock.isHeld()) {
                    Log.d(TAG, "updateProximitySensorMode: acquiring...");
                    mProximityWakeLock.acquire();
                } else {
                    Log.d(TAG, "updateProximitySensorMode: lock already held.");
                }
            } else {
                if (mProximityWakeLock.isHeld()) {
                    if (DBG) {
                        Log.d(TAG, "updateProximitySensorMode: releasing...");
                    }

                    mProximityWakeLock.release();

                    mCurrentCount++;
                    pCurrent.setText(getString(R.string.proximity_timer_current, mCurrentCount));
                } else {
                    Log.d(TAG, "updateProximitySensorMode: lock already released.");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mExistProximity) {
            if (isStarted) {
                isStarted = false;
                pOperate.setText(R.string.proximity_timer_start);

                mSensorMgr.unregisterListener(this);
            } else {
                isStarted = true;
                pOperate.setText(R.string.proximity_timer_stop);
                mCurrentCount = 0;

                mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }
}
