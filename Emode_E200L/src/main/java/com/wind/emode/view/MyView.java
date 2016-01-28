package com.wind.emode.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import com.wind.emode.testcase.TouchScreenTest;
import com.wind.emode.utils.Log;
import com.wind.emode.R;

public class MyView extends View {

    public interface Listener {
        void onTestFinished(int stage);
    }
    
    protected Listener mListener;
    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;
    protected int mNavigationBarHeight;
    protected int mTestStage = 0;
    
    public MyView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        DisplayMetrics dm = new DisplayMetrics();
        if (context instanceof Activity) {
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        } 
        if (context.getResources().getBoolean(R.bool.config_has_navigation_bar)) {
            mNavigationBarHeight = context.getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        } else {
            mNavigationBarHeight = 0;
        }
        mScreenHeight += mNavigationBarHeight;
    }
    
    public void setListener(Listener l, int stage) {
        mListener = l;
        mTestStage = stage;
    }


}
