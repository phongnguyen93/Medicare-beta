package com.namlongsolutions.medicare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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


import com.namlongsolutions.medicare.R;
import com.namlongsolutions.medicare.activities.BookingActivity;
import com.namlongsolutions.medicare.activities.ProfileActivity;
import com.namlongsolutions.medicare.adapters.RecyclerViewAdapter;
import com.namlongsolutions.medicare.functions.FavDoctorFunctions;
import com.namlongsolutions.medicare.functions.UserFunctions;
import com.namlongsolutions.medicare.model.Doctor;
import com.namlongsolutions.medicare.model.User;
import com.namlongsolutions.medicare.notification.MyNotification;

import java.util.ArrayList;


public class FavFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener,MyNotification.SnackBarAction {
    private static final int ACTION_SNACKBAR = 4;

    private Context context;
    private ArrayList<Doctor> doctors;
    private RecyclerViewAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private FavDoctorFunctions favDoctorFunctions;
    private LinearLayout linearLayout;
    private MyNotification notification;

    private User user;
    private Doctor tempDoctor;
    private static int LIST_ITEM_POSITION;
    private static int PREVIOUS_ITEM_POSITION;
    private static final int FAV_FRAGMENT_ID = 3;

    //Default values for popup menu actions
    private static final int ACTION_VIEW_DETAIL = 0;
    private static final int ACTION_QUICK_BOOKING = 1;
    private static final int ACTION_UNFAV = 2;

    private static final String INTENT_EXTRA = "doctor";
    private ViewStub empty_view;
    private RecyclerView rvContacts;

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set fragment has option menu
        setHasOptionsMenu(true);
        UserFunctions userFunctions = new UserFunctions(getContext());
        user = userFunctions.getCurrentUser();
        notification = new MyNotification(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fav_fragment, container, false);
        linearLayout = (LinearLayout) v.findViewById(R.id.fav_layout);
        favDoctorFunctions = new FavDoctorFunctions(context);
        setupView(v);
        return v;
    }

    //Register and setup views for this fragment
    private void setupView(View v) {
        rvContacts = (RecyclerView) v.findViewById(R.id.recycler_view);
        empty_view = (ViewStub) v.findViewById(R.id.empty_view);
        rvContacts.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        //set up recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvContacts.setLayoutManager(linearLayoutManager);
        rvContacts.setHasFixedSize(false);
        //get doctor list from local db
        doctors = favDoctorFunctions.getFavDoctor();
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
            viewProfile();
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

        switch (actionType) {
            //view doctor's profile
            case ACTION_VIEW_DETAIL:
                viewProfile();
                break;
            //make quick booking with doctor
            case ACTION_QUICK_BOOKING:
                quickBooking();
                break;
            //unfav doctor
            case ACTION_UNFAV:
                unFav();
                break;
            default:
                throw new UnsupportedOperationException("Unknown action :" + actionType);
        }
    }

    //remove doctor from list, unfav doctor and display snackbar for undo action
    private void unFav(){
        PREVIOUS_ITEM_POSITION =LIST_ITEM_POSITION;
        setTempDoctor(doctors.get(LIST_ITEM_POSITION));
        doctors.remove(LIST_ITEM_POSITION);
        adapter.notifyDataSetChanged();
        checkDataSetForLayoutChange();
        favDoctorFunctions.removeFavDoctor(tempDoctor.getId(), user.getId());
        //display snackbar for undo action
        String actionText = context.getResources().getString(R.string.unfav_undo);
        String displayText = context.getResources().getString(R.string.unfav_noti);
        notification.displaySnackbar(ACTION_SNACKBAR,linearLayout,displayText,actionText);
    }

    //go to profile activity with selected doctor
    private void viewProfile() {
        Doctor doctor = doctors.get(LIST_ITEM_POSITION);
        Intent t = new Intent(context, ProfileActivity.class);
        t.putExtra(INTENT_EXTRA, doctor);
        startActivity(t);
    }

    //go to booking activity with selected doctor
    private void quickBooking() {
        Doctor doctor = doctors.get(LIST_ITEM_POSITION);
        Intent t = new Intent(context, BookingActivity.class);
        t.putExtra(INTENT_EXTRA, doctor);
        startActivity(t);
    }


    // Undo action on snack bar action
    @Override
    public void setSnackBarAction() {
        favDoctorFunctions.addFavDoctor(getTempDoctor(), user.getId());
        doctors.add(PREVIOUS_ITEM_POSITION, tempDoctor);
        adapter.notifyDataSetChanged();
        checkDataSetForLayoutChange();
    }

    // Check data set items count to determine layout
    private void checkDataSetForLayoutChange(){
        if(adapter.getItemCount()>0) {
            rvContacts.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
        if(adapter == null ||adapter.getItemCount()==0){
            rvContacts.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }

    }


    private Doctor getTempDoctor() {
        return tempDoctor;
    }

    private void setTempDoctor(Doctor tempDoctor) {
        this.tempDoctor = tempDoctor;
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
