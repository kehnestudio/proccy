package com.example.flower;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

public class Goals extends AppCompatActivity {

    //TIMER VARIABLEN
    //private long START_TIME_IN_MILLIS = 300000;
    private long START_TIME_IN_MILLIS = 3000; //TEST VALUE

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    //TIMER TEXTVIEW UND BUTTONS
    private TextView mTextViewCountDown, mTextViewTimer;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private SeekBar seekbar_timer;

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;


    private int scoreDaily;
    private int scoreTotal;
    private int scoreTemp;

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

        seekbar_timer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewTimer.setText(progress+"minute timer");
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
                mButtonStartPause.setText ("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);

                scoreDaily = PreferencesConfig.loadDailyScore(getApplicationContext());
                scoreTotal = PreferencesConfig.loadTotalScore(getApplicationContext());

                scoreTemp = 0;
                if (checkBox1.isChecked()) {
                    scoreTemp += 25;
                }
                if (checkBox2.isChecked()) {
                    scoreTemp += 25;
                }
                if (checkBox3.isChecked()) {
                    scoreTemp += 25;
                }
                if (checkBox4.isChecked()) {
                    scoreTemp += 25;
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
/* //PAUSIERT DEN TIMER
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning= false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }
*/
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
}