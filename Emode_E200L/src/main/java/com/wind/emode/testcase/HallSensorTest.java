package com.wind.emode.testcase;

import android.app.Activity;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;


public class HallSensorTest extends BaseActivity {
    private Button mBtn;
    private TextView mTextOpen;
    private TextView mTextClose;
	View btn_pass = null;
	boolean flags[] = { false, false };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_HALL_TEST);
        setContentView(R.layout.em_hall_sensor);
        mTextOpen = (TextView) findViewById(R.id.m0_t1);
        mTextClose = (TextView) findViewById(R.id.m0_t2);
        mBtn = (Button) findViewById(R.id.m0_b);
        mBtn.setText("ok");
        mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
    }

    @Override
    protected void onResume() {
    	if (btn_pass == null) {
			View dv = getWindow().getDecorView();
			View btn_bar = dv.findViewById(R.id.btn_bar);
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) btn_bar
					.getLayoutParams();
			lp.bottomMargin += 100;
			lp.rightMargin += 200;
			btn_pass = dv.findViewById(R.id.btn_succ);
			btn_pass.setVisibility(View.GONE);
		}
    	super.onResume();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F9) {
			mTextOpen.setVisibility(View.GONE);
			mTextClose.setVisibility(View.VISIBLE);
            mTextClose.setText(R.string.close);	
        	flags[0] = true;
        } else if (keyCode == KeyEvent.KEYCODE_F10) {
            mTextOpen.setVisibility(View.VISIBLE);
            mTextClose.setVisibility(View.GONE);
            mTextOpen.setText(R.string.open);
        	flags[1] = true;
        }
        if(flags[0] && flags[1]){
        	btn_pass.callOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }
}
