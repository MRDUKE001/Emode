package com.wind.emode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.wind.emode.view.MyLineView;
import com.wind.emode.testcase.TouchScreenTest;
import com.wind.emode.utils.Log;

public class MyPointView extends MyView {
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private int[] times = new int[5];
    private Paint mPaintPoint;
    private int mRadius;
    private float[] positionx = new float[3];
    private float[] positiony = new float[3];
    private float mX;
    private float mY;

    public MyPointView(Context c) {
        super(c);

        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);        

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        
        mRadius = mScreenWidth / 13;
        int dxy = mScreenWidth / 9;
        int x1 = dxy;
        int x3 = mScreenWidth - dxy;
        int x2 = ((x3 - x1) / 2) + x1;
        int y1 = dxy;
        int y3 = mScreenHeight - dxy;
        int y2 = ((y3 - y1) / 2) + y1;
        positionx[0] = x1;
        positionx[1] = x2;
        positionx[2] = x3;
        positiony[0] = y1;
        positiony[1] = y2;
        positiony[2] = y3;
        
        for (int i = 0; i < 5; i++) {
            times[i] = 0;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x0000AAAA);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);        

        mPaintPoint = new Paint();
        mPaintPoint.setAntiAlias(true);
        mPaintPoint.setColor(Color.YELLOW);
        mPaintPoint.setStyle(Paint.Style.STROKE);
        
        canvas.drawCircle(positionx[0], positiony[0], mRadius, mPaintPoint);
        canvas.drawCircle(positionx[2], positiony[0], mRadius, mPaintPoint);
        canvas.drawCircle(positionx[1], positiony[1], mRadius, mPaintPoint);
        canvas.drawCircle(positionx[0], positiony[2], mRadius, mPaintPoint);
        canvas.drawCircle(positionx[2], positiony[2], mRadius, mPaintPoint);

    }

    private void touch_start(float x, float y) {
        mX = x;
        mY = y;
    }

    private void touch_up() {
        mCanvas.drawPoint(mX, mY, mPaint);

        double length;
        int point;

        double length1 = countLength(mX, mY, positionx[0], positiony[0]);
        length = length1;
        point = 1;

        double length2 = countLength(mX, mY, positionx[2], positiony[0]);

        if (length2 < length) {
            length = length2;
            point = 2;
        }

        double length3 = countLength(mX, mY, positionx[1], positiony[1]);

        if (length3 < length) {
            length = length3;
            point = 3;
        }

        double length4 = countLength(mX, mY, positionx[0], positiony[2]);

        if (length4 < length) {
            length = length4;
            point = 4;
        }

        double length5 = countLength(mX, mY, positionx[2], positiony[2]);

        if (length5 < length) {
            length = length5;
            point = 5;
        }

        float pos_x = 0;
        float pos_y = 0;

        if (length >= mRadius) {
            mPaintPoint.setColor(Color.RED);
            times[point - 1] = 0;
        } else {
            mPaintPoint.setColor(Color.GREEN);
            times[point - 1] = 1;
        }

        mPaintPoint.setStyle(Paint.Style.FILL);

        switch (point) {
        case 1:
            pos_x = positionx[0];
            pos_y = positiony[0];

            break;

        case 2:
            pos_x = positionx[2];
            pos_y = positiony[0];

            break;

        case 3:
            pos_x = positionx[1];
            pos_y = positiony[1];

            break;

        case 4:
            pos_x = positionx[0];
            pos_y = positiony[2];

            break;

        case 5:
            pos_x = positionx[2];
            pos_y = positiony[2];

            break;

        default:
            break;
        }

        mCanvas.drawCircle(pos_x, pos_y, mRadius, mPaintPoint);
    }


    private double countLength(float x1, float y1, float x2, float y2) {
        double b;
        float s;
        s = ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2));
        b = Math.sqrt((double) s);

        return b;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.i(TouchScreenTest.TAG, "onTouchEvent, point, action: " + event.getAction() + ", x: " + x + ", y: " + y);
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            invalidate();

            break;

        case MotionEvent.ACTION_UP:            
            touch_up();
            invalidate();            

            int item = 0;

            for (int j = 0; j < 5; j++) {
                if (times[j] == 1) {
                    item++;
                }
            }

            if (item == 5) {
                if (mListener != null) {
                    mListener.onTestFinished(mTestStage);
                }
            }

            break;
        }

        return true;
    }
}
