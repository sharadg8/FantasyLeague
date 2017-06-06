package com.fantasy.league.fantasyleague.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fantasy.league.fantasyleague.about.AboutActivity;
import com.fantasy.league.fantasyleague.about.CalculateActivity;
import com.fantasy.league.fantasyleague.chat.ChatActivity;
import com.fantasy.league.fantasyleague.match.MatchActivity;
import com.fantasy.league.fantasyleague.R;
import com.fantasy.league.fantasyleague.widget.RecyclerItemClickListener;
import com.fantasy.league.fantasyleague.login.SignInActivity;
import com.fantasy.league.fantasyleague.util.User;
import com.fantasy.league.fantasyleague.util.FirebaseKeys;
import com.fantasy.league.fantasyleague.util.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    private ProgressBar mSpinner;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);
        mSpinner = (ProgressBar)findViewById(R.id.progressBar);
        mFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mUserId = mFirebaseUser.getUid();

        final UserUtil users = new UserUtil(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_USERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            User user = dsp.getValue(User.class);
                            users.add(user.uid, user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MatchRecyclerAdapter recyclerAdapter = new MatchRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                        intent.putExtra(MatchActivity.KEY_MATCH_ID, recyclerAdapter.get(position).getKey());

                        startActivity(intent);
                    }
                })
        );

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Scroll Down
                    if (mFab.isShown()) {
                        mFab.hide();
                    }
                }
                else if (dy < 0) {
                    // Scroll Up
                    if (!mFab.isShown()) {
                        mFab.show();
                    }
                }
            }
        });

        // Use Firebase to populate the list.
        mDatabase.child(FirebaseKeys.KAY_MATCHES).child("Champions-Trophy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSpinner.setVisibility(View.GONE);
                recyclerAdapter.clear();
                recyclerAdapter.notifyDataSetChanged();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Match match = dsp.getValue(Match.class);
                    match.setKey(dsp.getKey());
                    recyclerAdapter.add(match);
                    recyclerAdapter.notifyDataSetChanged();
                }

                recyclerView.scrollToPosition(recyclerAdapter.getDefaultScrollTo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        for(int i=0; i<menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_calculate:
                startActivity(new Intent(MainActivity.this, CalculateActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            /*case R.id.action_logout:
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }
}
