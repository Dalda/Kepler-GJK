package gjk.kepler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/** Tato třída udržuje wake lock pro moji třídu NotificationService, která udělá svou práci a pak uvolní lock */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private boolean alarmSet = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificationService.class);
        startWakefulService(context, service); //spustit service a udržuj zařízení (telefon) zapnuté
    }

    /** Nastaví opakující se alarm
     *  Po spuštění alarmu aplikace v budoucnu broadcastne Intent do metody onReceive
     */
    public void setAlarm(Context context) {
        if(alarmSet) return; //nemá smysl nastavovat dvakrát

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0); //postará se o broadcast

        //spustit za hodinu a pak každou další hodinu
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR, alarmIntent);

        alarmSet = true;
    }

    /** Zruší alarm */
    public void cancelAlarm(Context context) {
        if(!alarmSet) return; //nemá smysl pokračovat v rušení

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmSet = false;
    }
}
