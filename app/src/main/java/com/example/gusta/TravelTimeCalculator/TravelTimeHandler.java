package com.example.gusta.TravelTimeCalculator;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class TravelTimeHandler {

    public String durationOrDistance;
    /*Distance or Duration*/
    public TravelTimeHandler(String durOrDist) {
        durationOrDistance = durOrDist;
    }

    /*STUB
    * This method is called when the camera target is changed.*/
    public void refreshDirections(LatLng orig, DBHelper db) {
        //Clear current markers
        Log.d("GustafTag", "RESPONDING TO MOVE!");
        /*Log.d("GustafTag", "Clearing all markers (NOT IMPLEMENTED YET!)");
        //Casting coordinates to database format

        //Search for coordinates from the current
        Log.d("GustafTag", "Querying database for any existing directions originating\n" +
                "or ending at this coordinate (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Querying Google Directions for partial entries in database" +
                " (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "   Adding directions to database, if additional found\n" +
                " (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Adding markers for all found directions (NOT IMPLEMENTED YET!)");*/
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
        Log.d("GustafTag", "Read directions from database between " + orig.toString()
                + " and " + dest);
        int origLat = (int) ( orig.latitude * 1000 );
        int origLon = (int) ( orig.longitude * 1000 );
        int destLat = (int) ( dest.latitude * 1000 );
        int destLon = (int) ( dest.longitude * 1000 );

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

        Log.d("GustafTag", "Best mode: " + resultMode + ", time: " +results[0]);
        return results;
    }


    public void saveDirectionsToDb(LatLng orig, LatLng dest, String modeOfTransport,
                                   int distance, int duration, DBHelper db){
        int origLat = (int) ( orig.latitude * 1000 );
        int origLon = (int) ( orig.longitude * 1000 );
        int destLat = (int) ( dest.latitude * 1000 );
        int destLon = (int) ( dest.longitude * 1000 );
        db.addEntry(origLat, origLon, destLat, destLon, modeOfTransport, distance, duration);
    }

    public void clearMarkers(){
        Log.d("GustafTag", "Clear Markers");
    }

    /*STUB
     * Add reference to map. If necessary, move method to MapsActivity*/
    public void addMarkerMarkers(LatLng coordinate, int transportMode){
        Log.d("GustafTag", "Add marker");
    }

    /*STUB - SAVE RESULTS TO DB
     * The location is rounded to three decimals by multiplying the coordinates by 1000 and
     * converting to integer. Rounding a float will give unpredictable numerical side effects.
     *
     */
    public void getDirectionsFromUrl(LatLng orig, LatLng dest, String modeOfTransport, DBHelper db) {
        Log.d("GustafTag", "In getDirectionsFromUrl");
        StringBuilder sb;
        sb = new StringBuilder();

        String apiKey = BuildConfig.DirectionsApiKey;
        String url;
        int roundedOrigLat = (int) (orig.latitude * 1000);
        int roundedOrigLon = (int) (orig.longitude * 1000);
        int roundedDestLat = (int) (dest.latitude * 1000);
        int roundedDestLon = (int) (dest.longitude * 1000);

        sb.append("https://maps.googleapis.com/maps/api/directions/json");
        sb.append("?origin=" + (float) (roundedOrigLat) / 1000 + "," + (float) (roundedOrigLon) / 1000);
        sb.append("&destination=" + (float) (roundedDestLat) / 1000 + "," + (float) (roundedDestLon) / 1000);
        sb.append("&mode=" + modeOfTransport.toLowerCase());
        sb.append("&key=" + apiKey);
        url = sb.toString();
        Log.d("GustafTag", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int[] results = readDirectionsFromJson(response);
                saveDirectionsToDb(orig, dest, modeOfTransport, results[0], results[1], db);
                getShortestDirectionFromDb(orig, dest, db);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
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

