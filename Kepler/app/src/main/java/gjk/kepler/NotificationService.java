package gjk.kepler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/** AlarmReceiver (WakefulBroadcast Receiver) drží wake lock pro tento service
 * Když service skončí, uvolní wake lock
 */
public class NotificationService extends IntentService {
    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String prefClass = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_class", "");

        String result = "tady bude html, ktere stahnu - nove suplovani";
        HTML_Loader html_loader = new HTML_Loader(this);
        if(html_loader.checkConnection()){
            result = html_loader.getHTML(getString(R.string.domain)+"?type=suplovani&trida="+prefClass);
        }

        if (result != null && result.contains("nove suplovani")) {
            sendNotification(result);
        }
        // Uvolnit wake lock
        AlarmReceiver.completeWakefulIntent(intent);
    }

    // Poslat notifikaci
    private void sendNotification(String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_menu_timetable)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentTitle("Nové suplování")
                            .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager notifMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.notify(1, mBuilder.build());
    }


}
