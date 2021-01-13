package com.procrastinator.proccy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Goals extends AppCompatActivity {

    //TIMER VARIABLEN
    //private long START_TIME_IN_MILLIS = 300000;
    private long START_TIME_IN_MILLIS = 3000; //TEST VALUE

    FirebaseDatabase rootNode;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    //TIMER TEXTVIEW UND BUTTONS
    private TextView mTextViewCountDown, mTextViewTimer;
    private Button mButtonStartPause, mButtonReset;
    private SeekBar seekbar_timer;

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private String language;

    private int scoreDaily;
    private int scoreTotal;
    private int scoreTemp;

    int score1 = 5;
    int score2 = 5;
    int score3 = 5;
    int score4 = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate erstellt die Aktivität initial
        super.onCreate(savedInstanceState);
        //setContentView setzt das layout, wie es in der XML  "activity_blume" definiert ist.
        setContentView(R.layout.activity_goals);

        mTextViewCountDown = findViewById(R.id.text_view_timer);
        mButtonStartPause = findViewById(R.id.button_start);
        mButtonReset = findViewById(R.id.button_reset);

        checkBox1 = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);

        seekbar_timer = findViewById(R.id.seekBar_timer);
        mTextViewTimer = findViewById(R.id.text_view_title);

        language = Locale.getDefault().getLanguage();

        changeCheckBox();

        seekbar_timer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewTimer.setText(progress+getString(R.string.textview_minuten_timer));
                START_TIME_IN_MILLIS = progress * 60000;
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mButtonStartPause.setOnClickListener(v -> {
            if (START_TIME_IN_MILLIS >= 3000){
                startTimer();
            }
        });

        mButtonReset.setOnClickListener(v -> resetTimer());

        updateCountDownText();

        //Zurück-Pfeil zuvorheriger Aktivität
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000){
            @Override
            public void onTick (long millisUntilFinished){
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish () {

                mTimerRunning = false;
                mButtonStartPause.setText(getString(R.string.textview_start));
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);

                scoreDaily = PreferencesConfig.loadDailyScore(getApplicationContext());
                scoreTotal = PreferencesConfig.loadTotalScore(getApplicationContext());

                Log.d("TAG", "onFinish1: "  + score1);
                Log.d("TAG", "onFinish2: "  + score2);
                Log.d("TAG", "onFinish3: "  + score3);
                Log.d("TAG", "onFinish4: "  + score4);

                scoreTemp = 0;
                if (checkBox1.isChecked()) {
                    scoreTemp += score1;
                }
                if (checkBox2.isChecked()) {
                    scoreTemp += score2;
                }
                if (checkBox3.isChecked()) {
                    scoreTemp += score3;
                }
                if (checkBox4.isChecked()) {
                    scoreTemp += score4;
                }
                scoreDaily += scoreTemp;
                scoreTotal += scoreTemp;

                PreferencesConfig.saveDailyScore(getApplicationContext(), scoreDaily);
                PreferencesConfig.saveTotalScore(getApplicationContext(), scoreTotal);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setVisibility(View.INVISIBLE);
        mButtonReset.setVisibility(View.INVISIBLE);
        mTextViewTimer.setVisibility(View.INVISIBLE);
        checkBox1.setEnabled(false);
        checkBox2.setEnabled(false);
        checkBox3.setEnabled(false);
        checkBox4.setEnabled(false);
        seekbar_timer.setEnabled(false);

    }

    //RESETTET DEN TIMER
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        mTextViewTimer.setVisibility(View.VISIBLE);
        checkBox1.setEnabled(true);
        checkBox2.setEnabled(true);
        checkBox3.setEnabled(true);
        checkBox4.setEnabled(true);
        seekbar_timer.setEnabled(true);
    }

    //Gibt den Intent mit, wenn Zurück-Pfeil benutzt wird.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void changeCheckBox(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Counts number of questions (children) on firebase
                long childrenCount = snapshot.getChildrenCount();
                //sets max number based on children count
                int max = (int) childrenCount;

                //creates a new ArrayList with size of childrenCount
                final List<Integer> l = new ArrayList<>();
                for (int j = 0; j < max; j++ ) {
                    l.add( j );
                }
                //Shuffles the created Arraylist
                Collections.shuffle( l );

                //Converts the Integers in Array List to Strings
                String count1 = Integer.toString(l.get(0));
                String count2 = Integer.toString(l.get(1));
                String count3 = Integer.toString(l.get(2));
                String count4 = Integer.toString(l.get(3));

                if (snapshot.exists()) {

                    Log.d("TAG", "onDataChange exists");
                    String check1 = snapshot.child(count1).child(language).getValue(String.class);
                    String check2 = snapshot.child(count2).child(language).getValue(String.class);
                    String check3 = snapshot.child(count3).child(language).getValue(String.class);
                    String check4 = snapshot.child(count4).child(language).getValue(String.class);


                    score1 = Integer.parseInt(String.valueOf(snapshot.child(count1).child("score").getValue()));
                    score2 = Integer.parseInt(String.valueOf(snapshot.child(count2).child("score").getValue()));
                    score3 = Integer.parseInt(String.valueOf(snapshot.child(count3).child("score").getValue()));
                    score4 = Integer.parseInt(String.valueOf(snapshot.child(count4).child("score").getValue()));

                   checkBox1.setText(check1);
                   checkBox2.setText(check2);
                   checkBox3.setText(check3);
                   checkBox4.setText(check4);

                } else {
                    Log.d("TAG", "onDataChange: doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}