package com.phongnguyen93.medicare.extras;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.calendarview.WeekViewEvent;
import com.phongnguyen93.medicare.model.Booking;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Phong Nguyen on 11/7/2015.
 */
public class Utils {
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
    public static ArrayList<WeekViewEvent> arrayToEventList(ArrayList<Booking> bookings,Context context) {
        ArrayList<WeekViewEvent> events = new ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            int id = booking.getId();
            String dr_name = booking.getDr_name();
            String date = booking.getDate();
            String time = booking.getTime();
            String phone = booking.getPhone();
            String email = booking.getEmail();
            boolean checked = booking.isChecked();
            int rebook_days = booking.getRebook_days();

            int start_hour = Integer.parseInt(time.substring(0, 2));
            int start_minute = Integer.parseInt(time.substring(3, 5));
            int end_hour = start_hour;
            int end_minute = start_minute + 5;
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8));
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, start_hour);
            startTime.set(Calendar.MINUTE, start_minute);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.MONTH, month);
            startTime.set(Calendar.YEAR, year);
            Calendar endTime = (Calendar) startTime.clone();
            startTime.set(Calendar.HOUR_OF_DAY, end_hour);
            startTime.set(Calendar.MINUTE, end_minute);
            WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
            if (checked) {
                event.setColor(context.getResources().getColor(R.color.accent));
            } else
                event.setColor(context.getResources().getColor(R.color.base_color));
            events.add(event);
        }
        return  events;
    }
    private static String getEventTitle(Calendar time) {
        return String.format("Lịch hẹn  %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    public static LatLng convertLatLng(String location) {
        String[] latlong =  location.split(",");
        double lat= Double.parseDouble(latlong[0]);
        double lng = Double.parseDouble(latlong[1]);
        return new LatLng(lat,lng);
    }
}
