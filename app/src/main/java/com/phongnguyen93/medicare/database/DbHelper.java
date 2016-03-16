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
    public static abstract class tableDoctor implements BaseColumns{

        public static final String Table_Name = "doctor";
        /* Columns name */
        // Doctor id column
        public static final String DR_ID = "dr_id";
        // Doctor name column
        public static final String DR_NAME = "dr_name";
        // Doctor email column
        public static final String DR_EMAIL = "dr_email";
        // Doctor address column
        public static final String DR_ADDRESS = "dr_address";
        // Doctor phone column
        public static final String DR_PHONE = "dr_phone";
        // Doctor image path column
        public static final String DR_IMAGE = "dr_image";
        // Doctor license column
        public static final String DR_LICENSE = "dr_license";
        // Doctor rating column
        public static final String DR_RATING = "dr_rating";
        // Doctor speciality column
        public static final String DR_SPECIALITY = "dr_speciality";
        // Doctor work days column
        public static final String DR_WORKDAYS = "dr_workdays";
        // Doctor work time column
        public static final String DR_WORKTIME = "dr_worktime";
        // Doctor location ( lat, lng) column
        public static final String DR_LOCATION = "dr_location";
        // Doctor is favoured by user
        public static final String IS_FAV = "is_fav";
    }
}
