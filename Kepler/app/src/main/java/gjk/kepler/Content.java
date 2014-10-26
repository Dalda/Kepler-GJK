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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Content extends Fragment {

    public static final String ARG_CONTENT_NUMBER = "content_number";

    private Activity parentActivity;
    private HTML_Loader html_loader;
    private LinearLayout content_layout;
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
        content_layout = (LinearLayout) rootView.findViewById(R.id.content_layout);

        type = getArguments().getInt(ARG_CONTENT_NUMBER);
        switch(type){
            case 0:
                //content_text.setMovementMethod(new ScrollingMovementMethod());
                String prefClass = PreferenceManager.getDefaultSharedPreferences(parentActivity).getString("pref_class", "");
                getPage(getString(R.string.domain)+"?type="+content_types[type]+"&trida="+prefClass);
                break;
            case 1:
                //content_text.setMovementMethod(new ScrollingMovementMethod());
                getPage(getString(R.string.domain)+"?type="+content_types[type]);
                break;
            case 2: //odkazy
                TextView content_text = new TextView(parentActivity);
                content_text.setTextSize(35);
                content_text.setLinkTextColor(R.color.primaryDark);
                content_text.setGravity(0x01);
                content_text.setTypeface(null, Typeface.BOLD);
                content_text.setLineSpacing(1,1);
                content_text.setMovementMethod(LinkMovementMethod.getInstance());
                content_text.setText(Html.fromHtml(getString(R.string.links_content)));
                content_layout.addView(content_text);
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
            Toast.makeText(parentActivity, "Nejste připojeni k internetu", Toast.LENGTH_LONG).show();
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
                result = getJidelna(s);
                break;
            default:
                result = "Chyba v požadavku";
                break;
        }
        TextView er = new TextView(parentActivity);
        er.setTextAppearance(parentActivity, R.style.TextAppearance_AppCompat_Medium);
        er.setText(Html.fromHtml(result));
        content_layout.addView(er);

    }

    private String getSuplovani(String s) {
        try {
            JSONObject res = new JSONObject(s);
            if (res.getString("type").equals(content_types[type])) {
                StringBuilder sb = new StringBuilder();
                String trida = res.getString("trida");
                sb.append("<h5>Třída " + trida + "</h5>");
                JSONArray dny = res.getJSONArray("dny");
                for (int i = 0; i < dny.length(); i++) {
                    JSONObject ob = dny.getJSONObject(i);
                    String den = ob.getString("den");
                    sb.append("<h5>" + den + "</h5>");
                    String info = ob.getString("info");
                    if(!info.equals("")) sb.append("<i>"+info+"</i><br />");

                    JSONArray hodiny = ob.getJSONArray("hodiny");
                    for (int j = 0; j < hodiny.length(); j++) {
                        JSONObject hod = hodiny.getJSONObject(j);
                        int hodina = hod.getInt("hodina");
                        String predmet = hod.getString("predmet");
                        String zmena = hod.getString("zmena");
                        sb.append("<b>" + hodina + ".hod\t" + predmet +"</b><br />\t\t"+zmena + "<br />");
                    }
                    if(hodiny.length() == 0){
                        sb.append("Žádné suplování<br />");
                    }
                }
                return sb.toString();
            } else {
                return "Chyba pří načítání suplování";
            }
        }catch(JSONException e){
            return "Chyba pří načítání suplování";
        }
    }

    private String getJidelna(String s){
        boolean prefFood = PreferenceManager.getDefaultSharedPreferences(parentActivity).getBoolean("pref_food", false);
        try {
            JSONObject res = new JSONObject(s);
            if (res.getString("type").equals(content_types[type])) {
                StringBuilder sb = new StringBuilder();
                JSONArray dny = res.getJSONArray("dny");
                for (int i = 0; i < dny.length(); i++) {
                    JSONObject ob = dny.getJSONObject(i);
                    String den = ob.getString("den");
                    sb.append("<h5>" + den + "</h5>");
                    JSONObject polevka = ob.getJSONObject("polevka");
                    String polevkaNazev = polevka.getString("nazev");
                    String polevkaAlergeny = polevka.getString("alergeny");
                    sb.append("<b>Polévka: </b>"+polevkaNazev+"<br />");
                    if(prefFood) sb.append("<i>\t\tAlergeny: " + polevkaAlergeny+ "</i><br />");

                    JSONArray jidla = ob.getJSONArray("jidla");
                    Calendar calendar = Calendar.getInstance();
                    int day_of_week = calendar.get(Calendar.DAY_OF_WEEK); //sobota 7 nedele 1 pondeli 2
                    if(day_of_week == 1 || day_of_week == 7) day_of_week = 0;
                    else day_of_week -= 2;

                    for (int j = day_of_week; j < jidla.length(); j++) {
                        JSONObject jidlo = jidla.getJSONObject(j);
                        String nazev = jidlo.getString("nazev");
                        String alergeny = jidlo.getString("alergeny");
                        sb.append("<b>"+(j+1)+")</b> "+nazev+"<br />");
                        if(prefFood) sb.append("<i>\t\tAlergeny: " + alergeny + "</i><br />");
                    }
                }
                return sb.toString();
            } else {
                return "Chyba pří načítání jídelny";
            }
        }catch(JSONException e){
            return "Chyba pří načítání jídelny";
        }
    }

}
