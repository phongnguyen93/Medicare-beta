package com.namlongsolutions.medicare.extras;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.namlongsolutions.medicare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Phong Nguyen on 11/7/2015.
 * Utilities that use in functions/ method
 */
public class Utils {

    public static final String DEFAULT_DATETIME_FORMAT_STRING = "yyyy-MM-dd - HH:mm:ss";

    // Format distance number
    public static String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("~%4.1f%s", distance, unit);
    }
    // Convert JSON number to readable string
    public static String convertWorkday(String workdays, Context context){
        ArrayList<String> mWorkdays = new ArrayList<>();
        String mDays="";
        String[] split = workdays.split(",");
        for ( String days : split){
            mWorkdays.add(days);
        }
        for(int i = 0;i<mWorkdays.size();i++){
            String day = "";
            switch (mWorkdays.get(i)){
                case "1":
                    day = context.getResources().getString(R.string.monday);
                    break;
                case "2":
                    day = context.getResources().getString(R.string.tuesday);
                    break;
                case "3":
                    day = context.getResources().getString(R.string.wednesday);
                    break;
                case "4":
                    day = context.getResources().getString(R.string.thursday);
                    break;
                case "5":
                    day = context.getResources().getString(R.string.friday);
                    break;
                case "6":
                    day = context.getResources().getString(R.string.saturday);
                    break;
                case "7":
                    day = context.getResources().getString(R.string.sunday);
                    break;
            }
            if(i>0) {
                mDays += ", " + day;
            }else
                mDays += day;
        }
        return mDays;
    }

    // Convert JSON number to readable string
    public static String convertWorktime(String worktime,Context context){
        String mWorktime="";
        String[] time = worktime.split(",");
        ArrayList<String> mTime = new ArrayList<>();
        for (String t : time){
            mTime.add(t);
        }
        mWorktime = mTime.get(0)+"h "+context.getResources().getString(R.string.to)+" "+mTime.get(1)+"h";
        return  mWorktime;
    }

    // Validate input from user
    public static boolean validateInput(String input, int type){
        if(input.length()==0)
            return false;
        for(int i=0;i<input.length();i++) {
            switch (type){
                case 0: //validate id input( letter or digit) , max =15 chars
                    final String namePattern = "((?=.*[a-zA-Z0-9]).{6,20})";
                    if(!input.matches(namePattern)) {return false;}
                    break;
                case 1: //validate password ( must have letter and digit), min = 6 chars
                    final String passPattern = "((?=.*\\d)(?=.*[a-z]).{6,20})";
                    if(!input.matches(passPattern)){return false;}
                    break;
                case 2: //validate email ( mail domain: abc@xyz.com)
                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if(!input.matches(emailPattern)){return false;}
                    break;
            }

        }
        return true;
    }

    // Convert string to LatLng object
    public static LatLng convertLatLng(String location) {
        String[] latlong =  location.split(",");
        double lat= Double.parseDouble(latlong[0]);
        double lng = Double.parseDouble(latlong[1]);
        return new LatLng(lat,lng);
    }

    // Convert string to Calendar date object
    public static Calendar convertToDateTime(String date, String time) {
        try{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_STRING, Locale.US);
            cal.setTime(sdf.parse(date+" - "+time));// all done
            Log.d("start time",cal.toString());
            return cal;
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    // Create event title string with date time with booking status
    public static String setEventTitle(Calendar time, boolean checked, Context context) {
        String baseText;
        if(checked)
            baseText = context.getResources().getString(R.string.checked_event_title);
        else
            baseText = context.getResources().getString(R.string.unchecked_event_title);
        return String.format(baseText+"\n%d/%d/%d\n%02d:%02d",
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH)+1,
                time.get(Calendar.YEAR),
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE)
                );
    }

    //Create event description with doctor name
    public static String setEventDescription(String dr_name, Context context) {
            return context.getResources().getString(R.string.minimize_doctor)+" : "+dr_name;
    }

    public static int setEventColor(boolean checked, Context context) {
        if(checked)
            return context.getResources().getColor(R.color.accent);
        else
            return context.getResources().getColor(R.color.divider);
    }
}
