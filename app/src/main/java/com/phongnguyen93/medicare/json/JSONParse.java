package com.phongnguyen93.medicare.json;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.maps.AddressDecode;
import com.phongnguyen93.medicare.pojo.Doctor;

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

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String phone = jsonObject.getString("phone");
                String license = jsonObject.getString("license");
                String spec = jsonObject.getString("speciality");
                String address = jsonObject.getString("address");
                String workdays = Utils.convertWorkday(jsonObject.getString("workdays"), context);
                String worktime = Utils.convertWorktime(jsonObject.getString("worktime"), context);
                boolean active = jsonObject.getBoolean("isactive");
                try {
                    LatLng mPosition = new AddressDecode().getLocation(context, address);
                    double distance = SphericalUtil.computeDistanceBetween(mPosition, myLocation);
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

}