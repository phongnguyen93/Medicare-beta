package com.phongnguyen93.medicare.database;

import android.provider.BaseColumns;

/**
 * Manages a local database for weather data.
 */

public class DbHelper{
    public DbHelper(){

    }
    public static abstract class TableInfo implements BaseColumns{
        public static final String SESSION_USER = "session_user";
        public static final String SESSION_TOKEN = "session_token";
        public static final String Database_Name ="medicare.db";
        public static final String Table_Name = "session";
    }
}
