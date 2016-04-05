package com.namlongsolutions.medicare.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Phong Nguyen on 08-Mar-16.
 * Defines tables and columns for app database
 */
public class DataContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.phongnguyen93.medicare";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // Path for look up user data
    public static final String PATH_USER = "user";
    // Path for look up doctor data
    public static final String PATH_DOCTOR = "doctor";
//    // Path for look up location data
//    public static final String PATH_LOCATION = "location";
    // Path for look up user favourite doctor
    public static final String PATH_FAV_DOCTOR = "fav_doctor";
    // Path for save user log data
    public static final String PATH_USER_LOG = "user_log";
    // Path for look up user booking data
    public static final String PATH_BOOKING = "booking";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julia
    // n day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the USER table */
    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        // Table name
        public static final String TABLE_NAME = "user";

        /* Columns name */
        // User id column
        public static final String COLUMN_USER_ID = "user_id";
        // User name column
        public static final String COLUMN_USER_NAME = "user_name";
        // User email column
        public static final String COLUMN_USER_EMAIL = "user_email";
        // User address column
        public static final String COLUMN_USER_ADDRESS = "user_address";
        // User phone column
        public static final String COLUMN_USER_PHONE = "user_phone";
        // User image path column
        public static final String COLUMN_USER_IMAGE = "user_image";
        // User birth day column
        public static final String COLUMN_USER_BDAY = "user_bday";
        // User session token
        public static final String COLUMN_USER_TOKEN = "user_token";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the DOCTOR table */
    public static final class DoctorEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DOCTOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DOCTOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DOCTOR;

        // Table name
        public static final String TABLE_NAME = "doctor";

        /* Columns name */
        // Doctor id column
        public static final String COLUMN_DR_ID = "dr_id";
        // Doctor name column
        public static final String COLUMN_DR_NAME = "dr_name";
        // Doctor email column
        public static final String COLUMN_DR_EMAIL = "dr_email";
        // Doctor address column
        public static final String COLUMN_DR_ADDRESS = "dr_address";
        // Doctor phone column
        public static final String COLUMN_DR_PHONE = "dr_phone";
        // Doctor image path column
        public static final String COLUMN_DR_IMAGE = "dr_image";
        // Doctor license column
        public static final String COLUMN_DR_LICENSE = "dr_license";
        // Doctor rating column
        public static final String COLUMN_DR_RATING = "dr_rating";
        // Doctor speciality column
        public static final String COLUMN_DR_SPECIALITY = "dr_speciality";
        // Doctor work days column
        public static final String COLUMN_DR_WORKDAYS = "dr_workdays";
        // Doctor work time column
        public static final String COLUMN_DR_WORKTIME = "dr_worktime";
        // Doctor location ( lat, lng) column
        public static final String COLUMN_DR_LOCATION = "dr_location";
        // Doctor is favoured by user
        public static final String COLUMN_IS_FAV = "is_fav";


        public static Uri buildDoctorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        //Build Uri to get doctor by id
        public static Uri buildDoctorWithId(String doctor_id){
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_DR_ID,doctor_id)
                    .build();
        }
        //Get doctor id from URI
        public static String getDoctorIdFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_DR_ID);
        }

    }

    /* Inner class that defines the table contents of the USER FAVOURITE DOCTOR table */
    public static final class FavDoctorEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_DOCTOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV_DOCTOR;

        // Table name
        public static final String TABLE_NAME = "fav_doctor";

        /* Columns name */
        // Foreign key (dr_id) column into DOCTOR table
        public static final String COLUMN_DR_ID = "dr_id";

        public static Uri buildFavDoctorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the BOOKING table */
    public static final class BookingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKING;

        // Table name
        public static final String TABLE_NAME = "booking";

        /* Columns name */
        // Booking id column
        public static final String COLUMN_BOOKING_ID = "booking_id";
        // Foreign key (booking_doctor) column into DOCTOR table
        public static final String COLUMN_BOOKING_DOCTOR = "booking_doctor";
        // Booking date column
        public static final String COLUMN_BOOKING_DATE = "booking_date";
        // Booking time column
        public static final String COLUMN_BOOKING_TIME = "booking_time";
        // Booking phone column
        public static final String COLUMN_BOOKING_PHONE = "booking_phone";
        // Booking email column
        public static final String COLUMN_BOOKING_EMAIL = "booking_email";
        // Status of the booking ( checked or not )
        public static final String COLUMN_IS_CHECKED = "is_checked";
        // Booking rebook days
        public static final String COLUMN_REBOOK_DAYS = "rebook_days";

        public static Uri buildBookingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        //Build Uri get booking by doctor id
        public static Uri buildBookingWithDoctorId(String doctor_id){
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_BOOKING_DOCTOR,doctor_id)
                    .build();
        }
        //Build Uri get booking by date
        public static Uri buildBookingWithDate(String date){
            long normalizeDate = normalizeDate(Long.parseLong(date));
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_BOOKING_DATE,Long.toString(normalizeDate))
                    .build();
        }
        //Build Uri get booking by date and doctor id
        public static Uri buildBookingWithDateAndDoctorId(String doctor_id, String date){
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_BOOKING_DOCTOR,doctor_id)
                    .appendQueryParameter(COLUMN_BOOKING_DATE,date)
                    .build();
        }
        //Get doctor id from booking Uri
        public static String getBookingDoctorIdFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_BOOKING_DOCTOR);
        }
        //Get booking date from Uri
        public static String getBookingDateFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_BOOKING_DATE);
        }
    }

    /* Inner class that defines the table contents of the USER LOG table */
    public static final class UserLogEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_LOG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_LOG;

        // Table name
        public static final String TABLE_NAME = "user_log";

        /* Columns name */
        // Log user column
        public static final String COLUMN_LOG_USER = "log_user";
        // Log doctor if has column
        public static final String COLUMN_LOG_DOCTOR = "log_doctor";
        // Log address column
        public static final String COLUMN_LOG_ADDRESS = "log_address";
        // Log location (lat,lng) column
        public static final String COLUMN_LOG_LOCATION = "log_location";
        // Log date column
        public static final String COLUMN_LOG_DATE = "log_date";
        // Log sync state with server
        public static final String COLUMN_LOG_SYNC = "log_sync";


        public static Uri buildUserLogUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
