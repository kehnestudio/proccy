package com.procrastinator.proccy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import static com.procrastinator.proccy.ApplicationClass.CHANNEL_2_ID;
import static com.procrastinator.proccy.Receiver.SEND_ON_FINISH;
import static com.procrastinator.proccy.Receiver.UPDATE_BUTTONS;
import static com.procrastinator.proccy.Receiver.UPDATE_COUNTDOWN_TEXT;

public class TimerService extends Service {

    private long START_TIME_IN_MILLIS = 3000; //TEST VALUE
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    public static final String TIME_LEFT_IN_MILLIS = "com.procrastinator.TIME_LEFT_IN_MILLIS";
    public static final String TIMER_RUNNING = "com.procrastinator.TIMER_RUNNING";

    private String title, textFront, textBack;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimeLeftInMillis = intent.getLongExtra("inputExtra", 3000);
        mTimerRunning = PreferencesConfig.loadTimerRunning(this);

       title = getString(R.string.timer_service_title);
       textFront = getString(R.string.timer_service_front);
       textBack = getString(R.string.timer_service_back);

        if (!mTimerRunning) {
            Intent notificationIntent = new Intent(this, Goals.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                    .setContentTitle(getString(R.string.timer_service_title))
                    .setContentText(getString(R.string.timer_service_front) + getTimeLeftInMinutes() + getString(R.string.timer_service_back))
                    .setSmallIcon(R.drawable.ic_sloth_svg)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(false)
                    .setOnlyAlertOnce(true)
                    .build();

            //notificationManagerCompat.notify(2, notification.build());
            startForeground(1, notification);
            startTimer();
        }
        return START_NOT_STICKY;
    }


    private void updateNotification() {
        int minutes = getTimeLeftInMinutes();
        String message = textFront + minutes + textBack;

        if (minutes == 0){
            message = getString(R.string.timer_service_message);
        }


        Intent notificationIntent = new Intent(this, Goals.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_sloth_svg)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .build();


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }

    public int getTimeLeftInMinutes() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        return minutes;
    }

    public void sendUpdateBroadcast() {
        Intent intent = new Intent(UPDATE_COUNTDOWN_TEXT);
        intent.putExtra(TIME_LEFT_IN_MILLIS, mTimeLeftInMillis);
        sendBroadcast(intent);
    }

    public void sendUpdateButtonBroadcast() {
        Intent intent = new Intent(UPDATE_BUTTONS);
        intent.putExtra(TIMER_RUNNING, mTimerRunning);
        sendBroadcast(intent);
    }

    public void sendOnFinish() {
        Intent intent = new Intent(SEND_ON_FINISH);
        sendBroadcast(intent);
    }

    private void startTimer() {
        mTimerRunning = PreferencesConfig.loadTimerRunning(this);
        mTimerRunning = true;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mTimerRunning) {
                    mTimeLeftInMillis = millisUntilFinished;
                    PreferencesConfig.saveMilliesLeft(getApplicationContext(), mTimeLeftInMillis);
                    updateNotification();
                    sendUpdateBroadcast();
                }
            }

            @Override
            public void onFinish() {
                sendUpdateButtonBroadcast();
                sendOnFinish();
                stopSelf();

                PreferencesConfig.saveTimerHasFinished(getApplicationContext(), true);
            }
        }.start();
        PreferencesConfig.saveTimerRunning(getApplicationContext(), mTimerRunning);
        sendUpdateBroadcast();
        sendUpdateButtonBroadcast();
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mTimerRunning = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
