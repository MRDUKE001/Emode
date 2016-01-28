package com.wind.emode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.wind.emode.manager.MainManager;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import com.wind.emode.utils.SaveTestResult;

/**
 * Created by lizusheng on 2015/12/28.
 */

    /**
     * 接收测试单元测试结束时的广播
     * 执行下一个单元测试
     */
    public class ModuleTestReceiver extends BroadcastReceiver {
        private final String TAG = this.getClass().getSimpleName();
        Context context;
        private int result;
        private String code;
        private Handler mHandler = new Handler();

        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"start to save the result");
                SaveTestResult.getInstance(context).saveResult(result, code);
            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            this.context=context;
            int isFinish = intent.getIntExtra(Constants.KEY_TEST_FINISH, -1);
            if (isFinish == Constants.TEST_FINISH) {
                Log.d(TAG, "received a finish broadcast, save the result and run the next test");
                result=intent.getIntExtra("result", 0);
                code=intent.getStringExtra("code");
                if (code != null){
                    mHandler.post(mRunnable);
                }
                MainManager.getInstance(context).runNextTest();
            }
        }
    }

