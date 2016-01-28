package com.wind.emode.testcase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.wind.emode.R;

public class VibratorStressTest extends Activity implements View.OnClickListener,
        DialogInterface.OnClickListener {
    private Vibrator mVibrator;
    private ToggleButton mToggleButton1;
    private ToggleButton mToggleButton2;
    private ToggleButton mToggleButton3;
    private ToggleButton mToggleButton4;

    private PowerManager.WakeLock wl;
    private boolean mChecked;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        wl = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "vibrator_test");
        wl.acquire();
        setContentView(R.layout.em_vibrator_test_hw);
        mVibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        mToggleButton1 = ((ToggleButton) findViewById(R.id.toggleButton1));
        mToggleButton2 = ((ToggleButton) findViewById(R.id.toggleButton2));
        mToggleButton3 = ((ToggleButton) findViewById(R.id.toggleButton3));
        mToggleButton4 = ((ToggleButton) findViewById(R.id.toggleButton4));
        mToggleButton1.setTag(1);
        mToggleButton2.setTag(2);
        mToggleButton3.setTag(3);
        mToggleButton4.setTag(4);
        mToggleButton1.setOnClickListener(this);
        mToggleButton2.setOnClickListener(this);
        mToggleButton3.setOnClickListener(this);
        mToggleButton4.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (wl != null) {
            wl.release();
            wl = null;
        }
        if(mChecked){
            mVibrator.cancel();
            mChecked = false;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent event) {
        if (code == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_vibrator_test_exit)
                    .setMessage(R.string.dialog_msg_vibrator_test_exit)
                    .setPositiveButton(android.R.string.ok, this)
                    .setNegativeButton(android.R.string.cancel, null).create()
                    .show();
        }
        return super.onKeyDown(code, event);
    }

    public void onClick(View view) {
        int tag = (Integer) view.getTag();
        mChecked = ((ToggleButton) view).isChecked();
        long[] array = new long[2];
        switch (tag) {
        case 1:
            array[0] = 2000;
            array[1] = 2000;
            mToggleButton2.setEnabled(!mChecked);
            mToggleButton3.setEnabled(!mChecked);
            mToggleButton4.setEnabled(!mChecked);
            break;
        case 2:
            array[0] = 500;
            array[1] = 1500;
            mToggleButton1.setEnabled(!mChecked);
            mToggleButton3.setEnabled(!mChecked);
            mToggleButton4.setEnabled(!mChecked);
            break;
        case 3:
            array[0] = 1000;
            array[1] = 1000;
            mToggleButton1.setEnabled(!mChecked);
            mToggleButton2.setEnabled(!mChecked);
            mToggleButton4.setEnabled(!mChecked);
            break;
        case 4:
            array[0] = 500;
            array[1] = 500;
            mToggleButton1.setEnabled(!mChecked);
            mToggleButton2.setEnabled(!mChecked);
            mToggleButton3.setEnabled(!mChecked);
            break;
        }
        if (mChecked) {
            mVibrator.vibrate(array, 0);
        } else {
            mVibrator.cancel();
        }
        Toast.makeText(
                this,
                mChecked ? R.string.toast_vibrator_test_begin
                        : R.string.toast_vibrator_test_end, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (wl != null) {
                wl.release();
                wl = null;
            }
            if(mChecked){
                mVibrator.cancel();
                mChecked = false;
            }
            finish();
        }
    }
}
