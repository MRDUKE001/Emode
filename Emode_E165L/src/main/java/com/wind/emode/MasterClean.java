package com.wind.emode;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;

import android.os.storage.IMountService;

import com.wind.emode.utils.Log;

import com.android.internal.os.storage.ExternalStorageFormatter;
/**
 *
 * @category for product-line
 */
public class MasterClean extends Activity {
    private static final String TAG = "MasterClean";

    private boolean isExtStorageEncrypted() {
        String state = SystemProperties.get("vold.decrypt");

        return !"".equals(state);
    }

    private boolean isSomeStorageEmulated() {
        boolean isExistEmulatedStorage = false;

        try {
            IMountService mountService = IMountService.Stub.asInterface(ServiceManager.getService(
                        "mount"));

            if (mountService != null) {
                isExistEmulatedStorage = mountService.isExternalStorageEmulated();
            } else {
                Log.e(TAG, "MountService return null");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException happens, couldn't talk to MountService");
        }

        Log.d(TAG, "isExistEmulatedStorage : " + isExistEmulatedStorage);

        return isExistEmulatedStorage;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        boolean isExtStorageEmulated = Environment.isExternalStorageEmulated();
        boolean isSomeStorageEmulated = isSomeStorageEmulated();
        boolean eraseSdCard = false;

        if (isSomeStorageEmulated || isExtStorageEmulated ||
                (!Environment.isExternalStorageRemovable() && isExtStorageEncrypted())) {
            eraseSdCard = !isExtStorageEmulated;
        } else {
            //equal show checkbox, this force checked!
            eraseSdCard = true;
        }

        if (eraseSdCard) {
            Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
            startService(intent);
        } else {
            Intent i = new Intent("android.intent.action.MASTER_CLEAR");

            if (false) {
                i.putExtra("keep_premedia", true);
            }

            sendBroadcast(i);

            // Intent handling is asynchronous -- assume it will happen soon.
        }

        finish();
    }
}
