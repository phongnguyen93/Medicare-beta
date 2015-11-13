package com.phongnguyen93.medicare.extras;

import android.content.Context;

import com.phongnguyen93.medicare.R;

import java.util.ArrayList;

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
}
