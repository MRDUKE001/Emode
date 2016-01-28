package com.wind.emode.testcase;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import com.wind.emode.utils.RunScript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class MemoryTest extends BaseActivity {
    public String availableMemory = null;
    public TextView memory = null;
    public TextView totalMemory = null;
    public Button finish = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_memory); 
        memory = (TextView) findViewById(R.id.sd_t1);
        finish = (Button) findViewById(R.id.sd_b);
        finish.setText("OK");
        finish.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    finish();
                }
            });

        totalMemory = (TextView) findViewById(R.id.sd_t0);

        exeFree();

        String path = "";
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();

        for (StorageVolume volume : volumes) {
            if (!volume.isRemovable() &&
                    Environment.MEDIA_MOUNTED.equals(mStorageManager.getVolumeState(
                            volume.getPath()))) {
                path = volume.getPath();

                break;
            }
        }

        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        String totalSpace = Formatter.formatFileSize(this, totalBlocks * blockSize);
        String availableSpace = Formatter.formatFileSize(this, availableBlocks * blockSize);

        TextView tvTotalSpace = (TextView) findViewById(R.id.sd_t2);
        TextView tvAvailableSpace = (TextView) findViewById(R.id.sd_t3);

        tvTotalSpace.setText(getString(R.string.internal_sd_total) + totalSpace);
        tvAvailableSpace.setText(getString(R.string.internal_sd_avail) + availableSpace);
    }

    public boolean exeFree() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memInfo = new MemoryInfo();
        am.getMemoryInfo(memInfo);

        String free = Formatter.formatFileSize(this, memInfo.availMem);
        memory.setText(getString(R.string.ram_avail) + free);

        String strFree = RunScript.runIt("cat /proc/meminfo");

        if (strFree == null) {
            return false;
        }

        String[] lines = strFree.split("\n");
        String[] t = lines[0].split("[\\s]+");
        totalMemory.setText(getString(R.string.ram_total) +
            Formatter.formatFileSize(this, Long.parseLong(t[1]) * 1024));

        return true;
    }

    public String getMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    public String getTotalMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();

        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }
}
