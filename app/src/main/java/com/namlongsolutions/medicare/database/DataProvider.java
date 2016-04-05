package com.namlongsolutions.medicare.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Phong Nguyen on 10-Mar-16.
 *  Provide method for query, insert, update, delete data from local SQLite database
 */
public class DataProvider extends ContentProvider {
    private DataDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder queryBuilder;
    private static final UriMatcher sURIMatcher = buildUriMatcher();
    private Context context;
    //Default codes for each type of URI to add into UriMatcher
    static final int USER = 100;
    static final int DOCTOR = 200;
    static final int DOCTOR_WITH_ID = 201;
    static final int BOOKING = 300;
    static final int BOOKING_WITH_DOCTOR_ID = 301;
    static final int BOOKING_WITH_DATE = 302;
    static final int BOOKING_WITH_DATE_AND_DOCTOR_ID = 303;
    static final int FAV_DOCTOR = 400;
    static final int USER_LOG = 500;

    static {
        queryBuilder =  new SQLiteQueryBuilder();
    }

    /*
     * Create selection strings for query
     */
    private static final String sDoctorIdSelection =
            DataContract.DoctorEntry.TABLE_NAME +
                    "."+ DataContract.DoctorEntry.COLUMN_DR_ID + " = ?";

    private static final String sBookingWithDoctorSelection =
            DataContract.BookingEntry.TABLE_NAME +
                    "."+ DataContract.BookingEntry.COLUMN_BOOKING_DOCTOR + " = ?";

    private static final String sBookingWithDateSelection =
            DataContract.BookingEntry.TABLE_NAME +
                    "."+ DataContract.BookingEntry.COLUMN_BOOKING_DATE + " = ?";

    private static final String sBookingWithDateAndDoctorId =
            DataContract.BookingEntry.TABLE_NAME +
                    "."+ DataContract.BookingEntry.COLUMN_BOOKING_DATE + " = ? AND "+
                    DataContract.BookingEntry.COLUMN_BOOKING_DOCTOR + " = ?";

