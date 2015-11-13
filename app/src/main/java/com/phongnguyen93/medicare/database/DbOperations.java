package com.phongnguyen93.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.phongnguyen93.medicare.database.DbHelper.TableInfo;
/**
 * Created by Phong Nguyen on 11/4/2015.
 */
public class DbOperations extends SQLiteOpenHelper {
    public static final int database_version =1;
    public String Create_Query ="CREATE TABLE "+ TableInfo.Table_Name+"("+ TableInfo.SESSION_USER+" TEXT,"+ TableInfo.SESSION_TOKEN  +" TEXT);";

    public DbOperations(Context context) {
        super(context, TableInfo.Database_Name, null,database_version );
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
        sdb.execSQL(Create_Query);
        Log.d("Database Operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
    public void putToken(DbOperations dop,String name, String token){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.SESSION_USER,name);
        cv.put(TableInfo.SESSION_TOKEN,token);
        long k= SQ.insert(TableInfo.Table_Name,null,cv);
        Log.d("Database Operations", "Token inserted");
    }
    public Cursor getToken(DbOperations dop){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {TableInfo.SESSION_USER,TableInfo.SESSION_TOKEN};
        Cursor Cr = SQ.query(TableInfo.Table_Name, columns, null, null, null, null, null);
        return Cr;
    }
    public void removeToken(DbOperations dop,String name, String token){
        String select = TableInfo.SESSION_USER +" LIKE ? AND "+ TableInfo.SESSION_TOKEN +" LIKE ?";
        String args[] = {name,token};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(TableInfo.Table_Name,select,args);
        Log.d("Database Operations", "token removed");
    }
}
