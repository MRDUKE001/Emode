package com.wind.emode.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.wind.emode.R;
import com.wind.emode.base.BaseActivity;
import com.wind.emode.base.CommonAdapter;
import com.wind.emode.base.ViewHolder;
import com.wind.emode.manager.MainManager;
import com.wind.emode.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lizusheng on 2015/12/25.
 */
public class MainActivity extends BaseActivity {
    private GridView gridView;
    private ArrayList <HashMap<String,Integer>> mApapterList;
    private CommonAdapter<HashMap<String,Integer>> mCommonAdapter;
    private static final int INDEX_TEST_ALL = 0;
    private static final int INDEX_TEST_SINGLE = 1;
    private static final int INDEX_UPLOAD = 2;
    private static final int INDEX_HELP = 3;
    private static MainManager mMainManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        android.app.ActionBar mActionbar = this.getActionBar();
        mActionbar.setDisplayShowHomeEnabled(false);
        mActionbar.setDisplayHomeAsUpEnabled(false);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.layout_actionbar_main);
    }

    @Override
    protected void findViewById() {
        gridView= (GridView) findViewById(R.id.gridView_main);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mMainManager = MainManager.getInstance(this);
        mApapterList=new ArrayList<HashMap<String,Integer>>();
        HashMap<String,Integer> mAdapterMap=new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_test_all_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_test_all_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap=new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_test_single_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_single_test_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap=new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_upload_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_repair_n);
        mApapterList.add(mAdapterMap);

        mAdapterMap=new HashMap<String, Integer>();
        mAdapterMap.put("Name", R.string.item_help_main);
        mAdapterMap.put("Image", R.mipmap.asus_diagnostic_ic_help_n);
        mApapterList.add(mAdapterMap);
        Log.d(TAG, "initView has finished");

        mCommonAdapter=new CommonAdapter<HashMap<String, Integer>>(this,mApapterList,R.layout.item_gridview_main) {
            @Override
            public void convert(ViewHolder holder, HashMap<String, Integer> item) {
                holder.setImageResource(R.id.iv_gv_main,item.get("Image"));
                holder.setText(R.id.tv_gv_main, getString(item.get("Name")));
            }
        };
        gridView.setAdapter(mCommonAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case INDEX_TEST_ALL:
                        mMainManager.mIsAutoTest = true;
                        mMainManager.startAllTest();
                        break;
                    case INDEX_TEST_SINGLE:
                        startActivity(new Intent(MainActivity.this,SingleTestActivity.class));
                        break;
                    case INDEX_UPLOAD:
                        break;
                    case INDEX_HELP:
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_test_result:
                startActivity(new Intent(MainActivity.this,ResultActivity.class));
                break;
            case R.id.action_clear_cache:
                break;
            case R.id.action_reset:
                break;
            case R.id.action_about:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void getModelName() {

    }
}
