package com.wind.emode.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.wind.emode.R;

public class MyComplexView extends MyView {
    private Rectangle[] mRectangles;
    private SlashShape mSlashShape;
    private Context mActivity;
    private boolean mHasSlash;
    public MyComplexView(Context c){
        super(c);
        int w = mScreenWidth;
        int h = mScreenHeight/* + getResources().getInteger(R.integer.navegation_height)*/;
        int wp = getResources().getInteger(R.integer.config_tp_test_rect_section_width);
        int hp = getResources().getInteger(R.integer.config_tp_test_rect_section_height);
        float uw = w*1.0f/wp;
        float uh = h*1.0f/hp;
        mRectangles = new Rectangle[2];
        mRectangles[0] = new Rectangle(wp, hp, (int)(uw*(wp)), (int)(uh*(hp)), 0, 0);
	    mRectangles[1] = new Rectangle((wp-2)/2, (hp-2)/2, (int)(uw*(wp-2)), (int)(uh*(hp-2)), (int)uw, (int)uh);
        mHasSlash = getResources().getBoolean(R.bool.config_tp_test_has_slash);
        if(mHasSlash){
            int offsetX = (int)(2*uw);
            int offsetY = (int)(2*uh);
            mSlashShape = new SlashShape(w - offsetX*2, h - offsetY*2, 26, offsetX, offsetY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].processTouchEvent(event);
        }
        if(mHasSlash){
            mSlashShape.processTouchEvent(event);
        }
        invalidate();
        if(event.getAction() == MotionEvent.ACTION_UP){
            boolean flag = true;
            for(int i = 0; i < len; i ++){
                if(!mRectangles[i].isAllTouch()){
                    flag = false;
                    break;
                }
            }
            if(flag && mHasSlash){
                flag &= mSlashShape.isAllTouch();
            }
            if(flag){
               if (mListener != null) {
                   mListener.onTestFinished(mTestStage);
               }
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int len = mRectangles.length;
        for(int i = 0; i < len; i ++){
            mRectangles[i].draw(canvas);
        }
        if(mHasSlash){
            mSlashShape.draw(canvas);
        }
    }
}
