package com.phongnguyen93.medicare.networking.service;

/**
 * Created by Phong Nguyen on 02-Mar-16.
 */
import com.phongnguyen93.medicare.model.Doctor;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataService {
     @GET("/service/{query}")
    public void getData(@Path("query") String query,@Query("limit") int limit,Callback<Doctor> callback);
}
