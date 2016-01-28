package com.wind.emode.testcase;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.IPowerManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import android.util.Log;

public class BacklightTest extends BaseActivity {
	Button backbutton;
	int oldbrightness;
        int mOldBrightnessMode;
	boolean stop = false;

	private static final int BRIGHT = 1;
	private static final int DARK = 2;
	private static final int STOP = 3;
        private static final int BUTTON_BACKLIGHT_ON = 4;
        private static final int BUTTON_BACKLIGHT_OFF = 5;
	private static final int HALFBRIGHT = 4;	
	private IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager
			.getService("power"));

	protected static final String LOG_TAG = "EMODE_BacklightTest";
        private String mButtonLightPath = "/sys/class/leds/button-backlight/brightness";

        private void setButtonLightBrightness(String brightness){
            try{
                FileOutputStream os = new FileOutputStream(mButtonLightPath);
                os.write(brightness.getBytes());
                os.close();
            }catch(FileNotFoundException fe){
                fe.printStackTrace();
            }catch(IOException ie){
                ie.printStackTrace();
            }
        }

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BRIGHT:
				try {
				    if (!stop)
					power.setTemporaryScreenBrightnessSettingOverride(255);
				} catch (RemoteException e) {
				}
				sendEmptyMessageDelayed(DARK, 3000);
				break;
			case DARK:
				try {
					if (!stop)
						power.setTemporaryScreenBrightnessSettingOverride(30);
				} catch (RemoteException e) {
				}
				sendEmptyMessageDelayed(BRIGHT, 3000);
				break;

			case STOP:
				removeMessages(DARK);
				removeMessages(BRIGHT);

				try {
					power.setTemporaryScreenBrightnessSettingOverride(oldbrightness);
				} catch (RemoteException e) {
				}
				restoreBrightnessMode();

				break;
                        case BUTTON_BACKLIGHT_ON:
                                if(!stop){
                                    setButtonLightBrightness("255");
                                    sendEmptyMessageDelayed(BUTTON_BACKLIGHT_OFF, 1500);
                                }else{
                                    setButtonLightBrightness("0");
                                }
                                break;
                        case BUTTON_BACKLIGHT_OFF:
                                if(!stop){ 
                                    setButtonLightBrightness("0");
                                    sendEmptyMessageDelayed(BUTTON_BACKLIGHT_ON, 1500);
                                }else{
                                    setButtonLightBrightness("0");
                                }
                                break;
			default:
				break;
			}
		}
	};

    private int getBrightnessMode(int defaultValue) {
        int brightnessMode = defaultValue;
        try {
            brightnessMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException snfe) {
            Log.d(LOG_TAG,"SettingNotFoundException");
        }
        Log.d(LOG_TAG,"brightnessMode=" + brightnessMode);
        return brightnessMode;
    }

    private void restoreBrightnessMode(){
        if(mOldBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_backlight);

		try {
			oldbrightness = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException snfe) {
			oldbrightness = 102;
		}

		mOldBrightnessMode = getBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		if(mOldBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
		    Settings.System.putInt(getContentResolver(),
	                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}

		handler.sendEmptyMessage(DARK);
        if (getResources().getBoolean(R.bool.config_touch_button_has_backlight)) {
            handler.sendEmptyMessage(BUTTON_BACKLIGHT_ON);
        } else {
            TextView tvTipKeypad = (TextView) findViewById(R.id.tv_tipKeypad);
            tvTipKeypad.setText(getString(R.string.tip_screen_backlight));
        }

//		if (EMflag != ManulDetect) {
//			EMtimer.schedule(EMtask, 7000);
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		stop = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		stop = true;
		try {
			power.setTemporaryScreenBrightnessSettingOverride(oldbrightness);
		} catch (RemoteException e) {
		}
		restoreBrightnessMode();
	}

	@Override
	protected void onStop() {
		super.onStop();

		handler.sendEmptyMessage(STOP);
	}
}
