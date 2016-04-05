package com.namlongsolutions.medicare.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Phong Nguyen on 14-Mar-16.
 * Test case for SQLite local database
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    //clean up the db before start to test
    void dbClear(){
        mContext.deleteDatabase(DataDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        dbClear();
    }

    //Test db if db and all tables has been created
    public void testCreateDb() throws Throwable{
        // build a HashSet of all of the table names
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(DataContract.UserEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.DoctorEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.BookingEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.FavDoctorEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.UserLogEntry.TABLE_NAME);

        mContext.deleteDatabase(DataDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DataDbHelper(mContext).getWritableDatabase();
        //pass if db is created and opened, fail if it is not
        assertEquals(true,db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // test if tables have correct columns
        // test DOCTOR tables
        c = db.rawQuery("PRAGMA table_info(" + DataContract.DoctorEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column of doctor table
        final HashSet<String> doctorColumnHashSet = new HashSet<String>();
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_ID);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_NAME);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_EMAIL);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_ADDRESS);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_PHONE);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_IMAGE);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_RATING);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_SPECIALITY);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_WORKDAYS);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_WORKTIME);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_LICENSE);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_DR_LOCATION);
        doctorColumnHashSet.add(DataContract.DoctorEntry.COLUMN_IS_FAV);

        int columnNameIndex = c.getColumnIndex(DataContract.DoctorEntry.COLUMN_DR_NAME);
        do {
            String columnName = c.getString(columnNameIndex);
            doctorColumnHashSet.remove(columnName);
        }while (c.moveToNext());
        // fail if db does not contain correct table
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                doctorColumnHashSet.isEmpty());
        db.close();
    }

    //Test doctor table
    public void testDoctorTable(){ insertDoctorTable();}

    private long insertDoctorTable() {
        DataDbHelper dbHelper = new DataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // create dummy data
        ContentValues values = TestUtils.createDummyDoctorData();
        // Insert ContentValues into database and get a row ID back
        long doctorRowId;
        doctorRowId = db.insert(DataContract.DoctorEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(doctorRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                DataContract.DoctorEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtils.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, values);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return doctorRowId;
    }
}


