package com.example.defensecommander;

import java.io.Serializable;

public class ScoreEntry implements Serializable {

    private long dateTimeMillis;
    private String initials;
    private int score;
    private int level;

    public ScoreEntry(long dateTimeMillis, String initials, int score, int level) {
        this.dateTimeMillis = dateTimeMillis;
        this.initials = initials;
        this.score = score;
        this.level = level;
    }

    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public String getInitials() {
        return initials;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }
}
