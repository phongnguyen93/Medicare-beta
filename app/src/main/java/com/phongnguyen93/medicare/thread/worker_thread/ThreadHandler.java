package com.phongnguyen93.medicare.thread.worker_thread;

import android.content.Context;
import android.os.AsyncTask;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.functions.BookingsFunctions;
import com.phongnguyen93.medicare.model.Booking;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Phong Nguyen on 24-Mar-16.
 */
public class ThreadHandler extends AsyncTask<String,Void,ArrayList<CalendarEvent>> {

    private Context context;
    private ThreadResult threadResult;
    public static final String CONVERT_BOOKING_TO_EVENT_THREAD = "convert booking";

    private String currentThreadAction;

    public ThreadHandler(Context context, ThreadResult threadResult){
        this.context = context;
        this.threadResult =threadResult;
    }

    public interface ThreadResult{
        void delegateConvertResult(ArrayList<CalendarEvent> eventList);
    }

    @Override
    protected ArrayList<CalendarEvent> doInBackground(String... params) {
        if(params[0].equals(CONVERT_BOOKING_TO_EVENT_THREAD)){
            currentThreadAction = CONVERT_BOOKING_TO_EVENT_THREAD;
            BookingsFunctions bookingsFunctions = new BookingsFunctions(context);
            ArrayList<Booking> bookings = bookingsFunctions.getAllBookings();
            ArrayList<CalendarEvent> mEvents =  new ArrayList<>();


            for(int i =0; i < bookings.size();i++){
                Booking booking = bookings.get(i);
                // get DateTime object converted from date & time string
                Calendar startTime = Utils.convertToDateTime(booking.getDate(), booking.getTime());
                // get end time
                mEvents.add(new BaseCalendarEvent(
                        //set event title from start time & booking status
                        booking.getId()+" # "+Utils.setEventTitle(startTime, booking.isChecked(), context),
                        //set event description with doctor name
                        Utils.setEventTitle(startTime, booking.isChecked(), context),
                        //set location with doctor address
                        booking.getAddress(),
                        // determine booking status to set event color
                        Utils.setEventColor(booking.isChecked(),context),
                        startTime, null,
                        false));
            }
            return mEvents;
        }
        return null;
    }

    // Get end time from start time with hour +1
    private Calendar getEndTime(Calendar startTime) {
        if(startTime!= null){
            startTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE)+20);
            return startTime;
        }else
            return null;
    }

    @Override
    protected void onPostExecute(ArrayList<CalendarEvent> objects) {
        super.onPostExecute(objects);
        if(objects != null){
            switch (currentThreadAction){
                case CONVERT_BOOKING_TO_EVENT_THREAD:
                    threadResult.delegateConvertResult(objects);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown thread action"+currentThreadAction);
            }
        }
    }
}
