package com.fantasy.league.fantasyleague.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.R;
import com.fantasy.league.fantasyleague.storage.Match;
import com.fantasy.league.fantasyleague.util.DateUtil;
import com.fantasy.league.fantasyleague.storage.FbPlayers;
import com.fantasy.league.fantasyleague.storage.Players;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Sharad on 10-May-17.
 */

public class MatchPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Players> itemList;
    private boolean isHidden;
    private String  thisUser;
    private Context context;
    private Match   match;
    public OnSavePlayerListener onSavePlayerListener;

    public MatchPlayerRecyclerAdapter(Context context, Match match, String thisUser) {
        this.context = context;
        this.match = match;
        this.itemList = new ArrayList<>();
        this.thisUser = thisUser;
        this.isHidden = true;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public void add(Players player) {
        this.itemList.add(player);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.match_user, parent, false);
        MatchViewHolder rcv = new MatchViewHolder(view);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MatchViewHolder holder = (MatchViewHolder) viewHolder;
        holder.name.setText(this.itemList.get(position).getName());

        holder.selectPlayers.setVisibility(View.GONE);
        holder.selectedPlayers.setVisibility(View.GONE);
        holder.userMessage.setVisibility(View.GONE);

        if((isHidden == false) || (this.itemList.get(position).getUserId().equals(this.thisUser))) {
            if(this.itemList.get(position).timestamp > 0) {
                String player1 = this.itemList.get(position).player1.equals("") ? "-" : this.itemList.get(position).player1;
                String player2 = this.itemList.get(position).player2.equals("") ? "-" : this.itemList.get(position).player2;
                holder.timestamp.setText(DateUtil.getFormattedDate(this.itemList.get(position).timestamp));
                holder.timestamp.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.selection_time, null));
                holder.player1.setText(player1);
                holder.player2.setText(player2);
                holder.selectedPlayers.setVisibility(View.VISIBLE);
                holder.userMessage.setVisibility(View.VISIBLE);
            } else {
                holder.timestamp.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.selection_missed, null));
                if(this.itemList.get(position).getUserId().equals(this.thisUser)) {
                    if(isHidden == true) {
                        //holder.timestamp.setText("Choose your players");
                        holder.selectPlayers.setVisibility(View.VISIBLE);
                        Calendar now = Calendar.getInstance();
                        long timeLeft = match.getTimeInMillis() - now.getTimeInMillis() - 30*60*1000;
                        if(timeLeft > 0) {
                            new CountDownTimer(timeLeft, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    millisUntilFinished -= TimeZone.getDefault().getRawOffset();
                                    holder.countdown.setText(sdf.format(new Date(millisUntilFinished)));
                                }
                                public void onFinish() { }
                            }.start();
                        }
                    } else {
                        holder.timestamp.setText("Missed selection :(");
                        holder.userMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.timestamp.setText("Missed selection :D");
                    holder.userMessage.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.userMessage.setVisibility(View.VISIBLE);
            if(this.itemList.get(position).timestamp > 0) {
                holder.timestamp.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.selection_hidden, null));
                holder.timestamp.setText("Selected - " + DateUtil.getFormattedDate(this.itemList.get(position).timestamp));
            } else {
                holder.timestamp.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.selection_waiting, null));
                holder.timestamp.setText("Waiting!");
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void clear() {
        this.itemList.clear();
    }

    public void update(FbPlayers update) {
        for(int i=0; i<this.itemList.size(); i++) {
            if(this.itemList.get(i).getUserId().equals(update.userId)) {
                Players players = this.itemList.get(i);
                players.timestamp = update.timestamp;
                players.player1 = update.player1;
                players.player2 = update.player2;
                this.itemList.set(i, players);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public List<Players> getItemList() {
        return this.itemList;
    }

    private class MatchViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView player1;
        public TextView player2;
        public TextView timestamp;

        public View     selectPlayers;
        public View     selectedPlayers;
        public View     userMessage;
        public TextView countdown;

        private EditText    editPlayer1;
        private EditText    editPlayer2;
        private Button      submit;

        public MatchViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.match_name);
            player1 = (TextView) itemView.findViewById(R.id.match_player1);
            player2 = (TextView) itemView.findViewById(R.id.match_player2);
            timestamp = (TextView) itemView.findViewById(R.id.match_timestamp);

            selectPlayers = itemView.findViewById(R.id.selectPlayers);
            selectedPlayers = itemView.findViewById(R.id.selectedPlayers);
            userMessage = itemView.findViewById(R.id.user_message);

            countdown = (TextView) itemView.findViewById(R.id.match_countdown);
            editPlayer1     = (EditText) itemView.findViewById(R.id.match_edit_player1);
            editPlayer2     = (EditText) itemView.findViewById(R.id.match_edit_player2);
            submit          = (Button) itemView.findViewById(R.id.match_submit);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onSavePlayerListener != null) {
                        onSavePlayerListener.onSavePlayers(editPlayer1, editPlayer2);
                    }
                }
            });
        }
    }

    public void setOnSavePlayerListener(OnSavePlayerListener listener) {
        onSavePlayerListener = listener;
    }

    public interface OnSavePlayerListener {
        void onSavePlayers(EditText e1, EditText e2);
    }
}
