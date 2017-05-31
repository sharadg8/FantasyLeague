package com.fantasy.league.fantasyleague.util;

import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by Sharad on 27-May-17.
 */

public class DateUtil {

    static public String getFormattedDate(long timeInMillis) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timeInMillis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "MMMM d, h:mm aa";
        if (now.get(Calendar.DATE) == time.get(Calendar.DATE) ) {
            return "Today " + DateFormat.format(timeFormatString, time);
        } else if (now.get(Calendar.DATE) - time.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, time);
        } else if (now.get(Calendar.YEAR) == time.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, time).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", time).toString();
        }
    }
}
