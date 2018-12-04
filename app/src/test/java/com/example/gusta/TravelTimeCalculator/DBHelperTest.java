package com.example.gusta.TravelTimeCalculator;

import org.junit.Test;
//import com.google.common;
//import com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import com.example.gusta.TravelTimeCalculator.DBHelper;

import static org.junit.Assert.assertNotNull;


//@RunWith(RobolectricTestRunner::class)
//@Config(constants = BuildConfig::class, sdk = intArrayOf(LOLLIPOP), packageName = "your.package.name")
//@Config(constants = BuildConfig::class)
public class DBHelperTest {

    //DBHelper mydb;

    @Test
    public void addition_isCorrect2() throws Exception {
        //mydb = new DBHelper(ApplicationProvider.getApplicationContext());


        assertEquals(4, 2 + 2);
    }


    @Test
    public void databaseCreation() throws Exception {
        assertEquals(4, 2 + 2);

    }
}