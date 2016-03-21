package com.phongnguyen93.medicare.functions;

import android.content.Context;
import android.database.Cursor;

import com.phongnguyen93.medicare.database.DbOperations;
import com.phongnguyen93.medicare.json.JSONArrayRequest;
import com.phongnguyen93.medicare.json.JSONObjectRequest;
import com.phongnguyen93.medicare.json.JSONParse;
import com.phongnguyen93.medicare.model.Doctor;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 15-Mar-16.
 * Implement FavDoctor interface
 */
public class FunctionFavDoctor implements FavDoctor, JSONArrayRequest.AsyncResponse,JSONObjectRequest.AsyncResponse {
    private DbOperations dop;

    private Context context;
    public static final int ACTION_FAV = 1;
    public static final int ACTION_UNFAV = 0;

    private JSONArrayRequest jsonArrayRequest;
    public static final String BASE_URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service/userfav";


    public FunctionFavDoctor(Context context){
        dop = new DbOperations(context);
        this.context = context;
        jsonArrayRequest = new JSONArrayRequest(this);
    }


    private boolean checkLocalDb() {
        Cursor cursor=  dop.getDoctorList(dop);
        return cursor.getCount() != 0;
    }


    @Override
    public void setupData(String userId) {
        if(!checkLocalDb())
            requestFavDoctorList(userId);
    }

    @Override
    public ArrayList<Doctor> getFavDoctor() {
        Cursor cursor=  dop.getDoctorList(dop);
        if(checkLocalDb())
            return getFavDoctorFromLocal(cursor);
        else
            return null;
    }

    private void requestFavDoctorList(String userId) {
        String query = BASE_URL+"/get?user="+userId;
        jsonArrayRequest.execute(query);
    }

    //get doctor list from local db if available
    private ArrayList<Doctor> getFavDoctorFromLocal(Cursor cursor) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        cursor.moveToFirst();
        do {
            String id = cursor.getString(0);
            String name =cursor.getString(1);
            String email = cursor.getString(3);
            String address = cursor.getString(2);
            String phone = cursor.getString(4);
            String spec = cursor.getString(7);
            String workdays = cursor.getString(8);
            String worktime = cursor.getString(9);
            String license = cursor.getString(5);
            String image = cursor.getString(6);
            doctors.add(new Doctor(id,name,email,phone,license,spec,address,true,null,0,workdays,worktime,image));
        }while (cursor.moveToNext());
        return doctors;
    }

    private void syncToServer(int actionType, String doctorId, String userId){
        if(actionType == ACTION_FAV){
            String query = BASE_URL+"/add?user="+userId+"&doctor="+doctorId;
            JSONObjectRequest syncFav = new JSONObjectRequest(this);
            syncFav.execute(query);
        }
        if(actionType == ACTION_UNFAV){
            String query = BASE_URL+"/remove?user="+userId+"&doctor="+doctorId;
            JSONObjectRequest syncUnFav = new JSONObjectRequest(this);
            syncUnFav.execute(query);
        }
    }

    @Override
    public boolean removeFavDoctor(String id,String userId) {
        syncToServer(ACTION_UNFAV,id,userId);
        return dop.removeDoctor(dop,id);
    }

    @Override
    public boolean addFavDoctor(Doctor doctor, String userId) {
        String id = doctor.getId();
        String name =doctor.getName();
        String email = doctor.getEmail();
        String address = doctor.getAddress();
        String phone = doctor.getPhone();
        String image = doctor.getImage();
        int rating  = 0;
        String spec = doctor.getSpec();
        String workdays = doctor.getWorkdays();
        String worktime = doctor.getWorktime();
        String location = "";
        String license = doctor.getLicense();
        int  isFav = 1;
        syncToServer(ACTION_FAV,id,userId);
        return  dop.insertDoctor(dop,
                id,name,address,email,phone,license,image,spec,
                workdays,worktime,location,rating,isFav);
    }

    @Override
    public boolean isFav(String id) {
        return dop.getDoctor(dop,id);
    }

    @Override
    public boolean removeAllDoctorFromLocal() {
         return dop.removeAllDoctor(dop);
    }

    //Handle response when request JSON Array
    @Override
    public void processFinish(JSONArray jsonArray) {
        ArrayList<Doctor> doctors = JSONParse.doctorList(jsonArray,context,null);
        if(doctors != null){
            for (Doctor doctor: doctors) {
                String id = doctor.getId();
                String name =doctor.getName();
                String email = doctor.getEmail();
                String address = doctor.getAddress();
                String phone = doctor.getPhone();
                int rating  = 0;
                String spec = doctor.getSpec();
                String workdays = doctor.getWorkdays();
                String worktime = doctor.getWorktime();
                String location = "";
                String license = doctor.getLicense();
                String image = doctor.getImage();
                int  isFav = 1;
                dop.insertDoctor(dop,
                        id,name,address,email,phone,license,image,spec,
                        workdays,worktime,location,rating,isFav);
            }
        }

    }

    //Handle response when request JSON Object
    @Override
    public void processFinish(JSONObject jsonObject) {

    }
}
