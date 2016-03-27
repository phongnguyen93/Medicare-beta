package com.phongnguyen93.medicare.extras;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.phongnguyen93.medicare.extras.Utils;

import com.phongnguyen93.medicare.model.Booking;
import com.phongnguyen93.medicare.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 11/6/2015.
 *
 */
public class JSONParse {

    public static ArrayList<Doctor> doctorList(JSONArray jsonArray,Context context, LatLng myLocation) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String phone = jsonObject.getString("phone");
                String license = jsonObject.getString("license");
                String spec = jsonObject.getString("speciality");
                String address = jsonObject.getString("address");
                String workdays = Utils.convertWorkday(jsonObject.getString("workdays"), context);
                String worktime = jsonObject.getString("worktime");
                String location = jsonObject.getString("location");
                String image = jsonObject.getString("image");
                boolean active = jsonObject.getBoolean("isactive");
                if( myLocation != null) {
                    LatLng mPosition = Utils.convertLatLng(location);
                    double distance = SphericalUtil.computeDistanceBetween(mPosition, myLocation);
                    Log.d("distance", distance + "");
                    doctors.add(new Doctor(id, name, email, phone, license, spec, address, active, mPosition, distance, workdays, worktime, image));
                }
                else
                    doctors.add(new Doctor(id, name, email, phone, license, spec, address, active, null, 0, workdays, worktime,image));
            } catch (JSONException jsonEx) {
                Log.e("JSON Error", jsonEx.getMessage());
            }
        }
        if (doctors.size() == 0) {
            Log.e("Doctor List", "is empty");
            return null;
        }
        return doctors;
    }

    public static ArrayList<Booking> bookingList(JSONArray jsonArray,Context context) {
        ArrayList<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String dr_name = jsonObject.getString("doctor");
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");
                String phone = jsonObject.getString("phone");
                String email= jsonObject.getString("email");
                boolean checked = jsonObject.getBoolean("checked");
                String address = jsonObject.getString("address");
                int rebook_days = jsonObject.getInt("rebook_days");
                bookings.add(new Booking(id,dr_name,date,time,phone,email,checked,rebook_days,address));
            } catch (JSONException jsonEx) {
                Log.e("JSON Error", jsonEx.getMessage());
            }
        }
        if (bookings.size() == 0) {
            Log.e("Doctor List", "is empty");
            return null;
        }
        return bookings;
    }

}
