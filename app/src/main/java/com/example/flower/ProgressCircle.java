package com.example.flower;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressCircle extends AppCompatActivity {

    private int scoreDaily;
    private ProgressBar progress;
    private TextView text;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_circle);

        //Lädt Shared Preferences
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        //Öffnet und lädt SharedPreferences
        scoreDaily = sp.getInt("scoreDaily", 0);

        progress = findViewById(R.id.progress_bar);
        text = findViewById(R.id.text_view_progress);
        reset = findViewById(R.id.resetButton);

        //Reset button listener
        reset.setOnClickListener(v -> {
            resetDailyScore();
        });

        //Loading progress
        updateProgressBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    public void updateProgressBar() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        scoreDaily = sp.getInt("scoreDaily", 0);
        text.setText(scoreDaily + " %");
        progress.setProgress(scoreDaily);
    }

    public void resetDailyScore(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        scoreDaily = sp.getInt("scoreDaily", 0);
        scoreDaily = 0;
        //SharedPreferences wird geupdated

        SharedPreferences.Editor editor =  sp.edit();
        editor.putInt("scoreDaily", scoreDaily).apply();
        updateProgressBar();
    }

    //Gibt den Intent mit, wenn Zurück-Pfeil benutzt wird.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
