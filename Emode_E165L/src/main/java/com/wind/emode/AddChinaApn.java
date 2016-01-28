/***************************************/
package com.wind.emode;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentUris;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.Telephony;
import android.net.Uri;
public class AddChinaApn extends Activity {

	TextView title = null;
	Button start = null;
	Button stop = null;
      public static final Uri CONTENT_URI = Uri.parse("content://telephony/carriers");
	static   boolean isAddedApn = false;
//carries_name apn_name  proxy       port mmsproxy mmsport user server password mmsc type mcc mnc
      String[][] apnChinaSets = new  String[][]{
      {"CMNET", "cmnet",   "",              "",    "",    "",   "",   "",    "",   "",  "default,supl,net",                                 "460","00"},
      {"CMWAP","cmwap", "10.0.0.172","80",  "",   "",   "",   "",    "",   "",    "",                                                     "460","00"},
      {"(China Mobile)","cmwap", "10.0.0.172","80", "10.0.0.172","80","","","","http://mmsc.monternet.com", "mms",  "460","00"},
      {"3g(China Unicom)","3gnet", "","", "","","","","","",                              "default,supl,net",        "460","01"},
      {"GPRS (China Unicom)","uninet", "","", "","","","","","","default,supl,net",                "460","01"},
      {"(China Unicom)","3gwap", "10.0.0.172","80", "","","","","","","",                "460","01"},    
      {"(China Unicom)","3gwap", "","", "10.0.0.172","80","","","","http://mmsc.myuni.com.cn","mms",                "460","01"},   
       {"(China Unicom)","uniwap", "","", "10.0.0.172","80","","","","http://mmsc.myuni.com.cn","mms",                "460","01"},  
      {"CMNET", "cmnet",   "",              "",    "",    "",   "",   "",    "",   "",  "default,supl,net",                                 "460","02"},
      {"CMWAP","cmwap", "10.0.0.172","80",  "",   "",   "",   "",    "",   "",    "",                                                     "460","02"},
      {"(China Mobile)","cmwap", "10.0.0.172","80", "10.0.0.172","80","","","","http://mmsc.monternet.com", "mms",  "460","02"}, 
      {"CMNET", "cmnet",   "",              "",    "",    "",   "",   "",    "",   "",  "default,supl,net",                                 "460","07"},
      {"CMWAP","cmwap", "10.0.0.172","80",  "",   "",   "",   "",    "",   "",    "",                                                     "460","07"},
      {"(China Mobile)","cmwap", "10.0.0.172","80", "10.0.0.172","80","","","","http://mmsc.monternet.com", "mms",  "460","07"},      
                                                                 };	  
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.em_addchianapn);
		title = (TextView) findViewById(R.id.title);
		start = (Button) findViewById(R.id.start);
		stop=(Button) findViewById(R.id.stop);
		title.setText("add china apn");

		start.setText("add china apn");
		//stop.setText("reset add flag");
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(!isAddedApn)
				{
                              addChinaApn();	
				}

				if(isAddedApn)
				{
				    start.setEnabled(false);
				}
			}
		});

		//stop.setText("Unroot");
		//stop.setEnabled(false);
    }
   private boolean addChinaApn() {
        ContentValues values = new ContentValues();   

        // Add a dummy name "Untitled", if the user exits the screen without adding a name but 
        // entered other information worth keeping.
        for(int i=0;i<apnChinaSets.length; i++)
	{
	 
        values.put(Telephony.Carriers.NAME,apnChinaSets[i][0]);
        values.put(Telephony.Carriers.APN, apnChinaSets[i][1]);
        values.put(Telephony.Carriers.PROXY, apnChinaSets[i][2]);
        values.put(Telephony.Carriers.PORT, apnChinaSets[i][3]);
        values.put(Telephony.Carriers.MMSPROXY,apnChinaSets[i][4]);
        values.put(Telephony.Carriers.MMSPORT, apnChinaSets[i][5]);
        values.put(Telephony.Carriers.USER, apnChinaSets[i][6]);
        values.put(Telephony.Carriers.SERVER, apnChinaSets[i][7]);
        values.put(Telephony.Carriers.PASSWORD, apnChinaSets[i][8]);
        values.put(Telephony.Carriers.MMSC, apnChinaSets[i][9]);

       // String authVal = mAuthType.getValue();
        //if (authVal != null) {
        //    values.put(Telephony.Carriers.AUTH_TYPE, Integer.parseInt(authVal));
        //}

        values.put(Telephony.Carriers.PROTOCOL, "");
        values.put(Telephony.Carriers.ROAMING_PROTOCOL, "");

        values.put(Telephony.Carriers.TYPE, apnChinaSets[i][10]);
        

      //  values.put(Telephony.Carriers.CARRIER_ENABLED, mCarrierEnabled.isChecked() ? 1 : 0);

        
        values.put(Telephony.Carriers.MCC, apnChinaSets[i][11]);
        values.put(Telephony.Carriers.MNC, apnChinaSets[i][12]);

        values.put(Telephony.Carriers.NUMERIC, apnChinaSets[i][11] + apnChinaSets[i][12]);



       // String bearerVal = mBearer.getValue();
       // if (bearerVal != null) {
        //    values.put(Telephony.Carriers.BEARER, Integer.parseInt(bearerVal));
        //}

        values.put(Telephony.Carriers.MVNO_TYPE, "");
        values.put(Telephony.Carriers.MVNO_MATCH_DATA, "");

        //ApnUtils.SOURCE_TYPE_USER_EDIT : 1
        values.put(Telephony.Carriers.SOURCE_TYPE, 1); 


          getContentResolver().insert(CONTENT_URI, values);
	}
        //if (mUri != null) {
      //      getContentResolver().update(mUri, values, null, null);
       // }	
       isAddedApn = true;
       return true;
   }
}

