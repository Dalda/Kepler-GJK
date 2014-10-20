package gjk.kepler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTML_Loader {
    private Context myContext;

    public HTML_Loader(Context myContext){
        this.myContext = myContext;
    }

    /* Vrací boolean v závislosti na možnosti připojení se k internetu */
    public boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //existuje připojení k síti a jsme připojeni k síti?
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // OK
            return true;
        } else {
            // chyba
            return false;
        }
    }

    /* Vrátí String HTML stažené stránky, která je zadána jako parametr */
    public String getHTML(String... urls) {
        InputStream is = null;
        String myURL = urls[0];
        //vezme URL a připojí se k internetu přes HttpURLConnection
        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // zde začne dotaz
            conn.connect();
            is = conn.getInputStream();

            java.util.Scanner s = new java.util.Scanner(is, "utf-8").useDelimiter("\\A"); // \\A je konec
            String result = s.hasNext() ? s.next() : "";

            is.close();
            s.close();
            return result;
        }
        catch (IOException e) {
            return null;
        }
    }
}
