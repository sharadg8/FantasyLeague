package com.fantasy.league.fantasyleague.storage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sharad on 10-May-17.
 */

public class Match {
    private long   timeInMillis = 0;
    private String key;
    public String  team1;
    public String  team2;
    public String  venue;
    public String  timestamp;

    public long getTimeInMillis() {
        if(timeInMillis == 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = Calendar.getInstance().getTime();
            try {
                date = format.parse(timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timeInMillis = date.getTime();
        }
        return timeInMillis;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
