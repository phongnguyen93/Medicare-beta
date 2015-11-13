package com.phongnguyen93.medicare.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Phong Nguyen on 10/21/2015.
 */
public class AddressDecode {


    public LatLng getLocation(Context context,String myAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.US);
        List<Address> listOfAddress;
        LatLng myPosition =null;
        try {
            listOfAddress = geocoder.getFromLocationName(myAddress, 50);
            if (listOfAddress != null && !listOfAddress.isEmpty()) {
                Address address = listOfAddress.get(0);
                Log.d("Address decode", address.toString());
                double longitude = address.getLongitude();
                double latitude = address.getLatitude();
                myPosition = new LatLng(latitude, longitude);
                Log.d("Address decode", myPosition.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myPosition;
    }
}
