package com.wind.emode.testcase;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.NvRAMAgent;
import com.wind.emode.R;
import com.wind.emode.utils.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VersionInfo extends Activity {
    protected static final String LOG_TAG = "VersionInfo";
    private static final String INNER_VERSION_START = "Plat:";
    private static final String INNER_VERSION_END = "Outer:";
    private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 36;
    private TextView mtextt;
    private TextView mtext;
    private TextView mtext1;
    private TextView mtext2;
    private TextView mtext3;
    private TextView mtext4;
    private TextView mtext5;
    private TextView mtext6;
    private TextView mtext7;
    private TextView mtext8;
    private Button mbutton;
    private String mInternalSoftwareVerProp = "ro.build.wind.version";
    private String mHardwareVerProp = "ro.product.hardware";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.em_devinfor);
        if (getResources().getBoolean(R.bool.config_display_zte_version_info)) {
            mInternalSoftwareVerProp = "ro.build.sw_internal_version";
            mHardwareVerProp = "ro.build.hadword.id";
        }
        mtextt = (TextView) findViewById(R.id.m0_tt);
        mtext = (TextView) findViewById(R.id.m0_t);
        mtext1 = (TextView) findViewById(R.id.m0_t1);
        mtext2 = (TextView) findViewById(R.id.m0_t2);
        mtext3 = (TextView) findViewById(R.id.m0_t3);
        mtext4 = (TextView) findViewById(R.id.m0_t4);
        mtext5 = (TextView) findViewById(R.id.m0_t5);
        mtext6 = (TextView) findViewById(R.id.m0_t6);
        mtext7 = (TextView) findViewById(R.id.m0_t7);
        mtext8 = (TextView) findViewById(R.id.m0_t8);

        mbutton = (Button) findViewById(R.id.m0_b);
        mbutton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        mbutton.setText("OK");

        mtextt.setText(getString(R.string.model) + Build.MODEL);
        mtext.setText(getString(R.string.android_version) + Build.VERSION.RELEASE);
        mtext1.setText(getString(R.string.baseband_version) +
            SystemProperties.get("gsm.version.baseband", getString(R.string.unknown)));

        String kernelVersion = getFormattedKernelVersion();
        mtext2.setText(getString(R.string.kernel_version) + kernelVersion);
        mtext3.setText(getString(R.string.external_version) +
            SystemProperties.get("ro.build.display.id", getString(R.string.unknown)));		
    	String wholeInnerVersion = SystemProperties.get(mInternalSoftwareVerProp, getString(R.string.unknown));
    	String innerVersion = wholeInnerVersion;
    	int startIndex = wholeInnerVersion.indexOf(INNER_VERSION_START);
    	int endIndex = wholeInnerVersion.indexOf(INNER_VERSION_END);
    	if (startIndex != -1 && endIndex != -1) {
    	    innerVersion = wholeInnerVersion.substring(startIndex + INNER_VERSION_START.length(), endIndex);
    	}
        mtext4.setText(getString(R.string.internal_version) + (TextUtils.isEmpty(innerVersion)? getString(R.string.unknown) : innerVersion));
        mtext5.setText(getString(R.string.hardware_version) +
            SystemProperties.get(mHardwareVerProp, "MBV1.0"));

        String utc = SystemProperties.get("ro.build.date.utc", "");

        if (!TextUtils.isEmpty(utc)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(Long.parseLong(utc) * 1000);
            mtext6.setText(format.format(ca.getTime()));
        } else {
            mtext6.setText("UNKNOWN");
        }

        if (getResources().getBoolean(R.bool.config_display_cu_info)) {
        	mtext7.setVisibility(View.VISIBLE);
            mtext7.setText(getString(R.string.cu_number) + getCU());
        }
        if (getResources().getBoolean(R.bool.config_display_root_info)) {
        	mtext8.setVisibility(View.VISIBLE);
            mtext8.setText(getString(R.string.system_root_or_not) + (getRootFlag2() ? "YES" : "NO"));
        }

    }

    private String getCU() {
        byte[] buff = new byte[1024];

        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            int count = is.read(buff);
            is.close();

            if (count < 114) {
                return "UNKNOWN";
            }

            String rever = new String(buff, 104, count - 104);
            Log.i(LOG_TAG, rever);

            int st = rever.indexOf("CUReference:");
            int end = rever.indexOf("End");

            if ((st != -1) && (end != -1)) {
                return rever.substring(st + 12, end);
            } else {
                return "UNKNOWN";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "UNKNOWN";
    }

    private boolean getRootFlag() {
        final int startIndex = 205;
        final String startStr = "ROOT:";
        final String endStr = "END";
        byte[] buff = new byte[256];

        try {
            FileInputStream is = new FileInputStream("/dev/pro_info");
            int count = is.read(buff);
            is.close();

            if (count < startIndex) {
                return false;
            }

            String str = new String(buff, startIndex, count - startIndex);
            Log.i(LOG_TAG, "getRootFlag:" + str);

            int start = str.indexOf(startStr);
            int end = str.indexOf(endStr);

            if ((start != -1) && (end != -1)) {
                return "Y".equals(str.substring(start + startStr.length(), end));
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean getRootFlag2() {
        final int startIndex = 205;
        final String startStr = "ROOT:";
        NvRAMAgent na = NvRAMAgent.Stub.asInterface(ServiceManager.getService("NvRAMAgent"));
        byte[] productInfo = null;

        if (na != null) {
            try {
                productInfo = na.readFile(AP_CFG_REEB_PRODUCT_INFO_LID);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "getRootFlag2, read: " + e);
            }
        }

        if (productInfo != null) {
            String str = new String(productInfo, startIndex, productInfo.length - startIndex);
            Log.d(LOG_TAG, "getRootFlag2, str: " + str);

            int index = str.indexOf(startStr);

            if (index != -1) {
                return 'Y' == productInfo[startIndex + index + startStr.length()];
            } else {
                return false;
            }

        }

        return false;
    }

    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);

            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                "\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
                "(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
                "(#\\d+) " + /* group 3: "#1" */
                "(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

            /*final String PROC_VERSION_REGEX = "\\w+\\s+" + // ignore: Linux
                            "\\w+\\s+" + // ignore: version
                            "([^\\s]+)\\s+" + // group 1: 2.6.22-omap1
                            "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + // group
                                                                                                                    // 2:(xxxxxx@xxxxx.constant)
                            "\\([^)]+\\)\\s+" + // ignore: (gcc ..)
                            "([^\\s]+)\\s+" + // group 3: #26
                            "(?:PREEMPT\\s+)?" + // ignore: PREEMPT (optional)
                            "(.+)"; // group 4: date*/
            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                //log("Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                //log("Regex match on /proc/version only returned " + m.groupCount() + " groups");
                return "Unavailable";
            } else {
                /*return (new StringBuilder(m.group(1)).append("\n").append(
                                m.group(2)).append(" ").append(m.group(3)).append("\n")
                                .append(m.group(4))).toString();*/
                return m.group(1) + "\n" + // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4); // Thu Jun 28 11:02:39 PDT 2012

                // return (new
                // StringBuilder(m.group(1)).append("\n").append("zte-kernel@Zdroid-SMT").toString());
            }
        } catch (IOException e) {
            //log("IO Exception when getting kernel version for Device Info screen" + e);
            return "Unavailable";
        }
    }
}
