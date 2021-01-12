package com.example.flower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.example.flower.ApplicationClass.CHANNEL_1_ID;
import static com.example.flower.ApplicationClass.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    //NOTIFICATION
    private NotificationManagerCompat notificationManager;
    //DESIGN
    private Button button, button2;
    private TextView scoreDailyTextView, scoreTotalTextView;
    private int scoreDaily;
    private int scoreTotal;
    private String scoreTextDaily, scoreTextTotal;
    //
    public static final String workTag = "notificationWork";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);

        long time = Calendar.getInstance().getTimeInMillis();

        PreferencesConfig.saveCurrentDay(getApplicationContext(), time);



        /*
        //The input into the worker:
        Data data = new Data.Builder()
                .putInt("number", 10)
                .build();

        //WorkManagers can have certain constraints: e.g. phone has to be loading
        Constraints constraints= new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build();

        //PERIODIC WORK REQUEST
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkerManager.class, 20, TimeUnit.SECONDS)
                .setInputData(data)
                //.setConstraints(constraints)
                .addTag("Periodic")
                .build();
        //This runs the WorkManager:
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
        */ // WORK MANAGER

        //NotificationManager
        notificationManager = NotificationManagerCompat.from(this);

        //GET INTENT - ersetzt durch Shared Preferences
        //Intent intent = getIntent();
        //chillPercentage = intent.getIntExtra("EXTRA_NUMBER",0);

        //DEFINE TEXTVIEW
        scoreDailyTextView = findViewById(R.id.dailyScoreDisplay);
        scoreTotalTextView = findViewById(R.id.totalScoreDisplay);
        //DEFINE BUTTONS
        button = findViewById(R.id.button_procrastinate);
        button2 = findViewById(R.id.button_overview);

        //BUTTON ON-CLICK LISTENEER
        button2.setOnClickListener(v -> openProgressOverview());
        button.setOnClickListener(v -> openGoals());

        updateDailyScore();
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

    public void openProgressOverview() {
        //ERSTELLUNG VON NOTIFICATION: Erstellt Notification mit Titel, Text, Priorität und Category
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_bedtime)
                .setContentTitle("Do nothing!-Reminder")
                .setContentText("Today's score: "+ scoreDaily +"%. Just a little more.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();   //Baut die Notification
        //notificationManager.notify(1,notification);

        //Öffnet neue Activity und übergibt den score Wert mit putExtra()
        Intent intent = new Intent(this, ProgressCircle.class);
        startActivity(intent);
    }

    public void openGoals() {
         //Öffnet neue Activity und übergibt den score Wert mit putExtra()
        Intent intent = new Intent(this, Goals.class);
        startActivity(intent);

    }

    public void updateScore(){
        scoreTextDaily = getResources().getString(R.string.textview_score_daily);
        scoreTextTotal = getResources().getString(R.string.textview_score_total);
        scoreDaily = PreferencesConfig.loadDailyScore(this);
        scoreTotal = PreferencesConfig.loadTotalScore(this);
        String dailyText, totalText;
        if (scoreDaily >= 0) {
            dailyText = (scoreTextDaily + " " + scoreDaily);
            totalText = (scoreTextTotal + " " + scoreTotal);
            scoreDailyTextView.setText(dailyText);
            scoreTotalTextView.setText(totalText);
        }
    }

    public void updateDailyScore(){

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        long lastCheckedMillis = PreferencesConfig.loadLastCheckedDay(this);
        long now = cal.getTimeInMillis();
        long diffMillis = now - lastCheckedMillis;

        if(diffMillis>=(3600000 * 24)){


            Log.d("UpdateDailyScore", "Wert "+ diffMillis +"ist größer als "+ now + " - LastCheckMillis: " + lastCheckedMillis);
            PreferencesConfig.saveLastCheckedDay(this, now);


            Log.d("RESET", "Reset jetzt");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
        Log.d("UpdateDailyScore", "Wert "+ diffMillis + "ist kleiner als " + now + " - LastCheckMillis: " + lastCheckedMillis);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateScore();
        }
    }


}