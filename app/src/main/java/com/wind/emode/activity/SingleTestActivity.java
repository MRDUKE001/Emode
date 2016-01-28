package com.wind.emode.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.wind.emode.R;
import com.wind.emode.base.BaseActivity;
import com.wind.emode.base.CommonAdapter;
import com.wind.emode.base.ViewHolder;
import com.wind.emode.data.TestCase;
import com.wind.emode.manager.MainManager;
import com.wind.emode.manager.ModuleManager;
import com.wind.emode.testcase.BluetoothTestAct;
import com.wind.emode.utils.Log;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class SingleTestActivity extends BaseActivity{
    private ListView listView;
    private ArrayList <HashMap<String,Integer>> mApapterList;
    private CommonAdapter<HashMap<String,Integer>> mCommonAdapter;

    private MainManager mMainManager;
    private ModuleManager mModuleManager;
    public static ArrayList<TestCase> mTestCases =new ArrayList<TestCase>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_singletest);
        setTitleColor(R.color.silver);
        mMainManager = MainManager.getInstance(this);
        mModuleManager = mModuleManager.getInstance(this);
    }

    @Override
    protected void findViewById() {
        listView= (ListView) findViewById(R.id.lv_single_test);
    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        startActivity(new Intent(SingleTestActivity.this, BluetoothTestAct.class));
                }
            }
        });
    }

    /**
     * 加载所有测试单元
     */
    @Override
    protected void init() {

        if (!mModuleManager.mXmlParsed) {
            if (DBG)Log.d(TAG, "the mXmlParse is false , start to parse the Xml");
            mModuleManager.parseXML();
        }
        mTestCases = (ArrayList<TestCase>) mModuleManager.loadTestModule();
        mApapterList =new ArrayList <HashMap<String,Integer>>();
        Log.d(TAG, "size of mAdapter :"+mApapterList.size());
        for (TestCase module:mTestCases){
            HashMap<String,Integer> mAdapterMap=new HashMap<String, Integer>();
            switch (module.getEnName()){
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
          //  Log.d(TAG, "add to mAdapter :"+module.getEnName());
        }

        mCommonAdapter = new CommonAdapter<HashMap<String, Integer>>(this,mApapterList,R.layout.item_listview_singletest) {
            @Override
            public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                holder.setText(R.id.tv_lv_single_test, getString(item.get("Name")));
                holder.setImageResource(R.id.iv_lv_single_test,item.get("Image"));
            }
        };
        listView.setAdapter(mCommonAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_singletest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_test_result:
                break;
            case R.id.action_reset:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = this.getWindow().getDecorView();
        decorView.findViewById(R.id.btn_start).setVisibility(View.GONE);

    }

    @Override
    public void getModelName() {

    }
}
