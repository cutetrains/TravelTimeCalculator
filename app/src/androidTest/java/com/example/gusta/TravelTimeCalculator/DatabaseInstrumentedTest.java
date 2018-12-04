package com.example.gusta.TravelTimeCalculator;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {
    Context appContext = InstrumentationRegistry.getTargetContext();
    DBHelper testDb = new DBHelper(appContext);

    @Before
    public void setup() {

        //private TestOpenHelper helper;
        //Context appContext = InstrumentationRegistry.getTargetContext();
        //DBHelper testDb = new DBHelper(appContext);
        //mydb.clearDbAndRecreate() // This is just to clear the db
    }

    @Test
    public void setupDatabase() throws Exception {
        assertEquals("com.example.gusta.traveltimecalculator", appContext.getPackageName());
    }


    @Test
    public void addDrivingEntryu() throws Exception {
    //Add one entry to the database with duration and distance for DRIVNG only.
    //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        assertEquals(testDb.getDistance(100,200,300,400), 500);
    }


        /*
    @Test
    public void addDuplicateEntry() throws Exception {
    //Add one entry to the database with duration and distance for DRIVNG only.
    //Try to add a second entry for the same pairs of coordinates.
    //Verify that the entry to the database is updated and that there are not two similar entries.
    }
     */
     @After
     public void tearDown(){
        //TearDown db
     }
}
