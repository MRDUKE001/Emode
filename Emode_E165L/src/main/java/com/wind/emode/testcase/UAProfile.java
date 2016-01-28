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

import android.content.res.Resources;
import android.content.res.Resources;

import android.os.Build;
import android.os.Bundle;

import android.widget.TextView;

import com.mediatek.custom.CustomProperties;

import com.wind.emode.R;
import com.wind.emode.utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UAProfile extends Activity {
    private final static String TAG = UAProfile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle("UAProfile");

        TextView tv = new TextView(this);
        tv.setText("Browser UserAgent:\n" + getUAString("browser", "UserAgent") + "\n" +
            "Browser UAprofile:\n" + getUAString("browser", "UAProfileURL"));
        setContentView(tv);
    }

    private String getUAString(String name, String type) {
        String str = null;

        try {
            str = CustomProperties.getString(name, type);
        } catch (java.lang.NoClassDefFoundError e) {
            e.printStackTrace();
        }

        return str;
    }

    public String getCMUserAgentString() {
        StringBuilder buffer = new StringBuilder();
        // add the system and version
        buffer.append("Linux");
        buffer.append("/");
        buffer.append(getKernelInfo());
        buffer.append(" ");

        // add release date
        buffer.append("Release");
        buffer.append("/");

        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(Build.TIME);

        Date date = cl.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
        buffer.append(sdf.format(date));
        buffer.append(" ");

        StringBuffer otherInfo = new StringBuffer();

        // Add version
        final String version = Build.VERSION.RELEASE;

        if (version.length() > 0) {
            if (Character.isDigit(version.charAt(0))) {
                // Release is a version, eg "3.1"
                otherInfo.append(version);
            } else {
                // Release is a codename, eg "Honeycomb"
                // In this case, use the previous release's version
                otherInfo.append("3.1");
            }
        } else {
            // default to "1.0"
            otherInfo.append("1.0");
        }

        otherInfo.append("; ");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;

            if (model.length() > 0) {
                otherInfo.append(model);
            }
        }

        //<string name="web_user_agent" translatable="false">Mozilla/5.0 (Linux; U; <xliff:g id="x">Android %s</xliff:g>) <xliff:g>%s</xliff:g>
        //AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30</string>
        final String base = getResources().getText(R.string.web_user_agent).toString();

        return String.format(base, otherInfo, buffer);
    }

    private String getKernelInfo() {
        String version;
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);

            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
                 * group 2:
                 * (xxxxxx@xxxxx
                 * .constant)
                 */
                "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                version = "2.6.35";
            } else if (m.groupCount() < 4) {
                Log.e(TAG,
                    "Regex match on /proc/version only returned " + m.groupCount() + " groups");
                version = "2.6.35";
            } else {
                version = m.group(1);
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen", e);
            version = "2.6.35";
        }

        return version;
    }
}
