package gjk.kepler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        HTML_Loader html_loader = new HTML_Loader(context);
        if (html_loader.checkConnection()) {
            // force refresh after restoring connection
            context.startService(new Intent(context, NotificationService.class));
        }

    }

}
