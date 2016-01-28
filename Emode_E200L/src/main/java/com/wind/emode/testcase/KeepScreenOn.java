/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wind.emode.testcase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.widget.TextView;
import com.wind.emode.R;

public class KeepScreenOn extends Activity {

    private final static String TAG = KeepScreenOn.class.getSimpleName();
    WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        TextView tv = new TextView(this);
        tv.setText(R.string.keep_screen_on_test);
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "emode_hardware_test");
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
}
