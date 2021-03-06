package com.fantasy.league.fantasyleague.about;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.R;

public class CalculateActivity extends AppCompatActivity {
    Batting mBatting;
    Bowling mBowling;
    Other mOther;

    TextView mTotalPoints;
    int battingPoints = 0;
    int bowlingPoints = 0;
    int otherPoints = 0;

    public class Batting {
        EditText etRuns;
        EditText etBalls;
        EditText et4s;
        EditText et6s;
        EditText etSR;
        TextView textTotal;

        public int total = 0;
        boolean  enable = false;
        View.OnFocusChangeListener focusListener;

        public Batting() {
            final View grpBatting = findViewById(R.id.calc_batting_group);

            focusListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    calculate();
                }
            };

            etRuns = (EditText) findViewById(R.id.calc_bat_runs);
            etRuns.setOnFocusChangeListener(focusListener);
            etBalls = (EditText) findViewById(R.id.calc_bat_balls);
            etBalls.setOnFocusChangeListener(focusListener);
            et4s = (EditText) findViewById(R.id.calc_bat_4s);
            et4s.setOnFocusChangeListener(focusListener);
            et6s = (EditText) findViewById(R.id.calc_bat_6s);
            et6s.setOnFocusChangeListener(focusListener);
            etSR = (EditText) findViewById(R.id.calc_bat_sr);
            etSR.setEnabled(false);
            textTotal = (TextView) findViewById(R.id.calc_bat_total);

