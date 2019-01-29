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

import java.util.ArrayList;

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
        assertArrayEquals(expectedArray, testDb.getShortestDistanceOrDuration(100,200,300,400, "Distance"));
        int [] expectedArray2={-1, -1};
        assertArrayEquals(expectedArray2, testDb.getShortestDistanceOrDuration(100,201,300,400, "Distance"));
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
        int [] expectedArray = {400, 2};
        assertArrayEquals(expectedArray, testDb.getShortestDistanceOrDuration(100,200,300,400, "Distance"));
        int [] expectedArray2 = {600, 1};
        assertArrayEquals(expectedArray2, testDb.getShortestDistanceOrDuration(100,200,300,400, "Duration"));
    }

    @Test
    public void addEntryAllModes_getAllData_normAndReversed() throws Exception {
        Log.d("GustafTag","Test: Testing addEntryMultipleModes_getShortestDistance");
        //Add one entry to the database with duration and distance for DRIVNG and TRANSIT.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        testDb.addEntry(100,200,300,400,"TRANSIT",
                400,900);
        testDb.addEntry(100,200,300,400,"WALKING",
                300,800);
        testDb.addEntry(100,200,300,400,"BICYCLING",
                200,700);
        int [] expectedArray = {200, 700, 500, 600, 400, 900, 300, 800};
        int [] defaultArray = {0,0};
        assertArrayEquals(expectedArray, testDb.getAllDistanceDuration(100,200,300,400));
        assertArrayEquals(expectedArray, testDb.getAllDistanceDuration(300,400,100,200));

        assertArrayEquals(defaultArray, testDb.getAllDistanceDuration(300,401,100,200));
    }


    @Test
    public void addEntryMultipleModesReversed_getShortestDistance() throws Exception {
        Log.d("GustafTag","Test: Testing addEntryMultipleModes_getShortestDistance");
        //Add one entry to the database with duration and distance for DRIVNG and TRANSIT.
        //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
        testDb.addEntry(100,200,300,400,"DRIVING",
                500,600);
        testDb.addEntry(300,400,100,200,"BICYCLING",
                100,900);
        int [] expectedArray = {100, 0};
        assertArrayEquals(expectedArray, testDb.getShortestDistanceOrDuration(100,200,300,400, "Distance"));
    }

    /*@Test
    public void addMultipleEntries_getCoordinatePairs() throws Exception {

        ArrayList<ArrayList<Integer>> expectedValues = [1,2],[3,4],[5,6]]
        int expectedValues[][] = {
            {1, 2},
            {3, 4},
            {5, 6}
        };
        ArrayList<ArrayList<Integer>> returnValues = testDb.getCoordinatePairsForPosition(300,400);
        assertEquals(returnValues.length, expectedValues.length);
        for(int iii=0; iii < expectedValues.length; iii++) {
            assertEquals(returnValues[iii].length, expectedValues[iii].length);
            for (int jjj = 0; jjj < expectedValues[iii].length; jjj++) {
                assertEquals(returnValues[iii][jjj], expectedValues[iii][jjj]);
                Log.d("GustafTest", "Test: addMultipleEntries_getCoordinatePairs "+
                        iii + " " + jjj);
            }
            //Log.d("GustafTest", "Test: addMultipleEntries_getCoordinatePairs");

        }
    }*/


}
