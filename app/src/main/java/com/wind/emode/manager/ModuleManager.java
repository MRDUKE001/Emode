package com.wind.emode.manager;

import android.content.Context;
import com.wind.emode.data.TestCase;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;
import com.wind.emode.utils.XmlParse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class ModuleManager {

    private final String TAG = this.getClass().getSimpleName();
    private static ModuleManager mModuleManager;
    private Context context;

    public static HashMap<String, TestCase> mTestcases = new HashMap<String, TestCase>();
    public static ArrayList<TestCase> mTestModule = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mPhoneTestcases = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mBoardTestcases = new ArrayList<TestCase>();
    public static ArrayList<TestCase> mOtherTestcases = new ArrayList<TestCase>();
    public static int mTestcaseType = Constants.TESTCASE_TYPE_DEFAULT;
    public static boolean mXmlParsed = false;
    public static boolean mIsCustomizeErrorCode = false;

    public ModuleManager(Context context) {
        this.context=context;
    }

    public static ModuleManager getInstance(Context context) {
        if (mModuleManager == null) {
            synchronized (ModuleManager.class) {
                if (mModuleManager == null) {
                    mModuleManager = new ModuleManager(context);
                }
            }
        }
        return mModuleManager;
    }

    /**
     * 清理测试单元
     */
    public void clearTestCase(){
        mTestcases.clear();
        mPhoneTestcases.clear();
        mBoardTestcases.clear();
        mOtherTestcases.clear();
        Log.d(TAG,"all the testcases has clear");
    }

    /**
     * 解析测试项的XML配置文件
     */
    public void parseXML(){
        clearTestCase();
        XmlParse.parseXml(context);
    //    mXmlParsed = true;
        Log.d(TAG, "start to parse XML");
    }

    /**
     * 加载测试单元
     */
    public List<TestCase> loadTestModule(){
        Log.d(TAG, "start to load test module ...");
        switch (mTestcaseType){
            case Constants.TESTCASE_TYPE_PHONE:
                return mPhoneTestcases;
            case Constants.TESTCASE_TYPE_BOARD:
                return mBoardTestcases;
            case Constants.TESTCASE_TYPE_OTHER:
                return mOtherTestcases;
            default:
                return mTestModule;
        }
    }

    /**
     * 加载测试单元的启动方法
     * @param className
     * @return
     */
    public HashMap getMethod(String className){
        ArrayList <Object> obj = new ArrayList<Object>();
        ArrayList <Method> methods = new ArrayList<Method>();
        HashMap<String,List> res = new HashMap<String,List>();
        Method method = null;
        try {
            Class cls=Class.forName(className);
            Object o = cls.newInstance();
            Class paramType[] =new Class[0];
            method=cls.getMethod("startTest",new Class[]{});

            obj.add(o);
            methods.add(method);
            res.put("object", obj);
            res.put("method",methods);
            Log.d(TAG,"get the Method :"+method);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getErrorCode(String moduleCode){
        for (TestCase testCase:mTestModule){
            if (testCase.getEnName().equals(moduleCode))
                return mTestcases.get(moduleCode).getErrorCode();
        }
        return 0000;
    }

    public String getEnName(String moduleCode){
        for (TestCase testCase:mTestModule){
            if (testCase.getEnName().equals(moduleCode))
                return mTestcases.get(moduleCode).getEnName();
        }
        return "";
    }

}