package com.namlongsolutions.medicare.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.namlongsolutions.medicare.R;

import java.util.ArrayList;

/**
 * Adapter for ViewPager, get fragment list to inflate
 */
public class MyPageViewAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    private int[] imageResId = {
            R.drawable.ic_view_list_white_24dp,
            R.drawable.ic_map_white_24dp,
            R.drawable.ic_favorite_border_white_24dp
    };
    private Context context;


    public MyPageViewAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    //Set tab title for each selected fragment
    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
