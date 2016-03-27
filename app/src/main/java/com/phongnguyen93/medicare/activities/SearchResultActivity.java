package com.phongnguyen93.medicare.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.adapters.RecyclerViewAdapter;
import com.phongnguyen93.medicare.thread.network_thread.JSONArrayRequest;
import com.phongnguyen93.medicare.extras.JSONParse;
import com.phongnguyen93.medicare.maps.LocationService;
import com.phongnguyen93.medicare.model.Doctor;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 05-Mar-16.
 *
 */
public class SearchResultActivity extends Activity implements JSONArrayRequest.AsyncResponse{
    private RecyclerView rvContacts;
    private ArrayList<Doctor> Doctors;


    private static final int SEARCH_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_layout);


        if (getIntent() != null) {
            handleIntent(getIntent());
        }
        setupView();
        onSearchRequested();
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {
//                String query = getIntent().getStringExtra(SearchManager.QUERY);
//                if(query.equals(""))
//                    finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void setupView() {
        rvContacts = (RecyclerView) findViewById(R.id.rvSearch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(linearLayoutManager);
        rvContacts.setHasFixedSize(false);

    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // SearchManager.QUERY is the key that a SearchManager will use to send a query string
            // to an Activity.
            String query = intent.getStringExtra(SearchManager.QUERY);
            try{
                requestDoctorList(URLEncoder.encode(query,"UTF-8"));
            }catch (UnsupportedEncodingException ex){
                ex.printStackTrace();
            }


        }
    }

    private void requestDoctorList(String name) {
        String request = "http://medicare1-phongtest.rhcloud.com/rest_web_service/service/getdoctorbyname?name="+name+"";
        Log.d("request API",request);
        JSONArrayRequest jsonArrayRequest = new JSONArrayRequest(this);
        jsonArrayRequest.execute(request);
    }

    @Override
    public void processFinish(JSONArray jsonArray) {
        try {
            if (jsonArray.length() > 0) {
                ArrayList<Doctor> tempData = JSONParse.doctorList(jsonArray, this, LocationService.getCurrentLocation());
                Doctors = new ArrayList<>();
                if (tempData != null) {
                    for (int i = 0; i < tempData.size(); i++) {
                        Doctors.add(tempData.get(i));
                    }
                }
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(Doctors, SEARCH_ID);
                adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Doctor doctor = Doctors.get(position);
                        Intent t = new Intent(SearchResultActivity.this, ProfileActivity.class);
                        t.putExtra("doctor", doctor);
                        startActivity(t);
                    }
                });
                rvContacts.setAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
