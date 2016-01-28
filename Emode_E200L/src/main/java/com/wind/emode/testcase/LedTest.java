package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;

import android.os.Bundle;
import android.widget.TextView;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import android.view.View;
import android.widget.Button;
import android.app.Service;
import android.os.Vibrator;
import android.os.Message;
import android.os.Handler;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.wind.emode.R;

public class LedTest extends BaseActivity {

	//Vibrator v;

	//private static final int VIBRATE_ONCE = 1;
	//private static final int STOP_VIBRATE = 2;
        private static final int LED_ON_RED = 0x66;
        private static final int LED_OFF_RED = 0x67;
        private static final int LED_ON_GREEN = 0x68;
        private static final int LED_OFF_GREEN = 0x69;
        private static final int LED_ON_BLUE = 0x70;
        private static final int LED_OFF_BLUE = 0x71;

	private static final int RED = 0xffff0000;
	private static final int BLUE = 0xff0000ff;
	private static final int GREEN = 0xff00ff00;

//	private int color = 0;

	private NotificationManager mNM;
	private Notification mNotification;
	private static final int ID_LED = 19871103;
        private boolean flag;
        private boolean isThreeColor;

//	NotificationManager nm;
//	Notification notification;

	protected static final String LOG_TAG = "EMODE_Led";

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*
			case VIBRATE_ONCE:
				v.vibrate(new long[]{1000,1000}, 0);
				break;

			case STOP_VIBRATE:
				removeMessages(VIBRATE_ONCE);
				v.cancel();
//				nm.cancel(107);
				break;
			*/
                        case LED_ON_RED:
                             if(flag){
                                 mNotification.ledARGB = RED;
                                 mNM.notify(ID_LED, mNotification);
                                 handler.sendEmptyMessageDelayed(LED_OFF_RED, 500);
                             }
                             break;
                        case LED_OFF_RED:
                             mNM.cancel(ID_LED);
                             if(flag)
                                 handler.sendEmptyMessageDelayed(LED_ON_GREEN, 100);
                             break;
                        case LED_ON_GREEN:
                             if(flag){
                                 mNotification.ledARGB = GREEN;
                                 mNM.notify(ID_LED, mNotification);
                                 handler.sendEmptyMessageDelayed(LED_OFF_GREEN, 500);
                             }
                             break;
                        case LED_OFF_GREEN:
                             mNM.cancel(ID_LED);
                             if(flag){
                                 if(isThreeColor){
                                     handler.sendEmptyMessageDelayed(LED_ON_BLUE, 100);
                                 }else{
                                     handler.sendEmptyMessageDelayed(LED_ON_RED, 100);
                                 }
                             }
                             break;
                        case LED_ON_BLUE:
                             if(flag){
                                 mNotification.ledARGB = BLUE;
                                 mNM.notify(ID_LED, mNotification);
                                 handler.sendEmptyMessageDelayed(LED_OFF_BLUE, 500);
                             }
                             break;
                        case LED_OFF_BLUE:
                             mNM.cancel(ID_LED);
                             if(flag)
                                 handler.sendEmptyMessageDelayed(LED_ON_RED, 100);
                             break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.em_led);
        isThreeColor = getResources().getBoolean(R.bool.config_three_color_led_support);
        mNM=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.flags = Notification.FLAG_SHOW_LIGHTS;
        flag = true;
        handler.sendEmptyMessageDelayed(LED_ON_RED, 100);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//handler.sendEmptyMessage(STOP_VIBRATE);
                flag = false;
                mNM.cancel(ID_LED);
	}
}
