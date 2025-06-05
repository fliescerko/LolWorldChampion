package com.example.lolworldchampion;

import java.util.HashMap;
import java.util.Map;

public class EventTypeMapper {
    private static final Map<String, String> EVENT_TYPE_MAP = new HashMap<>();

    static {
        EVENT_TYPE_MAP.put("CHAMPION_KILL", "英雄击杀");
        EVENT_TYPE_MAP.put("CHAMPION_SPECIAL_KILL", "单杀");
        EVENT_TYPE_MAP.put("ELITE_MONSTER_KILL", "野怪击杀");
        EVENT_TYPE_MAP.put("TURRET_PLATE_DESTROYED", "镀层获取");
        EVENT_TYPE_MAP.put("BUILDING_KILL", "防御塔击杀");
        EVENT_TYPE_MAP.put("DRAGON_SOUL_GIVEN", "龙魂获取时间点");

    }


    public static String getDisplayText(String eventType) {
        if (eventType == null) return "未知事件";
        return EVENT_TYPE_MAP.getOrDefault(eventType, eventType);
    }
}