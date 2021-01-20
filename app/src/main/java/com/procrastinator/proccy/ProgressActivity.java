package com.procrastinator.proccy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProgressActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int scoreDaily;
    private ProgressBar progress;
    private TextView progressText;
    private Button resetProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_circle);

        progress = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.text_view_progress);
        resetProgress = findViewById(R.id.resetButton);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.progresscircle);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.progresscircle:
                    return true;
                case R.id.main:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.goals:
                    startActivity(new Intent(getApplicationContext(), GoalsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        resetProgress.setOnClickListener(v -> resetDailyScore());
        updateProgressBar();
    }

    public void updateProgressBar() {
        scoreDaily = PreferencesConfig.loadDailyScore(this);
        progressText.setText(scoreDaily + " %");
        progress.setProgress(scoreDaily);
    }

    public void resetDailyScore() {
        PreferencesConfig.removeDailyScoreFromPref(this);
        updateProgressBar();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateProgressBar();
        }
    }
}
