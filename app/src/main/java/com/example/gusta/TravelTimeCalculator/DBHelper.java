package com.example.gusta.TravelTimeCalculator;


import android.content.Context;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// https://www.tutorialspoint.com/android/android_sqlite_database.htm
//public class DBHelper extends SQLiteOpenHelper {
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String DB_TABLE_NAME = "distanceDurationDb";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        Log.v("GustafTag", "In DBHelper:constructor");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS distanceDurationDb");
        onCreate(db);
        Log.v("GustafTag", "In DBHelper:onUpgrade");

    }

    public void addEntry(int origLat, int origLon, int destLat, int destLon, String modeOfTransport,
                         int distance, int duration){
        /*switch(modeOfTransport) {
            case "DRIVING":
                Log.d("GustafTag", "INSERT INTO distanceDurationDb (origLat, origLon, " +
                        "destLat, destLon, drivingDistance, drivingDuration) VALUES (100, 200, " +
                        "300, 400, 10, 20);");
                break;
            case "WALKING":
                Log.d("GustafTag", "INSERT INTO distanceDurationDb (origLat, origLon, " +
                        "destLat, destLon, walkingDistance, walkingDuration) VALUES (100, 200, " +
                        "300, 400, 10, 20);");
                break;
            case "TRANSIT":
                Log.d("GustafTag", "INSERT INTO distanceDurationDb (origLat, origLon, " +
                        "destLat, destLon, transitDistance, transitDuration) VALUES (100, 200, " +
                        "300, 400, 10, 20);");
            break;
            case "BICYCLE":
            Log.d("GustafTag", "INSERT INTO distanceDurationDb (origLat, origLon, " +
                    "destLat, destLon, bicycleDistance, bicycleDuration) VALUES (100, 200, " +
                    "300, 400, 10, 20);");
            break;
        }*/
        Log.d("GustafTag", "g");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub


        db.execSQL("CREATE TABLE distanceDurationDb " +
                   "(origLat int, origLon int, destLat int, destLon int, drivingDistance int, " +
                   "drivingDuration int, transitDistance int, transitDuration int, " +
                   "walkingDistance int, walkingDuration int, cyclingDistance int, " +
                   "cyclingDuration int, " +
                   "CONSTRAINT UC_Person UNIQUE (origLat, origLon, destLat, destLon) ); "
        );
        Log.v("GustafTag", "In DBHelper:onCreate");
    }

}
