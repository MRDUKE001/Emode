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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;


public class GSensorTest extends BaseActivity {
    SensorManager sensorManagerGSensor = null;
    GSensorView mGSensorView = null;
    SensorEventListener sensorEventListenerGSensor = null;
    float x;
    float y;
    float z;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_gsensor);
        try {
            sensorManagerGSensor = (SensorManager) getSystemService(SENSOR_SERVICE);

            Sensor sensor = sensorManagerGSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            final TextView txtX = (TextView) findViewById(R.id.txtGX);
            final TextView txtY = (TextView) findViewById(R.id.txtGY);
            final TextView txtZ = (TextView) findViewById(R.id.txtGZ);

            mGSensorView = new GSensorView(this);

            LinearLayout ll = (LinearLayout) findViewById(R.id.llGsensor);
            ll.addView(mGSensorView);
            sensorEventListenerGSensor = new SensorEventListener() {
                        public void onAccuracyChanged(Sensor arg0, int arg1) {
                            // TODO Auto-generated method stub
                        }

                        public void onSensorChanged(SensorEvent e) {
                            //Log.d("******", "**********onSensorChanged");
                            // TODO Auto-generated method stub
                            x = e.values[SensorManager.DATA_X];
                            y = e.values[SensorManager.DATA_Y];
                            z = e.values[SensorManager.DATA_Z];

                            if (txtX != null) {
                                txtX.setText("x : " + x);
                            }

                            if (txtY != null) {
                                txtY.setText("y : " + y);
                            }

                            if (txtZ != null) {
                                txtZ.setText("z : " + z);
                            }

                            mGSensorView.setPostion(x, y, z);
                            mGSensorView.invalidate();
                        }
                    };
            sensorManagerGSensor.registerListener(sensorEventListenerGSensor, sensor,
                SensorManager.SENSOR_DELAY_GAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GSensorView extends View {
        private Paint mPaint = new Paint();
        private Path mPath = new Path();
        private Paint paint = mPaint;
        private Paint textPaintTitle = new Paint();
        private int width;
        private int height;
        private float x;
        private float y;
        private float z;

        public GSensorView(Context context) {
            super(context);
            this.width = 400;
            this.height = 400;

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
            this.x = -x;
            this.y = y;
            this.z = z;
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawCircle((width / 2) + (x * 10), (height / 2) + (y * 10), Math.abs(z*2), mPaint);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }
}
