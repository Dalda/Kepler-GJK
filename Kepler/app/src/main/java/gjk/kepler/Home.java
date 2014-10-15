package gjk.kepler;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class Home extends Activity {

    private int current; //Nutný pro refresh akci v action baru

    //navigation drawer
    private String[] navigationTitles;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;
    private ActionBarDrawerToggle myDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.navigation_drawer);

        navigationTitles = getResources().getStringArray(R.array.navigation_titles);
        TypedArray navigationIcons = getResources().obtainTypedArray(R.array.navigation_icons);
        ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
        // Nastavit adapter, který naplní navigation_drawer položkami NavigationItem (TextView+ImageView),
        // které jsou specifikované v drawer_list_item.xml
        for(int i=0;i<navigationTitles.length;i++){
            navigationItems.add(new NavigationItem(navigationTitles[i], navigationIcons.getResourceId(i, -1)));
        }
        navigationIcons.recycle(); //uvolnit resources
        myDrawerList.setAdapter(new NavigationAdapter(this, R.layout.drawer_list_item, R.id.navigationTitle, navigationItems));

        // Zareagovat na kliknutí
        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
                /** Zavolá se teprve když je navigation drawer úplně zavřený */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getActionBar().setTitle(getTitle());
                    invalidateOptionsMenu(); // zavolá onPrepareOptionsMenu()
                }
                /** Zavolá se teprve když je navigation drawer úplně otevřený */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getActionBar().setTitle(R.string.title_navigation_drawer);
                    invalidateOptionsMenu(); // zavolá onPrepareOptionsMenu()
                }
            };
        // Nastavit vytvořený myDrawerToggle jako DrawerListener pro náš Layout s Navigation Drawerem
        myDrawerLayout.setDrawerListener(myDrawerToggle);
        // Nastaví app icon jako toggle pro navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        current = 0;
        selectItem(current); //načti první stránku při prvním spuštění aplikaci
    }

    /* override kvůli aktualizaci ikony navigation draweru kdykoli po activity restore*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync pokud byla zavolána onRestoreInstanceState
        myDrawerToggle.syncState();
    }

    /* Zavolána při změně orientace obrazovky apod. */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Zavolána při volání invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Pokud je navigation drawer otevřen, skryj ikony action baru
        boolean drawerOpen = myDrawerLayout.isDrawerOpen(myDrawerList);
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
        current = position;
        createContent(position);

        myDrawerList.setItemChecked(position, true);
        setTitle(navigationTitles[position]);
        myDrawerLayout.closeDrawer(myDrawerList);
    }

    /* Změní stávající fragment za nový, čímž vytvoří obsah hlavní stránky */
    private void createContent(int position){
        // Vytvoří nový fragment a nastaví obsah podle argumentu
        Fragment fragment = new Content(); //moje třída Content
        Bundle args = new Bundle();
        args.putInt(Content.ARG_CONTENT_NUMBER, position); //přibalíme argument
        fragment.setArguments(args);
        // Vložíme nový fragment nahrazením stávajícího
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
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
        if (myDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //jinak normálně zpracujeme action bar vpravo
        // Akce po kliknutí na jednotlivé položky v horní liště (action bar)
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                this.createContent(current);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
