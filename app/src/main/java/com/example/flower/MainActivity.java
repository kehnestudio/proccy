package com.example.flower;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import static com.example.flower.ApplicationClass.Channel_1_ID;
import static com.example.flower.ApplicationClass.Channel_2_ID;

public class MainActivity extends AppCompatActivity {

    //NOTIFICATION
    private NotificationManagerCompat notificationManager;
    //DESIGN
    private Button button, button2;
    private TextView scoreTextView;
    private int scoreDaily;
    private int scoreTotal;

    //SHARED PREFERENCES

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        scoreDaily = sp.getInt("scoreDaily", 0);
        scoreTotal = sp.getInt("scoreTotal", 0);
        /*
        //The input into the worker:
        Data data = new Data.Builder()
                .putInt("number", 10)
                .build();

        //WorkManagers can have certain constraints: e.g. phone has to be loading
        Constraints constraints= new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build();

        // ONE TIME WORK REQUEST
        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(WorkerManager.class)
                .setInputData(data)
                //.setConstraints(constraints)
                //.setInitialDelay(3, TimeUnit.MINUTES)
                .addTag("OneTime")
                .build();
        //This runs the  work manager:
        WorkManager.getInstance(this).enqueue(downloadRequest);

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
        scoreTextView = findViewById(R.id.textView2);

        //DEFINE BUTTONS
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        //BUTTON ON-CLICK LISTENEER
        button2.setOnClickListener(v -> openProgressOverview());
        button.setOnClickListener(v -> openGoals());

        updateScore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        scoreDaily = sp.getInt("scoreDaily", 0);
        scoreTotal = sp.getInt("scoreTotal", 0);
        updateScore();
    }

    public void openProgressOverview() {
        //ERSTELLUNG VON NOTIFICATION: Erstellt Notification mit Titel, Text, Priorität und Category
        Notification notification = new NotificationCompat.Builder(this, Channel_1_ID)
                .setSmallIcon(R.drawable.ic_bedtime)
                .setContentTitle("Do nothing!-Reminder")
                .setContentText("Today's score: "+ scoreDaily +"%. Just a little more.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();   //Baut die Notification
        //notificationManager.notify(1,notification);

        //Öffnet neue Activity und übergibt den score Wert mit putExtra()
        Intent intent = new Intent(this, ProgressCircle.class);
        //intent.putExtra("EXTRA_NUMBER", chillPercentage);
        startActivity(intent);
    }

    public void openGoals() {
        //ERSTELLUNG VON NOTIFICATION: Erstellt Notification mit Titel, Text, Priorität und Category
        Notification notification = new NotificationCompat.Builder(this, Channel_2_ID)
                .setSmallIcon(R.drawable.ic_bedtime)
                .setContentTitle("Work less, chill more")
                .setContentText("Today's result: "+ scoreDaily +"%")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();   //Baut die Notification
        //notificationManager.notify(2,notification);

        //Öffnet neue Activity und übergibt den score Wert mit putExtra()
        Intent intent = new Intent(this, Goals.class);
        startActivity(intent);
    }

    public void updateScore(){

        if (scoreDaily >= 0) {
            scoreTextView.setText("Score: " + scoreDaily +" % - Total Score: " + scoreTotal);
        }
    }
}