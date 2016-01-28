package com.wind.emode.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wind.emode.R;
import com.wind.emode.base.BaseActivity;
import com.wind.emode.base.CommonAdapter;
import com.wind.emode.base.ViewHolder;
import com.wind.emode.data.TestCase;
import com.wind.emode.manager.MainManager;
import com.wind.emode.manager.ModuleManager;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lizusheng on 2016/1/2.
 */
public class ResultActivity extends BaseActivity{
    private TextView mTvTitle;
    private ListView mListView;
    private CommonAdapter mCommonAdapter;
    private ModuleManager mModuleManager;
    public static ArrayList<TestCase> mTestCases =new ArrayList<TestCase>();
    private static ArrayList <HashMap<String,Integer>> mApapterList;
    private int mPassCount = 0;
    private int mFailedCount = 0;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_result);
    }

    @Override
    protected void findViewById() {
        mTvTitle= (TextView) findViewById(R.id.tv_title_result);
        mListView= (ListView) findViewById(R.id.lv_result_test);
    }

    @Override
    protected void setListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    protected void init() {
        mModuleManager=ModuleManager.getInstance(this);
        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        mApapterList = new ArrayList<HashMap<String, Integer>>();
        Log.d(TAG, "size of mAdapter :" + mApapterList.size());
        for (TestCase module : mTestCases) {
            HashMap<String, Integer> mAdapterMap = new HashMap<String, Integer>();
            switch (module.getEnName()) {
                case "Bluetooth Test":
                    mAdapterMap.put("Name", R.string.text_test_bluetooth);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_bt_test);
                    Log.d(TAG, "add Bluetooth to map");
                    break;
                case "WiFi Test":
                    mAdapterMap.put("Name", R.string.text_test_wifi);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_wifibus_test);
                    break;
                case "Battery Test":
                    mAdapterMap.put("Name", R.string.text_test_battery);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_battery_test);
                    break;
                case "ECompass Test":
                    mAdapterMap.put("Name", R.string.text_test_ecompass);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_ecompass_test);
                    break;
                default:
                    mAdapterMap.put("Name", R.string.text_test_ecompass);
                    mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_ecompass_test);
                    break;
            }
            mApapterList.add(mAdapterMap);
        }

            geResult();

            mCommonAdapter = new CommonAdapter<HashMap<String, Integer>>(this, mApapterList, R.layout.item_listview_result) {
                @Override
                public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                    holder.setText(R.id.tv_lv_result, getString(item.get("Name")));
                    holder.setImageResource(R.id.icon_lv_result, item.get("Image"));
                    int resultCode = item.get("Result");
                    if (resultCode == 0) {
                        holder.setImageResource(R.id.res_lv_result, R.drawable.asus_diagnostic_ic_pass2);
                    } else {
                        holder.setImageResource(R.id.res_lv_result, R.drawable.asus_diagnostic_ic_fail2);
                    }
                }
            };
            mListView.setAdapter(mCommonAdapter);
            mTvTitle.setText("Pass:" + mPassCount + "    " + "Failed:" + mFailedCount +
                    "    " + "UnTest:" + (mApapterList.size() - (mPassCount + mFailedCount)));
        }

    /**
     * 获取测试结果
     */
    public void geResult(){
        SharedPreferences sp = getSharedPreferences(Constants.PREF_NAME_TEST_RESULTS, Context.MODE_WORLD_WRITEABLE);
        String results = sp.getString(Constants.PREF_KEY_TEST_RESULTS_PHONE, "");
        if (!TextUtils.isEmpty(results)){
            String[] testModules = results.split(",");
            for (int i=0;i<mTestCases.size();i++){
                for (int j=0;j<testModules.length;j++){
                    String[] testModult = testModules[j].split("=");
                    if (mTestCases.get(i).getEnName().equals(testModult[0])){
                        mApapterList.get(i).put("Result", Integer.parseInt(testModult[1]));
                        if(Integer.parseInt(testModult[1]) == 0){
                            mPassCount++;
                        }else{
                            mFailedCount++;
                        }
                        Log.d(TAG, "mPassCount:" + mPassCount + "mFailedCount" + mFailedCount);
                        Log.d(TAG,"Module EnName:"+mTestCases.get(i).getEnName());
                    }
                }
            }
        }else {
            return;
        }
    }

    @Override
    public void getModelName() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = this.getWindow().getDecorView();
        decorView.findViewById(R.id.btn_start).setVisibility(View.GONE);
    }


}
