package gjk.kepler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * AlarmReceiver (WakefulBroadcast Receiver) drží wake lock pro tento service
 * Když service skončí, uvolní wake lock
 */
public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_HTTP_RESULT = "httpResult"; // timetable
    public static final String PREFS_HTTP_RESULT_DATE = "httpResultDate"; // download date
    public static final String PREFS_HTTP_FOOD = "httpFood"; // canteen
    public static final String PREFS_HTTP_FOOD_DATE = "httpFoodDate";
    private static final long dayMillis = 86400000L; // millisec - one day

    @Override
    protected void onHandleIntent(Intent intent) {
        HTML_Loader html_loader = new HTML_Loader(this);
        if (html_loader.checkConnection()) {
            String prefClass = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_class", "");
            String result = null;
            result = html_loader.getHTML(getString(R.string.domain) + "?type=suplovani&trida=" + prefClass);

            if (result != null) {
                String oldResult = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_HTTP_RESULT, "");
                // first value must have been set by Content
                if (!"".equals(oldResult)) {
                    // save new response
                    SharedPreferences.Editor shared = getSharedPreferences(PREFS_NAME, 0).edit();
                    shared.putString(PREFS_HTTP_RESULT, result);
                    shared.putLong(PREFS_HTTP_RESULT_DATE, Calendar.getInstance().getTimeInMillis());
                    shared.commit();

                    if (differ(result, oldResult)) {
                        sendNotification("Objevilo se nové suplování");
                    }
                }
            }

            // Refresh school canteen menu
            Long oldTimeMillis = getSharedPreferences(PREFS_NAME, 0).getLong(PREFS_HTTP_FOOD_DATE, 0L);
            if (oldTimeMillis == 0 || (Calendar.getInstance().getTimeInMillis() > (oldTimeMillis + dayMillis))) { // once per day
                String foodResult = null;
                foodResult = html_loader.getHTML(getString(R.string.domain) + "?type=jidelna");
                if (foodResult != null) {
                    // save new data
                    SharedPreferences.Editor shared = getSharedPreferences(PREFS_NAME, 0).edit();
                    shared.putString(PREFS_HTTP_FOOD, foodResult);
                    shared.putLong(PREFS_HTTP_FOOD_DATE, Calendar.getInstance().getTimeInMillis());
                    shared.commit();
                }
            }

            // if NetworkChangeReceiver set, turn it off
            ComponentName networkReceiver = new ComponentName(this, NetworkChangeReceiver.class);
            if (getPackageManager().getComponentEnabledSetting(networkReceiver) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                getPackageManager().setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } else { // not connected
            //if NetworkChangeReceiver not set, turn it on
            ComponentName networkReceiver = new ComponentName(this, NetworkChangeReceiver.class);
            if (getPackageManager().getComponentEnabledSetting(networkReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                getPackageManager().setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
        // Release wake lock
        AlarmReceiver.completeWakefulIntent(intent);
    }

    /* New data differs? */
    private boolean differ(String newRes, String oldRes) {
        if (newRes.equals(oldRes)) {
            return false;
        } else {
            String den = "\"den\":";
            int iB1 = oldRes.indexOf(den);
            int iA1 = newRes.indexOf(den);
            if (iA1 != -1 && iB1 != -1) {
                int iB2 = oldRes.indexOf(den, iB1);
                if (iB2 != -1 && (newRes.substring(iA1)).equals(oldRes.substring(iB2))) {
                    return false; // only old days deleted? not interested...
                }
            }
            return true;
        }
    }

    private void sendNotification(String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle("Nové suplování")
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager notifMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.notify(1, mBuilder.build());
    }


}
