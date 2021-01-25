package com.procrastinator.proccy;


import com.google.firebase.Timestamp;

public class DailyScore {
    private int score;
    private Timestamp date;

    public DailyScore() {
        //public no-arg constructor needed
    }

    public DailyScore(Timestamp date, int score) {
        this.date = date    ;
        this.score = score;
    }
    public Timestamp getDate() {
        return date;
    }
    public int getScore() {
        return score;
    }

}