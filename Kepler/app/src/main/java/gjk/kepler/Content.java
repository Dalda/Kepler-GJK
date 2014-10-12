package gjk.kepler;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Content extends Fragment {

    public static final String ARG_CONTENT_NUMBER = "content_number";

    private Activity parentActivity;
    private HTML_Loader html_loader;
    private TextView content_text;

    public Content() {
        // Nutně prázdný pro třídy dědící Fragment
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
        html_loader = new HTML_Loader(activity.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        content_text = (TextView) rootView.findViewById(R.id.text_content);

        int i = getArguments().getInt(ARG_CONTENT_NUMBER);
        switch(i){
            case 0:
            case 1:
                getPage(getResources().getStringArray(R.array.urls)[i]);
                break;
            case 2:
                content_text.setText("Tady bude nastavení (ale v nové aktivitě bez refresh)...");
                break;
        }
        return rootView;
    }

    private void getPage(String myURL){
        if(html_loader.checkConnection()){
            Toast.makeText(parentActivity, "Aktualizuji...", Toast.LENGTH_SHORT).show();
            new DownloadWebpageTask().execute(myURL);
        }else{
            //není připojení
            Toast.makeText(parentActivity, "Nejste připojeni k internetu.", Toast.LENGTH_LONG).show();
        }
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        //stáhne webovou stránku v novém vlákně - jinak by se UI sekalo
        @Override
        protected String doInBackground(String... urls) {
            return html_loader.getHTML(urls);
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null) {
                Toast.makeText(parentActivity, "Nelze získat webovou stránku. Chybná URL?", Toast.LENGTH_LONG).show();
            }else{
                show(result);
            }
        }
    }

    private void show(String s){
        Toast.makeText(parentActivity, "OK mám HTML", Toast.LENGTH_SHORT).show();
        content_text.setText(s);
    }

}

/* Nastavení bude ve vytahovaci liste vlevo
        -bude tam mit ikonu ozubeneho kola
        -(vse bude s ikonami) - ikona priboru pro obedy apod, podobne jako v gmail, gdocs apod
     O aplikaci - -tato polozka bude primo v aktivite "nastaveni"
*/
