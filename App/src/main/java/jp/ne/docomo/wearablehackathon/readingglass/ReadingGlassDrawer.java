package jp.ne.docomo.wearablehackathon.readingglass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class ReadingGlassDrawer implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    public void draw() {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1);
            canvas.drawLine(0, 0, 640, 360, paint);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
