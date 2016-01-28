package com.wind.emode;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

public class EmptyForegroundService extends Service {
    private static final String LOG_TAG = "EmptyForegroundService";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Notification notification = new Notification.Builder(this)
        .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(Constants.ACTION_EMODE_STOP), 0))
        .setSmallIcon(R.drawable.app_icon)
        .setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_content))
        .build();
        startForeground(10, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(LOG_TAG, "onDestroy");
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        Log.d(LOG_TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // TODO Auto-generated method stub
        Log.d(LOG_TAG, "onTrimMemory, level = " + level);
        super.onTrimMemory(level);
    }

    
    



}
