package gjk.kepler;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Content extends Fragment {

    public static final String ARG_CONTENT_NUMBER = "content_number";
	public static final String ARG_DOWNLOAD_CONTENT = "download_content";

    private Activity parentActivity;
    private HTML_Loader html_loader;
    private LinearLayout content_layout;
    private int type;

    private static final String content_types[] = {"suplovani", "jidelna", "odkazy"};

    private ProgressBar progressBar;

    private int dateID;

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

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        //uložení současného ID

        dateID = parentActivity.getSharedPreferences(Home.PREFS_NAME, 0).getInt(Home.PREFS_DATE_ID, 999);

		boolean downloadNew = getArguments().getBoolean(ARG_DOWNLOAD_CONTENT);

        type = getArguments().getInt(ARG_CONTENT_NUMBER);
        switch(type){
            case 0:
                String oldResult = parentActivity.getSharedPreferences(NotificationService.PREFS_NAME, 0).getString(NotificationService.PREFS_HTTP_RESULT, "");
				if(downloadNew || "".equals(oldResult)){ //vynucené stažení nebo stahujeme poprvé
                	String prefClass = PreferenceManager.getDefaultSharedPreferences(parentActivity).getString("pref_class", "");
                	getPage(getString(R.string.domain)+"?type="+content_types[type]+"&trida="+prefClass);
				} else{
					//zobraz naposledy stažené
					show(oldResult, false);
				}
                break;
            case 1:
                String oldFoodResult = parentActivity.getSharedPreferences(NotificationService.PREFS_NAME, 0).getString(NotificationService.PREFS_HTTP_FOOD, "");
                if(downloadNew || "".equals(oldFoodResult)){ //vynucená aktualizace (stažení) dat nebo stahujeme poprvé
                    getPage(getString(R.string.domain)+"?type="+content_types[type]);
                } else{
                    //zobrazení dříve staženého
                    show(oldFoodResult, false);
                }
                break;
            case 2: //odkazy
                TextView content_text = new TextView(parentActivity);
                content_text.setGravity(0x01);
                content_text.setTextAppearance(parentActivity, R.style.TextAppearance_AppCompat_Display1);
                content_text.setLinkTextColor(getResources().getColor(R.color.primaryDark));
                content_text.setMovementMethod(LinkMovementMethod.getInstance());
                content_text.setText(Html.fromHtml(getString(R.string.links_content)));
                content_layout.addView(content_text);
                break;
        }

        return rootView;
    }

    private void getPage(String myURL){
        if(html_loader.checkConnection()){
            progressBar.setVisibility(View.VISIBLE);
            new DownloadWebpageTask().execute(myURL);
        }else{
            //není připojení
            Toast.makeText(parentActivity, "Nejste připojeni k internetu", Toast.LENGTH_SHORT).show();
        }
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        //stáhne webovou stránku v novém vlákně - jinak by se UI sekalo
        @Override
        protected String doInBackground(String... urls) {
            return html_loader.getHTML(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if(result == null) {
                Toast.makeText(parentActivity, "Chyba při získávání dat", Toast.LENGTH_SHORT).show();
            }else{
                //nejprve zkontrolovat, že Home mezitím nezměnila View a data nebyla zneplatněna
                if(dateID == parentActivity.getSharedPreferences(Home.PREFS_NAME, 0).getInt(Home.PREFS_DATE_ID, 999)){
                    show(result, true);
                }
            }
        }
    }

    private void show(String s, boolean saveNew) {
        String result;
        switch(type){
            case 0:
				if(saveNew){
		            //uložení odpovědi serveru kvůli pozdějším notifikacím o změnách
		            SharedPreferences.Editor shared = parentActivity.getSharedPreferences(NotificationService.PREFS_NAME, 0).edit();
		            shared.putString(NotificationService.PREFS_HTTP_RESULT, s);
		            shared.apply();
				}

                result = getSuplovani(s);
                break;
            case 1:
                if(saveNew){
                    //uložení nejaktuálnějších dat z jídelny
                    SharedPreferences.Editor shared = parentActivity.getSharedPreferences(NotificationService.PREFS_NAME, 0).edit();
                    shared.putString(NotificationService.PREFS_HTTP_FOOD, s);
                    shared.apply();
                }

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

                String trida = res.getString("trida");
                createTextView("Třída " + trida, R.style.TextAppearance_AppCompat_Headline);

                //divider
                TextView divider = new TextView(parentActivity);
                divider.setBackgroundColor(getResources().getColor(R.color.accent));
                divider.setHeight(1);
                content_layout.addView(divider);
                createVerticalSpace(1);

                JSONArray dny = res.getJSONArray("dny");
                for (int i = 0; i < dny.length(); i++) {
                    JSONObject ob = dny.getJSONObject(i);

                    String den = ob.getString("den");
                    createTextView(den, R.style.TextAppearance_AppCompat_Subhead, R.color.accent);

                    String info = ob.getString("info");
                    if(!info.equals("")){
                        createTextView(info, R.style.TextAppearance_AppCompat_Caption);
                    }

                    JSONArray hodiny = ob.getJSONArray("hodiny");
                    for (int j = 0; j < hodiny.length(); j++) {
                        JSONObject hod = hodiny.getJSONObject(j);
                        int hodina = hod.getInt("hodina");
                        String predmet = hod.getString("predmet");
                        String zmena = hod.getString("zmena");

                        createTextRow("" + hodina + ".hod " + predmet, zmena, true);
                    }
                    if(hodiny.length() == 0){
                        createTextRow("Žádné suplování", "", false);
                    }

                    createVerticalSpace(1);
                }

                return "";
            } else {
                return "Chyba pří načítání suplování";
            }
        }catch(JSONException e){
            return "Chyba pří načítání suplování";
        }
    }

    private String getJidelna(String s){
        boolean prefFood = PreferenceManager.getDefaultSharedPreferences(parentActivity).getBoolean("pref_food", false);
        boolean prefSoup = PreferenceManager.getDefaultSharedPreferences(parentActivity).getBoolean("pref_soup", false);

        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK); //sobota 7 nedele 1 pondeli 2
        if(day_of_week == 1 || day_of_week == 7) day_of_week = 0;
        else day_of_week -= 2;

        try {
            JSONObject res = new JSONObject(s);
            if (res.getString("type").equals(content_types[type])) {
                JSONArray dny = res.getJSONArray("dny");
                for (int i = day_of_week; i < dny.length(); i++) {
                    JSONObject ob = dny.getJSONObject(i);

                    String den = ob.getString("den");
                    createTextView(den, R.style.TextAppearance_AppCompat_Subhead, R.color.accent);

                    if(prefSoup) {
                        JSONObject polevka = ob.getJSONObject("polevka");
                        String polevkaNazev = polevka.getString("nazev");
                        if (!"".equals(polevkaNazev)) {
                            createTextRow("", "Polévka: "+polevkaNazev, false);
                        }
                        if (prefFood) {
                            String polevkaAlergeny = polevka.getString("alergeny");
                            if (!"".equals(polevkaAlergeny)) {
                                createTextView("\tAlergeny: " + polevkaAlergeny, R.style.TextAppearance_AppCompat_Caption);
                            }
                        }
                    }

                    JSONArray jidla = ob.getJSONArray("jidla");
                    for (int j = 0; j < jidla.length(); j++) {
                        JSONObject jidlo = jidla.getJSONObject(j);
                        String nazev = jidlo.getString("nazev");
                        createTextRow("" + (j + 1) + ") ", nazev, false);
                        if(prefFood){
                            String alergeny = jidlo.getString("alergeny");
                            createTextView("\tAlergeny: "+alergeny, R.style.TextAppearance_AppCompat_Caption);
                        }
                    }
                    createVerticalSpace(1);
                }
                return "";
            } else {
                return "Chyba pří načítání jídelny";
            }
        }catch(JSONException e){
            return "Chyba pří načítání jídelny";
        }
    }

    private void createTextView(String s, int resid){
        TextView myTV = new TextView(parentActivity);
        myTV.setTextAppearance(parentActivity, resid);
        myTV.setText(s);
        content_layout.addView(myTV);
    }

    private void createTextView(String s, int resid, int colorid){
        TextView myTV = new TextView(parentActivity);
        myTV.setTextAppearance(parentActivity, resid);
        myTV.setTextColor(getResources().getColor(colorid));
        myTV.setText(s);
        content_layout.addView(myTV);
    }

    private void createVerticalSpace(int lines){
        TextView div = new TextView(parentActivity);
        div.setLines(lines);
        content_layout.addView(div);
    }

    private void createTextRow(String s1, String s2, boolean align){
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        content_layout.addView(linearLayout);

        TextView newTVleft = new TextView(parentActivity);

        newTVleft.setTextAppearance(parentActivity, R.style.TextAppearance_AppCompat_Body2);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            newTVleft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            newTVleft.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if(align){
            newTVleft.setEms(7);
        }
        newTVleft.setText(s1);

        TextView newTVright = new TextView(parentActivity);
        newTVright.setTextAppearance(parentActivity, R.style.TextAppearance_AppCompat_Body1);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            newTVright.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        newTVright.setText(s2);

        linearLayout.addView(newTVleft);
        linearLayout.addView(newTVright);
    }
}
