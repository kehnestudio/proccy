package com.procrastinator.proccy;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashMap;
import java.util.List;

public class DataHolder {

    private int totalscore;
    private String displayName;
    private HashMap<CalendarDay, Integer> dailyScoreHashMap;
    private  List<CalendarDay> calendarDays;

    private static final DataHolder instance = new DataHolder();

    public static DataHolder getInstance() {
        return instance;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalScore() {
        return totalscore;
    }
    public void setTotalScore(int totalscore) {
        this.totalscore = totalscore;
    }

    public HashMap<CalendarDay, Integer> getDailyScoreHashMap(){
        return dailyScoreHashMap;
    }
    public void setDailyScoreHashMap(HashMap<CalendarDay,Integer> dailyScoreHashMap) {
        this.dailyScoreHashMap = dailyScoreHashMap;
    }
    public List<CalendarDay> getCalendarDays(){
        return calendarDays;
    }
    public void setCalendarDays(List<CalendarDay> calendarDays) {
        this.calendarDays = calendarDays;
    }

}


