package com.phongnguyen93.medicare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Marker;
import com.phongnguyen93.medicare.R;

public class MapFragments extends Fragment {

    MapView mMapView;
    public GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.activity_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

       setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        infowindowAdapter();
        // latitude and longitude
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapView.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                start();

            }
        }
    }

    protected void start() {

    }
    protected  void infowindowAdapter(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                ImageView imgage = (ImageView) v.findViewById(R.id.info_image);
                TextView name = (TextView) v.findViewById(R.id.txt_name);
                TextView address = (TextView) v.findViewById(R.id.txt_address);
                if(marker.getTitle()!=null && marker.getSnippet()!=null) {
                    name.setText(marker.getTitle());
                    address.setText(marker.getSnippet());
                    imgage.setImageResource(R.drawable.applogo);
                }
                return v;
            }
        });
    }
}