package com.example.gusta.TravelTimeCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// https://www.tutorialspoint.com/android/android_sqlite_database.htm
//public class DBHelper extends SQLiteOpenHelper {
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        Log.v("GustafTag", "In DBHelper:constructor");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
        Log.v("GustafTag", "In DBHelper:onUpgrade");

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    /*    db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );*/
        Log.v("GustafTag", "In DBHelper:onCreate");

    }
}
