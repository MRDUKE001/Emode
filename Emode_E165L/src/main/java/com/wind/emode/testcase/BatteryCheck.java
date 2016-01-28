package com.wind.emode.testcase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;

import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.PowerManager;

import android.os.PowerManager.WakeLock;

import android.view.KeyEvent;

import android.widget.TextView;
import android.widget.Toast;

import com.wind.emode.R;
import com.wind.emode.utils.Log;

import java.io.FileDescriptor;
import java.io.IOException;


public class BatteryCheck extends Activity {
    private static final int SHOW_TIP_DIALOG = 0x6688;
    private BroadcastReceiver mReceiver;
    private TextView mTxt;
    private PowerManager.WakeLock wl;
    private MediaPlayer mp;
    private AudioManager audioManager;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "battery_check");
        wl.acquire();
        setContentView(R.layout.em_battery_check);
        mTxt = (TextView) findViewById(R.id.txt);
        mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                            int level = intent.getIntExtra("level", 0);
                            int scale = intent.getIntExtra("scale", 100);
                            num = (int) ((level * 1.0f) / scale * 100);

                            if (num > 60) {
                                mTxt.setText(String.format(getString(
                                            R.string.tip_battery_check_full), num + "%"));
                                mTxt.setTextColor(Color.GREEN);

                                if (mp == null) {
                                    mp = MediaPlayer.create(BatteryCheck.this,
                                            R.raw.action_signature);
                                    mp.setLooping(true);
                                    mp.start();
                                }
                            } else if (num < 40) {
                                mTxt.setText(String.format(getString(
                                            R.string.tip_battery_check_warning), num + "%"));
                                mTxt.setTextColor(Color.RED);
                            } else {
                                mTxt.setText(getString(R.string.tip_battery_check_current) + num +
                                    "%");
                                mTxt.setTextColor(Color.WHITE);
                            }
                        }
                    }
                };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (id == SHOW_TIP_DIALOG) {
            return new AlertDialog.Builder(this).setTitle(R.string.dialog_title_battery_check_exit)
                                                .setMessage(R.string.dialog_msg_battery_check_exit)
                                                .setCancelable(false).setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mReceiver != null) {
                            unregisterReceiver(mReceiver);
                            mReceiver = null;
                        }

                        if (wl != null) {
                            wl.release();
                            wl = null;
                        }

                        if (mp != null) {
                            mp.stop();
                            mp.release();
                            mp = null;
                        }

                        finish();
                    }
                }).setNegativeButton(android.R.string.cancel, null).create();
        }

        return super.onCreateDialog(id, args);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (num < 40)) {
            showDialog(SHOW_TIP_DIALOG);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        if (wl != null) {
            wl.release();
            wl = null;
        }

        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        super.onDestroy();
    }
}
