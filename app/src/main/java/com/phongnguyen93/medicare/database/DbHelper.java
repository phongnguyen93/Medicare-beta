package com.phongnguyen93.medicare.database;

import android.provider.BaseColumns;

/**
 * Manages a local database for weather data.
 */

public class DbHelper{
    public static final String Database_Name ="medicare.db";
    public DbHelper(){

    }
    public static abstract class tableSession implements BaseColumns{
        public static final String SESSION_USER = "session_user";
        public static final String SESSION_TOKEN = "session_token";
        public static final String Table_Name = "session";
    }
    public static abstract class tableUser implements BaseColumns{
        public static final String USER_ID = "user_id";
        public static final String USER_NAME = "user_name";
        public static final String USER_PHONE = "user_phone";
        public static final String USER_EMAIL = "user_email";
        public static final String Table_Name = "user";
    }
}
