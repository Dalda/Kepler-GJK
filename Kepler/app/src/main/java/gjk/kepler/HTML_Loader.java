package gjk.kepler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTML_Loader {
    private Context mContext;
    //private String result;

    public HTML_Loader(Context mContext){
        this.mContext = mContext;
        //this.result = null;
    }

    /*public String getResult(){
        if(this.result == null) return "";
        else{
            String tmp = this.result;
            this.result = null;
            return tmp;
        }
    }*/

    public void refreshPage(String myURL) {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //existuje připojení k síti a jsme připojeni k síti?
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // stáhni data
            Toast.makeText(mContext, "Aktualizuji...", Toast.LENGTH_LONG).show();
            new DownloadWebpageTask().execute(myURL);
        }
        else {
            // chyba
            Toast.makeText(mContext, "Nejste připojeni k internetu.", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        //stáhne webovou stránku v novém vlákně - jinak by se UI sekalo
        @Override
        protected String doInBackground(String... urls) {
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
                if(conn.getResponseCode() == 200){
                    is = conn.getInputStream();
                }
                java.util.Scanner s = new java.util.Scanner(is, "Cp1250").useDelimiter("\\A"); // \\A je konec
                String result = s.hasNext() ? s.next() : "";

                is.close();
                return result;

            } catch (IOException e) {
                return "Nelze získat webovou stránku. Chybná URL?";
            }
        }
        // onPostExecute nakonec uloží výsledek
        @Override
        protected void onPostExecute(String result) {
            ///HTML_Loader.this.result = result;
            /////misto toho " co je o radek vys" potrebuju zavolat Home.show nebo nejak predat ten string do HOMe
            /// to je problem, protoze Home nevi, kdy tato funkce v novem vlakne dobehne, takze to musi nejak rict tato funkce
            /// -nebo popripade nejaka funkce v HTML_Loader
            ///Home.this.show() jenze tohle je spatny pristup protoze show
        }
    }
}
