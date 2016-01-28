package com.wind.emode.testcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.wind.emode.utils.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;


public class GyroscopeSensorTest extends BaseActivity {
	SensorManager sensorManager = null;
	GyroscopeView mGyroscopeView = null;
	SensorEventListener sensorEventListener = null;
	Sensor mGyroscopeSensor;
	Sensor mCompassSensor;
	boolean mMSensorSupportGyroscope;
	float x;
	float y;
	float z;
	
	private int mTempValue;
	private int mCount = 0;
	View btn_pass = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("#########", "OnCreate");
		setContentView(R.layout.em_gyroscope);
		mMSensorSupportGyroscope = getResources().getBoolean(R.bool.config_magneticfield_sensor_support_gyroscope);
//		try {
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                        if (mMSensorSupportGyroscope) {
	                        mCompassSensor = sensorManager
						.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                        }
			mGyroscopeSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			final TextView txtX = (TextView) findViewById(R.id.txtGX);
			final TextView txtY = (TextView) findViewById(R.id.txtGY);
			final TextView txtZ = (TextView) findViewById(R.id.txtGZ);
			
			mGyroscopeView = new GyroscopeView(this);
			LinearLayout ll = (LinearLayout) findViewById(R.id.llGsensor);
			ll.addView(mGyroscopeView);
			sensorEventListener= new SensorEventListener() {

				public void onAccuracyChanged(Sensor arg0, int arg1) {
					// TODO Auto-generated method stub

				}

				public void onSensorChanged(SensorEvent e) {
					//Log.d("******", "**********onSensorChanged");
					// TODO Auto-generated method stub
					if (e.sensor.getType()  !=  Sensor.TYPE_GYROSCOPE) {
					    return;
					}
					
					if (mTempValue != (int) e.values[0]) {
						mTempValue = (int) e.values[0];
						mCount++;
					}
					if (mCount == 6) {
						btn_pass.setVisibility(View.VISIBLE);
					}
					
					x = e.values[0];
					y = e.values[1];
					z = e.values[2];

					if (txtX != null) {
						txtX.setText("x :" + x);
					}
					if (txtY != null) {
						txtY.setText("y :" + y);
					}
					if (txtZ != null) {
						txtZ.setText("z :" + z);
					}
					mGyroscopeView.setPostion(x, y, z);
					mGyroscopeView.invalidate();
				}

			};
			if (mMSensorSupportGyroscope) {
				sensorManager.registerListener(sensorEventListener,
						mCompassSensor, SensorManager.SENSOR_DELAY_GAME);
			}
			sensorManager.registerListener(sensorEventListener,
					mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	protected void onResume() {
		if (btn_pass == null) {
			View dv = getWindow().getDecorView();
			View btn_bar = dv.findViewById(R.id.btn_bar);
			btn_pass = dv.findViewById(R.id.btn_succ);
			btn_pass.setVisibility(View.GONE);
		}
		super.onResume();
	}
	
    @Override
    protected void onDestroy() {
        if (mMSensorSupportGyroscope) {
            sensorManager.unregisterListener(sensorEventListener, mCompassSensor);
        }
        sensorManager.unregisterListener(sensorEventListener, mGyroscopeSensor);
        super.onDestroy();
    }

	private class GyroscopeView extends View {
		private Paint mPaint = new Paint();
		private Path mPath = new Path();
		private Paint paint = mPaint;
		private Paint textPaintTitle = new Paint();

		private int width;
		private int height;

		private float x;
		private float y;
		private float z;

		public GyroscopeView(Context context) {
			super(context);
			this.width = 200;
			this.height = 200;

			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			textPaintTitle.setColor(Color.WHITE);
			textPaintTitle.setTextSize(20);

			mPath.moveTo(0, -50);
			mPath.lineTo(-20, 60);
			mPath.lineTo(0, 50);
			mPath.lineTo(20, 60);
			mPath.close();
		}

		public void setPostion(float x, float y, float z) {
			this.x = Math.abs(x);
			this.y = Math.abs(y);
			this.z = Math.abs(z);
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawCircle(width / 2 + x * 100, height / 2 + y * 100, z*100,
					mPaint);
		}

		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
		}

		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
		}
	}
}
