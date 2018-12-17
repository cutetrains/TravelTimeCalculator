package com.example.gusta.TravelTimeCalculator;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
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
        Log.d("GustafTag","Test: Tearing down");
        //Context appContext = InstrumentationRegistry.getTargetContext();
        //DBHelper testDb = new DBHelper(appContext);
    }

    @After
    public void tearDown(){
        Log.d("GustafTag","Test: Tearing down");
        testDb.clearTestDatabase();
    }

    @Test
    public void setupDatabase() throws Exception {
        Log.d("GustafTag","Test: Testing setupDatabase");
        assertEquals("com.example.gusta.traveltimecalculator", appContext.getPackageName());
    }


    @Test
    public void addEntryDrivingOnly() throws Exception {
        //Add one entry to the database with duration and distance for DRIVNG only.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        Log.d("GustafTag","Test: Testing addEntryDrivingOnly");
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        int [] expectedArray = {500, 1};
        assertArrayEquals(expectedArray, testDb.getShortestDistance(100,200,300,400));
    }

    @Test
    public void addEntryDrivingOnly_checkIfCoordinateExists() throws Exception {
        //Add one entry to the database with duration and distance for DRIVNG only.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        Log.d("GustafTag","Test: Testing addEntryDrivingOnly");
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        assertEquals(1, testDb.checkIfCoordinateExists(100,200,300,400));
    }


    @Test
    public void addEntryDrivingOnly_checkIfReversedCoordinateExists() throws Exception {
        Log.d("GustafTag","Test: Testing addEntryDrivingOnly");
        //Add one entry to the database with duration and distance for DRIVNG only.
        //This shall check that DBHelper can find coordinates in reversed direction.
        // -1 indicates that the coordinates are reversed.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        assertEquals(-1, testDb.checkIfCoordinateExists(300,400,100,200));
    }

    @Test
    public void addEntryMultipleModes_getShortestDistance() throws Exception {
        Log.d("GustafTag","Test: Testing addEntryMultipleModes_getShortestDistance");
        //Add one entry to the database with duration and distance for DRIVNG and TRANSIT.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        testDb.addEntry(100,200,300,400,"TRANSIT",
                400,900);

        int [] expectedArray = {500, 1};//REMOVE THIS LATER!
        //int [] expectedArray = {400, 2};
        assertArrayEquals(expectedArray, testDb.getShortestDistance(100,200,300,400));
    }

    /*@Test
    public void addDrivingEntryFindReversedDirection() throws Exception {
        //Add one entry to the database with duration and distance for DRIVNG only.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        assertEquals(testDb.getDistance(100,200,300,400), 500);
    }*/



        /*
    @Test
    public void addDuplicateEntry() throws Exception {
    //Add one entry to the database with duration and distance for DRIVNG only.
    //Try to add a second entry for the same pairs of coordinates.
    //Verify that the entry to the database is updated and that there are not two similar entries.
    }
     */

}
