package com.phongnguyen93.medicare.json;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.maps.AddressDecode;
import com.phongnguyen93.medicare.model.Booking;
import com.phongnguyen93.medicare.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 11/6/2015.
 */
public class JSONParse {

    public static ArrayList<Doctor> doctorList(JSONArray jsonArray,Context context, LatLng myLocation) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Log.d("json array",jsonArray.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String phone = jsonObject.getString("phone");
                String license = jsonObject.getString("license");
                String spec = jsonObject.getString("speciality");
                String address = jsonObject.getString("address");
                Log.d("check address",address);
                String workdays = Utils.convertWorkday(jsonObject.getString("workdays"), context);
                String worktime = jsonObject.getString("worktime");
                String location = jsonObject.getString("location");
                boolean active = jsonObject.getBoolean("isactive");
                try {
                    LatLng mPosition = Utils.convertLatLng(location);
                    double distance = SphericalUtil.computeDistanceBetween(mPosition, myLocation);
                    Log.d("distance",distance+"");
                    doctors.add(new Doctor(id, name, email, phone, license, spec, address, active, mPosition, distance, workdays, worktime));
                } catch (RuntimeException e) {
                    throw e;
                }
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
                int rebook_days = jsonObject.getInt("rebook_days");
                bookings.add(new Booking(id,dr_name,date,time,phone,email,checked,rebook_days));
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
