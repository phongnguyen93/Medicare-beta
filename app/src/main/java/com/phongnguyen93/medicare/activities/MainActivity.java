package com.phongnguyen93.medicare.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.extras.CurrentUser;
import com.phongnguyen93.medicare.extras.En_Decrypt;
import com.phongnguyen93.medicare.fragments.ListFragment;
import com.phongnguyen93.medicare.fragments.SecondFragment;
import com.phongnguyen93.medicare.fragments.TestFragment;
import com.phongnguyen93.medicare.fragments.ThirdFragment;
import com.phongnguyen93.medicare.maps.LocationService;
import com.phongnguyen93.medicare.maps.MapOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import dmax.dialog.SpotsDialog;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends BaseActivity implements ListFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener,
        ThirdFragment.OnFragmentInteractionListener, View.OnClickListener, MaterialTabListener {
    public static final int TAB_SEARCH_RESULTS = 0;
    //int corresponding to our 1st tab corresponding to the Fragment where box office hits are dispalyed
    public static final int TAB_HITS = 1;
    //int corresponding to our 2nd tab corresponding to the Fragment where upcoming movies are displayed
    public static final int TAB_UPCOMING = 2;
    public static final int TAB_COUNT = 3;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private boolean SERVER_TOKEN_REMOVED=false;
    private SpotsDialog progressDialog;
    private CurrentUser currentUser;
    private  LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.setCancelable(false);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentUser = new CurrentUser(getApplicationContext());
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);
        locationService = new LocationService(getBaseContext());
        setupTabs();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Fragment fragment = (Fragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());
    }
    @Override
    public void onTabSelected(MaterialTab materialTab) {
        //when a Tab is selected, update the ViewPager to reflect the changes
        mPager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int icons[] = {R.drawable.ic_format_list_bulleted_white_24dp,
                R.drawable.ic_map_white_24dp,
                R.drawable.ic_event_note_white_24dp};

        final String[] title={getResources().getString(R.string.tab_list),getResources().getString(R.string.tab_map),getResources().getString(R.string.tab_fav)};
        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            Fragment fragment = null;
//            L.m("getItem called for " + num);
            switch (num) {
                case TAB_SEARCH_RESULTS:
                    fragment = ListFragment.newInstance("", "");
                    break;
                case TAB_HITS:
                    fragment = new  MapOperations();
                    break;
                case TAB_UPCOMING:
                    fragment = ThirdFragment.newInstance("", "");
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabs)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
        private String getTabTitle(int position) {
            return title[position];
        }

    }
    private void setupTabs() {
        final String[] title={getResources().getString(R.string.tab_list),getResources().getString(R.string.tab_map),getResources().getString(R.string.tab_fav)};
        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        //when the page changes in the ViewPager, update the Tabs accordingly
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);

            }
        });
        //Add all the Tabs to the TabHost
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setIcon(mAdapter.getIcon(i))
                            .setTabListener(this));
        }
    }
    private void signOut() {
        String token,id;
        DbOperations dp = new DbOperations(this);
        Cursor CR = dp.getToken(dp);
        CR.moveToFirst();
        do{
            id= CR.getString(0);
            token= CR.getString(1);
        }while (CR.moveToNext());
        removeServerToken(id, token);
        removeLocalToken(id,token);
        currentUser.removeCurrentUser(En_Decrypt.fromHex(id));
        Intent t = new Intent(MainActivity.this,WelcomeActivity.class);
        t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(t);
    }

    private void removeServerToken(String id, String token) {
        NetworkConnection connection = new NetworkConnection();
        String URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/signout/user?id="+id+"&token="+token+"";
        connection.execute(URL);
    }
    private void removeLocalToken(String id, String token) {
        DbOperations dp = new DbOperations(this);
        dp.removeToken(dp,id,token);
    }

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
                if(jsonObject!= null)
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
}