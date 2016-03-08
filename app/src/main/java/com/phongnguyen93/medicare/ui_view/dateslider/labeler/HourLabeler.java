package com.phongnguyen93.medicare.ui_view.dateslider.labeler;

import com.phongnguyen93.medicare.ui_view.dateslider.TimeObject;

import java.util.Calendar;


/**
 * A Labeler that displays hours
 */
public class HourLabeler extends Labeler {
    private final String mFormatString;

    public HourLabeler(String formatString) {
        super(90, 45);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectfromCalendar(Util.addHours(time, val));
    }

    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
        return Util.getHour(c, mFormatString);
    }
}
