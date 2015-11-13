package com.phongnguyen93.medicare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.uiUtils.ScrollSmoothLineaerLayoutManager;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.ProfileActivity;
import com.phongnguyen93.medicare.adapters.DoctorListAdapter;
import com.phongnguyen93.medicare.json.JSONArrayRequest;
import com.phongnguyen93.medicare.json.JSONParse;
import com.phongnguyen93.medicare.pojo.Doctor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment implements JSONArrayRequest.AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LatLng myLocation;
    private LinearLayoutManager mLayoutManager;
    // TODO: Rename and change types of parameters
    private UltimateRecyclerView rvContacts;
    private ArrayList<Doctor> doctors;
    private OnFragmentInteractionListener mListener;
    private int limit = 5;
    private DoctorListAdapter adapter=null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create adapter passing in the sample user data

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.list_layout, container, false);
        connection(limit);
        rvContacts = (UltimateRecyclerView) v.findViewById(R.id.rvContacts);
        mLayoutManager = new ScrollSmoothLineaerLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false, 300);
        // Attach the adapter to the recyclerview to populate items
        // Set layout manager to position the items
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setHasFixedSize(false);


        ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(rvContacts.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                        Doctor doctor = doctors.get(position);
                        Intent t = new Intent(getActivity(), ProfileActivity.class);
                        t.putExtra("doctor",doctor);
                        startActivity(t);
                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {

                    }
                });
        rvContacts.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);
        return v;
    }


    private void connection(int limit) {
        String request = "http://service-phongtest.rhcloud.com/rest_web_service/service/getalldoctor?limit=" + limit + "";
        JSONArrayRequest jsonArrayRequest = new JSONArrayRequest(this);
        jsonArrayRequest.execute(request);
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        myLocation = currentLocation();
        doctors = JSONParse.doctorList(jsonArray, getContext(), myLocation);
        adapter = new DoctorListAdapter(doctors);
        rvContacts.setAdapter(adapter);
        rvContacts.enableLoadmore();
        adapter.setCustomLoadMoreView(LayoutInflater.from(getActivity().getApplicationContext())
                .inflate(R.layout.custom_bottom_progressbar, null));
        rvContacts.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        limit += 5;
                        connection(limit);
                        //mLayoutManager.scrollToPosition(maxLastVisiblePosition);
                    }
                }, 2500);
            }
        });
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
        public void onFragmentInteraction(Uri uri);
    }

    private LatLng currentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        return latLng;
    }

}
