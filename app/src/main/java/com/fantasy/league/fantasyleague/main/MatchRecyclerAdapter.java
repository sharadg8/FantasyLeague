package com.fantasy.league.fantasyleague.main;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sharad on 10-May-17.
 */

public class MatchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Match> itemList;

    public MatchRecyclerAdapter() {
        this.itemList =  new ArrayList<>();;
    }

    public void add(Match match) {
        // loop through all elements
        for (int i = 0; i < this.itemList.size(); i++) {
            // if the element you are looking at is smaller than x,
            // go to the next element
            if (this.itemList.get(i).getTimeInMillis() < match.getTimeInMillis()) continue;
            // otherwise, we have found the location to add x
            this.itemList.add(i, match);
            return;
        }
        // we looked through all of the elements, and they were all
        // smaller than x, so we add ax to the end of the list
        this.itemList.add(match);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.match_card, parent, false);
        MatchViewHolder rcv = new MatchViewHolder(view);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MatchViewHolder holder = (MatchViewHolder) viewHolder;
        holder.between.setText(itemList.get(position).team1 + " vs " + itemList.get(position).team2);
        holder.venue.setText(itemList.get(position).venue);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(itemList.get(position).getTimeInMillis());
        SimpleDateFormat df = new SimpleDateFormat("dd MMM");
        holder.date.setText(df.format(cal.getTime()));
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm aa");
        holder.time.setText(tf.format(cal.getTime()));
        if(itemList.get(position).isMatchActive()) {
            holder.frame.setCardBackgroundColor(Color.WHITE);
        } else {
            holder.frame.setCardBackgroundColor(Color.parseColor("#ECEFF1"));
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void clear() {
        this.itemList.clear();
    }

    public Match get(int position) {
        return this.itemList.get(position);
    }

    public int getDefaultScrollTo() {
        Calendar lastDay = Calendar.getInstance();
        lastDay.add(Calendar.DATE, -1);
        for (int i = 0; i < this.itemList.size(); i++) {
            if(this.itemList.get(i).getTimeInMillis() > lastDay.getTimeInMillis()) {
                return i;
            }
        }
        return 0;
    }

    private class MatchViewHolder extends RecyclerView.ViewHolder {
        public TextView between;
        public TextView venue;
        public TextView date;
        public TextView time;
        public CardView frame;

        public MatchViewHolder(View itemView) {
            super(itemView);
            frame =(CardView) itemView.findViewById(R.id.match_card);
            between = (TextView) itemView.findViewById(R.id.match_between);
            venue = (TextView) itemView.findViewById(R.id.match_venue);
            date = (TextView) itemView.findViewById(R.id.match_date);
            time = (TextView) itemView.findViewById(R.id.match_time);
        }
    }
}
