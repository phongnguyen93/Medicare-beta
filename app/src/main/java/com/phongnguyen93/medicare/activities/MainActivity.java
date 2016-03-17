package com.phongnguyen93.medicare.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.adapters.MyPageViewAdapter;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.fragments.FavFragment;
import com.phongnguyen93.medicare.functions.FunctionFavDoctor;
import com.phongnguyen93.medicare.functions.FunctionUser;
import com.phongnguyen93.medicare.extras.En_Decrypt;
import com.phongnguyen93.medicare.model.User;
import com.phongnguyen93.medicare.notification.InAppNotification;
import com.phongnguyen93.medicare.fragments.ListFragment;
import com.phongnguyen93.medicare.fragments.SecondFragment;
import com.phongnguyen93.medicare.fragments.TestFragment;
import com.phongnguyen93.medicare.maps.MapOperations;
import com.phongnguyen93.medicare.ui_view.tabs.SlidingTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MainActivity extends BaseActivity implements ListFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener,
        FavFragment.OnFragmentInteractionListener, View.OnClickListener, ViewPager.OnPageChangeListener,
        InAppNotification.SnackBarAction{
    public static final int LIST_FRAGMENT_ID = 0;
    public static final int MAP_FRAGMENT_ID = 1;
    public static final int SCHEDULE_FRAGMENT_ID = 2;

    public static final int ALERT_SNACKBAR = 0;
    public static final int WARNING_SNACKBAR = 1;

    public static final String CURRENT_TAB = "currentTab";
    private static final String PREFS_NAME = "myPreferences";

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private FunctionUser functionUser;
    private InAppNotification notification;
    private int currentPage;
    private LinearLayout linearLayout;
    private FunctionFavDoctor functionFavDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpotsDialog progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.setCancelable(false);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set up needed services
        functionUser = new FunctionUser(getApplicationContext());
        notification = new InAppNotification(this);
        User user = functionUser.getCurrentUser();
        functionFavDoctor = new FunctionFavDoctor(getApplicationContext());
        functionFavDoctor.setupData(user.getId());

        linearLayout = (LinearLayout) findViewById(R.id.main_layout);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if(settings != null){
            currentPage = settings.getInt(CURRENT_TAB,LIST_FRAGMENT_ID);
        }
        setupDrawer();
        setupConnCheck();
        setupTabs(currentPage);

    }


    @Override
    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT_TAB, currentPage);
        // Commit the edits!
        editor.apply();
    }


    //register the NetworkReciver to check connection change
    private void setupConnCheck() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkReceiver receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        receiver.goAsync();
    }


    //Handle the connection change
    public void doCheckConn(boolean isConnected, boolean isSlowConn) {
     String displayText;
        if (isConnected && isSlowConn) {
            displayText = getResources().getString(R.string.slow_connect_noti);
            notification.displaySnackbar(WARNING_SNACKBAR, linearLayout, displayText, null);
        }
        if (!isConnected) {
            displayText = getResources().getString(R.string.disconnect_noti);
            String actionText = getResources().getString(R.string.empty_button);
            notification.displaySnackbar(ALERT_SNACKBAR, linearLayout, displayText,actionText);
        }else
            notification.hideSnackbar();
    }

    //Set up navigation drawer
    private void setupDrawer() {
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);
    }

    /* This method create Sliding Tab Layout
     * Set up View Pager for Sliding Tab layout
     * Create fragment list to add into layout
     */
    private void setupTabs(int currentTab) {
        //create fragments list
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ListFragment());
        fragments.add(new MapOperations());
        fragments.add(new FavFragment());
        //setup ViewPager for tab
        ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);
        MyPageViewAdapter myPageViewAdapter = new MyPageViewAdapter(getApplicationContext(),
                getSupportFragmentManager(), fragments);
        mPager.setAdapter(myPageViewAdapter);
        //setup Tab
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);

        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);
        mSlidingTabLayout.setOnPageChangeListener(this);
        //set previous saved tab position
        mPager.setCurrentItem(currentTab);
    }



    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //Handle the action on drawer item select
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        if (menuItem.getItemId() == R.id.nav_exit){
            signOut();
        }else {
            Fragment fragment = null;
            Class fragmentClass;
            switch (menuItem.getItemId()) {
                case R.id.nav_first_fragment:
                    fragmentClass = ListFragment.class;
                    break;
                case R.id.nav_second_fragment:
                    fragmentClass = TestFragment.class;
                    break;
                case R.id.nav_third_fragment:
                    fragmentClass = TestFragment.class;
                    break;
                default:
                    fragmentClass = ListFragment.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Log.d("check fragment: ", "fragment=" + fragment.toString());

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("check fragment", e.getMessage());
            }

            // Insert the fragment by replacing any existing fragment

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit();
        }
        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    /**
     * This method execute the sign out action which remove current user & session tokens from local
     * and server database, then navigate to welcome screen
     */
    private void signOut() {
        //remove local tokens session
        String token, id;
        DbOperations dp = new DbOperations(this);
        Cursor CR = dp.getToken(dp);
        CR.moveToFirst();
        do {
            id = CR.getString(0);
            token = CR.getString(1);
        } while (CR.moveToNext());
        removeLocalToken(id, token);
        //remove server token
        removeServerToken(id, token);
        //remove current user
        functionUser.removeCurrentUser(En_Decrypt.fromHex(id));
        //remove all faved doctor from local db
        functionFavDoctor.removeAllDoctorFromLocal();
        //navigate to welcome screen
        Intent t = new Intent(MainActivity.this, WelcomeActivity.class);
        t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(t);
    }

    //This method remove session token from server db
    private void removeServerToken(String id, String token) {
        NetworkConnection connection = new NetworkConnection();
        String URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/signout/user?id=" + id + "&token=" + token + "";
        connection.execute(URL);
    }

    //This method remove session token from local db
    private void removeLocalToken(String id, String token) {
        DbOperations dp = new DbOperations(this);
        dp.removeToken(dp, id, token);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
         drawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //Change action bar title on tab strip selected
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case LIST_FRAGMENT_ID:
                currentPage = LIST_FRAGMENT_ID;
                this.setTitle(getResources().getString(R.string.tab_list_title));
                break;
            case MAP_FRAGMENT_ID:
                currentPage = MAP_FRAGMENT_ID;
                this.setTitle(getResources().getString(R.string.tab_map_title));
                break;
            case SCHEDULE_FRAGMENT_ID:
                currentPage = SCHEDULE_FRAGMENT_ID;
                this.setTitle(getResources().getString(R.string.tab_fav_title));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void setSnackBarAction(View v) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }


    //Make a API call and receive data
    public class NetworkConnection extends AsyncTask<String, Void, Boolean> {

        private final static String mLogTag = "Medi-Care";

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean isRemoved= false;
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();
                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();

                // Convert result to JSONObject
                JSONObject jsonObject =  new JSONObject(result.toString());
                isRemoved= jsonObject.getBoolean("status");
            } catch (IOException e) {
                Log.e(mLogTag, "JSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "JSON file could not be converted to a JSONArray");
            }
            return isRemoved;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean isRemoved) {

        }
    }

    /**
     * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
     * which indicates a connection change. It checks whether the device is connected or not, or on
     * a slow connection
     */
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = false;
            boolean isSlowConn = false;
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            NetworkInfo.DetailedState detailedState;
            if (networkInfo != null) {
                detailedState = networkInfo.getDetailedState();
                Log.d("Network state: ", detailedState.toString());
                //check network info type of connection
                if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                            || networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        isConnected = true;
                    }
                }
                //check disconnection
                if (detailedState == NetworkInfo.DetailedState.DISCONNECTED
                        || detailedState == NetworkInfo.DetailedState.FAILED
                        || detailedState == NetworkInfo.DetailedState.SUSPENDED
                        || detailedState == NetworkInfo.DetailedState.BLOCKED)
                    isConnected = false;
                //check slow connection or disconnection
                if (isConnected) {
                    //check slow connection
                    if (detailedState == NetworkInfo.DetailedState.IDLE
                            || detailedState == NetworkInfo.DetailedState.CONNECTING)
                        isSlowConn = true;
                }
            }
            doCheckConn(isConnected, isSlowConn);
        }

    }
}