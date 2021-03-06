package com.example.gusta.TravelTimeCalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.view.View;

//import com.example.gusta.TravelTimeCalculator.R;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static Context appContext;

    public GoogleMap mMap;//TEST
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private final LatLng mDefaultLocation = new LatLng(56, 13);
    private static final int DEFAULT_ZOOM = 15;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;

    public static List<Marker> markers = new ArrayList<>();

    public static LatLng truncatedCenter;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private Button settingsButton;

    /* FOR NOW, THE APP FOCUSES ON DURATION */
    TravelTimeHandler tth = new TravelTimeHandler( mMap);
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mydb = new DBHelper(this);
        appContext = getApplicationContext();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO Delete later?
        tth.updateSettings();
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

        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        Marker centerMarker = mMap.addMarker(new MarkerOptions()
                // .position(mMap.getCameraPosition().target)
                .position(new LatLng(55.6, 13))
                .title("Center")
                .snippet("Center is here!"));
        //centerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8target64));


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                tth.refreshDirections(mMap.getCameraPosition().target, mydb);
                truncatedCenter = new LatLng(
                        (Math.round(mMap.getCameraPosition().target.latitude * 1000.0) / 1000.0),
                        (Math.round(mMap.getCameraPosition().target.longitude * 1000.0) / 1000.0)
                );

                tth.clearMarkers();

                centerMarker.setPosition(truncatedCenter);
                ArrayList<ArrayList<Integer>> directionList = mydb.getCoordinatePairsForPosition(
                        (int) Math.round(truncatedCenter.latitude * 1000),
                        (int) Math.round(truncatedCenter.longitude * 1000));
                for (ArrayList<Integer> thisCoordinate : directionList) {
                    //Add marker
                    LatLng truncatedPoint = new LatLng(
                            (thisCoordinate.get(0) / 1000.0),
                            (thisCoordinate.get(1) / 1000.0));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(truncatedPoint));
                    MapsActivity.markers.add(marker);
                    //This will update the markers
                    tth.getShortestDirectionFromDb(truncatedPoint, mMap.getCameraPosition().target, mydb);
                }
            }
        });

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                tth.scanDirectionsForTap(point, mMap.getCameraPosition().target, mydb);
                LatLng truncatedPoint = new LatLng(
                        (Math.round(point.latitude * 1000.0) / 1000.0),
                        (Math.round(point.longitude * 1000.0) / 1000.0)
                );

                Marker marker = mMap.addMarker(new MarkerOptions().position(truncatedPoint));
                MapsActivity.markers.add(marker);
                tth.getShortestDirectionFromDb(point, mMap.getCameraPosition().target, mydb);

            }
        });
    }

    public void updateMarker(LatLng orig, LatLng dest, int bestValue, String unit, String bestMode) {
        //First, tell which point is related to the marker
        LatLng truncatedPoint;
        if (orig.equals(truncatedCenter)) {
            truncatedPoint = new LatLng(
                    (Math.round(dest.latitude * 1000.0) / 1000.0),
                    (Math.round(dest.longitude * 1000.0) / 1000.0));
        } else {
            truncatedPoint = new LatLng(
                    (Math.round(orig.latitude * 1000.0) / 1000.0),
                    (Math.round(orig.longitude * 1000.0) / 1000.0));
        }
        for (Marker thisMarker : MapsActivity.markers) {
            //CHECK if the marker coordinate match
            //Add information about best mode and best value

            if (truncatedPoint.equals(thisMarker.getPosition())) {
                thisMarker.setTag(bestMode);

                //Log.d("GustafTag", String.valueOf(bestValue) + unit);

                thisMarker.showInfoWindow();
                switch (bestMode) {
                    case "BICYCLING":
                        thisMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8bicycle24));
                        break;
                    case "DRIVING":
                        thisMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8car24));
                        break;
                    case "TRANSIT":
                        thisMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8bus24));
                        break;
                    case "WALKING":
                        thisMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons84running24));
                        break;
                }
                if(bestValue == 1000000000) {
                    thisMarker.setTitle("N/A");
                }else{
                    thisMarker.setTitle(String.valueOf(bestValue) + unit);
                }
            }
            thisMarker.showInfoWindow();
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation == null){
                                Log.e("GustafTag", "mLastKnownLocation is  null");
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
