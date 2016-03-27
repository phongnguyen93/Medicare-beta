package com.phongnguyen93.medicare.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.adapters.MyPageViewAdapter;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.extras.En_Decrypt;
import com.phongnguyen93.medicare.fragments.FavFragment;
import com.phongnguyen93.medicare.fragments.ListFragment;
import com.phongnguyen93.medicare.fragments.ManageBookingFragment;
import com.phongnguyen93.medicare.fragments.UserProfileFragment;
import com.phongnguyen93.medicare.functions.BookingsFunctions;
import com.phongnguyen93.medicare.functions.FavDoctorFunctions;
import com.phongnguyen93.medicare.functions.UserFunctions;
import com.phongnguyen93.medicare.maps.MapOperations;
import com.phongnguyen93.medicare.model.User;
import com.phongnguyen93.medicare.notification.MyNotification;
import com.phongnguyen93.medicare.notification.push_notification.RegistrationIntentService;
import com.phongnguyen93.medicare.notification.schedule_notification.AlarmReceiver;
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

public class MainActivity extends BaseActivity implements ListFragment.OnFragmentInteractionListener
        , ManageBookingFragment.OnFragmentInteractionListener,UserProfileFragment.OnFragmentInteractionListener,
        FavFragment.OnFragmentInteractionListener, View.OnClickListener, ViewPager.OnPageChangeListener,
        MyNotification.SnackBarAction {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final int LIST_FRAGMENT_ID = 0;
    public static final int MAP_FRAGMENT_ID = 1;
    public static final int SCHEDULE_FRAGMENT_ID = 2;

    public static final int DRAWER_ITEM_SEARCH_ID = 0;
    public static final int DRAWER_ITEM_MANAGE_ID = 1;
    public static final int DRAWER_ITEM_PROFILE_ID = 2;

    public static final String CURRENT_TAB = "currentTab";
    private static final String PREFS_NAME = "myPreferences";
    private static final String CURRENT_DRAWER_ITEM = "currentDrawerItem";

    private int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{android.R.attr.state_focused},
            new int[]{android.R.attr.state_pressed}
    };

    private int[] colors;

    private boolean isConnect = true;
    private DbOperations dp;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private UserFunctions userFunctions;
    private MyNotification notification;
    private int currentPage;
    private int currentDrawerItem;
    private Fragment fragment;
    private ArrayList<Fragment> fragments;
    private LinearLayout linearLayout;
    private FavDoctorFunctions favDoctorFunctions;
    private BookingsFunctions bookingsFunctions;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private ViewPager mPager;
    private SlidingTabLayout mSlidingTabLayout  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpotsDialog progressDialog = new SpotsDialog(this, R.style.Custom);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);



        progressDialog.setCancelable(false);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set up needed services
        userFunctions = new UserFunctions(getApplicationContext());
        notification = new MyNotification(this);
        User user = userFunctions.getCurrentUser();
        favDoctorFunctions = new FavDoctorFunctions(getApplicationContext());
        bookingsFunctions = new BookingsFunctions(getApplicationContext());
        favDoctorFunctions.setupData(user.getId());
        bookingsFunctions.setupBookings(user.getId());


        dp = new DbOperations(this);

        if (navigationView != null) {
            TextView header_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name);
            header_name.setText(user.getName());
        }

        //create fragments list
        fragments = new ArrayList<>();
        fragments.add(new ListFragment());
        fragments.add(new MapOperations());
        fragments.add(new FavFragment());

        linearLayout = (LinearLayout) findViewById(R.id.main_layout);

        colors=  new int[]{
                this.getResources().getColor(R.color.primary_text),
                this.getResources().getColor(R.color.primary),
                this.getResources().getColor(R.color.accent)
        };

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings != null) {
            currentPage = settings.getInt(CURRENT_TAB, LIST_FRAGMENT_ID);
            currentDrawerItem = settings.getInt(CURRENT_DRAWER_ITEM, DRAWER_ITEM_SEARCH_ID);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    notification.displaySnackbar(MyNotification.INFO_SNACKBAR,
                            linearLayout, "Token sent to server", null);
                } else {
                    notification.displaySnackbar(MyNotification.WARNING_SNACKBAR,
                            linearLayout, "Token NOT sent to server", null);
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        setupDrawer(currentDrawerItem);

        if(currentDrawerItem == DRAWER_ITEM_SEARCH_ID)
            setupTabs(currentPage);

        setupConnCheck();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            isReceiverRegistered = true;
        }
        super.onPause();
    }


    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT_TAB, currentPage);
        editor.putInt(CURRENT_DRAWER_ITEM, currentDrawerItem);
        // Commit the edits!
        editor.apply();
        dp.close();
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
            isConnect = true;
            displayText = getResources().getString(R.string.slow_connect_noti);
            notification.displaySnackbar(MyNotification.WARNING_SNACKBAR, linearLayout, displayText, null);
        }
        if (!isConnected) {
            isConnect = false;
            displayText = getResources().getString(R.string.disconnect_noti);
            String actionText = getResources().getString(R.string.empty_button);
            notification.displaySnackbar(MyNotification.ALERT_SNACKBAR, linearLayout, displayText, actionText);
        } else {
            isConnect = true;
            notification.hideSnackbar();
        }

    }

    //Set up navigation drawer
    private void setupDrawer(int currentSelected) {
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        if (nvDrawer != null) {
            // Setup drawer view
            setupDrawerContent(nvDrawer);
            drawerToggle = setupDrawerToggle();
            mDrawer.setDrawerListener(drawerToggle);
            nvDrawer.setItemTextAppearance(android.R.style.TextAppearance_Material_Body2);
            nvDrawer.setItemTextColor(new ColorStateList(states, colors));
            nvDrawer.setItemIconTintList(new ColorStateList(states, colors));
        }

        setupFragment(currentSelected);
    }

    /* This method create Sliding Tab Layout
     * Set up View Pager for Sliding Tab layout
     * Create fragment list to add into layout
     */
    private void setupTabs(int currentTab) {
        //setup ViewPager for tab
        mPager = (ViewPager) findViewById(R.id.viewPager);
        MyPageViewAdapter myPageViewAdapter = new MyPageViewAdapter(getApplicationContext(),
                getSupportFragmentManager(), fragments);
        if (mPager != null) {
            //set previous saved tab position
            mPager.setAdapter(myPageViewAdapter);
            mPager.setCurrentItem(currentTab);
        }
        //setup Tab
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        if (mSlidingTabLayout != null) {
            mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setViewPager(mPager);
            mSlidingTabLayout.setOnPageChangeListener(this);
            mSlidingTabLayout.setVisibility(View.VISIBLE);
        }
    }

    private void removeAllTabs(){
        if(fragments != null && fragments.size()>0 && mSlidingTabLayout !=null){
            mPager.setAdapter(null);
            mSlidingTabLayout.setVisibility(View.GONE);
            mSlidingTabLayout = null;

        }
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setBackgroundColor(getResources().getColor(R.color.icons));
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
    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        if (menuItem.getItemId() == R.id.nav_exit) {
            if (isConnect)
                signOut();
            else
                notification.displayToast(this, getResources().getString(R.string.signout_fail));
        } else {
            switch (menuItem.getItemId()) {
                case R.id.nav_first_fragment:
                    currentDrawerItem = DRAWER_ITEM_SEARCH_ID;
                    setupFragment(DRAWER_ITEM_SEARCH_ID);
                    break;
                case R.id.nav_second_fragment:
                    currentDrawerItem = DRAWER_ITEM_MANAGE_ID;
                    setupFragment(DRAWER_ITEM_MANAGE_ID);
                    break;
                case R.id.nav_third_fragment:
                    currentDrawerItem = DRAWER_ITEM_PROFILE_ID;
                    setupFragment(DRAWER_ITEM_PROFILE_ID);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown fragment");
            }
        }
        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }

    private void setupFragment( int currentPosition){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        switch (currentPosition){
            case DRAWER_ITEM_SEARCH_ID:
                setTitle(R.string.drawer_search);
                if(fragment!=null)
                    fragmentTransaction.remove(fragment).commit();
                setupTabs(currentPage);
                break;
            case DRAWER_ITEM_MANAGE_ID:
                removeAllTabs();
                setTitle(R.string.drawer_manage);
                fragment = new ManageBookingFragment();
                fragmentTransaction.replace(R.id.main_panel, fragment).commit();
                break;
            case DRAWER_ITEM_PROFILE_ID:
                removeAllTabs();
                setTitle(R.string.drawer_profile);
                fragment = new UserProfileFragment();
                fragmentTransaction.replace(R.id.main_panel, fragment).commit();
                break;
        }
    }

    /**
     * This method execute the sign out action which remove current user & session tokens from local
     * and server database, then navigate to welcome screen
     */
    private void signOut() {
        //remove local tokens session
        String token, id;
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
        userFunctions.removeCurrentUser(En_Decrypt.fromHex(id));
        //remove all faved doctor from local db
        favDoctorFunctions.removeAllDoctorFromLocal();

        bookingsFunctions.removeAllBookings();
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
    public void setSnackBarAction() {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Play service", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    //Make a API call and receive data
    public class NetworkConnection extends AsyncTask<String, Void, Boolean> {

        private final static String mLogTag = "Medi-Care";

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean isRemoved = false;
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
                JSONObject jsonObject = new JSONObject(result.toString());
                isRemoved = jsonObject.getBoolean("status");
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