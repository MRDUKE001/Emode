package com.wind.emode.testcase;

import android.app.Activity;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wind.emode.R;

public class HardwareInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView txtTitle = new TextView(this);
        txtTitle.setTextSize(25);
        txtTitle.setGravity(Gravity.CENTER);
        txtTitle.setText(R.string.title_hardware_type);
        ll.addView(txtTitle, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        InputStream is;
        byte[] bytes = new byte[256];
        int count;
        //----------LCD TYPE----------
        String lcdType = getString(R.string.unknown);;
        try {
			is = new FileInputStream("/sys/class/wind_device/device_info/lcm_info");  
			count = is.read(bytes);
			is.close();
			lcdType = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
        TextView txtLcdType = new TextView(this);
        txtLcdType.setText(getString(R.string.lcd_type) + lcdType);
        LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        lp0.topMargin = 20;
        ll.addView(txtLcdType, lp0);
        //----------TP TYPE----------
        String tpType = getString(R.string.unknown);;
        try {
			is = new FileInputStream("/sys/class/wind_device/device_info/ctp_info"); 
			count = is.read(bytes);
			is.close();
			tpType = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
        TextView txtTpType = new TextView(this);
        txtTpType.setText(getString(R.string.tp_type) + tpType);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        lp1.topMargin = 20;
        ll.addView(txtTpType, lp1);
        
        String flashType = getString(R.string.unknown);
	  String FILE_CID = "/sys/block/mmcblk0/device/cid";
        try {
			is = new FileInputStream(FILE_CID);
			count = is.read(bytes);
			is.close();
			flashType = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	String[] flashNameStr = getResources().getStringArray(R.array.flash_names);
	String[] flashCidStr = getResources().getStringArray(R.array.flash_cids);

	String flashcidstr=null;
	int i=0,n=flashCidStr.length;
	
	for( i=0;i<n;i++)
	{
		if(flashType.contains(flashCidStr[i]))
			{
			flashcidstr=flashNameStr[i];
			break;
			}
	}
	if(i>=n)
		flashcidstr=flashType;
		
        TextView txtFlashVersion = new TextView(this);
        txtFlashVersion.setText(getString(R.string.flash_type) + flashcidstr);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 20;
        ll.addView(txtFlashVersion, lp);

    String camid = getString(R.string.unknown);;
	String file_node = "/sys/class/wind_device/device_info/camera_info"; 
    try {
			is = new FileInputStream(file_node);
			count = is.read(bytes);
			is.close();
			camid = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

    TextView txtCamId = new TextView(this);
   	txtCamId.setText(getString(R.string.camera_type) + camid);
	LinearLayout.LayoutParams layoutp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        layoutp.topMargin = 20;
        ll.addView(txtCamId, layoutp);
         
		//----------gsensor info----------
/*
        String flashlightInfo = getString(R.string.unknown);;
        try {
			is = new FileInputStream("/proc/flashlight_info");
			count = is.read(bytes);
			is.close();
			flashlightInfo = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
        TextView textFlashlightInfo = new TextView(this);
        textFlashlightInfo.setText(getString(R.string.flashlight_type) + flashlightInfo +" ");
        LinearLayout.LayoutParams flashlightLayout = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        flashlightLayout.topMargin = 20;
        ll.addView(textFlashlightInfo, flashlightLayout);
 */

        String gsensorInfo = getString(R.string.unknown);;
        try {
			is = new FileInputStream("/sys/class/wind_device/device_info/gsensor_info"); 
			count = is.read(bytes);
			is.close();
			gsensorInfo = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
        TextView textGensorInfo = new TextView(this);
        textGensorInfo.setText(getString(R.string.gsensor_type) + gsensorInfo);
        LinearLayout.LayoutParams sensor = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        sensor.topMargin = 20;
        ll.addView(textGensorInfo, sensor);

        String msensorInfo = getString(R.string.unknown);;
        try {
			is = new FileInputStream("/sys/class/wind_device/device_info/msensor_info");
			count = is.read(bytes);
			is.close();
			msensorInfo = new String(bytes, 0, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
        TextView textdtvInfo = new TextView(this);
        textdtvInfo.setText(getString(R.string.msensor_type) + msensorInfo);
        LinearLayout.LayoutParams textMensorInfo = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        textMensorInfo.topMargin = 20;
        ll.addView(textdtvInfo, textMensorInfo);

        setContentView(ll);
    }

}
