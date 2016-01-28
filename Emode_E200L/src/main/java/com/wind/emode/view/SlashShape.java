package com.wind.emode.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

public class SlashShape {

    private int LENGTH = 26;
    private int mHeight;
    private int mWidth;
    private int mPx;
    private int mPy;
    private Paint mLinePaint = new Paint();
    private Paint mRegionPaint = new Paint();
    private Paint mTouchPaint = new Paint();
    private int[] mBackslashesPoints;
    private int[] mSlashPoints;
    private int mPointCount;
    private boolean[] mLeftPassAreas;
    private boolean[] mRightPassAreas;
    private int[] mRegionPos = new int[8];
    //private Path mTouchPath = new Path();
    private boolean mIsAllTouch = false;
    private int mOffsetX;
    private int mOffsetY;

    public SlashShape(int w, int h, int ul, int ox, int oy){
        LENGTH = ul;//getResources().getInteger(R.integer.region_length);
        mWidth = w;
        mHeight = h;
        mOffsetX = ox;
        mOffsetY = oy;

        mLinePaint.setColor(Color.RED);
        mRegionPaint.setColor(Color.GREEN);
        mTouchPaint.setColor(Color.WHITE);
        mTouchPaint.setAntiAlias(true);
        mTouchPaint.setStyle(Paint.Style.STROKE);
        mTouchPaint.setStrokeCap(Paint.Cap.ROUND);
        mTouchPaint.setStrokeWidth(2);
        mPointCount = mHeight / LENGTH * 4;
        mBackslashesPoints = new int[mPointCount];
        mSlashPoints = new int[mPointCount];
        mLeftPassAreas = new boolean[mPointCount / 4];
        mRightPassAreas = new boolean[mPointCount / 4];
        int count = mPointCount / 4;
        int j = 0;
        int pl = mWidth / (mHeight / LENGTH);
        for(int i = 0; i < count; i ++){
            j = i * 4;
            mBackslashesPoints[j] = i * pl;
            mBackslashesPoints[j + 1] = (i + 1) * LENGTH;
            mBackslashesPoints[j + 2] = LENGTH + i * pl;
            mBackslashesPoints[j + 3] = i * LENGTH;
            mSlashPoints[j] = mWidth - LENGTH - i * pl;
            mSlashPoints[j + 1] = i * LENGTH;
            mSlashPoints[j + 2] = mWidth - i * pl;
            mSlashPoints[j + 3] = (i + 1) * LENGTH;
        }
    }

    private boolean isPtInPolygon() {
        int nCross = 0;

        for (int i = 0; i < 4; i++) {
            int p1x = mRegionPos[i * 2];
            int p1y = mRegionPos[i * 2 + 1];
            int p2x = mRegionPos[(i + 1) % 4 * 2];
            int p2y = mRegionPos[(i + 1) % 4 * 2 + 1];

            if (p1y == p2y)
                continue;

            if (mPy < Math.min(p1y, p2y))
                continue;
            if (mPy >= Math.max(p1y, p2y))
                continue;

            double x = (double) (mPy - p1y) * (double) (p2x - p1x)
                    / (double) (p2y - p1y) + p1x;

            if (x > mPx)
                nCross++;
        }
        return (nCross % 2 == 1);
    }

    private void checkContains(int pos){
        int limit = mHeight / LENGTH - 1;
        if(pos > limit){
            pos = limit;
        }
        int index = pos * 4;
        mRegionPos[0] = mBackslashesPoints[index];
        mRegionPos[1] = mBackslashesPoints[index + 1];
        boolean isVertex = (pos == 0);
        mRegionPos[2] = isVertex ? 0 : mBackslashesPoints[index - 4];
        mRegionPos[3] = isVertex ? 0 : mBackslashesPoints[index - 3];
        mRegionPos[4] = mBackslashesPoints[index + 2];
        mRegionPos[5] = mBackslashesPoints[index + 3];
        isVertex = (pos == limit);
        mRegionPos[6] = isVertex ? mWidth : mBackslashesPoints[index + 6];
        mRegionPos[7] = isVertex ? mHeight : mBackslashesPoints[index + 7];
        if(isPtInPolygon()){
            mLeftPassAreas[pos] = true;
        }
        mRegionPos[0] = mSlashPoints[index];
        mRegionPos[1] = mSlashPoints[index + 1];
        isVertex = (pos == 0);
        mRegionPos[2] = isVertex ? mWidth : mSlashPoints[index - 2];
        mRegionPos[3] = isVertex ? 0 : mSlashPoints[index - 1];
        mRegionPos[4] = mSlashPoints[index + 2];
        mRegionPos[5] = mSlashPoints[index + 3];
        isVertex = (pos == limit);
        mRegionPos[6] = isVertex ? 0 : mSlashPoints[index + 4];
        mRegionPos[7] = isVertex ? mHeight : mSlashPoints[index + 5];
        if(isPtInPolygon()){
            mRightPassAreas[pos] = true;
        }
    }

