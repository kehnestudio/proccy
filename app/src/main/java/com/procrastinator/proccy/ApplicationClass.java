package com.procrastinator.proccy;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class ApplicationClass extends Application {

    //2 NOTIFICATION CHANNEL
    public static final String CHANNEL_1_ID = "channel_1";
    public static final String CHANNEL_2_ID = "channel_2";

    //SHARED PREFERENCES
    public SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        //METHODE UM CHANNEL ZU ERSTELLEN
        createNotificationChannel();

        //METHODE UM SHARED PREFERENCES ZU ERSTELLEN
        createSharedPreferences();

    }

    private void createNotificationChannel() {
        //CHECK, UM SDK VERSION ZU PRÜFEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel_1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel_1.setDescription("This is channel 1");

            NotificationChannel channel_2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel_2.setDescription("This is channel 2");

            //ERSTELLT DIE NOTIFICATIONCHANNEL
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel_1);
            manager.createNotificationChannel(channel_2);
        }
    }

    private void createSharedPreferences() {
        sp = getSharedPreferences("UserData", Context.MODE_PRIVATE);
    }
}
