package com.procrastinator.proccy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);

        onNewIntent(getIntent());
        scheduleDailyScoreReset();

        if (savedInstanceState == null) {
            setUpNavigation();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setUpNavigation();
    }

    public void setUpNavigation(){
        bottomNavigationView =findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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