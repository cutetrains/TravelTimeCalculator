package com.example.gusta.TravelTimeCalculator;

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
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
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

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mydb = new DBHelper(this);

        Log.d("GustafTag",  "Create DB");
        Log.d("GustafTag",  mydb.getDatabaseName());

        final Button button = findViewById(R.id.scan_button);
        //TODO: Rename button to "`scanButton"
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("GustafTag", "Button Tapped!");
                LatLng cameraTarget = mMap.getCameraPosition().target;
                double lat = cameraTarget.latitude;
                double lon = cameraTarget.longitude;
                Log.d("GustafTag", String.valueOf(lat));
                Log.d("GustafTag", String.valueOf(lon));
                if(cameraTarget == null){
                    Log.d("GustafTag","cameraTarget is null");
                } else {
                    Log.d("GustafTag","cameraTarget is not null");
                    getDirections(cameraTarget);
                }
            }
        });
                 Log.d("GustafTag",  "MapsActivity:Creating GeoData");
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
        Log.d("GustafTag",  "MapsActivity:onCreate finished!");

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(55.6, 13);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Malmö"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /*
     * The location is rounded to three decimals by multiplying the coordinates by 1000 and
     * converting to integer. Rounding a float will give unpredictable numerical side effects.
     *
     */
    private void getDirections(LatLng cameraCoordinates){
        StringBuilder sb;
        sb = new StringBuilder();

        String apiKey = BuildConfig.DirectionsApiKey;
        String url;
        int roundedLat = (int) (cameraCoordinates.latitude * 1000);
        int roundedLon = (int) (cameraCoordinates.longitude * 1000);

        if(cameraCoordinates == null){
            Log.d("GustafTag", "Null coordinates");
        } else {
            //Rounded location.

            Log.e("GustafTag",
                    "W : " + (roundedLat + 1) + "/" + (roundedLon - 1) +
                            " C : " + (roundedLat + 1) + "/" + roundedLon +
                            " E : " + (roundedLat + 1) + "/" + (roundedLon + 1));
            Log.e("GustafTag",
                    "W : " + roundedLat + "/" + (roundedLon - 1) +
                            " C : " + roundedLat + "/" + roundedLon +
                            " E : " + roundedLat + "/" + (roundedLon + 1));
            Log.e("GustafTag",
                    "W : " + (roundedLat - 1) + "/" + (roundedLon - 1) +
                            " C : " + (roundedLat - 1) + "/" + roundedLon +
                            " E : " + (roundedLat - 1) + "/" + (roundedLon + 1));
        }
        sb.append("https://maps.googleapis.com/maps/api/directions/json");
        sb.append("?origin=" + (float)(roundedLat-10)/1000+","+(float)(roundedLon)/1000);
        sb.append("&destination=" + (float)(roundedLat)/1000+","+(float)(roundedLon)/1000);
        sb.append("&mode=bicycling");
        sb.append("&key="+apiKey);
        url = sb.toString();
        Log.d("GustafTag", url);

        /**************************************************************
         * UNCOMMENT BELOW TO SEND REQUESTS                           *
         * ************************************************************
        JsonObjectRequest request= new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int [] results = readDirections(response);
                        Log.d("GustafTag", String.valueOf(results[0]));
                        Log.d("GustafTag", String.valueOf(results[1]));

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);
        **********************************************/
        //mydb.addEntry(30000, 40000, 30004, 40001,
        //        "DRIVING", 1000,100);
        mydb.addEntry(30000, 40000, 30004, 40001,
                "TRANSIT", 900,900);
        int[] value = mydb.getShortestDistance(30000, 40000, 30004, 40001);
        Log.d("GustafTag","Distance shall be 900. Is: " + String.valueOf(value[0]));
    }

    private int[] readDirections(JSONObject js){
        int duration = -1;
        int distance = -1;
        try {
            JSONObject legs = js.getJSONArray("routes").getJSONObject(0).
                    getJSONArray("legs").getJSONObject(0);
            distance = legs.getJSONObject("distance").getInt("value");
            duration = legs.getJSONObject("duration").getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new int[] {distance, duration};
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
                                Log.e("GustafTag", "mLastKnownLocation is  null");//THIS IS CAUSING CRASH! WHY ISN*T THAT POPULATED?
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
