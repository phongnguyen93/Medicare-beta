package com.phongnguyen93.medicare.functions;

import com.phongnguyen93.medicare.model.Doctor;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 15-Mar-16.
 * Fav doctor interface
 */
public interface FavDoctor {
    //setup data
    void setupData(String userId);
    // get favoured doctor list from local db if available else get from server
    ArrayList<Doctor> getFavDoctor();
    // remove doctor which is favoured by user to local db, then sync to server
    boolean removeFavDoctor(String doctorId,String userId);
    // add doctor which is favoured by user to local db, then sync to server
    boolean addFavDoctor(Doctor doctor,String userId);
    // check if doctor is favoured
    boolean isFav(String id);
    // remove all doctor from local db when user sign out
    boolean removeAllDoctorFromLocal();

}
