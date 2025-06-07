package com.example.lolworldchampion;

import java.io.Serializable;

public class FavoriteEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private FrameEvent event;
    private String note;
    private long favoriteTime;
    private String matchId;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public FavoriteEvent(FrameEvent event, String note) {
        this.event = event;
        this.note = note;
        this.favoriteTime = System.currentTimeMillis();
        this.matchId = matchId;
    }

    public FrameEvent getEvent() {
        return event;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getFavoriteTime() {
        return favoriteTime;
    }
}
