package com.example.gusta.TravelTimeCalculator;


import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

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
        Log.v("GustafTag", "In DBHelper:constructor");
        //db = getWritableDatabase();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS distanceDurationDb");
        onCreate(db);
        Log.d("GustafTag", "In DBHelper:onUpgrade");

    }

    public void addEntry(int origLat, int origLon, int destLat, int destLon, String modeOfTransport,
                         int distance, int duration){
        SQLiteDatabase db = this.getWritableDatabase();

        //TODO Investigate how to add to an existing entry.
        String distanceMode = "";
        String durationMode = "";
        //https://developer.android.com/training/data-storage/sqlite
        ContentValues values = new ContentValues();
        values.put(TABLE_ORIGLAT, origLat);
        values.put(TABLE_ORIGLON, origLon);
        values.put(TABLE_DESTLAT, destLat);
        values.put(TABLE_DESTLON, destLon);
        Log.d("GustafTag", "In DBHelper:AddEntry, "+ modeOfTransport );
        switch(modeOfTransport) {
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
            case "BICYCLING":
                distanceMode = "bicyclingDistance";
                durationMode = "bicyclingDuration";
                break;
        }
        values.put(distanceMode, distance);
        values.put(durationMode, duration);
        Log.d("GustafTag", values.toString());
        long newRowId = db.insert(DB_TABLE_NAME, null, values);
        db.close();
    }

    //TODO make generic
    public int getDistance(int origLat, int origLon, int destLat, int destLon){
        Cursor queryCursor;
        SQLiteDatabase db = this.getWritableDatabase();
        int dDist = -1;

        try{
            String sql = "select " + TABLE_DRIVING_DISTANCE + " from " + DB_TABLE_NAME + "where ";
            //String[] columns = {TABLE_BICYCLING_DISTANCE, TABLE_DRIVING_DISTANCE,
            //        TABLE_TRANSIT_DISTANCE, TABLE_WALKING_DISTANCE};
            String[] columns = {TABLE_DRIVING_DISTANCE};
            queryCursor = db.query(DB_TABLE_NAME, columns,
                    "origLat=" + origLat + " AND origLon=" + origLon + " AND destLat=" +
                            destLat + " AND destLon="+destLon,
                    null, null, null, null);
            if(queryCursor.moveToFirst()){
                dDist = queryCursor.getInt(0);
            }
        }catch(Exception ex){
            Log.d("GustafTag", ex.getMessage());
        }
        return dDist;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        Log.d("GustafTag", "In DBHelper:onCreate");
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
