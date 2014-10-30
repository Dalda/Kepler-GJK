package gjk.kepler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/** AlarmReceiver (WakefulBroadcast Receiver) drží wake lock pro tento service
 * Když service skončí, uvolní wake lock
 */
public class NotificationService extends IntentService {
    public NotificationService(){
        super("NotificationService");
    }

    private NotificationManager notificationMgr;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {

        String result = "tady bude html, ktere stahnu";

        // If the app finds the string "doodle" in the Google home page content, it
        // indicates the presence of a doodle. Post a "Doodle Alert" notification.
        if (result.indexOf("nove suplovani") != -1) {
            sendNotification("je nove suplovani");
        }
        // Uvolnit wake lock
        AlarmReceiver.completeWakefulIntent(intent);
    }

    // Poslat notifikaci
    private void sendNotification(String msg) {
        notificationMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.doodle_alert))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }


}
