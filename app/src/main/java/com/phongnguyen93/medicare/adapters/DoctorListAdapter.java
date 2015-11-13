package com.phongnguyen93.medicare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.pojo.Doctor;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 10/31/2015.
 */
public class DoctorListAdapter extends
        UltimateViewAdapter<DoctorListAdapter.ViewHolder> {
    ArrayList<Doctor>doctors;
    public DoctorListAdapter(ArrayList<Doctor>doctors){
        this.doctors=doctors;
    }


    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view,false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.card_layout, viewGroup, false);
        // Return a new holder instance
        return new ViewHolder(contactView,true);
    }




    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder.isitem) {

            holder.progressBarSample.setVisibility(View.GONE);
            Doctor doctor = doctors.get(position);
            TextView name = holder.nameTextView;
            TextView address = holder.addressTextView;
            TextView workdays = holder.workdaysTextView;
            TextView worktime = holder.worktimeTextView;
            TextView distance = holder.distanceTextView;
            ImageView img_view = holder.img_view;
            name.setText(doctor.getName());
            address.setText(doctor.getSpec());
            workdays.setText(doctor.getWorkdays());
            worktime.setText(doctor.getWorktime());
            try {
                Class res = R.drawable.class;
                Field field = res.getField("img_"+(position));
                int drawableId = field.getInt(null);
                if(drawableId!=0)
                    img_view.setImageResource(drawableId);
                else
                    img_view.setImageResource(R.drawable.applogo);
            }
            catch (Exception e) {
                Log.e("MyTag", "Failure to get drawable id.", e);
            }
            distance.setText(Utils.formatNumber(doctor.getDistance()) + "");
        }
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends UltimateRecyclerviewViewHolder {


        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView,addressTextView,workdaysTextView,worktimeTextView,distanceTextView,loadingTextView;
        ProgressBar progressBarSample,progressLoading;
        ImageView img_view;
        View item_view;
        boolean isitem =false;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, boolean isItem) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            progressBarSample = (ProgressBar) itemView.findViewById(R.id.progressbar);
            if(isItem) {
                isitem =true;
                item_view = itemView.findViewById(R.id.itemview);
                img_view = (ImageView)itemView.findViewById(R.id.icon);
                nameTextView = (TextView) itemView.findViewById(R.id.detail_name);
                addressTextView = (TextView) itemView.findViewById(R.id.detail_address);
                workdaysTextView = (TextView) itemView.findViewById(R.id.detail_workdays);
                worktimeTextView = (TextView) itemView.findViewById(R.id.detail_worktime);
                distanceTextView = (TextView) itemView.findViewById(R.id.detail_distance);
            }
                loadingTextView = (TextView) itemView.findViewById(R.id.txt_loading);
                progressLoading = (ProgressBar)itemView.findViewById(R.id.progress_loading);


        }
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
        public Doctor getItem(int position) {
            if (position < doctors.size())
                return doctors.get(position);
            else return null;
        }
    }

}