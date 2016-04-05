package com.namlongsolutions.medicare.functions;

import com.namlongsolutions.medicare.model.Booking;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 21-Mar-16.
 *
 * Bookings interface for implement functions
 */
public interface Bookings  {
    //setup booking data
    void setupBookings(String userId);
    // get all bookings
    ArrayList<Booking> getAllBookings();
    // get all bookings in precise date
    ArrayList<Booking> getBookingsByDate(String date);
    // remove booking from db
    boolean removeBooking(int bookingId);
    // get a booking
    Booking getBooking(int bookingId);
    // remove all bookings from db
    boolean removeAllBookings();
    // add new booking to local db when make new booking
    boolean insertBookingToLocal(Booking booking);
    // update booking status
    boolean updateBookingStatus(int bookingId, boolean status);

    boolean updateBookingDate(int id, String date);
}
