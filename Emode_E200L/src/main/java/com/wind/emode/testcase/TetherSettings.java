/*
 * Copyright (C) 2008 The Android Open Source Project
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


//import com.android.settings.wifi.WifiApEnabler;
import android.app.Activity;

//import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

//import android.content.pm.PackageManager.NameNotFoundException;
//import android.content.res.AssetManager;
import android.net.ConnectivityManager;

//import android.app.AlertDialog;
//import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;

//import android.preference.CheckBoxPreference;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceScreen;
//import android.provider.Settings;
//import com.wind.emode.utils.Log;
import android.view.Gravity;
import android.view.WindowManager;

//import java.util.Locale;

//MTR Tethering&Wifi-Sharing requirement 53867,53869
//check user wether subscriber data-plan
//import java.io.IOException;
//import android.net.Uri;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//MTR Tethering&Wifi-Sharing requirement 53922
//import Android packages
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.wind.emode.R;

//import android.webkit.WebView;

//import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class TetherSettings extends Activity implements OnCheckedChangeListener {

    private CheckBox mUsbTether;

    private BroadcastReceiver mTetherChangeReceiver;
    private String[] mUsbRegexs;

    //    private ArrayList mUsbIfaces;
    private HashMap<String, String> mTips = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mTips.put("usb_tethering_active_subtext", "Tethered");
        mTips.put("usb_tethering_available_subtext", "USB connected, check to tether");
        mTips.put("usb_tethering_errored_subtext", "USB tethering error");
        mTips.put("usb_tethering_storage_active_subtext", "Can't tether when USB storage in use");
        mTips.put("usb_tethering_unavailable_subtext", "USB not connected");

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(this);
        tv.setText("USB Tether");
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);
        tv.setHeight(50);
        ll.addView(tv, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        mUsbTether = new CheckBox(this);
        mUsbTether.setOnCheckedChangeListener(this);
        mUsbTether.setText(mTips.get("usb_tethering_unavailable_subtext"));
        ll.addView(mUsbTether, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
        setContentView(ll);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mUsbRegexs = cm.getTetherableUsbRegexs();

    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean massStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());

        if (!massStorageActive) {
            mUsbTether.setEnabled(false);
        } else {
            mUsbTether.setEnabled(true);
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
        mTetherChangeReceiver = new TetherChangeReceiver();

        Intent intent = registerReceiver(mTetherChangeReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(Intent.ACTION_MEDIA_UNSHARED);
        filter.addDataScheme("file");
        registerReceiver(mTetherChangeReceiver, filter);

        if (intent != null) {
            mTetherChangeReceiver.onReceive(this, intent);
        }

        //        mWifiApEnabler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTetherChangeReceiver);
        mTetherChangeReceiver = null;

        //        mWifiApEnabler.pause();
    }

    private void updateState() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        String[] available = cm.getTetherableIfaces();
        String[] tethered = cm.getTetheredIfaces();
        String[] errored = cm.getTetheringErroredIfaces();
        updateState(available, tethered, errored);
    }

    private void updateState(Object[] available, Object[] tethered, Object[] errored) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean usbTethered = false;
        boolean usbAvailable = false;
        int usbError = ConnectivityManager.TETHER_ERROR_NO_ERROR;
        boolean usbErrored = false;
        boolean massStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());

        for (Object o : available) {
            String s = (String) o;

            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    usbAvailable = true;

                    if (usbError == ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                        usbError = cm.getLastTetherError(s);
                    }
                }
            }
        }

        for (Object o : tethered) {
            String s = (String) o;

            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    usbTethered = true;
                }
            }
        }

        for (Object o : errored) {
            String s = (String) o;

            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    usbErrored = true;
                }
            }
        }

        if (usbTethered) {
            mUsbTether.setText(mTips.get("usb_tethering_active_subtext"));
            mUsbTether.setEnabled(true);
            mUsbTether.setChecked(true);
        } else if (usbAvailable) {
            if (usbError == ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                mUsbTether.setText(mTips.get("usb_tethering_available_subtext"));
            } else {
                mUsbTether.setText(mTips.get("usb_tethering_errored_subtext"));
            }

            mUsbTether.setEnabled(true);
            mUsbTether.setChecked(false);
        } else if (usbErrored) {
            mUsbTether.setText(mTips.get("usb_tethering_errored_subtext"));
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        } else if (massStorageActive) {
            mUsbTether.setText(mTips.get("usb_tethering_storage_active_subtext"));
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        } else {
            mUsbTether.setText(mTips.get("usb_tethering_unavailable_subtext"));
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //        if (preference == mUsbTether) {
        boolean newState = mUsbTether.isChecked();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (newState) {
            String[] available = cm.getTetherableIfaces();

            String usbIface = findIface(available, mUsbRegexs);

            if (usbIface == null) {
                updateState();

                return;
            }

            if (cm.tether(usbIface) != ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                mUsbTether.setChecked(false);
                mUsbTether.setText(mTips.get("usb_tethering_errored_subtext"));

                return;
            }

            mUsbTether.setText("");

        } else {
            String[] tethered = cm.getTetheredIfaces();

            String usbIface = findIface(tethered, mUsbRegexs);

            if (usbIface == null) {
                updateState();

                return;
            }

            if (cm.untether(usbIface) != ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                mUsbTether.setText(mTips.get("usb_tethering_errored_subtext"));

                return;
            }

            mUsbTether.setText("");
        }

        //        } else if (preference == mTetherHelp) {
        //
        //            showDialog(DIALOG_TETHER_HELP);
        //        }
    }

    private String findIface(String[] ifaces, String[] regexes) {
        for (String iface : ifaces) {
            for (String regex : regexes) {
                if (iface.matches(regex)) {
                    return iface;
                }
            }
        }

        return null;
    }

    private class TetherChangeReceiver extends BroadcastReceiver {
        public void onReceive(Context content, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.ACTION_TETHER_STATE_CHANGED)) {
                ArrayList<String> available = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_AVAILABLE_TETHER);
                ArrayList<String> active = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER);
                ArrayList<String> errored = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_ERRORED_TETHER);
                updateState(available.toArray(), active.toArray(), errored.toArray());
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_SHARED) ||
                    intent.getAction().equals(Intent.ACTION_MEDIA_UNSHARED)) {
                updateState();
            }
        }
    }
}
