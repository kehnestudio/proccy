package com.procrastinator.proccy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

    public static final String RESET_DAILY_SCORE = "com.procrastinator.proccy.RESET_DAILY_SCORE";
    public static final String UPDATE_COUNTDOWN_TEXT = "com.procrastinator.proccy.UPDATE_COUNTDOWN_TEXT";
    public static final String UPDATE_BUTTONS = "com.procrastinator.proccy.UPDATE_BUTTONS";
    public static final String SEND_ON_FINISH = "com.procrastinator.proccy.SEND_ON_FINISH";
    private static final String TAG = "ReceiverClass";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (RESET_DAILY_SCORE.equals(intent.getAction())){
            Log.d(TAG, "updateDailyScore");
            updateDailyScore(context);
        }
    }
    public void updateDailyScore(Context context) {
        PreferencesConfig.removeDailyScoreFromPref(context);
    }
}
