package jp.ne.docomo.wearablehackathon.readingglass;

import android.app.PendingIntent;
import android.app.Service;
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

    private ReadingGlassDrawer mTimerDrawer;

    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimelineManager = TimelineManager.from(this);
        mTimerDrawer = new ReadingGlassDrawer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);

            mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mTimerDrawer);
            mLiveCard.setNonSilent(true);

            Intent menuIntent = new Intent(this, MainActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish();
        } else {
            // TODO(alainv): Jump to the LiveCard when API is available.
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.getSurfaceHolder().removeCallback(mTimerDrawer);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
}
