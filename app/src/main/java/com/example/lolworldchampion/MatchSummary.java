package com.example.lolworldchampion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    private String matchId;
    private String blueTeamFullName;
    private String redTeamFullName;
    private String winningTeam;
    private String jsonFileName;
    private String gameId;
    private String game;
    private String startTime; // 添加 startTime 字段
    private List<Frame> frames; // 存储时间线数据

    public MatchSummary(String matchId, String blueTeamFullName, String redTeamFullName, String winningTeam, String game, String startTime) {
        this.matchId = matchId;
        this.blueTeamFullName = blueTeamFullName;
        this.redTeamFullName = redTeamFullName;
        this.winningTeam = winningTeam;
        this.jsonFileName = matchId ; // JSON文件名基于match_id命名
        this.frames = new ArrayList<>();
        this.gameId = gameId;
        this.game = game;
        this.startTime = startTime; // 初始化 startTime 字段
    }
    public MatchSummary() {}
    // Getter 和 Setter 方法
    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getgameId() {
        return gameId;
    }

    public void setgameId(String gameId) {
        this.gameId = gameId;
    }

    public String getBlueTeamFullName() {
        return blueTeamFullName;
    }

    public void setBlueTeamFullName(String blueTeamFullName) {
        this.blueTeamFullName = blueTeamFullName;
    }

    public String getRedTeamFullName() {
        return redTeamFullName;
    }

    public void setRedTeamFullName(String redTeamFullName) {
        this.redTeamFullName = redTeamFullName;
    }

    public String getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public void setJsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}