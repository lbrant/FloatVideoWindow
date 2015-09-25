/*
 * Barebones implementation of displaying camera preview.
 * Created by lisah0 on 2012-02-24
 */
package com.dtr.zbar.scan;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private Camera mCamera;
    private boolean mPreviewing;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        /*
         * Set camera to continuous focus if supported, otherwise use software
		 * auto-focus. Only works for API level >=9.
		 */
        /*
         * Camera.Parameters parameters = camera.getParameters(); for (String f
		 * : parameters.getSupportedFocusModes()) { if (f ==
		 * Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
		 * mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		 * autoFocusCallback = null; break; } }
		 */

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        if (camera == mCamera) {
            return;
        }
        mCamera = camera;
        startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        // Camera preview released in activity
        release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        /*
         * If your preview can change or rotate, take care of those events here.
		 * Make sure to stop the preview before resizing or reformatting it.
		 */
        if (getHolder().getSurface() == null) {
            // preview surface does not exist
            return;
        }

        startPreview();
    }

    private void startPreview() {
        if (mPreviewing) {
            stopPreview();
        }

        if (mCamera != null && getHolder().getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
                mPreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    public void setCameraDisplayOrientation(int facing, int rotation) {
        Log.d(TAG, "setCameraDisplayOrientation: facing = " + facing + ", rotation = " + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    // compensate the mirror
                    result = (360 - result) % 360;
                } else {
                    // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }

                Log.d(TAG, "setDisplayOrientation: " + result);
                mCamera.setDisplayOrientation(result);
                break;
            }
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mPreviewing = false;
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
        }
    }

//    public void autoFocus(AutoFocusCallback cb) {
//        if (mPreviewing && mCamera != null && cb != null) {
//            mCamera.autoFocus(cb);
//        }
//    }

    public void release() {
        stopPreview();
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            mCamera = null;
        }
    }
}
