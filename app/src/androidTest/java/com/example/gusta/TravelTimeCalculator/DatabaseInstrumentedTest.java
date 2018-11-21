package com.example.gusta.TravelTimeCalculator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
    @Test
    public void setupDatabase() throws Exception {
        // Verify that it is possible to create the database. INCOMPLETE
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBHelper mydb;
        mydb = new DBHelper(appContext);

        //"___" added by purpose to make the incomplete test fail.
        assertEquals("com.example.gusta.traveltimecalculator___", appContext.getPackageName());
    }

    /*
    @Test
    public void addDrivingEntryu() throws Exception {
    //Add one entry to the database with duration and distance for DRIVNG only.
    //Verify that there exists only one entry and that the corresponding TRANSIT values are null.
    }
     */

        /*
    @Test
    public void addDuplicateEntry() throws Exception {
    //Add one entry to the database with duration and distance for DRIVNG only.
    //Try to add a second entry for the same pairs of coordinates.
    //Verify that the entry to the database is updated and that there are not two similar entries.
    }
     */

}
