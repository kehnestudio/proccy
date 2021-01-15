package com.procrastinator.proccy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressCircle extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

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

        //Reset button listener
        resetProgress.setOnClickListener(v -> resetDailyScore());

        //Loading progress
        updateProgressBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
