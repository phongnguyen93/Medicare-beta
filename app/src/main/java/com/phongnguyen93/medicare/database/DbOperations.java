package com.phongnguyen93.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbHelper.tableSession;
import com.phongnguyen93.medicare.database.DbHelper.tableUser;
/**
 * Created by Phong Nguyen on 11/4/2015.
 */
public class DbOperations extends SQLiteOpenHelper {
    private static final int database_version =1;
    private static final String QUERRY_CREATE_TABLE_SESSION ="CREATE TABLE "
            + tableSession.Table_Name+"("
            + tableSession.SESSION_USER+" TEXT,"
            + tableSession.SESSION_TOKEN  +" TEXT);";

    private static final String QUERRY_CREATE_TABLE_USER ="CREATE TABLE "
            + tableUser.Table_Name+"("
            + tableUser.USER_ID+" TEXT,"
            + tableUser.USER_NAME+" TEXT,"
            + tableUser.USER_PHONE+" TEXT,"
            + tableUser.USER_EMAIL  +" TEXT);";
    public DbOperations(Context context) {
        super(context, DbHelper.Database_Name, null,database_version );
        // TODO Auto-generated constructor stub
    }

    public DbOperations(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version,
                             DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        Log.d("Database Operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        // TODO Auto-generated method stub
        sdb.execSQL(QUERRY_CREATE_TABLE_SESSION);
        sdb.execSQL(QUERRY_CREATE_TABLE_USER);
        Log.d("Database Operations", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
    public void putToken(DbOperations dop,String name, String token){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(tableSession.SESSION_USER,name);
        cv.put(tableSession.SESSION_TOKEN, token);
        long k= SQ.insert(tableSession.Table_Name,null,cv);
        Log.d("Database Operations", "Token inserted");
    }
    public Cursor getToken(DbOperations dop){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableSession.SESSION_USER,tableSession.SESSION_TOKEN};
        Cursor Cr = SQ.query(tableSession.Table_Name, columns, null, null, null, null, null);
        return Cr;
    }
    public void removeToken(DbOperations dop,String name, String token){
        String select = tableSession.SESSION_USER +" LIKE ? AND "+ tableSession.SESSION_TOKEN +" LIKE ?";
        String args[] = {name,token};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(tableSession.Table_Name,select,args);
        Log.d("Database Operations", "token removed");
    }
    public void putUser(DbOperations dop,String id, String name, String phone, String email){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(tableUser.USER_ID,id);
        cv.put(tableUser.USER_NAME,name);
        cv.put(tableUser.USER_PHONE,phone);
        cv.put(tableUser.USER_EMAIL,email);
        long k= SQ.insert(tableUser.Table_Name,null,cv);
        Log.d("Database Operations", "User inserted");
    }
    public Cursor getUser(DbOperations dop){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {tableUser.USER_ID,
                tableUser.USER_NAME,
                tableUser.USER_PHONE,
                tableUser.USER_EMAIL};
        Cursor Cr = SQ.query(tableUser.Table_Name, columns, null, null, null, null, null);
        return Cr;
    }
    public void removeUser(DbOperations dop,String id){
        String select = tableUser.USER_ID +" LIKE ? ";
        String args[] = {id};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(tableUser.Table_Name,select,args);
        Log.d("Database Operations", "User removed");
    }
}
