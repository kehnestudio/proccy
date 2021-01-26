package com.procrastinator.proccy;

import android.content.Intent;
import android.provider.ContactsContract;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utilities {

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
        CalendarDay today = CalendarDay.today();
        return today;
    }

    public static int getCurrentDayDailyScore(){
        int dailyscore;
        if (DataHolder.getInstance().getDailyScoreHashMap().containsKey(getCurrentCalendarDay())) {
            dailyscore = DataHolder.getInstance().getDailyScoreHashMap().get(getCurrentCalendarDay());
        } else {
            dailyscore = 0;
        }
        return dailyscore;
    }
}
