package gjk.kepler;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class Home extends BaseActivity {

    private int current; //Nutný pro refresh akci v action baru

    public static final String PREFS_NAME = "PrefsFileDateID"; //viz setNewDateID()
    public static final String PREFS_DATE_ID = "DateID";

    //navigation drawer
    private String[] navigationTitles;
    private ArrayList<NavigationItem> navigationItems;
    private NavigationAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    //notifikace
    private AlarmReceiver alarm;

    @Override protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set Content View se provede v BaseActivity

        //Zavolá se setDefaultValues jen při úplně prvním spuštění aplikace na zařízení
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        alarm = new AlarmReceiver();
        boolean notify = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_notify", false);
        if(notify){ //nastavit pravidelné notifikace
            alarm.setAlarm(this);
        } else{ //zrušit notifikace
            alarm.cancelAlarm(this);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.navigation_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        navigationTitles = getResources().getStringArray(R.array.navigation_titles);
        TypedArray navigationIcons = getResources().obtainTypedArray(R.array.navigation_icons);
        navigationItems = new ArrayList<NavigationItem>();
        // Nastavit adapter, který naplní navigation_drawer položkami NavigationItem (TextView+ImageView),
        // které jsou specifikované v drawer_list_item.xml
        for(int i=0;i<navigationTitles.length;i++){
            navigationItems.add(new NavigationItem(navigationTitles[i], navigationIcons.getResourceId(i, -1)));
        }
        navigationIcons.recycle(); //uvolnit resources
        drawerAdapter = new NavigationAdapter(this, R.layout.drawer_list_item, R.id.navigationTitle, navigationItems);
        drawerList.setAdapter(drawerAdapter);

        // Zareagovat na kliknutí
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
                /** Zavolá se teprve když je navigation drawer úplně zavřený */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(getTitle());
                    invalidateOptionsMenu(); // zavolá onPrepareOptionsMenu()
                }
                /** Zavolá se teprve když je navigation drawer úplně otevřený */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle(R.string.title_navigation_drawer);
                    invalidateOptionsMenu(); // zavolá onPrepareOptionsMenu()
                }
            };

        // Nastavit vytvořený drawerToggle jako DrawerListener pro náš Layout s Navigation Drawerem
        drawerLayout.setDrawerListener(drawerToggle);

        current = 0; //nastav první stránku
    }

    private boolean checkPreferenceSet(){
        return !("".equals(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_class", "")));
    }
   /** Vynucení nastavení GJK třídy uživatele
    * Je volána po onCreate(Bundle)
    * Na rozdíl od ní se ale volá i po návratu ze Settings nebo obnovení view této Activity nebo po zrušení pause Activity
    */
    @Override
    protected void onResume() {
        super.onResume();

        if(!checkPreferenceSet()){ //není nastavené
            //GJK třída musí být uživatelem nastavená, tak jdeme do nastavení
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(SettingsActivity.ARG_ASK, true); //kvůli Toast message
            startActivity(intent);
        }
        else{
            selectItem(current); //stáhni stránku vždy po obnovení této Activity
        }
    }

    /* override kvůli aktualizaci ikony navigation draweru kdykoli po activity restore */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync pokud byla zavolána onRestoreInstanceState
        drawerToggle.syncState();
    }

    /* Zavolána při změně orientace obrazovky apod. */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /* Zavolána při volání invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Pokud je navigation drawer otevřen, skryj ikony action baru
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /* Navigation drawer click event */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /* Navigation drawer click event */
    private void selectItem(int position) {
        if(position == 3) { //položka nastavení
            setNewDateID(); //kvůli zneplatnění dříve stahovaných dat
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            navigationItems.get(current).setActivated(false); //remove bold
            current = position;
            createContent(position);

            drawerList.setItemChecked(position, true); //default selector
            setTitle(navigationTitles[position]);
            navigationItems.get(position).setActivated(true); //set bold
            drawerAdapter.notifyDataSetChanged(); //update list (bold)
            drawerLayout.closeDrawer(drawerList);
        }
    }

    /* Změní stávající fragment za nový, čímž vytvoří obsah hlavní stránky */
    private void createContent(int position){
        setNewDateID(); //kvůli zneplatnění dříve stahovaných dat
        // Vytvoří nový fragment a nastaví obsah podle argumentu
        Fragment fragment = new Content(); //moje třída Content
        Bundle args = new Bundle();
        args.putInt(Content.ARG_CONTENT_NUMBER, position); //přibalíme argument
        fragment.setArguments(args);
        // Vložíme nový fragment nahrazením stávajícího
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    /* Slouží později ve třídě Content pro zneplatnění dříve stahovaných dat
     * Aplikace by jinak zapisovala na neexistující místo a spadla by
     */
    private void setNewDateID(){
        SharedPreferences.Editor shared = getSharedPreferences(PREFS_NAME, 0).edit();
        shared.putInt(PREFS_DATE_ID, (int)(Math.random()*100000));
        shared.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //vytvoří položky v horní liště (action bar)
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Předej event do ActionBarDrawerToggle
        // když vrátí true, tak zpracoval kliknutí na app icon
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //jinak normálně zpracujeme action bar vpravo
        // Akce po kliknutí na jednotlivé položky v horní liště (action bar)
        switch (item.getItemId()) {
            case R.id.action_refresh:
                this.createContent(current);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
