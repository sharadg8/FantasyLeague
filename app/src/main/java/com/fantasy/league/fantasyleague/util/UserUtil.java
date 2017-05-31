package com.fantasy.league.fantasyleague.util;

import android.content.Context;
import android.graphics.Color;

import com.fantasy.league.fantasyleague.R;
import com.fantasy.league.fantasyleague.storage.User;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sharad on 28-May-17.
 */

public class UserUtil {
    private HashMap<String, User> userList = new HashMap<>();
    private HashMap<String, Integer>  colorList = new HashMap<>();
    private static UserUtil mInstance;
    private int colorIndex = 0;
    private String[] allColors;

    public UserUtil(Context context) {
        mInstance = this;
        allColors = context.getResources().getStringArray(R.array.colors);
    }

    public static UserUtil getInstance() {
        return mInstance;
    }

    public void clear() {
        colorIndex = 0;
        userList.clear();
        colorList.clear();
    }

    public void add(String uid, User user) {
        user.name = capitalize(user.name);
        userList.put(uid, user);
        colorIndex = (colorIndex + 1) % allColors.length;
        colorList.put(uid, Color.parseColor(allColors[colorIndex]));
    }

    public HashMap<String, User> getUserList() {
        return userList;
    }

    public String getName(String uid) {
        return userList.get(uid).name;
    }

    public int getColor(String uid) {
        return colorList.get(uid);
    }

    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }
}
