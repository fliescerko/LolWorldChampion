package com.example.lolworldchampion;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonParser {
    public static MatchSummary parseMatchJson(String jsonString, Map<String, Map<Integer, String>> participantMap, String matchId) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        MatchSummary matchSummary = new Gson().fromJson(jsonString, MatchSummary.class);

        int frameInterval = json.optInt("frameInterval", 0);
        Log.d("JsonParser", "解析比赛: " + matchId + ", frameInterval: " + frameInterval);
        JSONArray framesArray = json.getJSONArray("frames");
        if (framesArray == null) {
            Log.e("JsonParser", "frames数组不存在于JSON中");
            return matchSummary;
        }

        List<Frame> frames = new ArrayList<>();
        Log.d("JsonParser", "总帧数: " + framesArray.length());

        for (int i = 0; i < framesArray.length(); i++) {
            JSONObject frameObj = framesArray.getJSONObject(i);
            Frame frame = new Frame();
            frame.setFrameInterval(frameInterval);

            JSONArray eventsArray = frameObj.getJSONArray("events");
            List<FrameEvent> events = new ArrayList<>();

            for (int j = 0; j < eventsArray.length(); j++) {
                JSONObject eventObj = eventsArray.getJSONObject(j);
                FrameEvent event = parseFrameEvent(eventObj);
                events.add(event);
            }

            frame.setEvents(events);
            JSONObject participantFramesObj = frameObj.optJSONObject("participantFrames");
            if (participantFramesObj != null) {
                Map<Integer, ParticipantFrame> participantFrames = new HashMap<>();

                // 获取所有参与者ID
                Iterator<String> keys = participantFramesObj.keys();
                while (keys.hasNext()) {
                    String participantIdStr = keys.next();
                    try {
                        int participantId = Integer.parseInt(participantIdStr);
                        JSONObject frameData = participantFramesObj.getJSONObject(participantIdStr);

                        ParticipantFrame participantFrame = parseParticipantFrame(frameData);
                        participantFrame.setParticipantId(participantId);

                        participantFrames.put(participantId, participantFrame);
                    } catch (NumberFormatException e) {
                        Log.e("JsonParser", "无效的参与者ID: " + participantIdStr);
                    }
                }

                frame.setParticipantFrames(participantFrames);
            }

            frames.add(frame);
        }

        matchSummary.setFrames(frames);
        Log.d("JsonParser", "成功解析帧数: " + frames.size());
        if (participantMap != null) {

            Map<Integer, String> participants = participantMap.get(matchId);
            if (participants != null) {
                Log.d("JsonParser", "Participants found for matchId: " + matchId);
                for (Frame frame : matchSummary.getFrames()) {
                    for (FrameEvent event : frame.getEvents()) {
                        if (event.getParticipantId() != null) {
                            event.setParticipantName(participants.get(event.getParticipantId()));
                        }
                        if (event.getKillerId() != null) {
                            event.setKillerName(participants.get(event.getKillerId()));
                        }
                        if (event.getVictimId() != null) {
                            event.setVictimName(participants.get(event.getVictimId()));
                        }
                        if (event.getAssistingParticipantIds() != null) {
                            List<String> assistingNames = new ArrayList<>();
                            for (Integer id : event.getAssistingParticipantIds()) {
                                String name = participants.get(id);
                                if (name != null) {
                                    assistingNames.add(name);
                                }
                            }
                            event.setAssistingParticipantNames(assistingNames);
                        }
                    }
                }
            } else {
                Log.w("JsonParser", "No participants found for matchId: " + matchId);
            }
        } else {
            Log.w("JsonParser", "Participant map is null");
        }


        return matchSummary;
    }

    private static FrameEvent parseFrameEvent(JSONObject eventObj) throws JSONException {
        FrameEvent event = new FrameEvent();

        event.setType(eventObj.getString("type"));
        event.setTimestamp(eventObj.getInt("timestamp"));
        if (eventObj.has("realTimestamp")) {
            event.setRealTimestamp(eventObj.getInt("realTimestamp"));
        }
        if ("ELITE_MONSTER_KILL".equals(event.getType())) {
            if (eventObj.has("monsterType")) {
                event.setMonsterType(eventObj.getString("monsterType"));
            }
            if (eventObj.has("monsterSubType")) {
                event.setMonsterSubType(eventObj.getString("monsterSubType"));
            }
        }
        if (eventObj.has("position")) {
            JSONObject posObj = eventObj.getJSONObject("position");
            Position pos = new Position();
            pos.setX(posObj.getInt("x"));
            pos.setY(posObj.getInt("y"));
            event.setPosition(pos);
        }

        event.setParticipantId(getNullableInt(eventObj, "participantId"));

        event.setKillerId(getNullableInt(eventObj, "killerId"));
        event.setVictimId(getNullableInt(eventObj, "victimId"));
        event.setTeamId(getNullableInt(eventObj, "teamId"));
        event.setKillerTeamId(getNullableInt(eventObj, "killerTeamId"));

        if (eventObj.has("assistingParticipantIds")) {
            JSONArray assistsArray = eventObj.getJSONArray("assistingParticipantIds");
            List<Integer> assists = new ArrayList<>();
            for (int i = 0; i < assistsArray.length(); i++) {
                assists.add(assistsArray.getInt(i));
            }
            event.setAssistingParticipantIds(assists);
        }

        event.setBuildingType(eventObj.optString("buildingType", null));
        event.setLaneType(eventObj.optString("laneType", null));
        event.setTowerType(eventObj.optString("towerType", null));
        event.setWardType(eventObj.optString("wardType", null));
        event.setItemId(getNullableInt(eventObj, "itemId"));
        event.setSkillSlot(getNullableInt(eventObj, "skillSlot"));
        event.setLevelUpType(eventObj.optString("levelUpType", null));
        event.setBounty(getNullableInt(eventObj, "bounty"));
        event.setShutdownBounty(getNullableInt(eventObj, "shutdownBounty"));
        event.setKillType(eventObj.optString("killType", null));
        event.setKillStreakLength(getNullableInt(eventObj, "killStreakLength"));
        event.setMultiKillLength(getNullableInt(eventObj, "multiKillLength"));
        event.setGoldGain(getNullableInt(eventObj, "goldGain"));
        event.setLevel(getNullableInt(eventObj, "level"));
        event.setCreatorId(getNullableInt(eventObj, "creatorId"));
        event.setAfterId(getNullableInt(eventObj, "afterId"));
        event.setBeforeId(getNullableInt(eventObj, "beforeId"));
        event.setName(eventObj.optString("name", null));
        event.setGameId(eventObj.optLong("gameId", 0));


        if (eventObj.has("victimDamageDealt")) {
            event.setVictimDamageDealt(parseDamageData(eventObj.getJSONArray("victimDamageDealt")));
        }
        if (eventObj.has("victimDamageReceived")) {
            event.setVictimDamageReceived(parseDamageData(eventObj.getJSONArray("victimDamageReceived")));
        }

        return event;
    }
    private static ParticipantFrame parseParticipantFrame(JSONObject frameData) throws JSONException {
        ParticipantFrame frame = new ParticipantFrame();

        // 解析championStats
        JSONObject championStatsObj = frameData.optJSONObject("championStats");
        if (championStatsObj != null) {
            ChampionStats championStats = new ChampionStats();
            championStats.setAbilityHaste(championStatsObj.optInt("abilityHaste"));
            championStats.setAbilityPower(championStatsObj.optInt("abilityPower"));
            championStats.setArmor(championStatsObj.optInt("armor"));
            championStats.setArmorPen(championStatsObj.optInt("armorPen"));
            championStats.setArmorPenPercent(championStatsObj.optInt("armorPenPercent"));
            championStats.setAttackDamage(championStatsObj.optInt("attackDamage"));
            championStats.setAttackSpeed(championStatsObj.optInt("attackSpeed"));
            championStats.setBonusArmorPenPercent(championStatsObj.optInt("bonusArmorPenPercent"));
            championStats.setBonusMagicPenPercent(championStatsObj.optInt("bonusMagicPenPercent"));
            championStats.setCcReduction(championStatsObj.optInt("ccReduction"));
            championStats.setCooldownReduction(championStatsObj.optInt("cooldownReduction"));
            championStats.setHealth(championStatsObj.optInt("health"));
            championStats.setHealthMax(championStatsObj.optInt("healthMax"));
            championStats.setHealthRegen(championStatsObj.optInt("healthRegen"));
            championStats.setLifesteal(championStatsObj.optInt("lifesteal"));
            championStats.setMagicPen(championStatsObj.optInt("magicPen"));
            championStats.setMagicPenPercent(championStatsObj.optInt("magicPenPercent"));
            championStats.setMagicResist(championStatsObj.optInt("magicResist"));
            championStats.setMovementSpeed(championStatsObj.optInt("movementSpeed"));
            championStats.setOmnivamp(championStatsObj.optInt("omnivamp"));
            championStats.setPhysicalVamp(championStatsObj.optInt("physicalVamp"));
            championStats.setPower(championStatsObj.optInt("power"));
            championStats.setPowerMax(championStatsObj.optInt("powerMax"));
            championStats.setPowerRegen(championStatsObj.optInt("powerRegen"));
            championStats.setSpellVamp(championStatsObj.optInt("spellVamp"));

            frame.setChampionStats(championStats);
        }

        // 解析当前金币
        frame.setCurrentGold(frameData.optInt("currentGold"));

        // 解析伤害统计
        JSONObject damageStatsObj = frameData.optJSONObject("damageStats");
        if (damageStatsObj != null) {
            DamageStats damageStats = new DamageStats();
            damageStats.setMagicDamageDone(damageStatsObj.optInt("magicDamageDone"));
            damageStats.setMagicDamageDoneToChampions(damageStatsObj.optInt("magicDamageDoneToChampions"));
            damageStats.setMagicDamageTaken(damageStatsObj.optInt("magicDamageTaken"));
            damageStats.setPhysicalDamageDone(damageStatsObj.optInt("physicalDamageDone"));
            damageStats.setPhysicalDamageDoneToChampions(damageStatsObj.optInt("physicalDamageDoneToChampions"));
            damageStats.setPhysicalDamageTaken(damageStatsObj.optInt("physicalDamageTaken"));
            damageStats.setTotalDamageDone(damageStatsObj.optInt("totalDamageDone"));
            damageStats.setTotalDamageDoneToChampions(damageStatsObj.optInt("totalDamageDoneToChampions"));
            damageStats.setTotalDamageTaken(damageStatsObj.optInt("totalDamageTaken"));
            damageStats.setTrueDamageDone(damageStatsObj.optInt("trueDamageDone"));
            damageStats.setTrueDamageDoneToChampions(damageStatsObj.optInt("trueDamageDoneToChampions"));
            damageStats.setTrueDamageTaken(damageStatsObj.optInt("trueDamageTaken"));

            frame.setDamageStats(damageStats);
        }

        // 解析其他属性
        frame.setGoldPerSecond(frameData.optInt("goldPerSecond"));
        frame.setJungleMinionsKilled(frameData.optInt("jungleMinionsKilled"));
        frame.setLevel(frameData.optInt("level"));
        frame.setMinionsKilled(frameData.optInt("minionsKilled"));
        frame.setParticipantId(frameData.optInt("participantId"));

        // 解析位置
        JSONObject positionObj = frameData.optJSONObject("position");
        if (positionObj != null) {
            Position position = new Position();
            position.setX(positionObj.optInt("x"));
            position.setY(positionObj.optInt("y"));
            frame.setPosition(position);
        }

        frame.setTimeEnemySpentControlled(frameData.optInt("timeEnemySpentControlled"));
        frame.setTotalGold(frameData.optInt("totalGold"));
        frame.setXp(frameData.optInt("xp"));

        return frame;
    }
    private static List<DamageData> parseDamageData(JSONArray damageArray) throws JSONException {
        List<DamageData> damageList = new ArrayList<>();

        for (int i = 0; i < damageArray.length(); i++) {
            JSONObject damageObj = damageArray.getJSONObject(i);
            DamageData damage = new DamageData();

            damage.setBasic(damageObj.optBoolean("basic", false));
            damage.setMagicDamage(damageObj.optInt("magicDamage", 0));
            damage.setPhysicalDamage(damageObj.optInt("physicalDamage", 0));
            damage.setTrueDamage(damageObj.optInt("trueDamage", 0));
            damage.setName(damageObj.optString("name", null));
            damage.setParticipantId(getNullableInt(damageObj, "participantId"));
            damage.setSpellName(damageObj.optString("spellName", null));
            damage.setSpellSlot(getNullableInt(damageObj, "spellSlot"));
            damage.setType(damageObj.optString("type", null));

            damageList.add(damage);
        }

        return damageList;
    }

    private static Integer getNullableInt(JSONObject obj, String key) throws JSONException {
        return obj.has(key) && !obj.isNull(key) ? obj.getInt(key) : null;
    }
}