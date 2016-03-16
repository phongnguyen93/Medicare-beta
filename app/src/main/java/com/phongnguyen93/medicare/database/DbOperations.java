package com.phongnguyen93.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbHelper.tableDoctor;
import com.phongnguyen93.medicare.database.DbHelper.tableSession;
import com.phongnguyen93.medicare.database.DbHelper.tableUser;

/**
 * Created by Phong Nguyen on 11/4/2015.
 */
public class DbOperations extends SQLiteOpenHelper {
    private static final int database_version = 1;
    private static final String QUERRY_CREATE_TABLE_SESSION = "CREATE TABLE "
            + tableSession.Table_Name + "("
            + tableSession.SESSION_USER + " TEXT,"
            + tableSession.SESSION_TOKEN + " TEXT);";

    private static final String QUERRY_CREATE_TABLE_USER = "CREATE TABLE "
            + tableUser.Table_Name + "("
            + tableUser.USER_ID + " TEXT,"
            + tableUser.USER_NAME + " TEXT,"
            + tableUser.USER_PHONE + " TEXT,"
            + tableUser.USER_EMAIL + " TEXT);";
    // Create a table to hold doctor info data.  A doctor  consists of the string supplied
    // with id, name, email, address, phone, image path, license, speciality, work time
    // , work days, rating and location coordination ( lat, lng) to add on map
    final String SQL_CREATE_DOCTOR_TABLE = "CREATE TABLE " + tableDoctor.Table_Name + " (" +
            tableDoctor.DR_ID + " TEXT UNIQUE PRIMARY KEY," +
            tableDoctor.DR_NAME + " TEXT," +
            tableDoctor.DR_ADDRESS + " TEXT NOT NULL," +
            tableDoctor.DR_EMAIL + " TEXT UNIQUE NOT NULL," +
            tableDoctor.DR_PHONE + " TEXT NOT NULL," +
            tableDoctor.DR_LICENSE + " TEXT NOT NULL," +
            tableDoctor.DR_IMAGE + " TEXT," +
            tableDoctor.DR_SPECIALITY + " TEXT NOT NULL," +
            tableDoctor.DR_WORKDAYS + " TEXT NOT NULL," +
            tableDoctor.DR_WORKTIME + " TEXT NOT NULL," +
            tableDoctor.DR_LOCATION + " TEXT NOT NULL," +
            tableDoctor.DR_RATING + " INTEGER," +
            tableDoctor.IS_FAV + " INTEGER " +
            ");";

    public DbOperations(Context context) {
        super(context, DbHelper.Database_Name, null, database_version);
    }

    public DbOperations(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version,
                        DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        Log.d("Database Operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(QUERRY_CREATE_TABLE_SESSION);
        sdb.execSQL(QUERRY_CREATE_TABLE_USER);
        sdb.execSQL(SQL_CREATE_DOCTOR_TABLE);
        Log.d("Database Operations", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {


    }

    public void putToken(DbOperations dop, String name, String token) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(tableSession.SESSION_USER, name);
        cv.put(tableSession.SESSION_TOKEN, token);
        long k = SQ.insert(tableSession.Table_Name, null, cv);
        if( k >0 )
            Log.d("Database Operations", "Token inserted");
    }

    public Cursor getToken(DbOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableSession.SESSION_USER, tableSession.SESSION_TOKEN};
        return SQ.query(tableSession.Table_Name, columns, null, null, null, null, null);
    }

    public void removeToken(DbOperations dop, String name, String token) {
        String select = tableSession.SESSION_USER + " LIKE ? AND " + tableSession.SESSION_TOKEN + " LIKE ?";
        String args[] = {name, token};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(tableSession.Table_Name, select, args);
        Log.d("Database Operations", "token removed");
    }

    public void putUser(DbOperations dop, String id, String name, String phone, String email) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(tableUser.USER_ID, id);
        cv.put(tableUser.USER_NAME, name);
        cv.put(tableUser.USER_PHONE, phone);
        cv.put(tableUser.USER_EMAIL, email);
        long k = SQ.insert(tableUser.Table_Name, null, cv);
        if(  k> 0)
            Log.d("Database Operations", "User inserted");
    }

