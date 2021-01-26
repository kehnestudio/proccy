package com.procrastinator.proccy;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;

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
        return CalendarDay.today();
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
