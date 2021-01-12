package com.example.flower;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesConfig {

    private static final String MY_PREFERENCE_NAME = "com.example.flower";
    public static final String PREF_DAILY_SCORE = "dailyScore";
    public static final String PREF_TOTAL_SCORE = "totalScore";
    public static final String PREF_CURRENT_DAY = "currentDay";
    public static final String PREF_LASTCHECKED_DAY = "lastCheckedDay";


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

    public static int loadDailyScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREF_DAILY_SCORE, 0);
    }

    public static int loadTotalScore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREF_TOTAL_SCORE, 0);
    }

    public static void saveCurrentDay(Context context, long currentDay) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(PREF_CURRENT_DAY, currentDay);
        editor.apply();
    }

    public static long loadCurrentDay(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getLong(PREF_CURRENT_DAY, 0);
    }

    public static void saveLastCheckedDay(Context context, long lastCheckedDay) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(PREF_LASTCHECKED_DAY, lastCheckedDay);
        editor.apply();
    }

    public static long loadLastCheckedDay(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getLong(PREF_LASTCHECKED_DAY, 0);
    }

    public static void removeDataFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_TOTAL_SCORE);
        editor.apply();
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