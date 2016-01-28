package com.wind.emode.testcase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;


public class LCDTest extends BaseActivity {
    protected static final String LOG_TAG = "EMODE_ColorTest";
    MyView t0;
    View btn_bar = null;
    private int what = 0;
    int delay = 1000;
    private boolean mManualTest;
    private int preX;
    
    public Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                handler.removeMessages(0);
                handler.removeMessages(1);
                handler.removeMessages(2);
                handler.removeMessages(3);
                handler.removeMessages(4);
                handler.removeMessages(5);

                switch (msg.what) {
                case 0:
                    //t0.setBackgroundColor(Color.WHITE);
                    what = 0;
                    t0.invalidate();
                    //				if (EMflag != ManulDetect)
                    handler.sendEmptyMessageDelayed(1, delay);

                    break;

                case 1:
                    //t0.setBackgroundColor(Color.RED);
                    what = 1;
                    t0.invalidate();
                    //				if (EMflag != ManulDetect)
                    handler.sendEmptyMessageDelayed(2, delay);

                    break;

                case 2:
                    //t0.setBackgroundColor(Color.GREEN);
                    what = 2;
                    t0.invalidate();
                    //				if (EMflag != ManulDetect)
                    handler.sendEmptyMessageDelayed(3, delay);

                    break;

                case 3:
                    //t0.setBackgroundColor(Color.BLUE);
                    what = 3;
                    t0.invalidate();
                    //				if (EMflag != ManulDetect)
                    handler.sendEmptyMessageDelayed(4, delay);

                    break;

                case 4:
                    //t0.setBackgroundColor(Color.BLACK);
                    what = 4;
                    t0.invalidate();
                    btn_bar.setVisibility(View.GONE);
                    //				if (EMflag != ManulDetect)
                    handler.sendEmptyMessageDelayed(5, delay);

                    break;

                case 5:
                    what = 5;
                    t0.invalidate();
                    btn_bar.setVisibility(View.VISIBLE);
                    //finish();
                    break;
                }
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t0 = new MyView(this);
        setContentView(t0); //R.layout.em_color_lcd
        getWindow().setBackgroundDrawable(null);
        mManualTest = getResources().getBoolean(R.bool.config_manual_lcd_test);

        //t0 = (ImageView) findViewById(R.id.ct_t0);
        //t0.setBackgroundColor(Color.WHITE);
        if (!mManualTest) {
            handler.sendEmptyMessageDelayed(1, delay);
        } else {
            Toast.makeText(this, R.string.tip_color_test, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        if (btn_bar == null) {
            btn_bar = getWindow().getDecorView().findViewById(R.id.btn_bar);
            btn_bar.setVisibility(View.GONE);
        }
        super.onResume();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:

            if (mManualTest) {
                if (what < 5) {
                    what++;

                    if (what == 5) {
                        btn_bar.setVisibility(View.VISIBLE);
                        finishTestcase(TAG_PASS);
                    }
                } else {
                    what = 5;
                }

                t0.invalidate();
            }

            return true;

        default:
            return super.onKeyDown(keyCode, event);
        }
    }*/
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	int x = (int)event.getX();
    	int action = event.getAction();
    	switch(action){
    	case MotionEvent.ACTION_DOWN:
    		preX = x;
    		break;
    	case MotionEvent.ACTION_UP:
    		if(x > preX + 200){
                    if (what > 0)
                        what--;
                    if(what == 4){
                        btn_bar.setVisibility(View.GONE);
                    }
                    t0.invalidate();    			
    		}else if(x < preX - 200){
                    if (what < 5) {
                        what ++;
                        if(what == 5){
                            btn_bar.setVisibility(View.VISIBLE);
                        }
                    }else{
                        what = 5;
                    }
                    t0.invalidate();
    		}
    		break;
    	}
    	return super.onTouchEvent(event);
    }


    private class MyView extends View {
        private int left;
        private int top;
        private Bitmap bm;

        public MyView(Context context) {
            super(context);

            Resources res = getResources();
            TypedValue value = new TypedValue();
            res.openRawResource(R.raw.gray_level, value);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTargetDensity = value.density;
            bm = BitmapFactory.decodeResource(res, R.raw.gray_level, opts);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            left = (dm.widthPixels - bm.getWidth()) / 2;
            top = (dm.heightPixels - bm.getHeight()) / 2;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            switch (what) {
            case 0:
                canvas.drawColor(Color.WHITE);

                break;

            case 1:
                canvas.drawColor(Color.RED);

                break;

            case 2:
                canvas.drawColor(Color.GREEN);

                break;

            case 3:
                canvas.drawColor(Color.BLUE);

                break;

            case 4:
                canvas.drawColor(Color.BLACK);

                break;

            case 5:

                if (false) {
                    canvas.drawBitmap(bm, left, top, null);
                } else {
                    canvas.drawColor(Color.BLACK);
                }

                break;
            }

            super.onDraw(canvas);
        }
    }
}
