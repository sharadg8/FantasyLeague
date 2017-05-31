package com.fantasy.league.fantasyleague;

import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.adapter.MatchPlayerRecyclerAdapter;
import com.fantasy.league.fantasyleague.storage.FbPlayers;
import com.fantasy.league.fantasyleague.storage.Match;
import com.fantasy.league.fantasyleague.storage.Players;
import com.fantasy.league.fantasyleague.storage.User;
import com.fantasy.league.fantasyleague.util.FirebaseKeys;
import com.fantasy.league.fantasyleague.util.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class MatchActivity extends AppCompatActivity {
    public static String KEY_MATCH_ID = "match_id";
    private String      mMatchId;
    private String      mUserId;
    private Match       mMatch;
    private Players     mMyPlayer;
    private View        mMatchView;
    private TextView    mTextTeam1;
    private TextView    mTextTeam2;
    private TextView    mTextVenue;
    private TextView    mTextTime;
    private TextView    mTextMessage;
    private TextView    mTextCountdown;
    private RecyclerView mRecyclerView;

    private MatchPlayerRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        mMatchView       = findViewById(R.id.match_card);
        mTextTeam1       = (TextView) findViewById(R.id.match_team1);
        mTextTeam2       = (TextView) findViewById(R.id.match_team2);
        mTextTime        = (TextView) findViewById(R.id.match_time);
        mTextVenue       = (TextView) findViewById(R.id.match_venue);
        mTextMessage     = (TextView) findViewById(R.id.match_message);
        mTextCountdown   =  (TextView) findViewById(R.id.match_countdown);

        Bundle extras = getIntent().getExtras();
        mMatchId = extras.getString(KEY_MATCH_ID, null);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAdapter = new MatchPlayerRecyclerAdapter(getApplicationContext(), mMatch, mUserId);
        mRecyclerAdapter.setOnSavePlayerListener(new MatchPlayerRecyclerAdapter.OnSavePlayerListener() {
            @Override
            public void onSavePlayers(EditText e1, EditText e2) {
                storePlayerSelection(e1, e2);
            }
        });

        mRecyclerView.setAdapter(mRecyclerAdapter);

        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_USERS)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRecyclerAdapter.clear();
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        User user = dsp.getValue(User.class);
                        Players player = new Players(user.uid, UserUtil.capitalize(user.name));
                        mRecyclerAdapter.add(player);
                    }

                    Collections.sort(mRecyclerAdapter.getItemList(), new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            Players p1 = (Players) o1;
                            Players p2 = (Players) o2;
                            return p1.getName().compareToIgnoreCase(p2.getName());
                        }
                    });

                    mRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_MATCHES)
                .child("Champions-Trophy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMatch = dataSnapshot.child(mMatchId).getValue(Match.class);
                if(mMatch != null) {
                    mRecyclerAdapter.setMatch(mMatch);
                    mMatchView.setVisibility(View.VISIBLE);
                    mTextTeam1.setText(mMatch.team1);
                    mTextTeam2.setText(mMatch.team2);
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm aa");
                    mTextTime.setText(df.format(mMatch.getTimeInMillis()));
                    mTextVenue.setText(mMatch.venue);

                    updateUI();

                    Calendar now = Calendar.getInstance();
                    long timeLeft = mMatch.getTimeInMillis() - now.getTimeInMillis() - 30*60*1000;
                    if(timeLeft > 0) {
                        new CountDownTimer(timeLeft, 1000) {
                            public void onTick(long millisUntilFinished) {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                millisUntilFinished -= TimeZone.getDefault().getRawOffset();
                                mTextCountdown.setText("List opens in - " + sdf.format(new Date(millisUntilFinished)));
                            }
                            public void onFinish() {
                                updateUI();
                            }
                        }.start();
                    }
                } else {
                    mTextMessage.setVisibility(View.GONE);
                    mMatchView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_PLAYERS).child(mMatchId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FbPlayers fbPlayer = ds.getValue(FbPlayers.class);
                        mRecyclerAdapter.update(fbPlayer);
                        if(fbPlayer.userId.equals(mUserId)) {
                            mMyPlayer = new Players(fbPlayer);
                        }
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void storePlayerSelection(EditText e1, EditText e2) {
        if(e1.getText().length() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Player field empty!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        if(e2.getText().length() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Player field empty!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        Players player = new Players(mUserId, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        player.score1 = 0;
        player.score2 = 0;
        player.player1 = e1.getText().toString();
        player.player2 = e2.getText().toString();
        player.timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        FbPlayers fbPlayer = new FbPlayers();
        fbPlayer.userId = mUserId;
        fbPlayer.player1 = player.player1;
        fbPlayer.player2 = player.player2;
        fbPlayer.timestamp = player.timestamp;

        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_PLAYERS).child(mMatchId).child(mUserId).setValue(fbPlayer);
        mMyPlayer = player;
        mRecyclerAdapter.update(player);
        mRecyclerAdapter.notifyDataSetChanged();

        Snackbar.make(mRecyclerView, "Your players submitted", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void updateUI() {
        mTextMessage.setVisibility(View.GONE);
        mTextCountdown.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        Calendar now = Calendar.getInstance();
        if(mMatch.getTimeInMillis() > (now.getTimeInMillis() + 24*60*60*1000)) {
            mTextMessage.setText("Player selection opens\n24 hours before match");
            mTextMessage.setVisibility(View.VISIBLE);
        } else if(mMatch.getTimeInMillis() > (now.getTimeInMillis() + 30*60*1000)) {
            mTextCountdown.setVisibility((mMyPlayer != null) ? View.VISIBLE : View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if(mMyPlayer == null) {
                mTextMessage.setText("You missed player selection");
                mTextMessage.setVisibility(View.VISIBLE);
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerAdapter.setHidden(false);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
