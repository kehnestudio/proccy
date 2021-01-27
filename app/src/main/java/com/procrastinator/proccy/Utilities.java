package com.procrastinator.proccy;

import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;

public class Utilities {

    private static final String TAG = "UTILITIES" ;
    private int newDailyScore;

    public static Date getDateWithoutTimeUsingCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static CalendarDay getCurrentCalendarDay(){
        return CalendarDay.today();
    }

    public static int getCurrentDayDailyScore(){
        int dailyscore = 0;
        if(DataHolder.getInstance().getDailyScoreHashMap()!=null) {
            if (DataHolder.getInstance().getDailyScoreHashMap().containsKey(getCurrentCalendarDay())) {
                dailyscore = DataHolder.getInstance().getDailyScoreHashMap().get(getCurrentCalendarDay());
            } else {
                dailyscore = 0;
            }
            return dailyscore;
        } else {
            Log.d(TAG, "getSelectedDayDailyScore: No DailyScoreHashMap found");
            return dailyscore;
        }
    }

    public static int getSelectedDayDailyScore(CalendarDay calendarDay){
        int dailyscore = 0;
        if(DataHolder.getInstance().getDailyScoreHashMap()!=null) {
            if (DataHolder.getInstance().getDailyScoreHashMap().containsKey(calendarDay)) {
                dailyscore = DataHolder.getInstance().getDailyScoreHashMap().get(calendarDay);
            } else {
                dailyscore = 0;
            }
            return dailyscore;
        } else {
            Log.d(TAG, "getSelectedDayDailyScore: No DailyScoreHashMap found");
            return dailyscore;
        }
        }
    }
