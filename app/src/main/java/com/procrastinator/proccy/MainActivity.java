package com.procrastinator.proccy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


import static com.procrastinator.proccy.ApplicationClass.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Button firebaseUpdate;

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

        //FIREBASE BUTTON
        firebaseUpdate = findViewById(R.id.button_FirebaseTest);
        firebaseUpdate.setOnClickListener(v -> {

            Intent intent = new Intent(this, FireBaseTest.class);
            startActivity(intent);
        });

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
        if (scoreDaily >= 0) {
            scoreDailyTextView.setText(scoreTextDaily + scoreDaily);
            scoreTotalTextView.setText(scoreTextTotal +scoreTotal);
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