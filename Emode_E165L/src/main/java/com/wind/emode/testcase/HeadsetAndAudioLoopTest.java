package com.wind.emode.testcase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;


public class HeadsetAndAudioLoopTest extends BaseActivity {
    private static final int SHOW_TEST_STATUS_DIALOG = 0x6688;
    protected static final String LOG_TAG = "HeadsetAndAudioLoopTest";
    private static final int ACOUSTIC_LOOPBACK_TYPE = 0;
    private static final int AFE_LOOPBACK_TYPE = 1;
    private int mLoopbackType = ACOUSTIC_LOOPBACK_TYPE;
    private TextView tip;
    private RadioGroup mLoopbackTypeGroup;
    private Button headsetbutton;
    private Button handsetbutton;
    private Button micToSpeakerButton;
    private Button refbutton;
    private Button backbutton;
	private View btn_pass = null;
	private View btn_fail = null;
	private boolean[] mPassTypes = {false, false};

    //IWindowManager wm;
    AudioManager audioManager;
    int oldVolumeControlStream;
    int flag = 0;
    int type;
    boolean mIsTesting;
    private boolean mIsDoubleMic;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_headset_audioloop);
        mIsDoubleMic = getResources().getBoolean(R.bool.config_double_mic_support);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        tip = (TextView) findViewById(R.id.tip);
        tip.setText(R.string.tip_insert_headset);

        mLoopbackTypeGroup = (RadioGroup) findViewById(R.id.loopback_type);
        mLoopbackTypeGroup.setVisibility(View.VISIBLE);
        mLoopbackTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.acoustic_loopback) {
                    mLoopbackType = ACOUSTIC_LOOPBACK_TYPE;
                } else if (checkedId == R.id.afe_loopback) {
                    mLoopbackType = AFE_LOOPBACK_TYPE;
                }
            }
        });

        micToSpeakerButton = (Button) findViewById(R.id.mic_to_speaker);
        micToSpeakerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    type = 3;
                    if (mLoopbackType == AFE_LOOPBACK_TYPE) {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=1,3");
                    } else {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=21,3");
                    }
                    mIsTesting = true;
                    showDialog(SHOW_TEST_STATUS_DIALOG);
                }
            });
        micToSpeakerButton.setText(getString(R.string.main_mic_phone_speaker));
        micToSpeakerButton.setVisibility(View.GONE);

        headsetbutton = (Button) findViewById(R.id.headset);
        headsetbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    type = 0;
                    if (mLoopbackType == AFE_LOOPBACK_TYPE) {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=2,1");
                    } else {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=22,1");
                    }
                    mIsTesting = true;
                    showDialog(SHOW_TEST_STATUS_DIALOG);
                    testPass(0);
                }
            });
        headsetbutton.setText(getString(R.string.headset_mic_phone_receiver));
        headsetbutton.setEnabled(false);

        handsetbutton = (Button) findViewById(R.id.handset);
        handsetbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    type = 1;
                    if (mLoopbackType == AFE_LOOPBACK_TYPE) {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=1,2");
                    } else {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=21,2");
                    }
                    mIsTesting = true;
                    showDialog(SHOW_TEST_STATUS_DIALOG);
                    testPass(1);
                }
            });
        handsetbutton.setText(getString(mIsDoubleMic ? R.string.main_mic_headset_speaker
                                                     : R.string.phone_mic_headset_speaker));
        handsetbutton.setEnabled(false);

        if (mIsDoubleMic) {
            refbutton = (Button) findViewById(R.id.ref);
            refbutton.setVisibility(View.VISIBLE);
            refbutton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        type = 2;
                        if (mLoopbackType == AFE_LOOPBACK_TYPE) {
                            AudioSystem.setParameters("SET_LOOPBACK_TYPE=3,2");
                        }  else {
                            AudioSystem.setParameters("SET_LOOPBACK_TYPE=25,2");
                        }
                        mIsTesting = true;
                        showDialog(SHOW_TEST_STATUS_DIALOG);
                    }
                });
            refbutton.setText(getString(R.string.ref_mic_headset_speaker));
            refbutton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
    	
    	if(btn_pass == null){
            View v = getWindow().getDecorView();
            btn_pass = v.findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
          /*  btn_fail = v.findViewById(R.id.btn_fail);
            btn_fail.setVisibility(View.GONE);*/
        }
    	
        mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();

                        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                            int state = intent.getIntExtra("state", 0);

                            if (state == 0) {
                                tip.setText(R.string.tip_insert_headset);
                                headsetbutton.setEnabled(false);
                                handsetbutton.setEnabled(false);

                                if (mIsDoubleMic) {
                                    refbutton.setEnabled(false);
                                }

                                if (mIsTesting) {
                                    dismissDialog(SHOW_TEST_STATUS_DIALOG);
                                    AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
                                    mIsTesting = false;
                                }
                            } else {
                                tip.setText("");
                                headsetbutton.setEnabled(true);
                                handsetbutton.setEnabled(true);

                                if (mIsDoubleMic) {
                                    refbutton.setEnabled(true);
                                }
                            }
                        }
                    }
                };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (id == SHOW_TEST_STATUS_DIALOG) {
            int msgId = R.string.headset_mic_phone_receiver_testing;

            if (type == 1) {
                msgId = mIsDoubleMic ? R.string.main_mic_headset_speaker_testing
                                     : R.string.phone_mic_headset_speaker_testing;
            } else if (type == 2) {
                msgId = R.string.ref_mic_headset_speaker_testing;
            } else if (type == 3) {
                msgId = R.string.main_mic_phone_speaker_testing;
            }

            ((AlertDialog) dialog).setMessage(getString(msgId));
        }

        super.onPrepareDialog(id, dialog, args);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (id == SHOW_TEST_STATUS_DIALOG) {
            return new AlertDialog.Builder(HeadsetAndAudioLoopTest.this).setMessage("---").setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
                        mIsTesting = false;
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
                        mIsTesting = false;
                    }
                }).create();
        }

        return super.onCreateDialog(id, args);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
        mIsTesting = false;

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        AudioSystem.setParameters("SET_LOOPBACK_TYPE=0");
        super.onDestroy();
    }
    
    public void testPass(int index){
    	mPassTypes[index] = true;
        if(mPassTypes[0] && mPassTypes[1]){
            btn_pass.setVisibility(View.VISIBLE);
         //   btn_fail.setVisibility(View.VISIBLE);
        }
  }
}
