
package com.wind.emode.testcase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;
import com.wind.emode.view.MyComplexView;
import com.wind.emode.view.MyPointView;
import com.wind.emode.view.MyView;

import java.util.*;


public class TouchScreenTest extends BaseActivity implements MyView.Listener {
    public static final String TAG = "TouchScreenTest";
    
    private static final int TEST_STAGE_POINT = 0;
    private static final int TEST_STAGE_LINE = 1;
    
    private View mBtnPass = null;
    private MyView mPointView;
    private MyView mComplexView;
        
    //	
    //	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;        
        if (getResources().getBoolean(R.bool.config_has_navigation_bar)) {            
            systemUiFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
        mPointView = new MyPointView(this);
        mPointView.setListener(this, TEST_STAGE_POINT);
        setContentView(mPointView);        
    }

    @Override
    protected void onResume() {
        if (mBtnPass == null) {
            View dv = getWindow().getDecorView();
            View btn_bar = dv.findViewById(R.id.btn_bar);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) btn_bar.getLayoutParams();
            lp.bottomMargin += 100;
            lp.rightMargin += 200;
            mBtnPass = dv.findViewById(R.id.btn_succ);
            mBtnPass.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Override
    public void onTestFinished(int stage) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onTestFinished, stage = " + stage);
        if (stage == TEST_STAGE_POINT) {
            mComplexView = new MyComplexView(this);            
            mComplexView.setListener(this, TEST_STAGE_LINE);
            setContentView(mComplexView);
        } else if (stage == TEST_STAGE_LINE) {
            mBtnPass.callOnClick();
        }
    }



	
}

