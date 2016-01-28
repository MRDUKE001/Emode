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
import com.wind.emode.manager.MainManager;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.Log;

/**
 * Created by lizusheng on 2015/12/29.
 */
public class BluetoothTesting extends BaseActivity{
    private TextView tv_status;
    private TextView tv_address;
    private TextView tv_device;
    private static final int MSG_BT_ON = 100;
    private static final int MSG_BT_OFF = 101;
    private static final int MSG_BT_TEST_PASS = 102;
    private static final int MSG_BT_TEST_FAIL = 103;
    private boolean mOrigBTState = false;
    private BluetoothAdapter mBtAdapter = null;
    private StringBuilder mDeviceList = new StringBuilder();
    private static final String mModuleName = "Bluetooth Test";

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_testing_bt);
    }

    @Override
    protected void findViewById() {
        tv_status= (TextView) findViewById(R.id.tv_status_bt);
        tv_address= (TextView) findViewById(R.id.tv_address_bt);
        tv_device= (TextView) findViewById(R.id.tv_device_bt);
    }


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
                    tv_status.setText(getString(R.string.status_test_bt) + "  " +getString(R.string.status_on_bt));
                    tv_address.setText(getString(R.string.address_test_bt)+ "  "
                            +mBtAdapter.getAddress());
                    break;
                case MSG_BT_TEST_PASS:
                    disEnableBluetooth();
                    finishActivity(1);
                    Log.d(TAG, "Bluetooth test is pass");
                    break;
                case MSG_BT_TEST_FAIL:
                    disEnableBluetooth();
                    finishActivity(2);
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
                tv_device.setText(getString(R.string.devicelist_test_bt)+"\n"+mDeviceList);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //    mScanStartTime = System.currentTimeMillis();
                tv_status.setText(getString(R.string.status_test_bt)+ getString(R.string.status_scanning_bt));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mDeviceList.length() == 0) {
                    mHandler.sendEmptyMessageDelayed(MSG_BT_TEST_FAIL, 500);
                    tv_status.setText(getString(R.string.no_bt_device));
                }
            }
        }
    };

    /**
     * 清理状态
     */
    public void clearStatus(){
        tv_status.setText(getString(R.string.status_test_bt)+ getString(R.string.status_off_bt));
        tv_address.setText(getString(R.string.address_test_bt)+ getString(R.string.unknown));
        tv_device.setText(getString(R.string.devicelist_test_bt));
    }

    /**
     * 测试正常结束返回
     * 根据是单项测试/自动测试：
     * 自动测试 --- MainManager.getInstance().sendFinishBroadcast()
     * 单项测试 --- 更新测试结果
     * @param resultCode
     */
    public void finishActivity(int resultCode){
       if (MainManager.getInstance(this).mIsAutoTest){
           MainManager.getInstance(this).sendFinishBroadcast(resultCode,mModuleName);
           finish();
           Log.d(TAG,"BluetoothTest finished ,send broadcast to run the next test module");
       }else {
           setResult(resultCode);
           finish();
       }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {

    }

    @Override
    public void startTest() {
        disEnableBluetooth();
        clearStatus();
        enableBluetooth();
        Log.d(TAG,"start to test bluetooth");
    }

    @Override
    public void getModelName() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().findViewById(R.id.btn_start).setVisibility(View.GONE);
        register();
        startTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disEnableBluetooth();
        unregisterReceiver(mReceiver);
    }

    /**
     * 手动结束测试返回
     */
    @Override
    public void onBackPressed() {
        disEnableBluetooth();
        setResult(2);
        super.onBackPressed();
    }
}
