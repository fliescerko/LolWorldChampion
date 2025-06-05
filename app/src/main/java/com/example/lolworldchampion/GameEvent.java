package com.example.lolworldchampion;

public class GameEvent {
    private int timestamp;
    private String type;
    private String killerId;
    private String victimId;
    private String assistingParticipantIds;
    private String position;

    public GameEvent(int timestamp, String type, String killerId, String victimId, String assistingParticipantIds, String position) {
        this.timestamp = timestamp;
        this.type = type;
        this.killerId = killerId;
        this.victimId = victimId;
        this.assistingParticipantIds = assistingParticipantIds;
        this.position = position;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKillerId() {
        return killerId;
    }

    public void setKillerId(String killerId) {
        this.killerId = killerId;
    }

    public String getVictimId() {
        return victimId;
    }

    public void setVictimId(String victimId) {
        this.victimId = victimId;
    }

    public String getAssistingParticipantIds() {
        return assistingParticipantIds;
    }

    public void setAssistingParticipantIds(String assistingParticipantIds) {
        this.assistingParticipantIds = assistingParticipantIds;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}