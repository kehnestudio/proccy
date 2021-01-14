package com.procrastinator.proccy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        updateDailyScore(context);
    }

    public void updateDailyScore(Context context) {
        Log.d("dailyScoreIsZero", "NULL NULL NULL");
        int dailyScoreIsZero = 0;
        PreferencesConfig.saveDailyScore(context, dailyScoreIsZero);
    }
}
