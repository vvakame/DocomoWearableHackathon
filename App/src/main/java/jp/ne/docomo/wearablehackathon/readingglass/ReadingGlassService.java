package jp.ne.docomo.wearablehackathon.readingglass;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

public class ReadingGlassService extends Service {

    private static final String LIVE_CARD_ID = "FallIntoDoze";

    public class TimerBinder extends Binder {
    }

    private final TimerBinder mBinder = new TimerBinder();

    private ReadingGlassDrawer mDrawer;

    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimelineManager = TimelineManager.from(this);
        mDrawer = new ReadingGlassDrawer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void publishCard(Context context) {
        if (mLiveCard == null) {
            mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);

            mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mDrawer);
            mLiveCard.setNonSilent(true);

            // TODO 起動するActivityを変更する
            Intent menuIntent = new Intent(this, CardSampleActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish();
        } else {
            // TODO(alainv): Jump to the LiveCard when API is available.
            return;
        }
    }

    private void unpublishCard(Context context) {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.getSurfaceHolder().removeCallback(mDrawer);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        publishCard(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unpublishCard(this);
        super.onDestroy();
    }
}
