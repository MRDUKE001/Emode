package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wind.emode.R;

public class MSensorTest extends BaseActivity {
	private static final String TAG = "Compass";
	private SensorManager mSensorManager;
	private SampleView mView;
	private Sensor mCompass;
	private Sensor mGSensor;
	private Sensor mOrientation;
	
	private float[] mValues;

	private int mTempValue;
	private int mCount = 0;
	View btn_pass = null;
	TextView tx;
	TextView ty;
	TextView tz;

	float x;
	float y;
	float z;

	private final SensorEventListener mListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onSensorChanged(SensorEvent e) {
			// TODO Auto-generated method stub
			if (e.sensor.getType() != Sensor.TYPE_ORIENTATION) {
				return;
			}
			if (Config.LOGD) {
				Log.d(TAG, "sensorChanger(" + e.values[0] + "," + e.values[1] + ","
						+ e.values[2]);
			}
			
			if (mTempValue != (int) e.values[0]) {
				mTempValue = (int) e.values[0];
				mCount++;
			}
			if (mCount == 6) {
				btn_pass.setVisibility(View.VISIBLE);
			}
			
			tx.setText("x :" + e.values[0]);
			ty.setText("y :" + e.values[1]);
			tz.setText("z :" + e.values[2]);
			mValues = e.values;
			if (mView != null) {
				mView.invalidate();
			}

		}

	};

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mView = new SampleView(this);
		setContentView(R.layout.em_compasstest);
		LinearLayout ll = (LinearLayout) findViewById(R.id.Compassssensor);
		ll.addView(mView);

		tx = (TextView) findViewById(R.id.c_txtGX);
		ty = (TextView) findViewById(R.id.c_txtGY);
		tz = (TextView) findViewById(R.id.c_txtGZ);
        mSensorManager.registerListener(mListener,
                mCompass,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mListener,
                mGSensor,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mListener,
                mOrientation,
                SensorManager.SENSOR_DELAY_GAME);
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
		mSensorManager.unregisterListener(mListener, mCompass);
		mSensorManager.unregisterListener(mListener, mGSensor);
		mSensorManager.unregisterListener(mListener, mOrientation);
                super.onDestroy();
	}

	private class SampleView extends View {
		private Paint mPaint = new Paint();
		private Path mPath = new Path();
		private boolean mAnimate;
		private long mNextTime;

		public SampleView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

			mPath.moveTo(0, -30);
			mPath.lineTo(-20, 40);
			mPath.lineTo(0, 30);
			mPath.lineTo(20, 40);
			mPath.close();
		}

		protected void onDraw(Canvas canvas) {
			Paint paint = mPaint;
			canvas.drawColor(Color.BLACK);

			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);

			int w = canvas.getWidth();
			int h = canvas.getHeight();
			int cx = w / 4;
			int cy = h / 4;

			canvas.translate(cx, cy);
			if (mValues != null) {
				canvas.rotate(-mValues[0]);
			}
			canvas.drawPath(mPath, mPaint);

		}

		protected void onAttachedToWindow() {
			mAnimate = true;
			super.onAttachedToWindow();
		}

		protected void onDetachedFromWindow() {
			mAnimate = false;
			super.onDetachedFromWindow();
		}

	}

}
