package com.procrastinator.proccy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView scoreDailyTextView, scoreTotalTextView;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);

        scoreDailyTextView = findViewById(R.id.dailyScoreDisplay);
        scoreTotalTextView = findViewById(R.id.totalScoreDisplay);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.main);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.main:
                    return true;
                case R.id.goals:
                    startActivity(new Intent(getApplicationContext(), Goals.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.progresscircle:
                    startActivity(new Intent(getApplicationContext(), ProgressCircle.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        scheduleDailyScoreReset();
        updateScore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferencesConfig.registerPref(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferencesConfig.unregisterPref(this, this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateScore();
        }
    }

    public void updateScore() {
        String scoreTextDaily = getResources().getString(R.string.textview_score_daily);
        String scoreTextTotal = getResources().getString(R.string.textview_score_total);
        int scoreDaily;
        int scoreTotal;

        scoreDaily = PreferencesConfig.loadDailyScore(this);
        scoreTotal = PreferencesConfig.loadTotalScore(this);

        if (scoreDaily >= 0) {
            scoreDailyTextView.setText(scoreTextDaily + scoreDaily);
            scoreTotalTextView.setText(scoreTextTotal + scoreTotal);
        }
    }

    public void scheduleDailyScoreReset() {

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Receiver.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            alarmMgr.cancel(pendingIntent);
        } else {

            alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }
}