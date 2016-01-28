package com.wind.emode.testcase;

import android.content.pm.PackageManager;
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


public class SensorAllTest extends BaseActivity implements SensorEventListener {
    protected static final String LOG_TAG = "SensorAllTest";
	private static final float DIFF = 0.0001f;
	private static final int VALUE_CHANGE_TIMES = 3;		
    TextView mtext;
    TextView mtext1;
    TextView mtext2;
    TextView mtext3;
    TextView mtext4;
    TextView mtext5;
    TextView mtext6;
    TextView mtext7;
    TextView mtext8;
    TextView mtext9;
    TextView mtext10;
    TextView mtext11;
    TextView mGsensorResult;
    TextView mCompassResult;
    TextView mLightResult;
    TextView mProximityResult;
    TextView mGyroscopeResult;

    //    Button mBtnSwitch;
    Button mbutton;
    SensorManager mSensorMgr;
    Sensor mSensor0;
    Sensor mSensor1;
    Sensor mSensor2;
    Sensor mSensor3;
    Sensor mSensor4;
    private boolean mFlag;
    private boolean mExistProximity;
    private boolean mExistGsensor;
    private boolean mExistCompass;
    private boolean mExistLight;
    private boolean mExistGyroscope;
    private int mLightUploadTime;
    private int mProximityUploadTime;
    private int mGsensorUploadTime;
    private int mGyroscopeUploadTime;
    private int mCompassUploadTime;
	private float mLastLightValue = 0.0f;
	private float mLastProximityValue = 0.0f;		
	private boolean resultFlags[] = {true, true, true, true, true};
	View btn_pass = null;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_sensor_noclib);
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
	boolean msensorSupportGyroscope = getResources().getBoolean(R.bool.config_magneticfield_sensor_support_gyroscope);
	PackageManager pm = getPackageManager();
        mExistProximity = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
        mExistGsensor = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        mExistCompass = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        mExistLight = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
        mExistGyroscope = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE) || (mExistCompass && msensorSupportGyroscope); 
        
        resultFlags[0] = !mExistCompass;
        resultFlags[1] = !mExistGsensor;
        resultFlags[2] = !mExistLight;
        resultFlags[3] = !mExistProximity;
        resultFlags[4] = !mExistGyroscope;

        if (mExistGsensor) {
            mSensor0 = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (mExistCompass) {
            mSensor1 = mSensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (mExistLight) {
            mSensor2 = mSensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        if (mExistProximity) {
            mSensor3 = mSensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (mExistGyroscope) {
            mSensor4 = mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        mtext = (TextView) findViewById(R.id.m0_t);
        mtext1 = (TextView) findViewById(R.id.m0_t1);
        mtext2 = (TextView) findViewById(R.id.m0_t2);
        mtext3 = (TextView) findViewById(R.id.m0_t3);
        mtext4 = (TextView) findViewById(R.id.m0_t4);
        mtext5 = (TextView) findViewById(R.id.m0_t5);
        mtext7 = (TextView) findViewById(R.id.m0_t7);
        mtext8 = (TextView) findViewById(R.id.m0_t8);
        mtext9 = (TextView) findViewById(R.id.m0_t9);
        mtext10 = (TextView) findViewById(R.id.m0_t10);
        mtext11 = (TextView) findViewById(R.id.m0_t11);

        mGsensorResult = (TextView) findViewById(R.id.gsensor_result);
        mCompassResult = (TextView) findViewById(R.id.compass_result);
        mLightResult = (TextView) findViewById(R.id.light_result);
        mProximityResult = (TextView) findViewById(R.id.proximity_result);
        mGyroscopeResult = (TextView) findViewById(R.id.gyroscope_result);

        //        mBtnSwitch = (Button) findViewById(R.id.btnSwitch);
        //        mBtnSwitch.setText("test proximity");
        //        mBtnSwitch.setOnClickListener(new View.OnClickListener() {
        //            public void onClick(View v) {
        //                sm.unregisterListener(sensorAll.this);
        //                if(!mFlag){
        //                    sm.registerListener(sensorAll.this, mSensor3, SensorManager.SENSOR_DELAY_NORMAL);
        //                    mBtnSwitch.setText("test other");
        //                    mProximityResult.setText("Proximity is Failed!");
        //                    mProximityResult.setTextColor(Color.RED);
        //                    mFlag = true;
        //                }else{
        //                    sm.registerListener(sensorAll.this, mSensor0, SensorManager.SENSOR_DELAY_NORMAL);
        //                    sm.registerListener(sensorAll.this, mSensor1, SensorManager.SENSOR_DELAY_NORMAL);
        //                    sm.registerListener(sensorAll.this, mSensor2, SensorManager.SENSOR_DELAY_NORMAL);
        //                    mBtnSwitch.setText("test proximity");
        //                    mFlag = false;
        //                }
        //            }
        //        });
        mtext.setText(getString(R.string.compass) + " X:  ");
        mtext1.setText(getString(R.string.compass) + " Y:  ");
        mtext2.setText(getString(R.string.compass) + " Z:  ");
        mtext3.setText(getString(R.string.gsensor) + " X:  ");
        mtext4.setText(getString(R.string.gsensor) + " Y:  ");
        mtext5.setText(getString(R.string.gsensor) + " Z:  ");
        mtext7.setText(getString(R.string.light) + " X:  ");
        mtext8.setText(getString(R.string.proximity) + " X : ");
        mtext9.setText(getString(R.string.gyroscope) + " X:  ");
        mtext10.setText(getString(R.string.gyroscope) + " Y:  ");
        mtext11.setText(getString(R.string.gyroscope) + " Z:  ");
        mProximityResult.setText(getString(R.string.proximity) + getString(R.string.test_fail));
        mProximityResult.setTextColor(Color.RED);
        mGyroscopeResult.setText(getString(R.string.gyroscope) + getString(R.string.test_fail));
        mGyroscopeResult.setTextColor(Color.RED);
        mGsensorResult.setText(getString(R.string.gsensor) + getString(R.string.test_fail));
        mGsensorResult.setTextColor(Color.RED);
        mCompassResult.setText(getString(R.string.compass) + getString(R.string.test_fail));
        mCompassResult.setTextColor(Color.RED);
        mLightResult.setText(getString(R.string.light) + getString(R.string.test_fail));
        mLightResult.setTextColor(Color.RED);

        if (!mExistCompass) {
            mtext.setVisibility(View.GONE);
            mtext1.setVisibility(View.GONE);
            mtext2.setVisibility(View.GONE);
            mCompassResult.setVisibility(View.GONE);
        }

        if (!mExistGsensor) {
            mtext3.setVisibility(View.GONE);
            mtext4.setVisibility(View.GONE);
            mtext5.setVisibility(View.GONE);
            mGsensorResult.setVisibility(View.GONE);
        }

        if (!mExistLight) {
            mtext7.setVisibility(View.GONE);
            mLightResult.setVisibility(View.GONE);
        }

        if (!mExistProximity) {
            mtext8.setVisibility(View.GONE);
            mProximityResult.setVisibility(View.GONE);
        }

        if (!mExistGyroscope) {
            mtext9.setVisibility(View.GONE);
            mtext10.setVisibility(View.GONE);
            mtext11.setVisibility(View.GONE);
            mGyroscopeResult.setVisibility(View.GONE);
        }

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
            if (mExistCompass && (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)) {
                mtext.setText(getString(R.string.compass) + " X:  " + values[0]);
                mtext1.setText(getString(R.string.compass) + " Y:  " + values[1]);
                mtext2.setText(getString(R.string.compass) + " Z:  " + values[2]);
                mCompassUploadTime++;

                if (mCompassUploadTime >= VALUE_CHANGE_TIMES) {
                    mCompassResult.setText(getString(R.string.compass) +
                        getString(R.string.test_pass));
                    mCompassResult.setTextColor(Color.GREEN);
                    testPass(0);
                }
            }

            if (mExistGsensor && (sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {
                mtext3.setText(getString(R.string.gsensor) + " X:  " + values[0]);
                mtext4.setText(getString(R.string.gsensor) + " Y:  " + values[1]);
                mtext5.setText(getString(R.string.gsensor) + " Z:  " + values[2]);
                mGsensorUploadTime++;

                if (mGsensorUploadTime >= VALUE_CHANGE_TIMES) {
                    mGsensorResult.setText(getString(R.string.gsensor) +
                        getString(R.string.test_pass));
                    mGsensorResult.setTextColor(Color.GREEN);
                    testPass(1);
                }
            }

            if (mExistLight && (sensor.getType() == Sensor.TYPE_LIGHT)) {
                mtext7.setText(getString(R.string.light) + " X:  " + values[0]);
				Log.d(LOG_TAG,  "Light, X:  " + values[0]);
				if (Math.abs(mLastLightValue - values[0]) > DIFF ) {
                    mLightUploadTime++;
					mLastLightValue = values[0];
				}								

                if (mLightUploadTime >= VALUE_CHANGE_TIMES) {
                    mLightResult.setText(getString(R.string.light) + getString(R.string.test_pass));
                    mLightResult.setTextColor(Color.GREEN);
                    testPass(2);
                }
            }

            if (mExistProximity && (sensor.getType() == Sensor.TYPE_PROXIMITY)) {
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
                    testPass(3);
                }
            }

            if (mExistGyroscope && (sensor.getType() == Sensor.TYPE_GYROSCOPE)) {
                mtext9.setText(getString(R.string.gyroscope) + " X:  " + values[0]);
                mtext10.setText(getString(R.string.gyroscope) + " Y:  " + values[1]);
                mtext11.setText(getString(R.string.gyroscope) + " Z:  " + values[2]);
                mGyroscopeUploadTime++;

                if (mGyroscopeUploadTime >= VALUE_CHANGE_TIMES) {
                    mGyroscopeResult.setText(getString(R.string.gyroscope) +
                        getString(R.string.test_pass));
                    mGyroscopeResult.setTextColor(Color.GREEN);
                    testPass(4);
                }
            }
        }
    }

    @Override
    protected void onResume() {
    	   if(btn_pass == null){
               View dv = getWindow().getDecorView();
               View btn_bar = dv.findViewById(R.id.btn_bar);
               btn_pass = dv.findViewById(R.id.btn_succ);
               btn_pass.setVisibility(View.GONE);
           }
        super.onResume();

        if (mExistGsensor) {
            mSensorMgr.registerListener(this, mSensor0, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mExistCompass) {
            mSensorMgr.registerListener(this, mSensor1, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mExistLight) {
            mSensorMgr.registerListener(this, mSensor2, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mExistProximity) {
            mSensorMgr.registerListener(this, mSensor3, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mExistGyroscope) {
            mSensorMgr.registerListener(this, mSensor4, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        mSensorMgr.unregisterListener(this);
        super.onStop();
    }

    // @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
    
    public void testPass(int index){
    	  resultFlags[index] = true;
          if(resultFlags[0] && resultFlags[1] && resultFlags[2] && resultFlags[3] && resultFlags[4]){
              btn_pass.setVisibility(View.VISIBLE);
              finishTestcase(TAG_PASS);
          }
    }
}
