package com.studder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.adapters.InboxListAdapter;
import com.studder.adapters.ViewPagerAdapter;
import com.studder.sharedpreferconfiguration.SaveSharedPreferences;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView inboxListView;

    String[] inboxItems = { "Marko Kljajic", "Darko Tacic", "Stefan Varajic", "Mirko Mikac"};
    Integer[] inboxImgs = { R.drawable.ic_menu_camera, R.drawable.ic_menu_camera, R.drawable.ic_menu_camera, R.drawable.ic_menu_camera };

    private FloatingActionButton matchingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        InboxListAdapter inboxListAdapter = new InboxListAdapter(this, inboxItems, inboxImgs);

        inboxListView = (ListView) findViewById(R.id.inboxList);
        inboxListView.setAdapter(inboxListAdapter);

        inboxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent chatActivity = new Intent(NavigationActivity.this, ChatActivity.class);
                startActivity(chatActivity);
            }
        });

        //fixed
        matchingButton = (FloatingActionButton) findViewById(R.id.fab);
        matchingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent matchingActivity = new Intent(NavigationActivity.this, MatchingActivity.class);
                startActivity(matchingActivity);
            }
        });
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

            // Tim6 -> Clear Data, Add Additional Options
            Ion.with(getApplicationContext())
                    .load("http://192.168.137.1:8080/auth/logout")
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if(result.getHeaders().code() == 200){
                                Log.i("Logout", "code == 200");
                                SaveSharedPreferences.setLoggedIn(getApplicationContext(), false);
                                Intent logoutActivity = new Intent(NavigationActivity.this, LoginActivity.class);
                                startActivity(logoutActivity);
                                finish();
                            } else{
                                Log.wtf("Logout", "code != 200");
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
}
