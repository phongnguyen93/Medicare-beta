package com.phongnguyen93.medicare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.BookingDetailActivity;
import com.phongnguyen93.medicare.activities.SettingActivity;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.functions.BookingsFunctions;
import com.phongnguyen93.medicare.model.Booking;
import com.phongnguyen93.medicare.thread.worker_thread.ThreadHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class ManageBookingFragment extends Fragment implements CalendarPickerController,ThreadHandler.ThreadResult {

    private static final String NO_EVENT = "No events";
    private AgendaCalendarView mAgendaCalendarView;
    private OnFragmentInteractionListener mListener;


    public ManageBookingFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ThreadHandler threadHandler = new ThreadHandler(getContext(),this);
        threadHandler.execute(ThreadHandler.CONVERT_BOOKING_TO_EVENT_THREAD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manage_booking, container, false);
        mAgendaCalendarView = (AgendaCalendarView)v.findViewById(R.id.agenda_calendar_view);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_booking, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_booking_setting:
                Intent t = new Intent(getActivity(), SettingActivity.class);
                startActivity(t);
        }
        return  true;
    }

    // Set up limit date (min/max) and event list
    private void intiAgendaCalendar(ArrayList<CalendarEvent> eventList) {
        // Set max / min time
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);
        // initialized calendar with callback
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
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

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    // Handle action when event is selected
    @Override
    public void onEventSelected(CalendarEvent event) {
        if(!event.getTitle().equals(NO_EVENT)){
            String[] titleSplit = event.getTitle().split("#");
            int bookingId = Integer.parseInt(titleSplit[0].trim());
            Intent t = new Intent(getActivity(), BookingDetailActivity.class);
            t.putExtra("booking_id",bookingId+"");
            startActivity(t);
        }

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

    @Override
    public void delegateConvertResult(ArrayList<CalendarEvent> eventList) {
        // Setup Agenda calendar
        intiAgendaCalendar(eventList);

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

}
