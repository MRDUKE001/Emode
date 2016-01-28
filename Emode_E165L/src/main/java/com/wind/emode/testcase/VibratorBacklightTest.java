package com.wind.emode.testcase;

import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.app.Service;

import android.provider.Settings;

import android.provider.Settings.SettingNotFoundException;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.R;
import com.wind.emode.utils.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class VibratorBacklightTest extends BaseActivity {
    private static final int BRIGHT = 1;
    private static final int DARK = 2;
    private static final int STOP = 3;
    private static final int BUTTON_BACKLIGHT_ON = 4;
    private static final int BUTTON_BACKLIGHT_OFF = 5;
    private static final int HALFBRIGHT = 4;
	private static final int VIBRATE_ONCE = 7;
	private static final int STOP_VIBRATE = 8;		
    protected static final String LOG_TAG = "EMODE_VibratorBacklightTest";
    Button backbutton;
    int oldbrightness;
    int mOldBrightnessMode;
    boolean stop = false;
	private Vibrator mVibrator;
    private IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    private String mButtonLightPath = "/sys/class/leds/button-backlight/brightness";
    public Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
				case VIBRATE_ONCE:
					mVibrator.vibrate(new long[]{1000,1000}, 0);
					break;

				case STOP_VIBRATE:
					removeMessages(VIBRATE_ONCE);
					mVibrator.cancel();
					break;
                case BRIGHT:

                    try {
                        if (!stop) {
                            power.setTemporaryScreenBrightnessSettingOverride(255);
                        }
                    } catch (RemoteException e) {
                    }

                    sendEmptyMessageDelayed(DARK, 1000);

                    break;

                case DARK:

                    try {
                        if (!stop) {
                            power.setTemporaryScreenBrightnessSettingOverride(30);
                        }
                    } catch (RemoteException e) {
                    }

                    sendEmptyMessageDelayed(BRIGHT, 1000);

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

                    if (!stop) {
                        setButtonLightBrightness("255");
                        sendEmptyMessageDelayed(BUTTON_BACKLIGHT_OFF, 1000);
                    } else {
                        setButtonLightBrightness("0");
                    }

                    break;

                case BUTTON_BACKLIGHT_OFF:

                    if (!stop) {
                        setButtonLightBrightness("0");
                        sendEmptyMessageDelayed(BUTTON_BACKLIGHT_ON, 1000);
                    } else {
                        setButtonLightBrightness("0");
                    }

                    break;

                default:
                    break;
                }
            }
        };

    private void setButtonLightBrightness(String brightness) {
        try {
            FileOutputStream os = new FileOutputStream(mButtonLightPath);
            os.write(brightness.getBytes());
            os.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private int getBrightnessMode(int defaultValue) {
        int brightnessMode = defaultValue;

        try {
            brightnessMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException snfe) {
            Log.d(LOG_TAG, "SettingNotFoundException");
        }

        Log.d(LOG_TAG, "brightnessMode=" + brightnessMode);

        return brightnessMode;
    }

    private void restoreBrightnessMode() {
        if (mOldBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_vibrator_backlight);

        try {
            oldbrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException snfe) {
            oldbrightness = 102;
        }

        mOldBrightnessMode = getBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        if (mOldBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        handler.sendEmptyMessage(DARK);

        if (getResources().getBoolean(R.bool.config_touch_button_has_backlight)) {
            handler.sendEmptyMessage(BUTTON_BACKLIGHT_ON);
        } else {
            TextView tvTipKeypad = (TextView) findViewById(R.id.tv_tipKeypad);
            tvTipKeypad.setText(getString(R.string.tip_screen_backlight));
        }

		mVibrator =  (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);		

    }

    @Override
    protected void onResume() {
        super.onResume();

        stop = false;
		handler.sendEmptyMessageDelayed(VIBRATE_ONCE, 100);
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
		handler.sendEmptyMessage(STOP_VIBRATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.sendEmptyMessage(STOP);
    }
}
