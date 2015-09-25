package com.lbrant.floatvideowindow;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.dtr.zbar.scan.CameraManager;
import com.dtr.zbar.scan.CameraPreview;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private CameraPreview mCameraPreview;
    private CameraManager mCameraManager;
    private FrameLayout mFrameLayout;
    private ImageButton mImageButtonChangeCamera;
    private ImageButton mImageButtonVideoAction;

    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mScreenRotation;

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

        }
    };

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBtnChangeCamera:
                    if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        startVideo();
                    } else if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                        startVideo();
                    }
                    break;
                case R.id.imgBtnVideoAction:
                    if (mCameraManager.isOpen()) {
                        stopVideo();
                        mImageButtonVideoAction.setImageResource(R.drawable.answer_phone_background);
                    } else {
                        startVideo();
                        mImageButtonVideoAction.setImageResource(R.drawable.hang_up_background);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        findViews();

        mScreenRotation = getWindowManager().getDefaultDisplay().getRotation();
        mCameraManager = new CameraManager(this, mPreviewCallback, mAutoFocusCallback);
        setOnClickListener(mOnClickListener);
    }

    private void findViews() {
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mImageButtonChangeCamera = (ImageButton) findViewById(R.id.imgBtnChangeCamera);
        mImageButtonVideoAction = (ImageButton) findViewById(R.id.imgBtnVideoAction);
    }

    private void setOnClickListener(View.OnClickListener listener) {
        mImageButtonVideoAction.setOnClickListener(listener);
        mImageButtonChangeCamera.setOnClickListener(listener);
    }

    private void startVideo() {
        if (mCameraManager.isOpen()) {
            mCameraManager.closeDriver();
        }

        try {
            mCameraManager.openDriver(mCameraFacing);
            if (mCameraPreview == null) {
                mCameraPreview = new CameraPreview(this, mCameraManager.getmCamera());
            } else {
                mCameraPreview.setCamera(mCameraManager.getmCamera());
            }

            if (mCameraPreview.getParent() == null) {
                mFrameLayout.addView(mCameraPreview, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
            mCameraPreview.setCameraDisplayOrientation(mCameraFacing, mScreenRotation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopVideo() {
        mCameraPreview.stopPreview();
        mCameraPreview.release();
        mCameraManager.closeDriver();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenRotation = getWindowManager().getDefaultDisplay().getRotation();
        if (mCameraPreview != null) {
            mCameraPreview.setCameraDisplayOrientation(mCameraFacing, mScreenRotation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPreview != null) {
            mCameraPreview.release();
        }
        mCameraManager.closeDriver();
        mCameraManager = null;
        setOnClickListener(null);
        mOnClickListener = null;
    }
}
