package com.wind.emode.testcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.media.AudioManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;

import android.view.IWindowManager;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;
import android.view.KeyEvent;
import android.graphics.Color;

public class HeadsetHookKeyTest extends BaseActivity {
    private static final int EVENT_HEADSET_PLUG_STATE_CHANGED = 1;
    static int mstate = 0;
    protected static final String LOG_TAG = "HeadsetHookKeyTest";
    TextView statetext;
    Button backbutton;
    private String[] status = new String[2]; //{ "off", "on" };
    IWindowManager wm;
    AudioManager audioManager;
    int oldVolumeControlStream;
    int flag = 0;
    Boolean ison = true;
    TextView hookstatetext;
    private String[] hookStatus = new String[2];
    static boolean headset_hook_key_down_flag = false;
    private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case EVENT_HEADSET_PLUG_STATE_CHANGED:
                    updateScreen(msg.arg1);

                    break;

                default:
                    break;
                }
            }
        };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    Message message = Message.obtain(mHandler, EVENT_HEADSET_PLUG_STATE_CHANGED,
                            intent.getIntExtra("state", 0), 0);
                    mHandler.sendMessage(message);
                }
            }
        };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_headset);
        //status[0] = getString(R.string.status_on);
        //status[1] = getString(R.string.status_off);
        status[0] = getString(R.string.headset_plug_out);
        status[1] = getString(R.string.headset_plug_in);
        hookStatus[0] = getString(R.string.headset_hook_key_up);
        hookStatus[1] = getString(R.string.headset_hook_key_down);
        //headset receiver
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        wm = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));

        statetext = (TextView) findViewById(R.id.stext_0);
        statetext.setText("" + status[mstate]);
        statetext.setTextColor(Color.RED);
        hookstatetext = (TextView) findViewById(R.id.headsetHook);
        hookstatetext.setText("" + hookStatus[mstate]);
        hookstatetext.setTextColor(Color.RED);
        headset_hook_key_down_flag = false;
        backbutton = (Button) findViewById(R.id.b_2);
        backbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        backbutton.setText("OK");
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
        case KeyEvent.KEYCODE_HEADSETHOOK:
            hookstatetext.setText("" + hookStatus[1]);
            hookstatetext.setTextColor(Color.GREEN);
                headset_hook_key_down_flag = true;
            break;
        }
        return true;
    }
    private void updateScreen(int state) {
        //headset receiver
        if (state == 0) {
            statetext.setText("" + status[state]);
            statetext.setTextColor(Color.RED);
            oldVolumeControlStream = getVolumeControlStream();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            audioManager.setParameter("LoopBack", "onheadset");
            flag = 0;
            ison = false;
            Log.e(LOG_TAG, " " + ison);
        } else {
            statetext.setText("" + status[state]);
            statetext.setTextColor(Color.GREEN);
            //if(ison &&flag == 1){
            audioManager.setParameter("LoopBack", "offhead");
            //setVolumeControlStream(oldVolumeControlStream);
            //}
            flag = 1;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if ((flag == 0) && !ison) {
            Log.e(LOG_TAG, " " + ison);
            audioManager.setParameter("LoopBack", "offhead");
            setVolumeControlStream(oldVolumeControlStream);

        }

        unregisterReceiver(mReceiver);
    }
}
