package gjk.kepler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * This class keeps wake lock for NotificationService
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private boolean alarmSet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificationService.class);
        startWakefulService(context, service);
    }

    /**
     * Set recurring alarm
     * Broadcast Intent to onReceive after triggering alarm
     */
    public void setAlarm(Context context) {
        if (alarmSet) return; //don't set twice

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // trigger every hour
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR, alarmIntent);

        alarmSet = true;
    }

    public void cancelAlarm(Context context) {
        if (!alarmSet) return;

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmSet = false;
    }
}
