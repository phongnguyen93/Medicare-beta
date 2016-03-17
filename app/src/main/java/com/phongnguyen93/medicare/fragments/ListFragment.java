package com.phongnguyen93.medicare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.google.android.gms.maps.model.LatLng;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.ProfileActivity;
import com.phongnguyen93.medicare.activities.SearchResultActivity;
import com.phongnguyen93.medicare.adapters.EndlessRecyclerViewScrollListener;
import com.phongnguyen93.medicare.adapters.RecyclerViewAdapter;
import com.phongnguyen93.medicare.json.JSONArrayRequest;
import com.phongnguyen93.medicare.json.JSONParse;
import com.phongnguyen93.medicare.maps.LocationService;
import com.phongnguyen93.medicare.model.Doctor;

import org.json.JSONArray;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class ListFragment extends Fragment implements JSONArrayRequest.AsyncResponse {

    private LatLng myLocation;
    private Context context;
    // TODO: Rename and change types of parameters
    private RecyclerView rvContacts;
    private ArrayList<Doctor> Doctors, tempData;

    private LocationService locationService;
    private OnFragmentInteractionListener mListener;
    private int limit = 50;
    private RecyclerViewAdapter adapter = null;
    private LinearLayoutManager linearLayoutManager;
    private ViewStub empty_view, loading_view;
    private android.support.design.widget.FloatingActionButton fab;

    private static final int LIST_FRAGMENT_ID = 1;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        locationService = new LocationService(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        Doctors = new ArrayList<>();
        tempData = new ArrayList<>();
        requestDoctorList(limit);
        context = getActivity().getApplicationContext();
        v = inflater.inflate(R.layout.list_fragment_layout, container, false);
        rvContacts = (RecyclerView) v.findViewById(R.id.rvContacts);
        empty_view = (ViewStub) v.findViewById(R.id.empty_view);
        loading_view = (ViewStub) v.findViewById(R.id.loading_view);
        fab = (android.support.design.widget.FloatingActionButton) v.findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(getActivity(), SearchResultActivity.class);
                startActivity(t);
            }
        });
        setupRecyclerView();
        rvContacts.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        empty_view.setVisibility(View.GONE);
        loading_view.setVisibility(View.VISIBLE);
        return v;
    }

    //Set up Recycler View
    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(context);
        rvContacts.setLayoutManager(linearLayoutManager);
        rvContacts.setHasFixedSize(false);
        rvContacts.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int curSize = adapter.getItemCount();
                int loadAmount = tempData.size() - curSize;
                if (loadAmount > 0) {
                    Doctors.add(tempData.get(curSize));
                }
                adapter.notifyItemInserted(curSize - 1);
                rvContacts.scrollToPosition(curSize - 3);
            }
        });
    }


    private void requestDoctorList(int limit) {
        String request = "http://medicare1-phongtest.rhcloud.com/rest_web_service/service/getalldoctor?limit=" + limit + "";
        JSONArrayRequest jsonArrayRequest = new JSONArrayRequest(this);
        jsonArrayRequest.execute(request);
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().setTitle(getResources().getString(R.string.tab_list_title));
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processFinish(JSONArray jsonArray) {
        myLocation = LocationService.getCurrentLocation();
        if (jsonArray != null) {
            rvContacts.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            loading_view.setVisibility(View.GONE);
            empty_view.setVisibility(View.GONE);
            tempData = JSONParse.doctorList(jsonArray, context, myLocation);
            Doctors = new ArrayList<>();
            for (int i = 0; i <= 7; i++) {
                Doctors.add(tempData.get(i));
            }
            adapter = new RecyclerViewAdapter(Doctors,LIST_FRAGMENT_ID);
            adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Doctor doctor = Doctors.get(position);
                    Intent t = new Intent(getActivity(), ProfileActivity.class);
                    t.putExtra("doctor", doctor);
                    startActivity(t);
                }
            });
            rvContacts.setAdapter(new ScaleInAnimationAdapter(adapter));
        }


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}
