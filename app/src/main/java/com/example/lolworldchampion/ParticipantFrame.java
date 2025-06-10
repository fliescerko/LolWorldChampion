package com.example.lolworldchampion;

import java.io.Serializable;

public class ParticipantFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer participantId;
    private ChampionStats championStats;
    private int currentGold;
    private DamageStats damageStats;
    private int goldPerSecond;
    private int jungleMinionsKilled;
    private int level;
    private int minionsKilled;
    private Position position;
    private int timeEnemySpentControlled;
    private int totalGold;
    private int xp;

    // Getters and setters
    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public ChampionStats getChampionStats() {
        return championStats;
    }

    public void setChampionStats(ChampionStats championStats) {
        this.championStats = championStats;
    }

    public int getCurrentGold() {
        return currentGold;
    }

    public void setCurrentGold(int currentGold) {
        this.currentGold = currentGold;
    }

    public DamageStats getDamageStats() {
        return damageStats;
    }

    public void setDamageStats(DamageStats damageStats) {
        this.damageStats = damageStats;
    }

    public int getGoldPerSecond() {
        return goldPerSecond;
    }

    public void setGoldPerSecond(int goldPerSecond) {
        this.goldPerSecond = goldPerSecond;
    }

    public int getJungleMinionsKilled() {
        return jungleMinionsKilled;
    }

    public void setJungleMinionsKilled(int jungleMinionsKilled) {
        this.jungleMinionsKilled = jungleMinionsKilled;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMinionsKilled() {
        return minionsKilled;
    }

    public void setMinionsKilled(int minionsKilled) {
        this.minionsKilled = minionsKilled;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getTimeEnemySpentControlled() {
        return timeEnemySpentControlled;
    }

    public void setTimeEnemySpentControlled(int timeEnemySpentControlled) {
        this.timeEnemySpentControlled = timeEnemySpentControlled;
    }

    public int getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(int totalGold) {
        this.totalGold = totalGold;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}