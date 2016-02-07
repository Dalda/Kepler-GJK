package gjk.kepler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTML_Loader {
    private Context myContext;

    public HTML_Loader(Context myContext) {
        this.myContext = myContext;
    }

    /* is connected? */
    public boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // OK
            return true;
        } else {
            return false;
        }
    }

    /* return HTML String */
    public String getHTML(String urlString) {
        InputStream is = null;
        String myURL = urlString;
        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line);
            }
            reader.close();
            is.close();
            return builder.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
