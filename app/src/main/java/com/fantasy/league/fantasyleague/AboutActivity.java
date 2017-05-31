package com.fantasy.league.fantasyleague;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.storage.User;
import com.fantasy.league.fantasyleague.util.FirebaseKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String version = "0";
        int verCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }

        final TextView textVersion = (TextView) findViewById(R.id.about_version);
        textVersion.setText("Version " + version + "." + verCode);

        Button feedback = (Button) findViewById(R.id.about_feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText feedbackText = new EditText(AboutActivity.this);
                feedbackText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                feedbackText.setVerticalScrollBarEnabled(true);
                feedbackText.setMinLines(2);
                new AlertDialog.Builder(AboutActivity.this)
                        .setTitle("Feedback")
                        .setView(feedbackText)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(feedbackText.getText().length() > 0) {
                                    String fbText = FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                                            + " : " + feedbackText.getText().toString();
                                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                    String fid = "" + cal.getTimeInMillis();
                                    FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_FEEDBACK).child(fid).setValue(fbText);
                                    Snackbar.make(textVersion, "Thanks for your feedback", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .show();
            }
        });

        final TextView textAbout = (TextView) findViewById(R.id.about_story);
        final TextView textRules = (TextView) findViewById(R.id.about_rules);
        final TextView textCredits = (TextView) findViewById(R.id.about_credits);

        FirebaseDatabase.getInstance().getReference().child(FirebaseKeys.KAY_ABOUT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            switch (dsp.getKey()) {
                                case "info":
                                    textAbout.setText(dsp.getValue().toString());
                                    break;
                                case "rules":
                                    textRules.setText(dsp.getValue().toString());
                                    break;
                                case "credits":
                                    textCredits.setText(dsp.getValue().toString());
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
