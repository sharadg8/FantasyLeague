package com.fantasy.league.fantasyleague.chat;

import com.fantasy.league.fantasyleague.chat.FbMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sharad on 27-May-17.
 */

public class Message extends FbMessage {
    public String name;
    public int    color;
    public long   time;

    public String getTimestamp() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(date.getTime());
    }
}
