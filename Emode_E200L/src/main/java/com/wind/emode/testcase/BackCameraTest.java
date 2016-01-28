package com.wind.emode.testcase;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import com.wind.emode.BaseActivity;
import com.wind.emode.EmodeApp;
import com.wind.emode.R;
import com.wind.emode.utils.Constants;
import com.wind.emode.utils.FileUtils;
import com.wind.emode.utils.Log;


public class BackCameraTest extends BaseActivity {
    private ImageView mPhotoView;
    private Uri mTempPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_camera);

        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(getString(R.string.title_camera_test_back));
        mPhotoView = (ImageView) findViewById(R.id.photo);
        //mTempPhotoUri = FileUtils.generateTempPhotoUri(this);
        if ((savedInstanceState == null) || !savedInstanceState.getBoolean("CameraTest_ed")) {
			Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA", null);
			intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_BACK);
			startActivity(intent);	
            /*			
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra("android.intent.extras.CAMERA_FACING",
                Camera.CameraInfo.CAMERA_FACING_BACK);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempPhotoUri);
            intent.putExtra("from_emode_test", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, mTempPhotoUri));
            startActivityForResult(intent, 0);
			*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.d(this, "onActivityResult, requestCode = " + requestCode + ",resultCode = " +
            resultCode);

        if (resultCode == Activity.RESULT_OK) {
            mPhotoView.setImageURI(mTempPhotoUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("CameraTest_ed", true);
    }
}
