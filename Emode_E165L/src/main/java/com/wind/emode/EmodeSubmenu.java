package com.wind.emode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Config;

import com.wind.emode.utils.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wind.emode.constants.EmodeIntent;
import com.wind.emode.constants.TestcaseType;
import com.wind.emode.data.Testcase;

import java.util.ArrayList;


public class EmodeSubmenu extends Activity {
    private MyAdapter mAdapter;
    private ListView mListview;
    private TextView mTitle;
    private String mCompletedStr;
    private String mTextResult;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                mAdapter.notifyDataSetInvalidated();
            }
        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Resources res = getResources();
        mTextResult = res.getString(R.string.submenu_text_result);
        mCompletedStr = res.getString(R.string.tip_completed);
        setContentView(R.layout.em_menu);

        mListview = (ListView) findViewById(R.id.ListView);
        mTitle = (TextView) findViewById(R.id.ctitile);

        switch (EmodeApp.mTestcaseType) {
        case TestcaseType.PHONE:
            mAdapter = new MyAdapter(this, EmodeApp.mPhoneTestcases);
            mListview.setAdapter(mAdapter);

            mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent(EmodeIntent.ACTION_EMODE_MENU);
                        intent.putExtra(EmodeIntent.EXTRA_EMODE_CODE,
                            EmodeApp.mPhoneTestcases.get(arg2).code);
                        sendBroadcast(intent);
                    }
                });


            break;

        case TestcaseType.BOARD:
            //mTitle.setText(mCompletedStr + "0/" + mBoardTestCases.size());
            mAdapter = new MyAdapter(this, EmodeApp.mBoardTestcases);
            mListview.setAdapter(mAdapter);

            mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent(EmodeIntent.ACTION_EMODE_MENU);
                        intent.putExtra(EmodeIntent.EXTRA_EMODE_CODE,
                            EmodeApp.mBoardTestcases.get(arg2).code);
                        sendBroadcast(intent);
                    }
                });


            break;

        case TestcaseType.OTHER:
            mTitle.setVisibility(View.GONE);					
            //mTitle.setText(mCompletedStr + "0/" + mOtherTestCases.size());
            mAdapter = new MyAdapter(this, EmodeApp.mOtherTestcases);
            mListview.setAdapter(mAdapter);

            mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent(EmodeIntent.ACTION_EMODE_MENU);
                        intent.putExtra(EmodeIntent.EXTRA_EMODE_CODE,
                            EmodeApp.mOtherTestcases.get(arg2).code);
                        sendBroadcast(intent);
                    }
                });


            break;

        default:
            finish();

            break;
        }
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    protected void onResume() {
        mAdapter.onResume();
        super.onResume();
    }


    public class MyAdapter extends BaseAdapter {
        private ArrayList<Testcase> mTestCases;
        private LayoutInflater mInflater;
        private int[] mResults;
        private Configuration mConfiguration;

        public MyAdapter(Context context, ArrayList<Testcase> testCases) {
            mConfiguration = context.getResources().getConfiguration();
            mTestCases = testCases;
            mInflater = LayoutInflater.from(context);
            mResults = new int[testCases.size()];
        }

        private void sum() {
            int notests = 0;
            int succs = 0;
            int fails = 0;
            int len = mResults.length;

            for (int i = 0; i < len; i++) {
                if (mResults[i] == 1) {
                    succs++;
                } else if (mResults[i] == 2) {
                    fails++;
                } else {
                    notests++;
                }
            }

            mTitle.setText(String.format(mTextResult, succs, fails, notests));
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;

            if ((convertView == null) || (convertView.getTag() == null)) {
                view = mInflater.inflate(R.layout.em_menu_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) view.findViewById(R.id.img);
                holder.text = (TextView) view.findViewById(R.id.txt);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            if ("cn".equalsIgnoreCase(mConfiguration.locale.getCountry())) {
                holder.text.setText(((Testcase) getItem(pos)).cnName);
            } else {
                holder.text.setText(((Testcase) getItem(pos)).enName);
            }

            if (mResults[pos] == 1) {
                holder.image.setImageResource(R.drawable.ic_right);
            } else if (mResults[pos] == 2) {
                holder.image.setImageResource(R.drawable.ic_wrong);
            } else {
                holder.image.setImageBitmap(null);
            }

            return view;
        }

        public void onResume() {
           /* ContentResolver cr = getContentResolver();
            String org_results = Settings.System.getString(cr,
                    (EmodeApp.mTestcaseType == TestcaseType.BOARD) ? "emode_board_results"
                                                                   : "emode_phone_results");*/
            
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(EmodeSubmenu.this);        
            String org_results = sp.getString((EmodeApp.mTestcaseType == TestcaseType.BOARD ? "emode_board_results": "emode_phone_results"),"");     
            Log.d("lizusheng", "org_results :"+org_results );
            Log.d("lizusheng", "EmodeApp.mTestcaseType :"+EmodeApp.mTestcaseType );
            
            if (org_results != null) {
                String[] pairs = org_results.split(",");
                String[] tp;

                //SharedPreferences sp = getSharedPreferences("results", Context.MODE_PRIVATE);
                int len = mResults.length;
                int s = pairs.length;

                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < s; j++) {
                        tp = pairs[j].split("=");

                        if (tp[0].equals(mTestCases.get(i).code)) {
                            mResults[i] = Integer.parseInt(tp[1]);
                        }
                    }
                }

                notifyDataSetChanged();
            }

            sum();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mTestCases.size();
        }

        @Override
        public Object getItem(int pos) {
            // TODO Auto-generated method stub
            return mTestCases.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            // TODO Auto-generated method stub
            return pos;
        }

        class ViewHolder {
            TextView text;
            ImageView image;
        }
    }
}
