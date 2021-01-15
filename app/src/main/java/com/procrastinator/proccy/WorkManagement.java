package com.procrastinator.proccy;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.procrastinator.proccy.ApplicationClass.CHANNEL_1_ID;

public class WorkManagement extends Worker {
    private NotificationManagerCompat notificationManager;

    public WorkManagement(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification();
        return Result.success();
    }

    private void displayNotification() {

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                //.setSmallIcon(R.drawable.ic_bedtime)
                .setContentTitle("Titel")
                .setContentText("Beschreibung")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();   //Baut die Notification
        notificationManager.notify(null, 1, notification);
    }
}
