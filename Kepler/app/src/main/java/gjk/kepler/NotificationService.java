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

/** AlarmReceiver (WakefulBroadcast Receiver) drží wake lock pro tento service
 * Když service skončí, uvolní wake lock
 */
public class NotificationService extends IntentService {
    public NotificationService(){
        super("NotificationService");
    }

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_HTTP_RESULT = "httpResult"; //suplovani
    public static final String PREFS_HTTP_FOOD = "httpFood"; //jidelna

    private static int refreshCount = 0;

    @Override
    protected void onHandleIntent(Intent intent) {
        String result = null;
        HTML_Loader html_loader = new HTML_Loader(this);
        if(html_loader.checkConnection()) {
            String prefClass = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_class", "");
            result = html_loader.getHTML(getString(R.string.domain) + "?type=suplovani&trida=" + prefClass);

            if (result != null) { //null je chyba při získávání HTML, např server nedostupný
                String oldResult = getSharedPreferences(PREFS_NAME, 0).getString(PREFS_HTTP_RESULT, "");
                //první hodnotu do oldResult neukládá tento Service, ale třída Content, proto prázdný string nebereme
                if (!"".equals(oldResult)) {
                    //uložit novou HTTP odpověď serveru
                    SharedPreferences.Editor shared = getSharedPreferences(PREFS_NAME, 0).edit();
                    shared.putString(PREFS_HTTP_RESULT, result);
                    shared.commit(); //nesmí použít apply(), jinak bychom neudrželi lock

                    if (differ(result, oldResult)) {
                        sendNotification("Objevilo se nové suplování");
                    }
                }
            }
            //pokud je povolen NetworkChangeReceiver, tak ho vypni
            ComponentName networkReceiver = new ComponentName(this, NetworkChangeReceiver.class);
            if(getPackageManager().getComponentEnabledSetting(networkReceiver) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
                getPackageManager().setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
        else{ //nelze se připojit k internetu
            //pokud je vypnut NetworkChangeReceiver, tak ho zapni
            ComponentName networkReceiver = new ComponentName(this, NetworkChangeReceiver.class);
            if(getPackageManager().getComponentEnabledSetting(networkReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
                getPackageManager().setComponentEnabledSetting(networkReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
        // Uvolnit wake lock
        AlarmReceiver.completeWakefulIntent(intent);
    }

    /* Zjistí, zda se nově stažená data o suplování liší od předchozích */
    private boolean differ(String newRes, String oldRes){
        if(newRes.equals(oldRes)){
            return false;
        }else{
            //smazání prvního dne v suplování != změna suplování
            String den = "\"den\":";
            int iB1 = oldRes.indexOf(den);//prvni den v old
            int iA1 = newRes.indexOf(den);//prvni den v new
            if(iA1 != -1 && iB1 != -1){
                int iB2 = oldRes.indexOf(den, iB1); //druhy den v old
                if(iB2 != -1 && (newRes.substring(iA1)).equals(oldRes.substring(iB2))){
                    return false; //o tuhle notifikaci uzivatel nestoji
                }
            }
            return true;
        }
    }

    // Poslat notifikaci
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
