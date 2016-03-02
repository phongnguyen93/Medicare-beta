package com.phongnguyen93.medicare.networking;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.phongnguyen93.medicare.networking.service.DataService;

/**
 * Created by Phong Nguyen on 02-Mar-16.
 * This class provide method to make call with API web service which response with JSON type data
 */

public class RestClient {
    private static final String BASE_URL = "http://medicare1-phongtest.rhcloud.com/rest_web_service";
    private DataService apiService;

    public RestClient(){

        //set up Retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataService dataService = retrofit.create(DataService.class);
    }

    public DataService getApiService(){
        return apiService;
    }
}
