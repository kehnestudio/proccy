package com.procrastinator.proccy;

public class DataHolder {

    private int totalscore, dailyscore;
    private String displayName;

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

    public int getDailyScore(){
        return dailyscore;
    }

    public void setDailyScore(int dailyscore) {
        this.dailyscore = dailyscore;
    }
}


