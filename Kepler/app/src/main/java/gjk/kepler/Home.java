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

    private int current; // Needed for refresh action

    public static final String PREFS_NAME = "PrefsFileDateID"; // see setNewDateID()
    public static final String PREFS_DATE_ID = "DateID";

    // Navigation drawer
    private String[] navigationTitles;
    private ArrayList<NavigationItem> navigationItems;
    private NavigationAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    // Notificiations
    private AlarmReceiver alarm;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Content View is set in BaseActivity

        // Calls setDefaultValues when launched for the first time
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        alarm = new AlarmReceiver();
        boolean notify = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_notify", false);
        if (notify) {
            alarm.setAlarm(this);
        } else { // cancel notifications
            alarm.cancelAlarm(this);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.navigation_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        navigationTitles = getResources().getStringArray(R.array.navigation_titles);
        TypedArray navigationIcons = getResources().obtainTypedArray(R.array.navigation_icons);
        navigationItems = new ArrayList<NavigationItem>();
        // fill navigation drawer
        for (int i = 0; i < navigationTitles.length; i++) {
            navigationItems.add(new NavigationItem(navigationTitles[i], navigationIcons.getResourceId(i, -1)));
        }
        navigationIcons.recycle(); // release resources
        drawerAdapter = new NavigationAdapter(this, R.layout.drawer_list_item, R.id.navigationTitle, navigationItems);
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Triggered when fully closed */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getTitle());
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            /** Triggered when fully opened */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.title_navigation_drawer);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        current = 0; // set first page
    }

    private boolean checkPreferenceSet() {
        return !("".equals(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_class", "")));
    }

    /**
     * Force student class selection
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPreferenceSet()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(SettingsActivity.ARG_ASK, true); // Toast message
            startActivity(intent);
        } else {
            selectItem(current);
        }
    }

    /* icon refresh */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /* orientation changed */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        if (position == 3) {
            setNewDateID(); // invalidate last data
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            navigationItems.get(current).setActivated(false); // remove highlight
            current = position;
            createContent(position, false);

            drawerList.setItemChecked(position, true);
            setTitle(navigationTitles[position]);
            navigationItems.get(position).setActivated(true); //set highlight
            drawerAdapter.notifyDataSetChanged(); // update list
            drawerLayout.closeDrawer(drawerList);
        }
    }

    /* Change page content */
    private void createContent(int position, boolean download) {
        setNewDateID(); // invalidate
        Fragment fragment = new Content();
        Bundle args = new Bundle();
        args.putInt(Content.ARG_CONTENT_NUMBER, position);
        args.putBoolean(Content.ARG_DOWNLOAD_CONTENT, download);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void setNewDateID() {
        SharedPreferences.Editor shared = getSharedPreferences(PREFS_NAME, 0).edit();
        shared.putInt(PREFS_DATE_ID, (int) (Math.random() * 100000));
        shared.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass event to ActionBarDrawerToggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_refresh:
                this.createContent(current, true); // refresh
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
