package com.wind.emode.testcase;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.MediaRecorder;
import com.wind.emode.utils.Log;
import com.wind.emode.R;

public class MicRecStressTest extends Activity {

	private static final int SHOW_TEST_STATUS_DIALOG = 0x6688;
	TextView tip;
	Button start;
	EditText etTime;
	RadioGroup group;
	AudioManager audioManager;
	int oldVolumeControlStream;
	int flag = 0;
	int type = 0;
	boolean mIsTesting;
	private boolean mIsDoubleMic;
	Timer timer;
	TimerTask task;

	protected static final String LOG_TAG = "MicStressTest";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_mic_rec_life_test);
		mIsDoubleMic = getResources().getBoolean(R.bool.config_double_mic_support);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		start = (Button) findViewById(R.id.start);
		etTime = (EditText) findViewById(R.id.time);
		start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				boolean flag = false;
				int num = 0;
				String str = etTime.getText().toString();
				if (str != null && !"".equals(str)) {
					try {
						num = Integer.parseInt(str);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (num > 0 && num < 240) {
						flag = true;
					} else {
						Toast.makeText(MicRecStressTest.this,
								R.string.tip_mic_rec_life_test_time_rang,
								Toast.LENGTH_LONG).show();
					}
				}
				if (flag) {
					if (mIsDoubleMic) {
						int id = group.getCheckedRadioButtonId();
						RadioButton rb = (RadioButton) group.findViewById(id);
						type = Integer.parseInt(rb.getTag().toString());
					}
					if (type == 0) {
						AudioSystem.setParameters("SET_LOOPBACK_TYPE=21,1");
					} else {
						AudioSystem.setParameters("SET_LOOPBACK_TYPE=25,1");
					}
					mIsTesting = true;
					showDialog(SHOW_TEST_STATUS_DIALOG);
					if (task != null) {
						task.cancel();
					}
					task = new TimerTask() {
						@Override
						public void run() {
							if (mIsTesting) {
								stopTest();
								MicRecStressTest.this
										.dismissDialog(SHOW_TEST_STATUS_DIALOG);
							}
						}
					};
					timer.schedule(task, num * 60 * 60 * 1000);
				}
			}
		});
		start.setText(getString(R.string.start));
		group = (RadioGroup) findViewById(R.id.radioGroup);
		if (mIsDoubleMic) {
			group.setVisibility(View.VISIBLE);
			group.findViewById(R.id.radioMain).setTag(0);
			group.findViewById(R.id.radioRef).setTag(1);
		}
		timer = new Timer();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == SHOW_TEST_STATUS_DIALOG) {
			int msgId = R.string.main_mic_phone_receiver_testing;
			if (type == 1) {
				msgId = R.string.ref_mic_phone_receiver_testing;
			}
			((AlertDialog) dialog).setMessage(getString(msgId));
		}
		super.onPrepareDialog(id, dialog, args);
	}

	public void stopTest() {
		AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
		mIsTesting = false;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == SHOW_TEST_STATUS_DIALOG) {
			return new AlertDialog.Builder(MicRecStressTest.this)
					.setTitle(R.string.dialog_title_mic_rec_life_test)
					.setMessage("---")
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									stopTest();
								}
							}).create();
		}
		return super.onCreateDialog(id, args);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopTest();
	}

	@Override
	protected void onDestroy() {
		stopTest();
		super.onDestroy();
	}
}
