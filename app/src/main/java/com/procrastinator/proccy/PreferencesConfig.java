package com.procrastinator.proccy;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesConfig {

    private static final String MY_PREFERENCE_NAME = "com.example.proccy";
    public static final String PREF_DAILY_SCORE = "dailyScore";
    public static final String PREF_TOTAL_SCORE = "totalScore";
    public static final String PREF_TEMP_SCORE = "tempScore";
    public static final String PREF_TIMER_MILLIESLEFT = "millisLeft";
    public static final String PREF_TIMER_RUNNING = "timerRunning";
    public static final String PREF_TIMER_HAS_FINISHED = "timerFinished";
    public static final String PREF_USER_NAME = "userName";

    public static void saveUserName(Context context, String userName) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.apply();
    }

    public static String loadUserName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_USER_NAME, "my friend");
    }

    public static void saveDailyScore(Context context, int dailyScore) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_DAILY_SCORE, dailyScore);
        editor.apply();
    }

    public static void saveTotalScore(Context context, int totalScore) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_TOTAL_SCORE, totalScore);
        editor.apply();
    }

    public static void saveTempScore(Context context, int tempScore) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_TEMP_SCORE, tempScore);
        editor.apply();
    }

    public static int loadTempScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREF_TEMP_SCORE, 0);
    }



    public static void saveMilliesLeft(Context context, long milliesLeft) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(PREF_TIMER_MILLIESLEFT, milliesLeft);
        editor.apply();
    }

    public static int loadDailyScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREF_DAILY_SCORE, 0);
    }

    public static void saveTimerHasFinished(Context context, boolean timerFinished) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_TIMER_HAS_FINISHED, timerFinished);
        editor.apply();
    }

    public static boolean loadTimerHasFinished(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_TIMER_HAS_FINISHED, false);
    }


    public static int loadTotalScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREF_TOTAL_SCORE, 0);
    }

    public static long loadMilliesLeft(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getLong(PREF_TIMER_MILLIESLEFT, 3000);
    }


    public static void saveTimerRunning(Context context, boolean timerRunning) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_TIMER_RUNNING, timerRunning);
        editor.apply();
    }

    public static boolean loadTimerRunning(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_TIMER_RUNNING, false);
    }

    public static void removeDailyScoreFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_DAILY_SCORE);
        editor.remove(PREF_USER_NAME);
        editor.remove(PREF_TOTAL_SCORE);
        editor.apply();
    }

    public static void removeTempScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_TEMP_SCORE);
        editor.apply();
    }

    public static void removeMILLISLEFTandTIMERRUNNING(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_TIMER_RUNNING);
        editor.remove(PREF_TIMER_MILLIESLEFT);
        editor.apply();
    }

    public static void clearAllPreferences(Context context){
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    public static void registerPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.unregisterOnSharedPreferenceChangeListener(listener);
    }

}