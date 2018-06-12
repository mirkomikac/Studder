package com.studder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.database.schema.UserTable;
import com.studder.model.Profile;
import com.studder.model.StudderCard;
import com.studder.model.User;
import com.studder.model.UserMatch;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static final String TAG = "GoogleMapsActivity";

    private LocationManager mLocationManager;
    private String provider;
    private Marker mCurrentPosition;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
                updateWithNewLocation(location);
                long minTime = 5000;// ms
                float minDist = 5.0f;// meter
                mLocationManager.requestLocationUpdates(provider, minTime, minDist, this);
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



    private void updateWithNewLocation(Location location) {
        Log.d(TAG, "updateWithNewLocation(location)->  get current position");
        if (location != null && provider != null) {
            double lng = location.getLongitude();
            double lat = location.getLatitude();
            Log.d(TAG, "updateWithNewLocation(location)-> addBoundaryToCurrentPosition(lat, lng)");
            addBoundaryToCurrentPosition(lat, lng);
            Log.d(TAG, "updateWithNewLocation(location)-> addUserMarkers()");

            Log.d(TAG, "updateWithNewLocation(location)-> cameraPostion()");
            CameraPosition camPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(12f).build();

            if (mMap != null)
                Log.d(TAG, "updateWithNewLocation(location)-> animateCamera()");
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPosition));
        } else {
            Log.d(TAG, "updateWithNewLocation(Location location)-> ERROR location == null || provider == null");
        }
    }

    private void addBoundaryToCurrentPosition(double lat, double lang) {

        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(new LatLng(lat, lang));
        mMarkerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.map_marker_blue));
        mMarkerOptions.anchor(0.5f, 0.5f);
        mMarkerOptions.flat(true);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        final Integer radius = preferences.getInt(UserTable.Cols.RADIUS, -1);
        mMarkerOptions.title(preferences.getString(UserTable.Cols.USERNAME, ""));
        mMarkerOptions.snippet(preferences.getString(UserTable.Cols.NAME, "")+" "+ preferences.getString(UserTable.Cols.SURNAME, ""));
        CircleOptions mOptions = new CircleOptions()
                .center(new LatLng(lat, lang)).radius(radius*1000)
                .strokeColor(0x110000FF).strokeWidth(1).fillColor(0x110000FF);
        mMap.addCircle(mOptions);
        if (mCurrentPosition != null)
            mCurrentPosition.remove();
        mCurrentPosition = mMap.addMarker(mMarkerOptions);

        final String ipConfig = getResources().getString(R.string.ipconfig);
        Log.d(TAG, "updateWithNewLocation(location)-> addBoundaryToCurrentPosition(lat, lng)-> get userMarkers");
        Ion.with(getApplicationContext())
                .load("http://"+ipConfig+"/users/getForMarking")
                .as(new TypeToken<List<User>>() {})
                .withResponse()
                .setCallback(new FutureCallback<Response<List<User>>>() {
                    @Override
                    public void onCompleted(Exception e, Response<List<User>> result) {
                        if(result.getHeaders().code() == 200){
                            Log.i(TAG, "success");
                            ArrayList<User> users = (ArrayList<User>) result.getResult();
                            //profile image
                            MarkerOptions mMarkerOptions = new MarkerOptions();
                            mMarkerOptions.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.map_marker_red));
                            mMarkerOptions.anchor(0.5f, 0.5f);
                            mMarkerOptions.flat(true);
                            for(User u : users){
                                mMarkerOptions.title(u.getUsername());
                                mMarkerOptions.snippet(u.getName()+" "+ u.getSurname());
                                mMarkerOptions.position(new LatLng(u.getLatitude(), u.getLongitude()));
                                mMap.addMarker(mMarkerOptions);
                            }
                        } else {
                            Log.e(TAG, "response != 200");
                        }

                    }
                });

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
    public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        updateWithNewLocation(null);
    }
}
