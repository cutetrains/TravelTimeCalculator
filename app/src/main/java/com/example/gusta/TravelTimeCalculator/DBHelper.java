package com.example.gusta.TravelTimeCalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS distanceDurationDb");
        onCreate(db);
        Log.d("GustafTag", "In DBHelper:onUpgrade");
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

    /*Type is a string of either "Distance" or "Duration". */
    public int[] getShortestDistanceOrDuration(int origLat, int origLon, int destLat, int destLon, String type){
        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        int shortestMode = -1;
        int shortestValue = Integer.MAX_VALUE;
        int typeIsDuration = ( type == "Duration" ? 1 : 0 ) ;
        try{
            String[] columns = {TABLE_BICYCLING_DISTANCE, TABLE_BICYCLING_DURATION,
                    TABLE_DRIVING_DISTANCE, TABLE_DRIVING_DURATION, TABLE_TRANSIT_DISTANCE,
                    TABLE_TRANSIT_DURATION, TABLE_WALKING_DISTANCE, TABLE_WALKING_DURATION};
            queryCursor = db.query(DB_TABLE_NAME, columns,
                    "origLat=" + origLat + " AND origLon=" + origLon + " AND destLat=" +
                            destLat + " AND destLon="+destLon,
                    null, null, null, null);
            if(queryCursor.moveToFirst()){
                for(int iii = 0; iii< columns.length; iii++){
                    /* typeIsDuration = 0 (distance)
                     * iii  Mod 2
                     * 0   0       0   Match
                     * 1   1       0     -
                     *
                     * typeIsDuration= = 1 (duration)
                     * 0   0       0     -
                     * 1   1       1   Match
                     *
                      * */
                    if(iii % 2 == typeIsDuration){
                        if(queryCursor.getInt(iii) != 0) {
                            if (queryCursor.getInt(iii) < shortestValue) {
                                shortestValue = queryCursor.getInt(iii);
                                shortestMode = iii/2;
                            }
                        }
                    }
                }
            }
            queryCursor.close();

        }catch(Exception ex){
            Log.d("GustafTag", ex.getMessage());
        }

        db.close();//ADDED TO RESOLVE POSSIBLE LOCK ISSUE
        if(shortestValue == Integer.MAX_VALUE) {
            return new int[] {-1, -1};
        } else {
            return new int[] {shortestValue, shortestMode};
        }
    }

    /*Type is a string of either "Distance" or "Duration". */
    public int[] getCheapestTravelMode(int origLat, int origLon, int destLat, int destLon,
                                       int emissionCost, int timeCost, int bicyclingStartStopTime,
                                       int drivingStartStopTime, int drivingCost, int drivingEmissions,
                                       int tranistCost, int transitEmissions) {
        return new int[] {-1, -1};

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
