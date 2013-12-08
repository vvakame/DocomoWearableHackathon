package jp.ne.docomo.wearablehackathon.readingglass;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.glass.timeline.LiveCardCallback;

import java.io.IOException;
import java.util.List;

public class ReadingGlassDrawer implements LiveCardCallback {

    static final String TAG = ReadingGlassDrawer.class.getSimpleName();

    private SurfaceHolder mHolder;

    Camera mCamera;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        mHolder = holder;

        Log.d(TAG, "Has " + Camera.getNumberOfCameras() + " camera.");
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            Log.d(TAG, "Id=" + i + ", " + info.facing + ", " + info.orientation);
        }

        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            Log.e(TAG, "an error occured!", e);
            mCamera.release();
            mCamera = null;
        }
    }

    int mFormat = -1;
    int mWidth = -1;
    int mHeight = -1;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged, format=" + format + ", w=" + width + ", h=" + height);
        mHolder = holder;
        mFormat = format;
        mWidth = width;
        mHeight = height;

        if (mCamera == null) {
            mCamera = Camera.open(0);
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                Log.e(TAG, "an error occured!", e);
                mCamera.release();
                mCamera = null;
                return;
            }
        }
        mCamera.stopPreview();

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size applySize = sizes.get(0);
        for (Camera.Size size : sizes) {
            Log.d(TAG, "size=" + size.width + "," + size.height);
            if (size.width == width && size.height == height) {
                applySize = size;
            }
        }
        Log.d(TAG, "use size=" + applySize.width + "," + applySize.height);
        // http://stackoverflow.com/questions/19235477/google-glass-preview-image-scrambled-with-new-xe10-release
        parameters.setPreviewFpsRange(30000, 30000);
        Log.d(TAG, "isZoomSupported:" + parameters.isZoomSupported());
        Log.d(TAG, "getMaxZoom:" + parameters.getMaxZoom());
        parameters.setZoom(parameters.getMaxZoom());
        parameters.setPreviewSize(applySize.width, applySize.height);

        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            Log.e(TAG, "an error occured!", e);
            mCamera.release();
            mCamera = null;
            return;
        }
        mCamera.startPreview();
        }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        mHolder = null;
    }

    @Override
    public void renderingPaused(SurfaceHolder holder, boolean paused) {
        Log.d(TAG, "renderingPaused paused=" + paused);

        if (paused) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } else {
            surfaceChanged(holder, mFormat, mWidth, mHeight);
        }
    }
}
