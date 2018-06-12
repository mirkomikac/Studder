package com.studder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.database.schema.UserTable;
import com.studder.fragments.InboxFragment;
import com.studder.fragments.ProfileFragment;
import com.studder.model.User;
import com.studder.sharedpreferconfiguration.SaveSharedPreferences;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "NavigationActivity";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private FloatingActionButton matchingButton;
    private ViewPager mFragmentVewPager;
    private LocationManager mLocationManager;
    private String provider;


    private TextView drawerProfileNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bunlde) -> start");

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        View headerView = navigationView.getHeaderView(0);
        drawerProfileNameTextView = (TextView) headerView.findViewById(R.id.text_view_nav_header_navigation_profile_name);
        drawerProfileNameTextView.setText(pref.getString(UserTable.Cols.NAME, "Application drawer"));

        navigationView.getMenu().findItem(R.id.nav_share).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_send).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_gallery).setVisible(false);

        navigationView.setNavigationItemSelectedListener(this);

        mFragmentVewPager = findViewById(R.id.view_pager_app_bar_navigation_fragments);


        FragmentManager fm = getSupportFragmentManager();
        SwipePagerAdapter spa = new SwipePagerAdapter(fm);

        mFragmentVewPager.setAdapter(spa);
        Log.d(TAG, "onCreate(Bunlde) -> added SwipePagerAdapter -> ViewPager");


        matchingButton = (FloatingActionButton) findViewById(R.id.fab);
        matchingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent matchingActivity = new Intent(NavigationActivity.this, MatchingActivity.class);
                startActivity(matchingActivity);
            }
        });
        Log.d(TAG, "onCreate(Bunlde) -> success");


        if (isProviderAvailable() && (provider != null)) {
            Log.d(TAG, "onMapReady(googleMap)-> locateCurrentPosition()");
            locateCurrentPosition();
        }
    }

    private void locateCurrentPosition() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            Log.d(TAG, "locateCurrentPosition()->  permission not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                Log.d(TAG, "locateCurrentPosition()-> request permission ");
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            // permission has been granted, continue as usual

            Location location = mLocationManager.getLastKnownLocation(provider);
            if(location == null){
                location = new Location(provider);
            }

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            final String username = preferences.getString(UserTable.Cols.USERNAME, "");

            JsonObject  json = new JsonObject ();
            json.addProperty("username", username);
            json.addProperty("latitude",location.getLatitude());
            json.addProperty("longitude",location.getLongitude());
            final String ipConfig = getResources().getString(R.string.ipconfig);
            Ion.with(getApplicationContext())
                    .load("POST","http://"+ipConfig+"/users/update")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject>  result) {
                            if(result.getHeaders().code() == 200){
                                Log.d(TAG, "code == 200");
                            } else {
                                Log.d(TAG, "code != 200");
                            }
                        }
                    });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()->  validate request permission");
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult()->  locateCurrentPosition()");
                locateCurrentPosition();
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    private boolean isProviderAvailable() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        if (mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;

            return true;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        if (provider != null) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // Tim6 -> Settings Activity
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        } else if(id == R.id.action_logout){
            String ipConfig = getResources().getString(R.string.ipconfig);
            // Tim6 -> Clear Data, Add Additional Options
        
            User u = new User();
            u.setUserDeviceToken(FirebaseInstanceId.getInstance().getToken());
            
            Ion.with(getApplicationContext())
                    .load("http://"+ipConfig+"/auth/logout")
                    .setJsonPojoBody(u)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if(result.getHeaders().code() == 200){
                                Log.d(TAG, "ion -> success 200");
                                SaveSharedPreferences.setLoggedIn(getApplicationContext(), false);
                                Intent logoutActivity = new Intent(NavigationActivity.this, LoginActivity.class);
                                startActivity(logoutActivity);
                                finish();
                            } else{
                                Log.d(TAG, "ion fail -> != 200");
                            }
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_match) {
            Intent matchActivity = new Intent(this, MatchingActivity.class);
            startActivity(matchActivity);
            return true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent googleMapsActivity = new Intent(this, GoogleMapsActivity.class);
            startActivity(googleMapsActivity);
            return true;
        } else if (id == R.id.nav_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class SwipePagerAdapter extends FragmentPagerAdapter{

        public SwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:{
                    return InboxFragment.newInstance();
                }
                case 1:{
                    return ProfileFragment.newInstance();
                }
                default:{
                    return  InboxFragment.newInstance();
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
