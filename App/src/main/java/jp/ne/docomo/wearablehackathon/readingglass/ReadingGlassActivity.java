package jp.ne.docomo.wearablehackathon.readingglass;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ReadingGlassActivity extends Activity {

    static final String TAG = ReadingGlassDrawer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SurfaceView surfaceView = new SurfaceView(this);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(mSurfaceListener);

        setContentView(surfaceView);
    }

    SurfaceHolder mHolder;

    Camera mCamera;

    SurfaceHolder.Callback mSurfaceListener = new MySurfaceCallback();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        super.onPause();
        this.finish();
    }

    class MySurfaceCallback implements SurfaceHolder.Callback {
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

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.d(TAG, "surfaceChanged, format=" + format + ", w=" + width + ", h=" + height);
            mHolder = holder;

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

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");

            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            mHolder = null;
        }
    }
}