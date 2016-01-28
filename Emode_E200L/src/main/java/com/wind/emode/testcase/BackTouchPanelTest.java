package com.wind.emode.testcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;


public class BackTouchPanelTest extends BaseActivity {
    private final static int MSG_CHECK_CHANNEL = 0x6688;
    View btn_pass = null;
    MyView myView;
    private byte[] buffer;
    String content = "";
    boolean[] flags = { false, false, false, false, false, false };
    private Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                FileInputStream is = null;

                try {
                    is = new FileInputStream("/sys/bus/i2c/drivers/AW9163_ts/2-002c/delta");

                    int count = is.read(buffer);
                    content = new String(buffer, 0, count);

                    String[] nums = content.substring("delta:".length(), content.length()).split(",");

                    for (int i = 0; i < 6; i++) {
                        if (Integer.parseInt(nums[i].trim()) > 30) {
                            flags[i] = true;
                        }
                    }

                    is.close();
                    is = null;
                    myView.invalidate();
                    checkResult();
                } catch (Exception e) {
                    e.printStackTrace();

                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };

    private void checkResult() {
        int len = flags.length;

        for (int i = 0; i < len; i++) {
            if (!flags[i]) {
                mHandler.sendEmptyMessageDelayed(MSG_CHECK_CHANNEL, 500);

                return;
            }
        }

        btn_pass.callOnClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView(this);
        setContentView(myView);
    }

    @Override
    protected void onResume() {
        if (btn_pass == null) {
            View dv = getWindow().getDecorView();
            btn_pass = dv.findViewById(R.id.btn_succ);
            btn_pass.setVisibility(View.GONE);
        }

        super.onResume();
    }

    public class MyView extends View {
        String title = "";
        String thimble_result = "";
        String touch_tip = "";
        String touch_doohickey_tip = "";
        String succ_tip = "";
        Paint mTitlePaint = new Paint();
        Paint mWhiteTipPaint = new Paint();
        Paint mRedTipPaint = new Paint();
        Paint mGreenTipPaint = new Paint();
        private Paint mFullPaint = new Paint();
        private Paint mEmptyPaint = new Paint();
        private Paint mThimblePaint;
        private boolean flag_thimble = false;

        public MyView(Context context) {
            super(context);
            title = getString(R.string.backpanel_title);
            touch_tip = getString(R.string.touch_tip);
            touch_doohickey_tip = getString(R.string.touch_doohickey_tip);
            succ_tip = getString(R.string.backpanel_succ_tip);
            mTitlePaint.setTextSize(60);
            mTitlePaint.setColor(Color.WHITE);
            mWhiteTipPaint.setTextSize(40);
            mWhiteTipPaint.setColor(Color.WHITE);
            mRedTipPaint.setTextSize(40);
            mRedTipPaint.setColor(Color.RED);
            mGreenTipPaint.setTextSize(40);
            mGreenTipPaint.setColor(Color.GREEN);
            mEmptyPaint.setStyle(Style.STROKE);
            mEmptyPaint.setColor(Color.BLUE);
            mFullPaint.setStyle(Style.FILL);
            mFullPaint.setColor(Color.GREEN);
            buffer = new byte[128];

            FileInputStream is = null;

            try {
                is = new FileInputStream("/sys/bus/i2c/drivers/AW9163_ts/2-002c/rawdata");

                int count = is.read(buffer);
                String thimble = new String(buffer, 0, count);
                String[] nums = thimble.substring("base:".length(), thimble.length()).split(",");
                int i = 0;
                int value = 0;

                for (i = 0; i < 6; i++) {
                    value = Integer.parseInt(nums[i].trim());

                    if ((value < 500) || (value > 3500)) {
                        break;
                    }
                }

                if (i == 6) {
                    flag_thimble = true;
                    thimble_result = getString(R.string.thimble_joined);
                    mThimblePaint = mWhiteTipPaint;

                    if (EmodeApp.mTestcaseType != Constants.TESTCASE_TYPE_BOARD) {
                        mHandler.sendEmptyMessage(MSG_CHECK_CHANNEL);
                    }
                } else {
                    thimble_result = getString(R.string.thimble_unjoin);
                    mThimblePaint = mRedTipPaint;
                }

                is.close();
                is = null;
            } catch (Exception e) {
                e.printStackTrace();

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawText(title, 380, 80, mTitlePaint);
            canvas.drawText(thimble_result, 10, 200, mThimblePaint);

            if (EmodeApp.mTestcaseType != Constants.TESTCASE_TYPE_BOARD) {
                canvas.drawText(touch_tip, 10, 280, mWhiteTipPaint);
                canvas.drawText(touch_doohickey_tip, 10, 350, mWhiteTipPaint);

                for (int i = 0; i < 6; i++) {
                    if (flags[i]) {
                        canvas.drawCircle((i * 150) + 80, 480, 50, mFullPaint);
                    } else {
                        canvas.drawCircle((i * 150) + 80, 480, 50, mEmptyPaint);
                    }
                }
            } else if (flag_thimble) {
                canvas.drawText(succ_tip, 10, 280, mGreenTipPaint);
                btn_pass.setVisibility(View.VISIBLE);
            }
        }
    }
}
