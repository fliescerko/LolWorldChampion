package com.example.lolworldchampion;


import java.util.List;

public class Match {
    private String matchId;
    private String matchName;
    private String date;
    private List<String> teams;
    private List<GameEvent> events;

    public Match(String matchId, String matchName, String date, List<String> teams, List<GameEvent> events) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.date = date;
        this.teams = teams;
        this.events = events;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }
}