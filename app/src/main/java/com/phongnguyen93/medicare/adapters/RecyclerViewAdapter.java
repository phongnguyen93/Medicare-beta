package com.phongnguyen93.medicare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.extras.Utils;
import com.phongnguyen93.medicare.model.Doctor;

import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 02-Mar-16.
 * This is class an adapter for Recycler View with a custom ViewHolder
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_DATA = 1;
    private static OnItemClickListener listener;
    //Store variable for list of doctors
    private ArrayList<Doctor> doctors;

    //Constructor of this class, @param ArrayList<Doctor>
    public RecyclerViewAdapter(ArrayList<Doctor> doctors) {
        this.doctors=doctors;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d("view type", viewType + "");
        if (viewType == VIEW_TYPE_DATA) {
            View view = inflater.inflate(R.layout.card_layout, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.progress_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Doctor doctor = doctors.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            TextView name = viewHolder.nameTextView;
            TextView address = viewHolder.addressTextView;
            TextView workdays = viewHolder.workdaysTextView;
            TextView worktime = viewHolder.worktimeTextView;
            TextView distance = viewHolder.distanceTextView;
            ImageView img_view = viewHolder.img_view;
            name.setText(doctor.getName());
            address.setText(doctor.getSpec());
            workdays.setText(doctor.getWorkdays());
            worktime.setText(doctor.getWorktime());
            img_view.setImageResource(R.drawable.applogo);
            distance.setText(Utils.formatNumber(doctor.getDistance()) + "");
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == doctors.size() - 1) ? VIEW_TYPE_LOADING
                : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        RecyclerViewAdapter.listener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /*
     *    View holder class for RecyclerViewAdapter, this view has progress bar to indicated when loading data
     */
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }


    /*
     *    View holder class for RecyclerViewAdapter, this view has child views to display data
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, workdaysTextView, worktimeTextView, distanceTextView;
        ImageView img_view;
        View item_view;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            item_view = itemView.findViewById(R.id.itemview);
            img_view = (ImageView) itemView.findViewById(R.id.icon);
            nameTextView = (TextView) itemView.findViewById(R.id.detail_name);
            addressTextView = (TextView) itemView.findViewById(R.id.detail_address);
            workdaysTextView = (TextView) itemView.findViewById(R.id.detail_workdays);
            worktimeTextView = (TextView) itemView.findViewById(R.id.detail_worktime);
            distanceTextView = (TextView) itemView.findViewById(R.id.detail_distance);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });

        }

    }
}
