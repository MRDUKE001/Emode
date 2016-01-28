package com.wind.emode.testcase;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import com.wind.emode.utils.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.content.res.Resources;
import android.media.AudioManager;
import android.app.Activity;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.io.IOException;
import android.database.Cursor;
import android.media.RingtoneManager;
import com.wind.emode.R;

public class ReceiverRingTest extends Activity {
//	private Button play;
	private Button finish;
	private TextView tv;
	private MediaPlayer mp;
	private int currentMode = -1;
	private int currentVolume = -1;
	AudioManager audioManager;

    //File path;
    private Uri mUri;
	WakeLock mWakeLock;

    protected static final String LOG_TAG = "EMODE_ReceiverRing";


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_receiver_ring);
		finish = (Button) findViewById(R.id.click);
		tv=(TextView) findViewById(R.id.title2);
		tv.setText("Audio ReceiverRing Test");
        mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "emode_hardware_test");
		finish.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        if(mp != null){
    				mp.stop();
    				mp.release();
    				mp = null;
		        }
				finish();
			}
		});
		

	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            if(mUri != null){
                //mp = MediaPlayer.create(this, mUri);
                mp = new MediaPlayer();
				
				mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				mp.setScreenOnWhilePlaying(true);
				try{
				    mp.setDataSource(this, mUri);
				    mp.prepare();
			    }catch(IOException e){
                     e.printStackTrace();
			    }
				
				mp.setLooping(true);
				mp.setVolume(1.0f,1.0f);
                mp.start();
            }
        }else{
            if(mp != null){
                mp.stop();
                mp.release();
                mp = null;
            }

        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }

	@Override
    protected void onResume() {
        mWakeLock.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWakeLock.release();
        super.onPause();
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
		    if(mp != null){
	            mp.stop();
	            mp.release();
	            mp = null;
	        }
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}

