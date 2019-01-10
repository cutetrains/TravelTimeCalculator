package com.example.gusta.TravelTimeCalculator;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class TravelTimeHandler {

    /*STUB
    * This method is called when the camera target is changed.*/
    public void refreshDirections(LatLng orig, DBHelper db) {
        //Clear current markers
        Log.d("GustafTag", "Clearing all markers (NOT IMPLEMENTED YET!)");
        //Casting coordinates to database format
        Log.d("GustafTag", "Casting coordinates to db format (NOT IMPLEMENTED YET!)");
        //Search for coordinates from the current
        Log.d("GustafTag", "Querying database for any existing directions originating\n" +
                "or ending at this coordinate (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Querying Google Directions for partial entries in database" +
                " (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "   Adding directions to database, if additional found\n" +
                " (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Adding markers for all found directions (NOT IMPLEMENTED YET!)");
    }

    /*STUB
     * This method is called when the user taps the map.*/
    public void scanDirectionsForTap(LatLng orig, LatLng dest, DBHelper db){
        //Querying the database shouldn't be necessary, but the app needs to check whether the
        //position has a marker or not.
        Log.d("GustafTag", "Casting coordinates to db format (NOT IMPLEMENTED YET!)");
        //TODO BELOW ARE SAME AS THE LAST ONES IN refreshDirections. Create a method?
        Log.d("GustafTag", "Querying database for existing directions (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Querying directions for the missing directions (NOT IMPLEMENTED YET!)");
        Log.d("GustafTag", "Adding markers for all found directions (NOT IMPLEMENTED YET!)");
    }

    /*STUB
    * This will end with a call to the Google directions for adding missing directions to DB*/
    public void getDirectionFromDb(LatLng orig, LatLng dest, DBHelper db){
        Log.d("GustafTag", "Read directions from database between " + orig.toString()
                + " and " + dest);
        //db.listTableToConsole();

        //db.getAllDistanceDuration(orig.latitude)
    }

    /*STUB*/
    public void saveDirectionsToDb(DBHelper db){
        Log.d("GustafTag", "Save directions to datbase");
    }

    /*STUB
    * Add reference to map. If necessary, move method to MapsActivity*/
    public void clearMarkers(){
        Log.d("GustafTag", "Clear Markers");
    }

    /*STUB
     * Add reference to map. If necessary, move method to MapsActivity*/
    public void addMarkerMarkers(LatLng coordinate, int transportMode){
        Log.d("GustafTag", "Add marker");
    }

    /*
     * The location is rounded to three decimals by multiplying the coordinates by 1000 and
     * converting to integer. Rounding a float will give unpredictable numerical side effects.
     *
     */
    public void getDirectionsFromUrl(LatLng cameraCoordinates){
        StringBuilder sb;
        sb = new StringBuilder();

        String apiKey = BuildConfig.DirectionsApiKey;
        String url;
        int roundedLat = (int) (cameraCoordinates.latitude * 1000);
        int roundedLon = (int) (cameraCoordinates.longitude * 1000);

        if(cameraCoordinates == null){
            Log.d("GustafTag", "Null coordinates");
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
        //db.addEntry(30000, 40000, 30004, 40001,
        //        "TRANSIT", 900,320);
        //db.addEntry(30000, 40000, 30004, 40001,
        //        "DRIVING", 300,450);
        //db.addEntry(30000, 40000, 12004, 40001,
        //        "DRIVING", 300,450);
        //int[] distDurData = db.getAllDistanceDuration(30000,40000,30004,40001);

        //Log.d("GustafTag", Arrays.toString(distDurData));
        //int[] value = db.getShortestDistanceOrDuration(30000, 40000, 30004, 40001, "Distance");
        //Log.d("GustafTag","Distance shall be 300. Is: " + String.valueOf(value[0]));
        //value = db.getShortestDistanceOrDuration(30000, 40000, 30004, 40001, "Duration");
        //Log.d("GustafTag","Duration shall be 320. Is: " + String.valueOf(value[0]));
        //db.listTableToConsole();
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

