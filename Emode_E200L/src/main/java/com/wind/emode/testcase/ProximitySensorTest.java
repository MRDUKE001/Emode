package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import com.wind.emode.utils.Log;
import android.view.View;
import android.view.Window;
import com.wind.emode.R;

public class ProximitySensorTest extends BaseActivity implements SensorEventListener {
	TextView mtitle;
	TextView mtext;
	Button mbutton;

	SensorManager sm;
	Sensor mSensor;

	protected static final String LOG_TAG = "ProximitySensorTest";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_sensor_proximity);

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		mtext = (TextView) findViewById(R.id.m0_t);
		mtext.setText(getString(R.string.proximity) + " X : 9.0");

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
			if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
				mtext.setText(getString(R.string.proximity) + " X:  " + values[0]);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
}