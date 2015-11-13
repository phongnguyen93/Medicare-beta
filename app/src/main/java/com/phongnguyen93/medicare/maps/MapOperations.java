package com.phongnguyen93.medicare.maps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.activities.ProfileActivity;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.fragments.MapFragments;
import com.phongnguyen93.medicare.json.JSONParse;
import com.phongnguyen93.medicare.json.JSONArrayRequest;
import com.phongnguyen93.medicare.pojo.Doctor;

import org.json.JSONArray;

import java.util.ArrayList;


/**
 * Created by Phong Nguyen on 10/21/2015.
 */
public class MapOperations extends MapFragments implements ClusterManager.OnClusterClickListener<Doctor>, ClusterManager.OnClusterInfoWindowClickListener<Doctor>, ClusterManager.OnClusterItemClickListener<Doctor>, ClusterManager.OnClusterItemInfoWindowClickListener<Doctor>,JSONArrayRequest.AsyncResponse {
    AlertDialog progressDialog;
    private ClusterManager<Doctor> mClusterManager;
    private LatLng myLocation;

    protected void start() {
        myLocation = currentLocation();
        mClusterManager = new ClusterManager<Doctor>(getContext(), getMap());
        connection();
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setRenderer(new DoctorRenderer());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);


    }

    private void connection() {

        String request = "http://service-phongtest.rhcloud.com/rest_web_service/service/getalldoctor?limit=100";
        JSONArrayRequest jsonArrayRequest = new JSONArrayRequest(this);
        jsonArrayRequest.execute(request);
    }

    @Override
    public boolean onClusterClick(Cluster<Doctor> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Doctor> cluster) {

    }

    @Override
    public boolean onClusterItemClick(Doctor item) {

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Doctor item) {
        Intent t =  new Intent(getActivity(), ProfileActivity.class);
        t.putExtra("doctor",item);
        startActivity(t);
    }




    private LatLng currentLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        return latLng;
    }

    @Override
    public void processFinish(JSONArray jsonArray) {
        ArrayList<Doctor> doctors= JSONParse.doctorList(jsonArray, getContext(), myLocation);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        for (Doctor doctor: doctors){
            mClusterManager.addItem(doctor);
        }
        Log.d("medicare",doctors.size()+"");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation())      // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private class DoctorRenderer extends DefaultClusterRenderer<Doctor> {
        public DoctorRenderer() {
            super(getContext(), getMap(), mClusterManager);

        }

        @Override
        protected void onBeforeClusterItemRendered(Doctor doctor, MarkerOptions markerOptions) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(doctor.getName() + "    " + Utils.formatNumber(doctor.getDistance())).snippet(doctor.getAddress());
        }



        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }


}
