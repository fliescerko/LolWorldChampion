package com.example.lolworldchampion;

import java.io.Serializable;
import java.util.List;

public class FrameEvent implements Serializable {
    private int timestamp;
    private String type;
    private Integer realTimestamp;
    private Position position;
    private Integer participantId;
    private String participantName;
    private Integer killerId;
    private String killerName;
    private Integer victimId;
    private String victimName;
    private Integer teamId;
    private Integer killerTeamId;
    private List<Integer> assistingParticipantIds;
    private List<String> assistingParticipantNames; // 新增


    private String monsterType;
    private String monsterSubType;
    private String buildingType;
    private String laneType;
    private String towerType;
    private String wardType;
    private Integer itemId;
    private Integer skillSlot;
    private String levelUpType;
    private Integer bounty;
    private Integer shutdownBounty;
    private String killType;
    private Integer killStreakLength;
    private Integer multiKillLength;
    private Integer goldGain;
    private Integer level;
    private Integer creatorId;
    private Integer afterId;
    private Integer beforeId;
    private String name;
    private Long gameId;
    private List<DamageData> victimDamageDealt;
    private List<DamageData> victimDamageReceived;

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

    public Integer getRealTimestamp() {
        return realTimestamp;
    }

    public void setRealTimestamp(Integer realTimestamp) {
        this.realTimestamp = realTimestamp;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }
    public List<String> getAssistingParticipantNames() {
        return assistingParticipantNames;
    }

    public void setAssistingParticipantNames(List<String> assistingParticipantNames) {
        this.assistingParticipantNames = assistingParticipantNames;
    }


    public Integer getKillerId() {
        return killerId;
    }

    public void setKillerId(Integer killerId) {
        this.killerId = killerId;
    }

    public String getKillerName() {
        return killerName;
    }

    public void setKillerName(String killerName) {
        this.killerName = killerName;
    }

    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getKillerTeamId() {
        return killerTeamId;
    }

    public void setKillerTeamId(Integer killerTeamId) {
        this.killerTeamId = killerTeamId;
    }

    public List<Integer> getAssistingParticipantIds() {
        return assistingParticipantIds;
    }

    public void setAssistingParticipantIds(List<Integer> assistingParticipantIds) {
        this.assistingParticipantIds = assistingParticipantIds;
    }

    public String getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(String monsterType) {
        this.monsterType = monsterType;
    }

    public String getMonsterSubType() {
        return monsterSubType;
    }

    public void setMonsterSubType(String monsterSubType) {
        this.monsterSubType = monsterSubType;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }

    public String getTowerType() {
        return towerType;
    }

    public void setTowerType(String towerType) {
        this.towerType = towerType;
    }

    public String getWardType() {
        return wardType;
    }

    public void setWardType(String wardType) {
        this.wardType = wardType;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getSkillSlot() {
        return skillSlot;
    }

    public void setSkillSlot(Integer skillSlot) {
        this.skillSlot = skillSlot;
    }

    public String getLevelUpType() {
        return levelUpType;
    }

    public void setLevelUpType(String levelUpType) {
        this.levelUpType = levelUpType;
    }

    public Integer getBounty() {
        return bounty;
    }

    public void setBounty(Integer bounty) {
        this.bounty = bounty;
    }

    public Integer getShutdownBounty() {
        return shutdownBounty;
    }

    public void setShutdownBounty(Integer shutdownBounty) {
        this.shutdownBounty = shutdownBounty;
    }

    public String getKillType() {
        return killType;
    }

    public void setKillType(String killType) {
        this.killType = killType;
    }

    public Integer getKillStreakLength() {
        return killStreakLength;
    }

    public void setKillStreakLength(Integer killStreakLength) {
        this.killStreakLength = killStreakLength;
    }

    public Integer getMultiKillLength() {
        return multiKillLength;
    }

    public void setMultiKillLength(Integer multiKillLength) {
        this.multiKillLength = multiKillLength;
    }

    public Integer getGoldGain() {
        return goldGain;
    }

    public void setGoldGain(Integer goldGain) {
        this.goldGain = goldGain;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getAfterId() {
        return afterId;
    }

    public void setAfterId(Integer afterId) {
        this.afterId = afterId;
    }

    public Integer getBeforeId() {
        return beforeId;
    }

    public void setBeforeId(Integer beforeId) {
        this.beforeId = beforeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<DamageData> getVictimDamageDealt() {
        return victimDamageDealt;
    }

    public void setVictimDamageDealt(List<DamageData> victimDamageDealt) {
        this.victimDamageDealt = victimDamageDealt;
    }

    public List<DamageData> getVictimDamageReceived() {
        return victimDamageReceived;
    }

    public void setVictimDamageReceived(List<DamageData> victimDamageReceived) {
        this.victimDamageReceived = victimDamageReceived;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Killer: ").append(killerName != null ? killerName : killerId).append(", ");
        details.append("Victim: ").append(victimName != null ? victimName : victimId).append(", ");
        if (assistingParticipantIds != null) {
            details.append("Assistants: ").append(assistingParticipantIds).append(", ");
        }
        details.append("Type: ").append(type).append(", ");
        if (position != null) {
            details.append("Position: (").append(position.getX()).append(", ").append(position.getY()).append("), ");
        }
        if (victimDamageDealt != null) {
            details.append("Damage Dealt: ").append(victimDamageDealt).append(", ");
        }
        if (victimDamageReceived != null) {
            details.append("Damage Received: ").append(victimDamageReceived).append(", ");
        }
        return details.toString();
    }
}