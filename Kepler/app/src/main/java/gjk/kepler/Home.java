package gjk.kepler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Home extends Activity {
    /*Intent intent = new Intent(getApplicationContext(), gjk.kepler.Food.class);
                startActivity(intent);
    */
    private String url_suplovani = "http://old.gjk.cz/suplovani.php";
    private String url_obedy = "http://gjk.cz/?id=4332"; //presunout do Food class
    private TextView text_suplovani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        text_suplovani = (TextView) findViewById(R.id.text_suplovani);
    }

    public void refreshPage() {
        ConnectivityManager connMgr = (ConnectivityManager)
                                      getSystemService(Context.CONNECTIVITY_SERVICE);
        //existuje připojení k síti a jsme připojeni k síti?
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // stáhni data
            Toast.makeText(this, "Aktualizuji...", Toast.LENGTH_LONG).show();
            new DownloadWebpageTask().execute(url_suplovani);

        } else {
            // chyba
            text_suplovani.setText("Nejste připojeni k internetu.");
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
                java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A"); // \\A je konec
                String result = s.hasNext() ? s.next() : "";

                is.close();
                return result;

            } catch (IOException e) {
                return "Nelze získat webovou stránku. Chybná URL?";
            }
        }
        // onPostExecute nakonec zobrazí výsledek.
        @Override
        protected void onPostExecute(String result) {
            text_suplovani.setText(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //vytvoří položky v horní liště (action bar)
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Akce po kliknutí na jednotlivé položky v horní liště (action bar)
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                refreshPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
