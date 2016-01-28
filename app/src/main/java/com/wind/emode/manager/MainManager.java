package com.wind.emode.manager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import com.wind.emode.receiver.ModuleTestReceiver;
import com.wind.emode.data.TestCase;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

/**
 * Created by lizusheng on 2015/12/25.
 */
public class MainManager {
    private final String TAG = this.getClass().getSimpleName();
    private final static boolean DEBUG = true;
    private Context context;
    private static MainManager mMainManager;
    private static ModuleManager mModuleManager;
    private BroadcastReceiver mModuleTestReceiver;

    public static boolean mIsAutoTest = false;
    public static boolean mIsSaveTestResultsToFile = false;
    public static ArrayList<TestCase> mTestCases;
    private static int RunningIndex = -1;
    private static final String modulePackageName = "com.wind.emode";


    public MainManager(Context context){
        this.context=context;
        mModuleTestReceiver=new ModuleTestReceiver();
        mModuleManager = ModuleManager.getInstance(context);
        mTestCases= new ArrayList<TestCase>();
    }

    public static MainManager getInstance(Context context){
        if (mMainManager == null){
            synchronized (MainManager.class){
                if (mMainManager == null){
                    mMainManager =new MainManager(context);
                }
            }
        }
        return mMainManager;
    }


    /**
     * 注册所有的广播
     */
    public void registerAll(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_MODULE_TEST);
        context.registerReceiver(mModuleTestReceiver, filter);
        if (DEBUG)Log.d(TAG, "mModuleTestReceiver has register");
    }

    /**
     * 解除注册的广播
     */
    public void unRegisterAll(){
        context.unregisterReceiver(mModuleTestReceiver);
        if (DEBUG)Log.d(TAG, "mModuleTestReceiver has unregister");
    }

    /**
     * 运行下一个测试模块
     */
    public void runNextTest(){

        if (++RunningIndex >= mTestCases.size()){
            onAllFinish();
            Log.d(TAG, "All test module finish");
            return;
        }

        Log.d(TAG,"RunningIndex :"+RunningIndex);
        runNextModule(RunningIndex);
    }

    /**
     * 获取下一个运行模块
     * @param position
     * @return method
     */
    public static void getNextModule(int position){
        String className =modulePackageName + mTestCases.get(position).getActivity();
        HashMap map = mModuleManager.getMethod(className);
        ArrayList<Object> objList =(ArrayList) map.get("object");
        ArrayList<Method> methodList =(ArrayList) map.get("method");
        Object obj = objList.get(0);
        Method method = methodList.get(0);
        try {
            method.invoke(obj,new Class[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // return mModuleManager.getMethod(className);
        //return method;
    }

    public void runNextModule(int position){
        String className =modulePackageName + ".testcase." +mTestCases.get(position).getActivity();
        Intent intent = new Intent().setComponent(new ComponentName(modulePackageName, className));
        context.startActivity(intent);
    }

    /**
     * 开始自动测试
     */
    public void startAllTest() {
        registerAll();
        if (!mModuleManager.mXmlParsed) {
            if (DEBUG)Log.d(TAG, "the mXmlParse is false , start to parse the Xml");
            mModuleManager.parseXML();
        }

        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        if (DEBUG)Log.d(TAG, "all the test module have load ");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                runNextTest();
                if (DEBUG)Log.d(TAG,"run the first test module");
            }
                @Override
                protected Void doInBackground (Void...voids){
                    return null;
                }
        }.execute();
    }

    /**
     * 结束所有的模块测试
     */
    public void finishAllTest(){
        unRegisterAll();
    }

    /**
     * 所有的模块测试完成
     */
    public void onAllFinish(){
        clear();
        unRegisterAll();
    }

    /**
     * 保存测试结果
     */
    public void saveResultToFile(){

    }

    /**
     * 清理测试模块
     */
    public void clear(){
        Log.d(TAG, "start to clear ... ");
        RunningIndex = -1;
    }

    /**
     * 测试单元运行结束时调用
     */
    public void sendFinishBroadcast(int result, final String moduleName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent();
                intent.putExtra(Constants.KEY_TEST_FINISH,Constants.TEST_FINISH);
                intent.putExtra("code",moduleName);
                intent.setAction(Constants.BROADCAST_MODULE_TEST);
                context.sendBroadcast(intent);
                if (DEBUG)Log.d(TAG,"send a broadcast to run the next test module");
            }
        }).start();
    }

    /**
     * 测试单元运行结束时的回调
     */
    private OnTestFinishedCallback callback;

    public interface OnTestFinishedCallback {
        public void onSaveTestResult();
    }

    /**
     * 设置回调
     * @param callBack
     */
    public void setOnTestFinishedCallback(OnTestFinishedCallback callBack) {
        this.callback = callBack;
    }


}
