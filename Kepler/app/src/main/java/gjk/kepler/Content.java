package gjk.kepler;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Content extends Fragment {

    public static final String ARG_CONTENT_NUMBER = "content_number";

    private Activity parentActivity;
    private HTML_Loader html_loader;
    private TextView content_text;
    private int type;

    private static final String content_types[] = {"suplovani", "jidelna", "odkazy"};

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

        type = getArguments().getInt(ARG_CONTENT_NUMBER);
        switch(type){
            case 0:
                content_text.setMovementMethod(new ScrollingMovementMethod());
                String prefClass = PreferenceManager.getDefaultSharedPreferences(parentActivity).getString("pref_class", "");
                getPage(getString(R.string.domain)+"?type="+content_types[type]+"&trida="+prefClass);
                break;
            case 1:
                content_text.setMovementMethod(new ScrollingMovementMethod());
                getPage(getString(R.string.domain)+"?type="+content_types[type]);
                break;
            case 2: //odkazy
                content_text.setTextSize(35);
                content_text.setGravity(0x01);
                content_text.setTypeface(null, Typeface.BOLD);
                content_text.setLineSpacing(1,1);
                content_text.setMovementMethod(LinkMovementMethod.getInstance());
                content_text.setText(Html.fromHtml(getString(R.string.links_content)));
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

    private void show(String s) {
        String result;
        switch(type){
            case 0:
                result = getSuplovani(s);
                break;
            case 1:
                //result = getJidelna(s);
                break;
            default:
                result = "Chyba v požadavku.";
                break;
        }
        content_text.setText(result);

    }

    private String getSuplovani(String s) {
        try {
            JSONObject res = new JSONObject(s);
            if (res.getString("type").equals(content_types[type])) {
                StringBuilder sb = new StringBuilder();
                String trida = res.getString("trida");
                sb.append("<h4>Třída: " + trida + "</h4><br />");
                JSONArray dny = res.getJSONArray("dny");
                for (int i = 0; i < dny.length(); i++) {
                    JSONObject ob = dny.getJSONObject(i);
                    String den = ob.getString("den");
                    sb.append("<h5>" + den + "</h5><br />");
                    String zmeneno = ob.getString("zmeneno");

                    JSONArray hodiny = ob.getJSONArray("hodiny");
                    for (int j = 0; j < hodiny.length(); j++) {
                        JSONObject hod = hodiny.getJSONObject(j);
                        int hodina = hod.getInt("hodina");
                        String predmet = hod.getString("predmet");
                        String zmena = hod.getString("zmena");
                        sb.append("<b>" + hodina + "</b>" + predmet + zmena + "<br />");
                    }
                }
                return sb.toString();
            } else {
                return "Chyba pří načítání suplování.";
            }
        }catch(JSONException e){
            return "Chyba pří načítání suplování.";
        }
    }


}
