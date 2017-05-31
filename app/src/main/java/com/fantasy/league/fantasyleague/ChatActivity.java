package com.fantasy.league.fantasyleague;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.fantasy.league.fantasyleague.adapter.ChatRecyclerAdapter;
import com.fantasy.league.fantasyleague.adapter.EndlessRecyclerOnScrollListener;
import com.fantasy.league.fantasyleague.storage.FbMessage;
import com.fantasy.league.fantasyleague.storage.Message;
import com.fantasy.league.fantasyleague.util.FirebaseKeys;
import com.fantasy.league.fantasyleague.util.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;


public class ChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    private ImageButton mSendButton;
    private EditText    mMessageText;

    RecyclerView        mRecyclerView;
    ChatRecyclerAdapter mRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSendButton = (ImageButton) findViewById(R.id.chat_send);
        mMessageText = (EditText) findViewById(R.id.chat_text);

        mMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0) {
                    mSendButton.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setColorFilter(getResources().getColor(R.color.blue_grey_200), PorterDuff.Mode.SRC_ATOP);
                    mSendButton.setEnabled(false);
                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMessageText.getText().length() > 0) {
                    FbMessage message = new FbMessage();
                    message.msg = mMessageText.getText().toString();
                    message.sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    String mid = ""+cal.getTimeInMillis();
                    FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_MESSAGES).child(mid).setValue(message);
                    mMessageText.setText("");
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerAdapter = new ChatRecyclerAdapter(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        loadData();
    }

    private void loadData() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase
                .child(FirebaseKeys.KAY_MESSAGES)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRecyclerAdapter.clear();
                mRecyclerAdapter.notifyDataSetChanged();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Message message = dsp.getValue(Message.class);
                    long time = 0;
                    try {
                        time = Long.parseLong(dsp.getKey());
                    } catch (NumberFormatException e) { }

                    message.time = time;
                    message.name = UserUtil.getInstance().getName(message.sid);
                    message.color = UserUtil.getInstance().getColor(message.sid);
                    mRecyclerAdapter.add(message);
                }
                mRecyclerAdapter.notifyDataSetChanged();

                Collections.sort(mRecyclerAdapter.getItemList(), new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Message m1 = (Message) o1;
                        Message m2 = (Message) o2;
                        return Long.valueOf(m1.time).compareTo(Long.valueOf(m2.time));
                    }
                });
                if(mRecyclerAdapter.getScrollTo() > 0) {
                    mRecyclerView.scrollToPosition(mRecyclerAdapter.getScrollTo());
                    //mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, mRecyclerAdapter.getScrollTo());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
