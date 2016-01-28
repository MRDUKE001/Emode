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
import android.database.Cursor;
import android.media.AudioManager;

//import com.mediatek.common.featureoption.FeatureOption;
import com.wind.emode.BaseActivity;

import android.view.View;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import com.wind.emode.R;

public class SDRingTest extends BaseActivity {
	private Button finish;
	private MediaPlayer mp;
	
    TextView statetext;
    TextView t1;
    TextView t2;
    TextView t3;
    Button backbutton;
    String status;
    String total;
    String available;
    String used;

    StatFs stat;
    //private Uri mUri;

    static int mstate = 0;
    static long blockSize = 0;
    static long totalBlocks = 0;
    static long availableBlocks = 0;

	private int mOldVolume = 0;
	private View btn_pass = null;
		

    protected static final String LOG_TAG = "EMODE_SDTest";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
			  Log.d("lizusheng", "action:"+action);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                if(stat != null){
                    blockSize = stat.getBlockSize();
                    totalBlocks = stat.getBlockCount();
                    availableBlocks = stat.getAvailableBlocks();
					  Log.d("lizusheng", "availableBlocks:"+availableBlocks);
                    updateScreen();
                }
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                blockSize = 0;
                totalBlocks = 0;
                availableBlocks = 0;
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
                updateScreen();
            } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
                updateScreen();
            }
        }
    };

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_sd_ring);
        Log.d("lizusheng", "onCreate:");
		finish = (Button) findViewById(R.id.click);
        //mUri = getUriByName(getString(R.string.test_audio_name));

		finish.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View arg0) {
		        if(mp != null){
    				mp.stop();
    				mp.release();
    				mp = null;
		        }
				finish();
			}
		});
		

        statetext = (TextView) findViewById(R.id.sd_t0);

	String externalPath = "";
                StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] volumes = mStorageManager.getVolumeList();
		for (StorageVolume volume : volumes) {
			if(volume.isRemovable() && Environment.MEDIA_MOUNTED.equals(mStorageManager.getVolumeState(volume.getPath()))){
				externalPath = volume.getPath();
				break;
			}
		}

        if(!"".equals(externalPath)){
            try{
                stat = new StatFs(externalPath);
            }catch(Exception e){
                Toast.makeText(this, R.string.tip_insert_sdcard, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{          
             Toast.makeText(this, R.string.tip_insert_sdcard, Toast.LENGTH_LONG).show();
        }
        if(stat != null){
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }else{
            statetext.setText("unmount");
	    blockSize = 0;
            totalBlocks = 0;
            availableBlocks = 0;
        }

        total = formatSize(totalBlocks * blockSize);
        available = formatSize(availableBlocks * blockSize);
        used = formatSize((totalBlocks - availableBlocks) * blockSize);
	
        t1 = (TextView) findViewById(R.id.sd_t1);
        t1.setText(getString(R.string.sdcard_total) + total);


        t2 = (TextView) findViewById(R.id.sd_t2);
        t2.setText(getString(R.string.sdcard_used) + used);


        t3 = (TextView) findViewById(R.id.sd_t3);
        t3.setText(getString(R.string.sdcard_available) + available);


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
		filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);

//        if (EMflag == AutoDetect) {
//            EMtimer.schedule(EMtask, 15000);
//        }

        mOldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);  

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            //if(mUri != null){
            mp = MediaPlayer.create(this, R.raw.twirl_away);
			mp.setVolume(1.0f,1.0f);
            mp.setLooping(true);
            mp.start();
            //}
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
    protected void onResume() {
    	if(btn_pass == null){
            View v = getWindow().getDecorView();
            btn_pass = v.findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
        }
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOldVolume, 0);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    private void updateScreen() {
        boolean hasSdcard = false;
        //if(FeatureOption.MTK_SHARED_SDCARD){
            if(!Environment.isExternalStorageEmulated()){
                hasSdcard = true;
            }
        //}else{
        //    if(Environment.isExternalStorageRemovable()){
         //       hasSdcard = true;
        //    }
            Log.d("lizusheng", "hasSdcard1:"+hasSdcard);
        //}
        if(hasSdcard){
            status = Environment.getExternalStorageState();
            statetext.setText("" + status);

            total = formatSize(totalBlocks * blockSize);
            available = formatSize(availableBlocks * blockSize);
            used = formatSize((totalBlocks - availableBlocks) * blockSize);

            t1.setText(getString(R.string.sdcard_used) + used);
            t2.setText(getString(R.string.sdcard_total) + total);
            t3.setText(getString(R.string.sdcard_available) + available);
            btn_pass.setVisibility(View.VISIBLE);
            Log.d("lizusheng", "set btn_pass visible");
        }else{
        	Log.d("lizusheng", " statetext.setText : unmount");
            statetext.setText("unmount");
            t1.setText(getString(R.string.sdcard_used) + 0);
            t2.setText(getString(R.string.sdcard_total) + 0);
            t3.setText(getString(R.string.sdcard_available) + 0);
        }
    }

	private String formatSize(long size) {

        String suffix = null;

        if (size <= 0)
            return "0";

        // add K or M suffix if size is greater than 1K or 1M
        if (size >= 1024) {
            suffix = "K";
            size /= 1024;
            if (size >= 1024) {
                suffix = "M";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);

        return resultBuffer.toString();
    }

    /*private Uri getUriByName(String name){
        Uri uri = null;
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                new String[]{"_id", "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\""},
                "_display_name='" + name + "'", null, null);
        if(c.moveToFirst()){
            uri = ContentUris.withAppendedId(Uri.parse(c.getString(1)), c.getLong(0));
        }
        c.close();
        return uri;
    }*/

}