    public void processTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        if(x < mOffsetX || y < mOffsetY || x > mOffsetX + mWidth || y > mOffsetY + mHeight){
            return;
        }
        int action = event.getAction();
        mPx = x - mOffsetX;
        mPy = y - mOffsetY;
        if(action == MotionEvent.ACTION_MOVE){
            //case MotionEvent.ACTION_DOWN:
                //mTouchPath.moveTo(mPx, mPy);
            //    break;
            //case MotionEvent.ACTION_MOVE:
                //mTouchPath.lineTo(mPx, mPy);
                checkContains(mPy / LENGTH);
            //    break;
        }
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, mOffsetY);
        canvas.drawLine(mBackslashesPoints[0], mBackslashesPoints[1],
                mBackslashesPoints[mPointCount - 4], mBackslashesPoints[mPointCount - 3], mLinePaint);
        canvas.drawLine(mBackslashesPoints[2], mBackslashesPoints[3],
                mBackslashesPoints[mPointCount - 2], mBackslashesPoints[mPointCount - 1], mLinePaint);
        canvas.drawLine(mSlashPoints[0], mSlashPoints[1],
                mSlashPoints[mPointCount - 4], mSlashPoints[mPointCount - 3], mLinePaint);
        canvas.drawLine(mSlashPoints[2], mSlashPoints[3],
                mSlashPoints[mPointCount - 2], mSlashPoints[mPointCount - 1], mLinePaint);
        int count = mPointCount - 4;
        for(int i = 0; i < count; i += 4){
            canvas.drawLine(mBackslashesPoints[i], mBackslashesPoints[i + 1],
                    mBackslashesPoints[i + 6], mBackslashesPoints[i + 7], mLinePaint);
            canvas.drawLine(mSlashPoints[i + 4], mSlashPoints[i + 5],
                    mSlashPoints[i + 2], mSlashPoints[i + 3], mLinePaint);
        }
        int len = mPointCount / 4;
        boolean isVertex = false;
        int index = 0;
        int p1x = 0;
        int p1y = 0;
        int p2x = 0;
        int p2y = 0;
        int p3x = 0;
        int p3y = 0;
        int p4x = 0;
        int p4y = 0;
        for (int i = 0; i < len; i++) {
            index = i * 4;
            if (mLeftPassAreas[i]) {
                p1x = mBackslashesPoints[index];
                p1y = mBackslashesPoints[index + 1];
                isVertex = (i == 0);
                p2x = isVertex ? 0 : mBackslashesPoints[index - 4];
                p2y = isVertex ? 0 : mBackslashesPoints[index - 3];
                p3x = mBackslashesPoints[index + 2];
                p3y = mBackslashesPoints[index + 3];
                isVertex = (i >= (mHeight / LENGTH - 1));
                p4x = isVertex ? mWidth : mBackslashesPoints[index + 6];
                p4y = isVertex ? mHeight : mBackslashesPoints[index + 7];
                canvas.drawLine(p1x, p1y, p2x, p2y, mRegionPaint);
                canvas.drawLine(p2x, p2y, p3x, p3y, mRegionPaint);
                canvas.drawLine(p3x, p3y, p4x, p4y, mRegionPaint);
                canvas.drawLine(p4x, p4y, p1x, p1y, mRegionPaint);
            }
            if (mRightPassAreas[i]) {
                p1x = mSlashPoints[index];
                p1y = mSlashPoints[index + 1];
                isVertex = (i == 0);
                p2x = isVertex ? mWidth : mSlashPoints[index - 2];
                p2y = isVertex ? 0 : mSlashPoints[index - 1];
                p3x = mSlashPoints[index + 2];
                p3y = mSlashPoints[index + 3];
                isVertex = (i >= (mHeight / LENGTH - 1));
                p4x = isVertex ? 0 : mSlashPoints[index + 4];
                p4y = isVertex ? mHeight : mSlashPoints[index + 5];
                canvas.drawLine(p1x, p1y, p2x, p2y, mRegionPaint);
                canvas.drawLine(p2x, p2y, p3x, p3y, mRegionPaint);
                canvas.drawLine(p3x, p3y, p4x, p4y, mRegionPaint);
                canvas.drawLine(p4x, p4y, p1x, p1y, mRegionPaint);
            }
        }
        //canvas.drawPath(mTouchPath, mTouchPaint);
        canvas.restore();
    }

    public boolean isAllTouch(){
        int len = mPointCount / 4;
        int flag = 0;
        for (int i = 0; i < len; i++) {
            if (mLeftPassAreas[i]) {
                flag ++;
            }
            if (mRightPassAreas[i]) {
                flag ++;
            }
        }
        if(flag * 2 == mPointCount){
            return true;
        }else{
            return false;
        }
    }
}