    /*
     *  Create cursor for CONTENT_ITEM_TYPE URIs query
     */
    //return a row in doctor table with dr_id match the selectionArgs
    private Cursor getDoctorById(Uri uri, String[] projection, String sortOrder){
        String doctor_id = DataContract.DoctorEntry.getDoctorIdFromUri(uri);
        String selection = sDoctorIdSelection;
        String[] selectionArgs = new String[]{doctor_id};
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                sortOrder);
    }

    //return all rows in booking table with booking_date match selectionArgs
    private Cursor getBookingByDate(Uri uri, String[] projection, String sortOrder){
        Long date = Long.parseLong(DataContract.BookingEntry.getBookingDateFromUri(uri));
        String selection = sBookingWithDateSelection;
        String[] selectionArgs = new String[]{Long.toString(date)};
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                sortOrder);
    }

    //return all rows in booking table with booking_doctor match selectionArgs
    private Cursor getBookingByDoctorId(Uri uri, String[] projection, String sortOrder){
        String dr_id = DataContract.BookingEntry.getBookingDoctorIdFromUri(uri);
        String selection = sBookingWithDoctorSelection;
        String[] selectionArgs = new String[]{dr_id};
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                sortOrder);
    }

    //return all rows in booking table with booking_doctor and booking_date match selectionArgs
    private Cursor getBookingByDateAndDoctorId(Uri uri, String[] projection, String sortOrder){
        String dr_id = DataContract.BookingEntry.getBookingDoctorIdFromUri(uri);
        Long date = Long.parseLong(DataContract.BookingEntry.getBookingDateFromUri(uri));
        String selection = sBookingWithDateAndDoctorId;
        String[] selectionArgs = new String[]{Long.toString(date),dr_id};
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        //Add URI codes to matcher for each types of URI
        //User URI
        matcher.addURI(authority,DataContract.PATH_USER,USER);
        //Doctor URIs
        matcher.addURI(authority,DataContract.PATH_DOCTOR,DOCTOR);
        matcher.addURI(authority,DataContract.PATH_DOCTOR + "/*",DOCTOR_WITH_ID);
        //Booking URIs
        matcher.addURI(authority,DataContract.PATH_BOOKING,BOOKING);
        matcher.addURI(authority,DataContract.PATH_BOOKING + "/*",BOOKING_WITH_DOCTOR_ID);
        matcher.addURI(authority,DataContract.PATH_BOOKING + "/#",BOOKING_WITH_DATE);
        matcher.addURI(authority,DataContract.PATH_BOOKING + "/#*",BOOKING_WITH_DATE_AND_DOCTOR_ID);
        //Fav doctor URI
        matcher.addURI(authority,DataContract.PATH_FAV_DOCTOR,FAV_DOCTOR);
        //User log URI
        matcher.addURI(authority,DataContract.PATH_USER_LOG,USER_LOG);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        //create instance for DataDbHelper
        mOpenHelper = new DataDbHelper(getContext());
        context = getContext();
        return true;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case USER:
                return DataContract.UserEntry.CONTENT_TYPE;
            case DOCTOR:
                return DataContract.DoctorEntry.CONTENT_TYPE;
            case DOCTOR_WITH_ID:
                return DataContract.DoctorEntry.CONTENT_ITEM_TYPE;
            case BOOKING:
                return DataContract.BookingEntry.CONTENT_TYPE;
            case BOOKING_WITH_DATE:
                return DataContract.BookingEntry.CONTENT_ITEM_TYPE;
            case BOOKING_WITH_DATE_AND_DOCTOR_ID:
                return DataContract.BookingEntry.CONTENT_ITEM_TYPE;
            case BOOKING_WITH_DOCTOR_ID:
                return DataContract.BookingEntry.CONTENT_ITEM_TYPE;
            case FAV_DOCTOR:
                return DataContract.FavDoctorEntry.CONTENT_TYPE;
            case USER_LOG:
                return DataContract.UserLogEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // switch matcher code with given a URI to determine what kind of request it is,
        // and return a cursor that query the database accordingly.
        Cursor retCursor;
        switch (sURIMatcher.match(uri)){
            // "/user"
            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "/doctor"
            case DOCTOR:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.DoctorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case DOCTOR_WITH_ID:{
                retCursor = getDoctorById(uri,projection,sortOrder);
                break;
            }

            // "/booking"
            case BOOKING:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.BookingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case BOOKING_WITH_DATE:{
                retCursor = getBookingByDate(uri,projection,sortOrder);
                break;
            }
            case BOOKING_WITH_DOCTOR_ID:{
                retCursor = getBookingByDoctorId(uri,projection,sortOrder);
                break;
            }
            case BOOKING_WITH_DATE_AND_DOCTOR_ID:{
                retCursor = getBookingByDateAndDoctorId(uri,projection,sortOrder);
                break;
            }

            // "/fav_doctor"
            case FAV_DOCTOR:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.FavDoctorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "/user_log"
            case USER_LOG: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.UserLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Notify the ContentResolver if the content has changed
        if(retCursor != null)
            retCursor.setNotificationUri(context.getContentResolver(),uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        Uri returnUri;

        //Match returned codes from UriMatcher to determine which table to operate
        switch (match){
            case USER:{
                long _id =db.insert(DataContract.UserEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = DataContract.UserEntry.buildUserUri(_id);
                else
                    throw new SQLException("Fail to insert rows to table "+DataContract.UserEntry.TABLE_NAME+" : "+ uri);
                break;
            }
            case DOCTOR:{
                long _id =db.insert(DataContract.DoctorEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = DataContract.DoctorEntry.buildDoctorUri(_id);
                else
                    throw new SQLException("Fail to insert rows to table "+DataContract.DoctorEntry.TABLE_NAME+" - "+uri);
                break;
            }
            case BOOKING:{
                long _id =db.insert(DataContract.BookingEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = DataContract.BookingEntry.buildBookingUri(_id);
                else
                    throw new SQLException("Fail to insert rows to table "+DataContract.BookingEntry.TABLE_NAME+" - "+uri);
                break;
            }
            case FAV_DOCTOR:{
                long _id =db.insert(DataContract.FavDoctorEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = DataContract.FavDoctorEntry.buildFavDoctorUri(_id);
                else
                    throw new SQLException("Fail to insert rows to table "+DataContract.FavDoctorEntry.TABLE_NAME+" - "+uri);
                break;
            }
            case USER_LOG:{
                long _id =db.insert(DataContract.UserLogEntry.TABLE_NAME,null,values);
                if(_id >0)
                    returnUri = DataContract.UserLogEntry.buildUserLogUri(_id);
                else
                    throw new SQLException("Fail to insert rows to table "+DataContract.UserLogEntry.TABLE_NAME+" - "+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);

        }
        //Notify the ContentResolver if contents have been inserted
        context.getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match){
            case USER:
                rowsDeleted = db.delete(DataContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DOCTOR:
                rowsDeleted = db.delete(DataContract.DoctorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKING:
                rowsDeleted = db.delete(DataContract.BookingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAV_DOCTOR:
                rowsDeleted = db.delete(DataContract.FavDoctorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_LOG:
                rowsDeleted = db.delete(DataContract.UserLogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        // Notify the ContentResolver if contents have been deleted
        if (rowsDeleted != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsUpdated;
        // this makes update all rows return the number of rows updated
        if ( null == selection ) selection = "1";
        switch (match){
            case USER:
                rowsUpdated = db.update(DataContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DOCTOR:
                rowsUpdated = db.update(DataContract.DoctorEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BOOKING:
                normalizeDate(values);
                rowsUpdated = db.update(DataContract.BookingEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAV_DOCTOR:
                rowsUpdated = db.update(DataContract.FavDoctorEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_LOG:
                normalizeDate(values);
                rowsUpdated = db.update(DataContract.UserLogEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        // Notify the ContentResolver if contents have been updated
        if (rowsUpdated != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,@NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case DOCTOR:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(DataContract.DoctorEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case BOOKING:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(DataContract.BookingEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case USER_LOG:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(DataContract.UserLogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        long dateValue;
        if (values.containsKey(DataContract.BookingEntry.COLUMN_BOOKING_DATE )){
            dateValue = values.getAsLong(DataContract.BookingEntry.COLUMN_BOOKING_DATE);
            values.put(DataContract.BookingEntry.COLUMN_BOOKING_DATE
                    , DataContract.normalizeDate(dateValue));
        }else if(values.containsKey(DataContract.UserLogEntry.COLUMN_LOG_DATE)){
            dateValue = values.getAsLong(DataContract.UserLogEntry.COLUMN_LOG_DATE);
            values.put(DataContract.UserLogEntry.COLUMN_LOG_DATE,DataContract.normalizeDate(dateValue));
        }
    }
}
