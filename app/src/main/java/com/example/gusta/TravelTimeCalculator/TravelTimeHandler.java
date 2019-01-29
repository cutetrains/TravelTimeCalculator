package com.example.gusta.TravelTimeCalculator;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TravelTimeHandler {

    public String durationOrDistance;
    public GoogleMap thisMap;//TEMP
    /*Distance or Duration*/
    public TravelTimeHandler(String durOrDist, GoogleMap gmap) {//TEMP

        durationOrDistance = durOrDist;
        thisMap = gmap;//TEMP
    }

    /*STUB
    * This method is called when the camera target is changed.*/
    public void refreshDirections(LatLng orig, DBHelper db) {
        //Clear current markers

    }

    /*STUB
     * This method is called when the user taps the map.*/
    public void scanDirectionsForTap(LatLng orig, LatLng dest, DBHelper db){
        int [] results = getShortestDirectionFromDb(orig, dest, db);
        if( results[1] == -1 ) {
            getDirectionsFromUrl(orig, dest, "BICYCLING", db);
            getDirectionsFromUrl(orig, dest, "DRIVING", db);
            getDirectionsFromUrl(orig, dest, "TRANSIT", db);
            getDirectionsFromUrl(orig, dest, "WALKING", db);
        } else {
            Log.d("GustafTag", "Entry found in database. Msrker should already be there");
        }
    }

    /*CODE COMPLETE*/
    public int[] getShortestDirectionFromDb(LatLng orig, LatLng dest, DBHelper db){
        int origLat = (int) Math.round( orig.latitude * 1000 );
        int origLon = (int) Math.round( orig.longitude * 1000 );
        int destLat = (int) Math.round( dest.latitude * 1000 );
        int destLon = (int) Math.round( dest.longitude * 1000 );

        int [] results = db.getShortestDistanceOrDuration(origLat, origLon, destLat, destLon, durationOrDistance);
        String resultMode ="";

        switch(results[1]) {
            case 0:
                resultMode = "BICYCLING";
                break;
            case 1:
                resultMode = "DRIVING";
                break;
            case 2:
                resultMode = "WALKING";
                break;
            case 3:
                resultMode = "TRANSIT";
                break;
            default:
                resultMode = "N/A";
                break;
        }
        // Count all non-zero entries. If there are four good resullts, add a marker to the map.
        int [] nonNullResults = db.getAllDistanceDuration(origLat, origLon, destLat, destLon);
        int numberOfNulls = 0;
        for(int i : nonNullResults){
            if(i==0){
                numberOfNulls ++;
            }
        }
        if(numberOfNulls == 0) {
            MapsActivity mActivity= new MapsActivity();
            //We don't know if we're looking in the direct or reversed direction.
            // So provide both and let updateMarker tell which to look for
            // (the latlng that isn't at the center).
            mActivity.updateMarker(orig, dest, results[0], resultMode);
        }
        return results;
    }



    public void saveDirectionsToDb(LatLng orig, LatLng dest, String modeOfTransport,
                                   int distance, int duration, DBHelper db){

        //This rounds float values properly, eg 13.3399633->13340
        int origLat = (int) Math.round( orig.latitude * 1000 );

        //This truncates the product, eg 13.3399633 -> 13339
        int origLon = (int) Math.round( orig.longitude * 1000 );


        //int origLon = (int) Math.round( orig.longitude * 1000 );
        int destLat = (int) Math.round( dest.latitude * 1000 );
        int destLon = (int) Math.round( dest.longitude * 1000 );
        db.addEntry(origLat, origLon, destLat, destLon, modeOfTransport, distance, duration);
    }

    public void clearMarkers(){
        for (Marker thisMarker:  MapsActivity.markers){
            thisMarker.remove();
       }
       MapsActivity.markers.clear();

    }



    /*
     * The location is rounded to three decimals by multiplying the coordinates by 1000 and
     * converting to integer. Rounding a float will give unpredictable numerical side effects.
     *
     */
    public void getDirectionsFromUrl(LatLng orig, LatLng dest, String modeOfTransport, DBHelper db) {
        StringBuilder sb = new StringBuilder();
        String apiKey = BuildConfig.DirectionsApiKey;
        String url;
        int roundedOrigLat = (int) Math.round(orig.latitude * 1000);
        int roundedOrigLon = (int) Math.round(orig.longitude * 1000);
        int roundedDestLat = (int) Math.round(dest.latitude * 1000);
        int roundedDestLon = (int) Math.round(dest.longitude * 1000);

        sb.append("https://maps.googleapis.com/maps/api/directions/json");
        sb.append("?origin=" + (float) (roundedOrigLat) / 1000 + "," + (float) (roundedOrigLon) / 1000);
        sb.append("&destination=" + (float) (roundedDestLat) / 1000 + "," + (float) (roundedDestLon) / 1000);
        sb.append("&mode=" + modeOfTransport.toLowerCase());
        sb.append("&key=" + apiKey);
        url = sb.toString();
        Log.d("GustafTag", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int[] results = readDirectionsFromJson(response);
                saveDirectionsToDb(orig, dest, modeOfTransport, results[0], results[1], db);
                getShortestDirectionFromDb(orig, dest, db);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Context context = MapsActivity.getAppContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        queue.add(request);
    }

    /* CODE COMPLETE */
    private int[] readDirectionsFromJson(JSONObject js){
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
}

