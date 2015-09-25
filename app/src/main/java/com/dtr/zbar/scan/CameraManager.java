/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtr.zbar.scan;

import java.io.IOException;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.IntDef;
import android.util.Log;

/**
 * 邮箱: 1076559197@qq.com | tauchen1990@gmail.com
 * <p/>
 * 作者: 陈涛
 * <p/>
 * 日期: 2014年8月20日
 * <p/>
 * 描述: 该类主要负责对相机的操作
 */
public final class CameraManager {
    private static final String TAG = "CameraManager";

    @IntDef({Camera.CameraInfo.CAMERA_FACING_FRONT, Camera.CameraInfo.CAMERA_FACING_BACK})
    public @interface CameraFacing {
    }

    private final CameraConfigurationManager mConfigManager;

    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;
    private Camera.AutoFocusCallback mAutoFocusCallback;

    public CameraManager(Context context, Camera.PreviewCallback previewCb, Camera.AutoFocusCallback autoFocusCb) {
        this.mConfigManager = new CameraConfigurationManager(context);
        mPreviewCallback = previewCb;
        mAutoFocusCallback = autoFocusCb;
    }

    private void setupConfig() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.autoFocus(mAutoFocusCallback);
            mConfigManager.initFromCameraParameters(mCamera);

            Camera.Parameters parameters = mCamera.getParameters();
            String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
            // these,
            // temporarily
            try {
                mConfigManager.setDesiredCameraParameters(mCamera, false);
            } catch (RuntimeException re) {
                // Driver failed
                Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
                Log.i(TAG, "Resetting to saved mCamera params: " + parametersFlattened);
                // Reset:
                if (parametersFlattened != null) {
                    parameters = mCamera.getParameters();
                    parameters.unflatten(parametersFlattened);
                    try {
                        mCamera.setParameters(parameters);
                        mConfigManager.setDesiredCameraParameters(mCamera, true);
                    } catch (RuntimeException re2) {
                        // Well, darn. Give up
                        Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                    }
                }
            }
        }
    }


    public synchronized void openDriver(@CameraFacing int facing) throws IOException {
        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                mCamera = Camera.open(i);
                if (mCamera == null) {
                    Log.e(TAG, "Can't open camera '" + i + "'");
                }
                break;
            }
        }
        setupConfig();
    }


    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the mCamera will draw preview frames
     *               into.
     * @throws IOException Indicates the mCamera driver failed to open.
     */
    public synchronized void openDriver() throws IOException {
        mCamera = Camera.open();
        setupConfig();
    }

    public synchronized boolean isOpen() {
        return mCamera != null;
    }

    public Camera getmCamera() {
        return mCamera;
    }

    /**
     * Closes the mCamera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.autoFocus(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 获取相机分辨率
     *
     * @return
     */
    public Point getCameraResolution() {
        return mConfigManager.getCameraResolution();
    }
}
