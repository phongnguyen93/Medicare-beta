package com.phongnguyen93.medicare.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.extras.CurrentUser;
import com.phongnguyen93.medicare.json.JSONArrayRequest;
import com.phongnguyen93.medicare.model.Booking;
import com.phongnguyen93.medicare.model.User;
import com.phongnguyen93.medicare.ui_view.calendarview.DateTimeInterpreter;
import com.phongnguyen93.medicare.ui_view.calendarview.MonthLoader;
import com.phongnguyen93.medicare.ui_view.calendarview.WeekView;
import com.phongnguyen93.medicare.ui_view.calendarview.WeekViewEvent;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThirdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment implements
        WeekView.EventClickListener, WeekView.EventLongPressListener,View.OnClickListener,AdapterView.OnItemSelectedListener,JSONArrayRequest.AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private User user;
    private TextView today_action;
    private String mParam1;
    private String mParam2;
    private Context context;
    private WeekView mWeekView;
    private CurrentUser currentUser;

    private ArrayList<Booking> mBookings;
    private OnFragmentInteractionListener mListener;
    private String REQUEST_URL = "http://medicare-phongtest.rhcloud.com/rest_web_service/service/getallbookingbyuser?user=";

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Set long press listener for events.
       // mWeekView.setEventLongPressListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_third, container, false);
        // Inflate the layout for this fragment
        viewHolder(v);

        mWeekView = (WeekView)v.findViewById(R.id.weekView);

        currentUser = new CurrentUser(context);
        user = currentUser.getCurrentUser();
        mWeekView.setOnEventClickListener(this);
        requestAllBooking(user.getId());
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                return  events;
            }
        });
        setupDateTimeInterpreter(false);

        return v;
    }


    private void requestAllBooking(String user_id){
        String request = REQUEST_URL+user_id;
        JSONArrayRequest jsonArrayRequest = new JSONArrayRequest(this);
        jsonArrayRequest.execute(request);
    }

    private void viewHolder(View v){
        Spinner spinner = (Spinner)v.findViewById(R.id.spinner);
        today_action=(TextView)v.findViewById(R.id.action_today);
        today_action.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Theo tuần");
        categories.add("Theo ngày");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void setupCalendar() {
        // Get a reference for the week view in the layout.

    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.action_today:
                mWeekView.goToToday();
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setupDateTimeInterpreter(position == 0);
        switch (position)
        {
            case 0:
                mWeekViewType = TYPE_WEEK_VIEW;
                mWeekView.setNumberOfVisibleDays(7);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                break;
            case 1:
                mWeekViewType = TYPE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(1);
                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void processFinish(JSONArray jsonArray) {
//        if(jsonArray!= null){
//            setmBookings(JSONParse.bookingList(jsonArray,context));
//            mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
//                @Override
//                public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
//                    List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
//                    ArrayList<Booking> bookings = getmBookings();
//                    if (bookings != null) {
//                        for (int i = 0; i < bookings.size(); i++) {
//                            Booking booking = bookings.get(i);
//                            int id = booking.getId();
//                            String dr_name = booking.getDr_name();
//                            String date = booking.getDate();
//                            String time = booking.getTime();
//                            String phone = booking.getPhone();
//                            String email = booking.getEmail();
//                            boolean checked = booking.isChecked();
//                            int rebook_days = booking.getRebook_days();
//                            int start_hour = Integer.parseInt(time.substring(0, 2));
//                            int start_minute = Integer.parseInt(time.substring(3, 5));
//                            int end_hour = start_hour + 1;
//                            int end_minute = start_minute;
//                            Log.d("time", start_hour + start_minute + "");
//                            int year = Integer.parseInt(date.substring(0, 4));
//                            int month = Integer.parseInt(date.substring(5, 7));
//                            int day = Integer.parseInt(date.substring(8));
//                            String name;
//                            if(checked){
//                                name = "\nLịch hẹn " + start_hour + ":" + start_minute + "-" + day + "/" + month + "/" + year + "\n" + dr_name+"\n"+"Đã xác nhận";
//                            }else
//                                name = "\nLịch hẹn " + start_hour + ":" + start_minute + "-" + day + "/" + month + "/" + year + "\n" + dr_name+"\n"+"Chưa xác nhận";
//                            WeekViewEvent event = new WeekViewEvent((long) id, name, year, month, day, start_hour, start_minute, year, month, day, end_hour, end_minute);
//                            Log.d("event", name);
//                            if (checked) {
//                                event.setColor(context.getResources().getColor(R.color.accent));
//                            } else
//                                event.setColor(context.getResources().getColor(R.color.base_color));
//                            events.add(event);
//                        }
//
//                    }
//                    Log.d("events size", events.size() + "");
//                    return events;
//                }
//            });
//            mWeekView.setWeekViewLoader(new WeekViewLoader() {
//                @Override
//                public double toWeekViewPeriodIndex(Calendar instance) {
//                    return 0;
//                }
//
//                @Override
//                public List<? extends WeekViewEvent> onLoad(int periodIndex) {
//                    List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
//                    ArrayList<Booking> bookings = getmBookings();
//                    if (bookings != null) {
//                        for (int i = 0; i < bookings.size(); i++) {
//                            Booking booking = bookings.get(i);
//                            int id = booking.getId();
//                            String dr_name = booking.getDr_name();
//                            String date = booking.getDate();
//                            String time = booking.getTime();
//                            String phone = booking.getPhone();
//                            String email = booking.getEmail();
//                            boolean checked = booking.isChecked();
//                            int rebook_days = booking.getRebook_days();
//                            int start_hour = Integer.parseInt(time.substring(0, 2));
//                            int start_minute = Integer.parseInt(time.substring(3, 5));
//                            int end_hour = start_hour + 1;
//                            int end_minute = start_minute;
//                            Log.d("time", start_hour + start_minute + "");
//                            int year = Integer.parseInt(date.substring(0, 4));
//                            int month = Integer.parseInt(date.substring(5, 7));
//                            int day = Integer.parseInt(date.substring(8));
//                            String name;
//                            if (checked) {
//                                name = "\nLịch hẹn " + start_hour + ":" + start_minute + "-" + day + "/" + month + "/" + year + "\n" + dr_name + "\n" + "Đã xác nhận";
//                            } else
//                                name = "\nLịch hẹn " + start_hour + ":" + start_minute + "-" + day + "/" + month + "/" + year + "\n" + dr_name + "\n" + "Chưa xác nhận";
//                            WeekViewEvent event = new WeekViewEvent((long) id, name, year, month, day, start_hour, start_minute, year, month, day, end_hour, end_minute);
//                            Log.d("event", name);
//                            if (checked) {
//                                event.setColor(context.getResources().getColor(R.color.accent));
//                            } else
//                                event.setColor(context.getResources().getColor(R.color.base_color));
//                            events.add(event);
//                        }
//
//                    }
//                    Log.d("events size", events.size() + "");
//                    return events;
//                }
//            });
//        }
//
//        Log.d("booking size", getmBookings().size() + "");


    }

    private String getEventTitle(Calendar time) {
        return String.format("Lịch hẹn  %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    private ArrayList<Booking> getmBookings() {
        return mBookings;
    }

    private void setmBookings(ArrayList<Booking> mBookings) {
        this.mBookings = mBookings;
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