            ((CheckBox) findViewById(R.id.calc_check_bat)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            grpBatting.setVisibility(b ? View.VISIBLE : View.GONE);
                            enable = b;
                            calculate();
                        }
                    });

            clear();
        }

        public void clear() {
            etRuns.setText("");
            etBalls.setText("");
            et4s.setText("");
            et6s.setText("");
            etSR.setText("");

            calculate();
        }

        public void calculate() {
            if(enable) {
                int runs = (etRuns.getText().length() > 0) ? Integer.parseInt(etRuns.getText().toString()) : 0;
                int balls = (etBalls.getText().length() > 0) ? Integer.parseInt(etBalls.getText().toString()) : 0;
                int bat4s = (et4s.getText().length() > 0) ? Integer.parseInt(et4s.getText().toString()) : 0;
                int bat6s = (et6s.getText().length() > 0) ? Integer.parseInt(et6s.getText().toString()) : 0;
                float strikeRate = (balls > 0) ? ((float)runs*100 / balls) : 0;
                etSR.setText(""+strikeRate);
                total = 0;
                if(runs > 99) { total += (100 + runs*3);
                } else if(runs > 74) { total += runs*3;
                } else if(runs > 49) { total += runs*2;
                } else { total += runs; }

                if(runs < 25) { total += (runs - 25) * 2; }

                total += (bat4s * 5);
                total += (bat6s * 10);

                if(balls > 9) { total += (strikeRate * 0.8);
                } else { total += (strikeRate * 0.4); }

                if(strikeRate < 65) { total += ((strikeRate - 60) * 2); }
                if((balls>1) && (runs==0)) { total -= 5; }
                if((balls==1) && (runs==0)) { total -= 30; }
                if((balls==0) && (runs==0)) { total -= 55; }
            } else {
                total = 0;
            }
            textTotal.setText(""+total);
            battingPoints = total;
            mTotalPoints.setText(""+(battingPoints+bowlingPoints+otherPoints));
        }
    }

    public class Bowling {
        EditText etOvers;
        EditText etRuns;
        EditText etMaidenOver;
        EditText etEconomyRate;
        EditText etWickets;
        EditText et0s;
        EditText et4s;
        EditText et6s;
        EditText etNoBall;
        EditText etWideBall;
        TextView textTotal;

        public int total = 0;
        boolean  enable = false;
        View.OnFocusChangeListener focusListener;

        public Bowling() {
            final View layout = findViewById(R.id.calc_bowling_group);
            ((CheckBox) findViewById(R.id.calc_check_ball)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            layout.setVisibility(b ? View.VISIBLE : View.GONE);
                            enable = b;
                            calculate();
                        }
                    });

            focusListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    calculate();
                }
            };

            etOvers = (EditText) findViewById(R.id.calc_ball_over);
            etOvers.setOnFocusChangeListener(focusListener);
            etRuns = (EditText) findViewById(R.id.calc_ball_runs);
            etRuns.setOnFocusChangeListener(focusListener);
            etMaidenOver = (EditText) findViewById(R.id.calc_ball_mo);
            etMaidenOver.setOnFocusChangeListener(focusListener);
            etEconomyRate = (EditText) findViewById(R.id.calc_ball_er);
            etEconomyRate.setEnabled(false);
            etWickets = (EditText) findViewById(R.id.calc_ball_wicket);
            etWickets.setOnFocusChangeListener(focusListener);
            et0s = (EditText) findViewById(R.id.calc_ball_0s);
            et0s.setOnFocusChangeListener(focusListener);
            et4s = (EditText) findViewById(R.id.calc_ball_4s);
            et4s.setOnFocusChangeListener(focusListener);
            et6s = (EditText) findViewById(R.id.calc_ball_6s);
            et6s.setOnFocusChangeListener(focusListener);
            etNoBall = (EditText) findViewById(R.id.calc_ball_nb);
            etNoBall.setOnFocusChangeListener(focusListener);
            etWideBall = (EditText) findViewById(R.id.calc_ball_wb);
            textTotal = (TextView) findViewById(R.id.calc_ball_total);

            clear();
        }

        public void clear() {
            etOvers.setText("");
            etRuns.setText("");
            etMaidenOver.setText("");
            etEconomyRate.setText("");
            etWickets.setText("");
            et0s.setText("");
            et4s.setText("");
            et6s.setText("");
            etNoBall.setText("");
            etWideBall.setText("");

            calculate();
        }

        public void calculate() {
            if(enable) {
                float overs = (etOvers.getText().length() > 0) ? Float.parseFloat(etOvers.getText().toString()) : 0;
                int runs = (etRuns.getText().length() > 0) ? Integer.parseInt(etRuns.getText().toString()) : 0;
                int maidenOver = (etMaidenOver.getText().length() > 0) ? Integer.parseInt(etMaidenOver.getText().toString()) : 0;
                int wickets = (etWickets.getText().length() > 0) ? Integer.parseInt(etWickets.getText().toString()) : 0;
                int ball0s = (et0s.getText().length() > 0) ? Integer.parseInt(et0s.getText().toString()) : 0;
                int ball4s = (et4s.getText().length() > 0) ? Integer.parseInt(et4s.getText().toString()) : 0;
                int ball6s = (et6s.getText().length() > 0) ? Integer.parseInt(et6s.getText().toString()) : 0;
                int noBall = (etNoBall.getText().length() > 0) ? Integer.parseInt(etNoBall.getText().toString()) : 0;
                int wideBall = (etWideBall.getText().length() > 0) ? Integer.parseInt(etWideBall.getText().toString()) : 0;
                float economyRate = (overs > 0) ? ((float)runs / overs) : 0;
                etEconomyRate.setText(""+economyRate);
                int balls = (int) (Math.floor(overs) * 6);
                balls += ((overs - Math.floor(overs)) * 10);
                total = 0;

                total += (balls * 2);
                total += (maidenOver * 20);
                total += (ball0s * 2);
                if(wickets < 3) {
                    total += (wickets*20);
                } else if(wickets < 5) {
                    total += (wickets*40);
                } else {
                    total += 100;
                    total += (wickets*60);
                }

                total -= (ball4s*10);
                total -= (ball6s*10);
                total -= ((noBall+wideBall)*10);

                total += (overs*5);

                if(economyRate <= 6.5) {
                    total += (50 + (12-economyRate)*overs);
                } else if(economyRate <= 8) {
                    total += (6-economyRate)*overs;
                } else {
                    total += (0-economyRate)*overs*2;
                    total -= 100;
                }
            } else {
                total = 0;
            }
            textTotal.setText(""+total);
            bowlingPoints = total;
            mTotalPoints.setText(""+(battingPoints+bowlingPoints+otherPoints));
        }
    }

    public class Other {
        EditText etExtra;
        TextView textTotal;
        boolean isMOM = false;
        boolean isNO = false;

        public int total = 0;
        boolean  enable = false;
        View.OnFocusChangeListener focusListener;

        public Other() {
            final View layout = findViewById(R.id.calc_other_group);
            ((CheckBox) findViewById(R.id.calc_check_other)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            layout.setVisibility(b ? View.VISIBLE : View.GONE);
                            enable = b;
                            calculate();
                        }
                    });

            ((CheckBox) findViewById(R.id.calc_other_mom)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            isMOM = b;
                            calculate();
                        }
                    });

            ((CheckBox) findViewById(R.id.calc_other_no)).setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            isNO = b;
                            calculate();
                        }
                    });

            focusListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    calculate();
                }
            };

            etExtra = (EditText) findViewById(R.id.calc_other_extra);
            etExtra.setOnFocusChangeListener(focusListener);
            textTotal = (TextView) findViewById(R.id.calc_other_total);

            clear();
        }

        public void clear() {
            etExtra.setText("");

            calculate();
        }

        public void calculate() {
            if(enable) {
                float extra = (etExtra.getText().length() > 0) ? Float.parseFloat(etExtra.getText().toString()) : 0;
                total = 0;

                total += extra*20;
                total += isMOM ? 100 : 0;
                total += isNO ? 30 : 0;
            } else {
                total = 0;
            }
            textTotal.setText(""+total);
            otherPoints = total;
            mTotalPoints.setText(""+(battingPoints+bowlingPoints+otherPoints));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTotalPoints = (TextView) findViewById(R.id.calc_total);
        mBatting = new Batting();
        mBowling = new Bowling();
        mOther = new Other();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculate, menu);

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
            case R.id.action_clear:
                mBatting.clear();
                mBowling.clear();
                mOther.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
