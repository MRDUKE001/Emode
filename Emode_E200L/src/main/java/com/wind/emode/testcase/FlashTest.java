package com.wind.emode.testcase;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;  //shengbotao 20150801 add

import java.util.List;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;


public class FlashTest extends BaseActivity {

    private static final String TAG = "camera";

    private Parameters mParameters;

    private Camera mCameraDevice = null;
    private Button mOpen;
    private Button mClose;
	private View btn_pass = null;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.em_flash);

        mOpen = (Button)findViewById(R.id.btn_open);
        mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //fix bug 80015:can't open the flashlight befor test emode flashlight
                try{
                mCameraDevice = Camera.open();
                mCameraDevice.startPreview();
                mParameters = mCameraDevice.getParameters();
                mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCameraDevice.setParameters(mParameters);
                mOpen.setEnabled(false);
            	btn_pass.setVisibility(View.VISIBLE);
              }catch(Exception e){
              	Toast.makeText(FlashTest.this, R.string.wind_flash_light_open_error, Toast.LENGTH_LONG).show();
              	e.printStackTrace();
              	}
            }
        });

    }
    
    @Override
    protected void onResume() {
    	if(btn_pass == null){
            View dv = getWindow().getDecorView();
            btn_pass = dv.findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
        }
    	super.onResume();	
    }

    @Override
    protected void onPause() {
        if(mCameraDevice != null){
            mParameters = mCameraDevice.getParameters();
            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCameraDevice.setParameters(mParameters);
            mCameraDevice.stopPreview();
            mCameraDevice.release();
            mCameraDevice = null;
        }
        super.onPause();
    }

    
}
