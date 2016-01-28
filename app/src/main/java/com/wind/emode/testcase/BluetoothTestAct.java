package com.wind.emode.testcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wind.emode.R;
import com.wind.emode.base.BaseActivity;
import com.wind.emode.base.IModel;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class BluetoothTestAct extends BaseActivity{
    private Button mBtnStart;
    private TextView tv_result;
    private ImageView iv_result;
/*    private static final int MSG_BT_ON = 100;
    private static final int MSG_BT_OFF = 101;
    private static final int MSG_BT_TEST_PASS = 102;
    private static final int MSG_BT_TEST_FAIL = 103;
    private boolean mOrigBTState = false;
    private BluetoothAdapter mBtAdapter = null;
    private StringBuilder mDeviceList = new StringBuilder();*/

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_common);
      //  register();
    }

    @Override
    protected void findViewById() {
        ((ImageView)findViewById(R.id.iv_common)).setImageResource(R.mipmap.img_wifi_test);
        mBtnStart = (Button) this.getWindow().getDecorView().findViewById(R.id.btn_start);
        tv_result= (TextView) findViewById(R.id.tv_result_common);
        iv_result= (ImageView) findViewById(R.id.iv_result_common);
    }

/*

    public void register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);
    }

    public void enableBluetooth(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            finish();
            Log.d(TAG, "BtAdapter is null");
        } else {
            if (!mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
                Log.d(TAG, "enable BtAdapter");
            } else {
                mOrigBTState = true;
            }
            if (mBtAdapter.isEnabled()) {
                mHandler.sendEmptyMessage(MSG_BT_ON);
            }
        }
    }

    public void disEnableBluetooth(){
        Log.d(TAG, "disEnableBluetooth");
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
            if (mBtAdapter.isEnabled()) {
                mBtAdapter.disable();
                Log.d(TAG, "disEnable BtAdapter");
            }

            if (!mOrigBTState) {
                Intent newIntent = new Intent(Constants.ACTION_EMODE_RESTORE_BT);
                newIntent.setPackage(getPackageName());
                startService(newIntent);
            }
        }
    }


    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what) {
                case MSG_BT_ON:
                    Log.d(TAG, "start to disCovery");
                    mBtAdapter.startDiscovery();
                    break;
                case MSG_BT_TEST_PASS:
                        tv_result.setVisibility(View.VISIBLE);
                        iv_result.setVisibility(View.VISIBLE);
                        iv_result.setImageResource(R.drawable.asus_diagnostic_ic_pass);
                        tv_result.setText(getString(R.string.pass_result_bluetooth));
                        tv_result.setTextColor(Color.GREEN);
                        mBtnStart.setText(getString(R.string.btn_test_again));
                        disEnableBluetooth();
                    Log.d(TAG, "Bluetooth test is pass");
                    break;
                case MSG_BT_TEST_FAIL:
                        tv_result.setVisibility(View.VISIBLE);
                        iv_result.setVisibility(View.VISIBLE);
                        tv_result.setText(getString(R.string.failed_result_bluetooth));
                        tv_result.setTextColor(Color.RED);
                        mBtnStart.setText(getString(R.string.btn_test_again));
                        disEnableBluetooth();
                    Log.d(TAG, "Bluetooth test is failed");
                    break;
                default:
                    break;
            }
        }

    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    mHandler.sendEmptyMessage(MSG_BT_ON);
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDeviceList.toString().contains(device.getAddress())) {
                    mDeviceList.append(device.getName());
                    mDeviceList.append(":");
                    mDeviceList.append(device.getAddress());
                    mDeviceList.append("\n");
                    mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_PASS, 500);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //    mScanStartTime = System.currentTimeMillis();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mDeviceList.length() == 0) {
                    mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_FAIL, 500);
                }
            }
        }
    };
*/

    /**
     * 清理状态
     */
    public void clearStatus(){
        if (tv_result.getVisibility()==View.VISIBLE)tv_result.setVisibility(View.GONE);
        if (iv_result.getVisibility()==View.VISIBLE) iv_result.setVisibility(View.GONE);
    }

    /**
     * 设置状态
     * @param result
     */
    public void setStatus(int result){
        tv_result.setVisibility(View.VISIBLE);
        iv_result.setVisibility(View.VISIBLE);
        switch (result){
            case 0:
                iv_result.setImageResource(R.drawable.asus_diagnostic_ic_pass);
                tv_result.setText(getString(R.string.pass_result_bluetooth));
                tv_result.setTextColor(Color.GREEN);
                mBtnStart.setText(getString(R.string.btn_test_again));
                break;
            case 1:
                tv_result.setText(getString(R.string.failed_result_bluetooth));
                tv_result.setTextColor(Color.RED);
                mBtnStart.setText(getString(R.string.btn_test_again));
                break;
        }

    }

    public void setBtnText(){
        tv_result.setVisibility(View.VISIBLE);
        tv_result.setText(getString(R.string.text_testing));
        tv_result.setTextColor(Color.RED);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {

    }

    /**
     * 开始测试
     */
    @Override
    public void startTest() {
     /*   disEnableBluetooth();
        clearStatus();
        setBtnText();
        enableBluetooth();*/
        clearStatus();
        setBtnText();
        startActivityForResult(new Intent(BluetoothTestAct.this,BluetoothTesting.class),0);
        Log.d(TAG,"start to test bluetooth");
    }

    /**
     * 接收测试返回结果进行相应处理
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 0:
              setStatus(0);
                break;
            case 1:
              setStatus(1);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reStartTest() {

    }

    @Override
    public void getModelName() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 //       unregisterReceiver(mReceiver);
    }
}
