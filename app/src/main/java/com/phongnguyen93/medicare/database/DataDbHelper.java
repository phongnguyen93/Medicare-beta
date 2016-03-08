package com.phongnguyen93.medicare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phongnguyen93.medicare.database.DataContract.UserEntry;
import com.phongnguyen93.medicare.database.DataContract.DoctorEntry;
import com.phongnguyen93.medicare.database.DataContract.UserLogEntry;
import com.phongnguyen93.medicare.database.DataContract.BookingEntry;
import com.phongnguyen93.medicare.database.DataContract.FavDoctorEntry;


/**
 * Created by Phong Nguyen on 08-Mar-16.
 * Manages local database for Medicare app
 */
public class DataDbHelper extends SQLiteOpenHelper {

    // Current version of the database
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "medicare_data.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold user data.  A user consists of the string supplied
        // with id, name, email, address, phone, image path, birth day and session login token
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE" + UserEntry.TABLE_NAME + " (" +
                UserEntry.COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL," +
                UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL," +
                UserEntry.COLUMN_USER_ADDRESS + " TEXT," +
                UserEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL," +
                UserEntry.COLUMN_USER_PHONE + " TEXT NOT NULL," +
                UserEntry.COLUMN_USER_BDAY + "INTEGER," +
                UserEntry.COLUMN_USER_IMAGE + "TEXT," +
                UserEntry.COLUMN_USER_TOKEN + "TEXT NOT NULL" +
                ");";

        // Create a table to hold doctor info data.  A doctor  consists of the string supplied
        // with id, name, email, address, phone, image path, license, speciality, work time
        // , work days, rating and location coordination ( lat, lng) to add on map
        final String SQL_CREATE_DOCTOR_TABLE = "CREATE TABLE" + DoctorEntry.TABLE_NAME + " (" +
                DoctorEntry._ID +" INTEGER AUTOINCREMENT,"+
                DoctorEntry.COLUMN_DR_ID + " TEXT PRIMARY KEY," +
                DoctorEntry.COLUMN_DR_NAME + " TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_ADDRESS + " TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_EMAIL + " TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_PHONE + " TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_LICENSE + "TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_IMAGE + "TEXT," +
                DoctorEntry.COLUMN_DR_SPECIALITY + "TEXT NOT NULL," +
                DoctorEntry.COLUMN_DR_WORKDAYS + "TEXT NOT NULL,"+
                DoctorEntry.COLUMN_DR_WORKTIME + "TEXT NOT NULL,"+
                DoctorEntry.COLUMN_DR_LOCATION + "TEXT NOT NULL,"+
                DoctorEntry.COLUMN_DR_RATING + "INTEGER"+
                ");";

        // Create table hold booking info data. A booking data consists of string and boolean value
        // like : booking id, doctor, date , time, email, phone, is checked or not, rebook days
        // Foreign key (booking_doctor) column with dr_id column on DOCTOR table
        final String SQL_CREATE_BOOKING_TABLE = "CREATE TABLE" + BookingEntry.TABLE_NAME + " (" +
                BookingEntry._ID +" INTEGER AUTOINCREMENT,"+
                BookingEntry.COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY," +
                BookingEntry.COLUMN_BOOKING_DOCTOR + " TEXT NOT NULL," +
                BookingEntry.COLUMN_BOOKING_DATE + " INTEGER NOT NULL," +
                BookingEntry.COLUMN_BOOKING_TIME + " TEXT NOT NULL," +
                BookingEntry.COLUMN_BOOKING_EMAIL + " TEXT NOT NULL," +
                BookingEntry.COLUMN_BOOKING_PHONE + "TEXT NOT NULL," +
                BookingEntry.COLUMN_REBOOK_DAYS + "INTEGER," +
                BookingEntry.COLUMN_IS_CHECKED + "INTEGER," +
                // Set up the booking_doctor column as a foreign key to DOCTOR table.
                " FOREIGN KEY (" + BookingEntry.COLUMN_BOOKING_DOCTOR + ") REFERENCES " +
                DoctorEntry.TABLE_NAME + " (" + DoctorEntry.COLUMN_DR_ID + "), " +
                ");";

        //Create table hold favoured doctor by current user. This contain foreign key with DOCTOR
        // table
        final String SQL_CREATE_FAV_DOCTOR_TABLE = "CREATE TABLE" + FavDoctorEntry.TABLE_NAME + " (" +
                FavDoctorEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                FavDoctorEntry.COLUMN_DR_ID + " TEXT NOT NULL," +
                // Set up the dr_id column as a foreign key to DOCTOR table.
                " FOREIGN KEY (" + FavDoctorEntry.COLUMN_DR_ID + ") REFERENCES " +
                DoctorEntry.TABLE_NAME + " (" + DoctorEntry.COLUMN_DR_ID + "), " +
                ");";

        //Create table hold user logging data ( sign in, sign out, make booking, location,etc...) ,
        // and contain 2 foreign keys: 1 with DOCTOR table(log_doctor) and 1 with USER table(log_user)
        final String SQL_CREATE_USER_LOG_TABLE = "CREATE TABLE" + UserLogEntry.TABLE_NAME + " (" +
                UserLogEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                UserLogEntry.COLUMN_LOG_USER + " TEXT NOT NULL," +
                UserLogEntry.COLUMN_LOG_DOCTOR + "TEXT," +
                UserLogEntry.COLUMN_LOG_ADDRESS + "TEXT NOT NULL,"+
                UserLogEntry.COLUMN_LOG_DATE + "INTEGER NOT NULL,"+
                UserLogEntry.COLUMN_LOG_LOCATION + "TEXT NOT NULL,"+
                // Set up the log_doctor column as a foreign key to DOCTOR table.
                " FOREIGN KEY (" + UserLogEntry.COLUMN_LOG_DOCTOR + ") REFERENCES " +
                DoctorEntry.TABLE_NAME + " (" + DoctorEntry.COLUMN_DR_ID + "), " +
                // Set up the log_user column as a foreign key to USER table.
                " FOREIGN KEY (" + UserLogEntry.COLUMN_LOG_USER + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + "), " +
                ");";

        //Execute SQL query to create tables
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_DOCTOR_TABLE);
        db.execSQL(SQL_CREATE_BOOKING_TABLE);
        db.execSQL(SQL_CREATE_FAV_DOCTOR_TABLE);
        db.execSQL(SQL_CREATE_USER_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
