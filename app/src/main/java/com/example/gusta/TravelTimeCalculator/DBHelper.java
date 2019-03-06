package com.example.gusta.TravelTimeCalculator;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;

// https://www.tutorialspoint.com/android/android_sqlite_database.htm
//public class DBHelper extends SQLiteOpenHelper {
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String DB_TABLE_NAME = "distanceDurationDb";
    public static final String TABLE_ORIGLAT = "origLat";
    public static final String TABLE_ORIGLON = "origLon";
    public static final String TABLE_DESTLAT = "destLat";
    public static final String TABLE_DESTLON = "destLon";
    public static final String TABLE_BICYCLING_DISTANCE = "bicyclingDistance";
    public static final String TABLE_BICYCLING_DURATION = "bicyclingDuration";
    public static final String TABLE_DRIVING_DISTANCE = "drivingDistance";
    public static final String TABLE_DRIVING_DURATION = "drivingDuration";
    public static final String TABLE_TRANSIT_DISTANCE = "transitDistance";
    public static final String TABLE_TRANSIT_DURATION = "transitDuration";
    public static final String TABLE_WALKING_DISTANCE = "walkingDistance";
    public static final String TABLE_WALKING_DURATION = "walkingDuration";
    public static final String TABLE_CONSTRAINT_COORDINATE = "UC_coordinates";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS distanceDurationDb");
        onCreate(db);
    }

    //TODO add method for printing db to console

    public void addEntry(int origLat, int origLon, int destLat, int destLon, String modeOfTransport,
                         int distance, int duration){
        SQLiteDatabase db = this.getWritableDatabase();
        int coordinatesExistInDb = checkIfCoordinateExists(origLat, origLon, destLat, destLon);

        String distanceMode = "";
        String durationMode = "";
        switch(modeOfTransport) {
            case "BICYCLING":
                distanceMode = "bicyclingDistance";
                durationMode = "bicyclingDuration";
                break;
            case "DRIVING":
                distanceMode = "drivingDistance";
                durationMode = "drivingDuration";
                break;
            case "WALKING":
                distanceMode = "walkingDistance";
                durationMode = "walkingDuration";
                break;
            case "TRANSIT":
                distanceMode = "transitDistance";
                durationMode = "transitDuration";
                break;
        }

        ContentValues values = new ContentValues();
        if(coordinatesExistInDb == 0) {
            values.put(TABLE_ORIGLAT, origLat);
            values.put(TABLE_ORIGLON, origLon);
            values.put(TABLE_DESTLAT, destLat);
            values.put(TABLE_DESTLON, destLon);
            values.put(distanceMode, distance);
            values.put(durationMode, duration);
            long newRowId = db.insert(DB_TABLE_NAME, null, values);
        } else {
            String[] whereArgs = {String.valueOf(origLat), String.valueOf(origLon),
                    String.valueOf(destLat), String.valueOf(destLon)};
            values.put(distanceMode, distance);
            values.put(durationMode, duration);
            if(coordinatesExistInDb == 1) {
                db.update(DB_TABLE_NAME,
                        values,
                        TABLE_ORIGLAT + " = ?  AND " + TABLE_ORIGLON + " = ? AND " +
                                TABLE_DESTLAT + " = ? AND " + TABLE_DESTLON + " = ?",
                        whereArgs);
            } else { //REVERSED
                db.update(DB_TABLE_NAME,
                        values,
                        TABLE_DESTLAT + " = ?  AND " + TABLE_DESTLON + " = ? AND " +
                                TABLE_ORIGLAT + " = ? AND " + TABLE_ORIGLON + " = ?",
                        whereArgs);
            }
        }
        db.close();
    }

    /* ComparisonMode is
    *  0 Cost - not valid!
    *  1 Distance
    *  2 Duration*/
    public int[] getBestTravelMode(int origLat, int origLon, int destLat, int destLon, int comparisonMode){
        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        int bestMode = -1;
        int bestValue = Integer.MAX_VALUE;
        int start;
        int end;
        if(comparisonMode == 1){ //Cost
            start = 0;
            end = 7;
        } else if(comparisonMode == 2) { // Distance
            start = 0;
            end = 3;
        } else {// //Duration
            start = 4;
            end = 7;
        }

        try{
            String[] columns = {TABLE_BICYCLING_DISTANCE, TABLE_DRIVING_DISTANCE,
                                TABLE_TRANSIT_DISTANCE, TABLE_WALKING_DISTANCE,
                                TABLE_BICYCLING_DURATION, TABLE_DRIVING_DURATION,
                                TABLE_TRANSIT_DURATION, TABLE_WALKING_DURATION};

            int coordinateIsNotReversed = checkIfCoordinateExists(origLat, origLon, destLat, destLon);

            if (coordinateIsNotReversed == 1){
                queryCursor = db.query(DB_TABLE_NAME, columns,
                        "origLat=" + origLat + " AND origLon=" + origLon + " AND destLat=" +
                                destLat + " AND destLon="+destLon,
                        null, null, null, null);
            } else {
                queryCursor = db.query(DB_TABLE_NAME, columns,
                        "origLat=" + destLat + " AND origLon=" + destLon + " AND destLat=" +
                                origLat + " AND destLon="+ origLon ,
                        null, null, null, null);
            }

            if (queryCursor.moveToFirst()) {
                for (int iii = start; iii <= end; iii++) {
                    if (queryCursor.getInt(iii) != 0) {
                        if (queryCursor.getInt(iii) < bestValue) {
                            bestValue = queryCursor.getInt(iii);
                            bestMode = iii - start;
                        }
                    }
                }
            }
            if(comparisonMode == 1){
                //Log.d("GustafTag", "Calculating costs!");
                //for(int iii =0; iii<8; iii++){
                //    Log.d("GustafTag", String.valueOf(queryCursor.getInt(iii)));
                //}
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
                int costEmissions = Integer.valueOf(sharedPreferences.getString("general_cost_emission", "-1"));
                int costTime = Integer.valueOf(sharedPreferences.getString("general_cost_time", "-1"));
                int bicyclingStartStopTime = Integer.valueOf(sharedPreferences.getString("travel_mode_bicycling_start_stop", "-1"));
                int drivingStartStopTime = Integer.valueOf(sharedPreferences.getString("travel_mode_driving_start_stop","-1"));
                int drivingCostkm = Integer.valueOf(sharedPreferences.getString("travel_mode_driving_cost", "-1"));
                int drivingEmissionPerkm = Integer.valueOf(sharedPreferences.getString("travel_mode_driving_emissions", "-1"));
                int transitTicketCost = Integer.valueOf(sharedPreferences.getString("travel_mode_transit_cost", "-1"));
                int transitEmissions = Integer.valueOf(sharedPreferences.getString("travel_mode_transit_emissions", "-1"));

                int bicyclingDistance = queryCursor.getInt(0);
                int bicyclingDuration = queryCursor.getInt(4);
                double bicyclingCost = (bicyclingDuration + bicyclingStartStopTime * 2) * costTime / 3600.0;
                //Log.d("GustafTag", "Bicycle cost: " + bicyclingCost);

                /*
                 *   [s + s] * [SEK / h] * [h / s] =SEK
                  *  [m] * [SEK/km + SEK/kg * g/km] *[km / m] = SEK
                 */
                int drivingDistance = queryCursor.getInt(1);
                int drivingDuration = queryCursor.getInt(5);
                double drivingCost = ( drivingDuration + drivingStartStopTime * 2) * costTime / 3600.0 +
                        ( drivingCostkm + costEmissions * drivingEmissionPerkm/1000.0) * drivingDistance / 1000.0 ;
                //Log.d("GustafTag", "Driving cost: " + drivingCost );

                /* [SEK] + [s * SEK / h *  h / s] + [m * g/ km * SEK/kg *km/m * g/kg] */
                int transitDistance = queryCursor.getInt(2);
                int transitDuration = queryCursor.getInt(6);

                double transitCost = transitTicketCost + transitDuration * costTime / 3600.0 +
                        transitDistance * transitEmissions * costEmissions /1000000.0;
                //Log.d("GustafTag", "Transit cost: " + transitCost );

                /* [s * SEK/h * h/s] */
                int walkingDistance = queryCursor.getInt(3);
                int walkingDuration = queryCursor.getInt(7);
                double walkingCost = walkingDuration * costTime /3600.0;
                //Log.d("GustafTag", "Walking cost: " + walkingCost );

                //Compare the costs and return the lowest cost!
                bestMode = -1;
                bestValue = 1000000000;
                if(bicyclingCost < bestValue && bicyclingCost > 0 &&
                        bicyclingDistance != -1 && bicyclingDuration != -1){
                    bestValue = (int) bicyclingCost;
                    bestMode = 0;
                }
                if(drivingCost < bestValue && drivingCost > 0 &&
                        drivingDistance != -1 && drivingDuration != -1){
                    bestValue = (int) drivingCost;
                    bestMode = 1;
                }
                if(transitCost < bestValue && transitCost > 0 &&
                        transitDistance != -1 && transitDuration != -1){
                    bestValue = (int) transitCost;
                    bestMode = 2;
                }
                if(walkingCost < bestValue && drivingCost > 0 &&
                        walkingDistance != -1 && walkingDuration != -1){
                    bestValue = (int) walkingCost;
                    bestMode = 3;
                }
            }
            queryCursor.close();
        }catch(Exception ex){
            Log.d("GustafTag", ex.getMessage());
        }

        db.close();//ADDED TO RESOLVE POSSIBLE LOCK ISSUE
        if(bestValue == Integer.MAX_VALUE) {
            return new int[] {-1, -1};
        } else {
            return new int[] {bestValue, bestMode};
        }
    }

        //Returns all distances and durations for a coordinate.
    public int[] getAllDistanceDuration(int origLat, int origLon, int destLat, int destLon){
        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean queryOk = false;
        try{
            String[] columns = {TABLE_BICYCLING_DISTANCE, TABLE_BICYCLING_DURATION,
                    TABLE_DRIVING_DISTANCE, TABLE_DRIVING_DURATION,
                    TABLE_TRANSIT_DISTANCE, TABLE_TRANSIT_DURATION,
                    TABLE_WALKING_DISTANCE, TABLE_WALKING_DURATION};
            queryCursor = db.query(DB_TABLE_NAME, columns,
                    "origLat=" + origLat + " AND origLon=" + origLon + " AND destLat=" +
                            destLat + " AND destLon="+destLon,
                    null, null, null, null);
            int[] results =  {0,0,0,0,0,0,0,0};
            if(queryCursor.moveToFirst()){
                queryOk = true;
            } else{
                queryCursor = db.query(DB_TABLE_NAME, columns,
                        "origLat=" + destLat + " AND origLon=" + destLon + " AND destLat=" +
                                origLat + " AND destLon=" + origLon,
                        null, null, null, null);
                if(queryCursor.moveToFirst()) {
                    queryOk = true;
                }
            }
            if(queryOk == true){
                for (int iii = 0; iii < columns.length; iii++) {
                    results[iii] = queryCursor.getInt(iii);
                }
                return results;
            }
        }catch(Exception ex){
            Log.d("GustafTag", ex.getMessage());
        }
        return new int[] {0, 0};
    }

    //Returns 0 if no coordinate found, 1 if found or -1 if reversed is found.
    public int checkIfCoordinateExists(int origLat, int origLon, int destLat, int destLon) {
        Cursor queryCursor;
        Cursor reversedQueryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {TABLE_BICYCLING_DISTANCE, TABLE_DRIVING_DISTANCE,
                TABLE_TRANSIT_DISTANCE, TABLE_WALKING_DISTANCE};
        queryCursor = db.query(DB_TABLE_NAME, columns,
                "origLat=" + origLat + " AND origLon=" + origLon + " AND destLat=" +
                        destLat + " AND destLon="+destLon,
                null, null, null, null);
        int count = queryCursor.getCount();
        queryCursor.close();
        if(count == 0){
            reversedQueryCursor = db.query(DB_TABLE_NAME, columns,
                    "origLat=" + destLat + " AND origLon=" + destLon + " AND destLat=" +
                            origLat + " AND destLon=" + origLon,
                    null, null, null, null);
            int reversedCount = reversedQueryCursor.getCount();
            reversedQueryCursor.close();
            if(reversedCount == 0){
                return 0;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }

    public void listTableToConsole() {
        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String tableString = String.format("Table %s:\n", DB_TABLE_NAME);
        try {
            String[] columns = {"*"};
            queryCursor = db.rawQuery("SELECT * FROM "+ DB_TABLE_NAME, null);
            if (queryCursor.moveToFirst()) {
                String[] columnNames = queryCursor.getColumnNames();
                for (String name: columnNames) {
                    tableString+= String.format("%s ", name);
                }
                tableString += "\n";
                do {
                    for (String name: columnNames) {
                        tableString += String.format("%s;",
                                queryCursor.getString(queryCursor.getColumnIndex(name)));
                    }
                    tableString += "\n";
                } while (queryCursor.moveToNext());
            }
            queryCursor.close();
        } catch (Exception ex) {
            Log.d("GustafTag", ex.getMessage());
        }
        //Log.d("GustafTag", tableString);
        db.close();
    }

    ArrayList<ArrayList<Integer>> getCoordinatePairsForPosition(int pointLat, int pointLon){
        ArrayList<ArrayList<Integer>> returnList = new ArrayList<ArrayList<Integer>>();

        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        //Query all entries for dest with point as orig
        String[] columns = {TABLE_DESTLAT, TABLE_DESTLON};
        queryCursor = db.query(DB_TABLE_NAME, columns,
                "origLat=" + pointLat + " AND origLon=" + pointLon,
                null, null, null, null);
        if (queryCursor.moveToFirst()) {
            String[] columnNames = queryCursor.getColumnNames();
            do {
                ArrayList<Integer> x = new ArrayList<Integer>();
                for (String name: columnNames) {
                    x.add(queryCursor.getInt(queryCursor.getColumnIndex(name)));
                }
                returnList.add(x);

            } while (queryCursor.moveToNext());
        }
        //queryCursor.close();
        //Query all entries with point as dest
        String[] columnsReversed = {TABLE_ORIGLAT, TABLE_ORIGLON};
        queryCursor = db.query(DB_TABLE_NAME, columnsReversed,
                "destLat=" + pointLat + " AND destLon=" + pointLon,
                null, null, null, null);
        if (queryCursor.moveToFirst()) {
            String[] columnNames = queryCursor.getColumnNames();
            do {
                ArrayList<Integer> y = new ArrayList<Integer>();
                for (String name: columnNames) {
                    y.add(queryCursor.getInt(queryCursor.getColumnIndex(name)));
                }
                returnList.add(y);
            } while (queryCursor.moveToNext());

        }
        queryCursor.close();
        return returnList;
    }


    /* This method clears the test database  */
    public void clearTestDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_NAME, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " ("+ TABLE_ORIGLAT +" int, " +
                TABLE_ORIGLON + " int, " + TABLE_DESTLAT + " int, " + TABLE_DESTLON + " int, " +
                TABLE_BICYCLING_DISTANCE + " int, " + TABLE_BICYCLING_DURATION + " int, " +
                TABLE_DRIVING_DISTANCE + " int, " + TABLE_DRIVING_DURATION + " int, " +
                TABLE_TRANSIT_DISTANCE + " int, " + TABLE_TRANSIT_DURATION + " int, " +
                TABLE_WALKING_DISTANCE + " int, " + TABLE_WALKING_DURATION + " int, CONSTRAINT " +
                TABLE_CONSTRAINT_COORDINATE + " UNIQUE (" + TABLE_ORIGLAT + ", " + TABLE_ORIGLON +
                ", " + TABLE_DESTLAT + ", " + TABLE_DESTLON +") );"
        );
    }
}
