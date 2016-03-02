package com.phongnguyen93.medicare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.phongnguyen93.medicare.R;
import com.phongnguyen93.medicare.model.Doctor;
import java.util.ArrayList;

/**
 * Created by Phong Nguyen on 11/9/2015.
 */
public class RecyclerViewAdapter extends UltimateViewAdapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    ArrayList<Doctor> doctors;

    public RecyclerViewAdapter(ArrayList<Doctor>doctors){
        this.doctors=doctors;
    }
    @Override
    public ViewHolder getViewHolder(View view) {
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }

    public class ViewHolder extends UltimateRecyclerviewViewHolder {

        public TextView nameTextView,addressTextView,workdaysTextView,worktimeTextView,distanceTextView;
        ProgressBar progressBarSample;
        View item_view;

        public ViewHolder(View itemView, boolean isItem) {
            super(itemView);
//            super(itemView);

            if(isItem) {
                item_view = itemView.findViewById(R.id.itemview);
                nameTextView = (TextView) itemView.findViewById(R.id.detail_name);
                addressTextView = (TextView) itemView.findViewById(R.id.detail_address);
                workdaysTextView = (TextView) itemView.findViewById(R.id.detail_workdays);
                worktimeTextView = (TextView) itemView.findViewById(R.id.detail_worktime);
                distanceTextView = (TextView) itemView.findViewById(R.id.detail_distance);

            }
        }
}
}
