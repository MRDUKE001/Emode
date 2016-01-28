package com.wind.emode.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

public class Rectangle {

    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mRectPaint;
    private Paint mPaint;
    private int isGreen[];
    private int mWidth;
    private int mHeight;
    private int positionx[];
    private int positiony[];
    private Rect[] mRectF;
    private int mGridCount;
    private float mX, mY;
    private boolean mIsAllTouch = false;
    private int mOffsetX;
    private int mOffsetY;
    private int mSectionWidth;
    private int mSectionHeight;

    public Rectangle(int wp, int hp, int w, int h, int ox, int oy){
        mWidth = w;
        mHeight = h;
        mOffsetX = ox;
        mOffsetY = oy;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mRectPaint = new Paint();
        mRectPaint.setColor(Color.BLUE);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mGridCount = (wp + hp - 2) * 2;
        isGreen =new int[mGridCount];
        for(int i=0; i<mGridCount; i++){
            isGreen[i] = 0;
        }
        positionx = new int[wp + 1];
        positiony = new int[hp + 1];
        mSectionWidth = mWidth / wp;
        for(int i = 0; i < wp + 1; i ++){
            positionx[i] = i * mWidth / wp;
        }
        positionx[wp] = positionx[wp] - 1;
        mSectionHeight = mHeight / hp;
        for(int i = 0; i < hp + 1; i ++){
            positiony[i] = i * mHeight / hp;
        }
        positiony[hp] = positiony[hp] - 1;
        mRectF= new Rect[mGridCount];
        int k = 0;
        int pIndex = wp;
        for(; k < pIndex; k ++){
            mRectF[k] = new Rect(positionx[k], positiony[0], positionx[k + 1], positiony[1]);
        }
        pIndex = k + hp - 2;
        for(; k < pIndex; k ++){
            mRectF[k] = new Rect(positionx[0], positiony[k - wp + 1], positionx[1], positiony[k - wp + 2]);
        }
        pIndex = k + wp;
        int pp = wp + hp -2;
        for(; k < pIndex; k ++){
            mRectF[k] = new Rect(positionx[k - pp], positiony[hp - 1], positionx[k -pp + 1], positiony[hp]);
        }
        pIndex = k + hp - 2;
        pp += wp;
        for(; k < pIndex; k ++){
            mRectF[k] = new Rect(positionx[wp - 1], positiony[k - pp + 1], positionx[wp], positiony[k - pp + 2]);
        }
    }

    private boolean isInArea(float x, float y){
        if((x > mOffsetX && x < mOffsetX + mSectionWidth) || (x > mOffsetX + mWidth - mSectionWidth && x < mOffsetX + mWidth)
                || (y > mOffsetY && y < mOffsetY + mSectionHeight) || (y > mOffsetY + mHeight - mSectionHeight && y < mOffsetY + mHeight)){
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, mOffsetY);
        canvas.drawColor(0x0000AAAA);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        for(int i=0; i<mGridCount; i++){
            canvas.drawRect(mRectF[i], mRectPaint);
        }
        canvas.restore();
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
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mPath.reset();
    }

    private void onTouchJuage(float x, float y){
        for(int i=0; i<mGridCount; i++){
            if(isInRect(x,y,mRectF[i])){
                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawRect(mRectF[i], mPaint);
                isGreen[i]=1;
            }
        }
    }

    private boolean isInRect(float x, float y, Rect r){
        if(x >r.left && x < r.right && y >r.top && y < r.bottom){
            return true;
        }
        return false;
    }

    public void processTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isInArea(x, y)) {
            x -= mOffsetX;
            y -= mOffsetY;

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
            }
            onTouchJuage(x, y);
        }
    }

    public boolean isAllTouch(){
        if(!mIsAllTouch){
            int times = 0;
            for (int j = 0; j < mGridCount; j++) {
                if (isGreen[j] == 1) {
                    times++;
                }
            }
            if (times == mGridCount) {
                mIsAllTouch = true;
            }
        }
        return mIsAllTouch;
    }
}