    public Cursor getUser(DbOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableUser.USER_ID,
                tableUser.USER_NAME,
                tableUser.USER_PHONE,
                tableUser.USER_EMAIL};
        return SQ.query(tableUser.Table_Name, columns, null, null, null, null, null);
    }

    public void removeUser(DbOperations dop, String id) {
        String select = tableUser.USER_ID + " LIKE ? ";
        String args[] = {id};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(tableUser.Table_Name, select, args);
        Log.d("Database Operations", "User removed");
    }

    public boolean insertDoctor(DbOperations dop,
                             String id, String name, String address, String email, String phone,
                             String license, String image, String spec, String workdays,
                             String worktime, String location, int rating, int is_fav) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(tableDoctor.DR_ID, id);
        cv.put(tableDoctor.DR_NAME, name);
        cv.put(tableDoctor.DR_ADDRESS, address);
        cv.put(tableDoctor.DR_EMAIL, email);
        cv.put(tableDoctor.DR_PHONE, phone);
        cv.put(tableDoctor.DR_LICENSE, license);
        cv.put(tableDoctor.DR_IMAGE, image);
        cv.put(tableDoctor.DR_SPECIALITY, spec);
        cv.put(tableDoctor.DR_WORKDAYS, workdays);
        cv.put(tableDoctor.DR_WORKTIME, worktime);
        cv.put(tableDoctor.DR_LOCATION, location);
        cv.put(tableDoctor.DR_RATING, rating);
        cv.put(tableDoctor.IS_FAV, is_fav);
        long k = SQ.insert(tableDoctor.Table_Name, null, cv);
        if (k != 0){
            Log.d("Db operation: ", "doctor inserted !");
            return true;
        }else
            return false;
    }

    public boolean removeDoctor(DbOperations dop, String id) {
        String select = tableDoctor.DR_ID + " LIKE ? ";
        String args[] = {id};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        int deletedRowIndex = SQ.delete(tableDoctor.Table_Name, select, args);
        if (deletedRowIndex > 0){
            Log.d("Db operation: ", "doctor deleted !");
            return true;
        }else
            return false;

    }
    public boolean removeAllDoctor(DbOperations dop){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        int deletedRowIndex = SQ.delete(tableDoctor.Table_Name, null, null);
        if (deletedRowIndex > 0){
            Log.d("Db operation: ", "doctor deleted !");
            return true;
        }else
            return false;
    }

    public Cursor getDoctorList(DbOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableDoctor.DR_ID,
                tableDoctor.DR_NAME,
                tableDoctor.DR_ADDRESS,
                tableDoctor.DR_EMAIL,
                tableDoctor.DR_PHONE,
                tableDoctor.DR_LICENSE,
                tableDoctor.DR_IMAGE,
                tableDoctor.DR_SPECIALITY,
                tableDoctor.DR_WORKDAYS,
                tableDoctor.DR_WORKTIME,
                tableDoctor.DR_LOCATION,
                tableDoctor.DR_RATING,
                tableDoctor.IS_FAV};
        return SQ.query(tableDoctor.Table_Name, columns, null, null, null, null, null);
    }
    public boolean getDoctor(DbOperations dop, String id) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableDoctor.DR_ID,
                tableDoctor.DR_NAME,
                tableDoctor.DR_ADDRESS,
                tableDoctor.DR_EMAIL,
                tableDoctor.DR_PHONE,
                tableDoctor.DR_LICENSE,
                tableDoctor.DR_IMAGE,
                tableDoctor.DR_SPECIALITY,
                tableDoctor.DR_WORKDAYS,
                tableDoctor.DR_WORKTIME,
                tableDoctor.DR_LOCATION,
                tableDoctor.DR_RATING,
                tableDoctor.IS_FAV};
        String select = tableDoctor.DR_ID + " LIKE ? ";
        String arg[] = {id};
        Cursor cursor = SQ.query(tableDoctor.Table_Name, columns, select, arg, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}
