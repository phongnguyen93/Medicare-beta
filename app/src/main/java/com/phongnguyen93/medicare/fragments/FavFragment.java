package com.phongnguyen93.medicare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.BookingActivity;
import com.phongnguyen93.medicare.activities.ProfileActivity;
import com.phongnguyen93.medicare.adapters.RecyclerViewAdapter;
import com.phongnguyen93.medicare.functions.FunctionFavDoctor;
import com.phongnguyen93.medicare.functions.FunctionUser;
import com.phongnguyen93.medicare.model.Doctor;
import com.phongnguyen93.medicare.model.User;

import java.util.ArrayList;


public class FavFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener {
    // TODO: Rename parameter arguments, choose names that match

    private RecyclerView rvContacts;
    private Context context;
    private ArrayList<Doctor> doctors;
    private RecyclerViewAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private FunctionFavDoctor functionFavDoctor;
    private LinearLayout linearLayout;

    private User user;
    private static int LIST_ITEM_POSITION;
    private static final int FAV_FRAGMENT_ID = 3;

    //Default values for popup menu actions
    private static final int ACTION_VIEW_DETAIL = 0;
    private static final int ACTION_QUICK_BOOKING = 1;
    private static final int ACTION_UNFAV = 2;

    private static final String INTENT_EXTRA = "doctor";

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set fragment has option menu
        setHasOptionsMenu(true);
        FunctionUser functionUser = new FunctionUser(getContext());
        user = functionUser.getCurrentUser();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fav_fragment, container, false);
        linearLayout = (LinearLayout) v.findViewById(R.id.fav_layout);
        functionFavDoctor = new FunctionFavDoctor(context);
        setupView(v);
        return v;
    }

    //Register and setup views for this fragment
    private void setupView(View v) {
        rvContacts = (RecyclerView) v.findViewById(R.id.recycler_view);
        ViewStub empty_view = (ViewStub) v.findViewById(R.id.empty_view);
        rvContacts.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        //set up recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvContacts.setLayoutManager(linearLayoutManager);
        rvContacts.setHasFixedSize(false);
        //get doctor list from local db
        doctors = functionFavDoctor.getFavDoctor();
        //if doctor list size > 0,  set adapter for recycler view
        // else show empty layout
        if (doctors != null && doctors.size() > 0) {
            rvContacts.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
            adapter = new RecyclerViewAdapter(doctors, FAV_FRAGMENT_ID);
            adapter.setOnItemClickListener(this);
            rvContacts.setAdapter(adapter);
        }
    }


    //Handle view's click event on recycle view
    @Override
    public void onItemClick(View itemView, int position) {
        LIST_ITEM_POSITION = position;
        if (itemView.getId() == R.id.img_btn) {
            showPopup(itemView);
        } else {
            viewProfile(doctors.get(position));
        }
    }
    //Show popup menu on icon click
    private void showPopup(View itemView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), itemView, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.menu_fav_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }


    //Handle item click on popup menu
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail:
                actionPopupMenu(ACTION_VIEW_DETAIL);
                break;
            case R.id.menu_quickbooking:
                actionPopupMenu(ACTION_QUICK_BOOKING);
                break;
            case R.id.menu_unfav:
                actionPopupMenu(ACTION_UNFAV);
                break;
        }
        return true;
    }


    //Handle action type on popup menu item click
    private void actionPopupMenu(int actionType) {
        Doctor doctor = doctors.get(LIST_ITEM_POSITION);
        switch (actionType) {
            //view doctor's profile
            case ACTION_VIEW_DETAIL:
                viewProfile(doctor);
                break;
            //make quick booking with doctor
            case ACTION_QUICK_BOOKING:
                quickBooking(doctor);
                break;
            //unfav doctor
            case ACTION_UNFAV:
                unFav(doctor);
                break;
            default:
                throw new UnsupportedOperationException("Unknown action :" + actionType);
        }
    }

    //remove doctor from list, unfav doctor and display snackbar for undo action
    private void unFav(Doctor doctor){
        doctors.remove(LIST_ITEM_POSITION);
        adapter.notifyDataSetChanged();
        String dr_id = doctor.getId();
        functionFavDoctor.removeFavDoctor(dr_id, user.getId());
        displayUndoSnackbar(doctor);
    }

    //go to profile activity with selected doctor
    private void viewProfile(Doctor doctor) {
        Intent t = new Intent(context, ProfileActivity.class);
        t.putExtra(INTENT_EXTRA, doctor);
        startActivity(t);
    }

    //go to booking activity with selected doctor
    private void quickBooking(Doctor doctor) {
        Intent t = new Intent(context, BookingActivity.class);
        t.putExtra(INTENT_EXTRA, doctor);
        startActivity(t);
    }

    //Show snackbar to display undo action
    private void displayUndoSnackbar(final Doctor doctor) {
        Snackbar snackbar = Snackbar.make(linearLayout, getResources().getString(R.string.unfav_noti), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.unfav_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        functionFavDoctor.addFavDoctor(doctor, user.getId());
                        doctors.add(LIST_ITEM_POSITION,doctor);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.primary));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(Color.BLACK);
        snackbar.show();
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
}
