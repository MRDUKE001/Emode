package com.wind.emode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.wind.emode.testcase.TouchScreenTest;
import com.wind.emode.utils.Log;

public class MyLineView extends MyView {
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;
    private Paint mBitmapPaint;
    private int[] isGreen = new int[32];

    private Rect[] mRectF;
    private float mX;
    private float mY;
    public MyLineView(Context c) {
        super(c);

        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        
        for (int i = 0; i < 32; i++) {
            isGreen[i] = 0;
        }
        
        int x1 = (0 * mScreenWidth) / 7;
        int x2 = ((1 * mScreenWidth) / 7) - 1;
        int x3 = ((2 * mScreenWidth) / 7) - 1;
        int x4 = ((3 * mScreenWidth) / 7) - 1;
        int x5 = ((4 * mScreenWidth) / 7) - 1;
        int x6 = ((5 * mScreenWidth) / 7) - 1;
        int x7 = ((6 * mScreenWidth) / 7) - 1;
        int x8 = ((7 * mScreenWidth) / 7) - 1;
        int y1 = (0 * mScreenHeight) / 11;
        int y2 = ((1 * mScreenHeight) / 11) - 1;
        int y3 = ((2 * mScreenHeight) / 11) - 1;
        int y4 = ((3 * mScreenHeight) / 11) - 1;
        int y5 = ((4 * mScreenHeight) / 11) - 1;
        int y6 = ((5 * mScreenHeight) / 11) - 1;
        int y7 = ((6 * mScreenHeight) / 11) - 1;
        int y8 = ((7 * mScreenHeight) / 11) - 1;
        int y9 = ((8 * mScreenHeight) / 11) - 1;
        int y10 = ((9 * mScreenHeight) / 11) - 1;
        int y11 = ((10 * mScreenHeight) / 11) - 1;
        int y12 = ((11 * mScreenHeight) / 11) - 1;
        
        mRectF = new Rect[] {
                new Rect(x1, y1, x2, y2), new Rect(x2, y1, x3, y2), new Rect(x3, y1, x4, y2),
                new Rect(x4, y1, x5, y2), new Rect(x5, y1, x6, y2), new Rect(x6, y1, x7, y2),
                new Rect(x7, y1, x8, y2),
                
                new Rect(x1, y2, x2, y3), new Rect(x1, y3, x2, y4), new Rect(x1, y4, x2, y5),
                new Rect(x1, y5, x2, y6), new Rect(x1, y6, x2, y7), new Rect(x1, y7, x2, y8),
                new Rect(x1, y8, x2, y9), new Rect(x1, y9, x2, y10), new Rect(x1, y10, x2, y11),
                
                new Rect(x1, y11, x2, y12), new Rect(x2, y11, x3, y12), new Rect(x3, y11, x4, y12),
                new Rect(x4, y11, x5, y12), new Rect(x5, y11, x6, y12), new Rect(x6, y11, x7, y12),
                new Rect(x7, y11, x8, y12),
                
                new Rect(x7, y2, x8, y3), new Rect(x7, y3, x8, y4), new Rect(x7, y4, x8, y5),
                new Rect(x7, y5, x8, y6), new Rect(x7, y6, x8, y7), new Rect(x7, y7, x8, y8),
                new Rect(x7, y8, x8, y9), new Rect(x7, y9, x8, y10), new Rect(x7, y10, x8, y11)
            };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x0000AAAA);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 32; i++) {
            canvas.drawRect(mRectF[i], paint);
        }
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if ((dx >= TOUCH_TOLERANCE) || (dy >= TOUCH_TOLERANCE)) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.i(TouchScreenTest.TAG, "onTouchEvent, line, action: " + event.getAction() + ", x: " + x + ", y: " + y);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            invalidate();

            break;

        case MotionEvent.ACTION_MOVE:
            touch_move(x, y);
            invalidate();

            break;

        case MotionEvent.ACTION_UP:
            touch_up();
            invalidate();
        
            int times = 0;

            for (int j = 0; j < 32; j++) {
                if (isGreen[j] == 1) {
                    times++;
                }
            }

            if (times == 32) {
                if (mListener != null) {
                    mListener.onTestFinished(mTestStage);
                }

            }


            break;
        }

        onTouchJuage(x, y);

        return true;
    }

    private void onTouchJuage(float x, float y) {
        for (int i = 0; i < 32; i++) {
            if (isInRect(x, y, mRectF[i])) {
                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawRect(mRectF[i], mPaint);
                isGreen[i] = 1;
            }
        }
    }

    private boolean isInRect(float x, float y, Rect r) {
        if ((x > r.left) && (x < r.right) && (y > r.top) && (y < r.bottom)) {
            return true;
        }

        return false;
    }
}
