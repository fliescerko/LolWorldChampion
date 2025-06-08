package com.example.lolworldchampion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lolworldchampion.R;
import com.example.lolworldchampion.FrameEvent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    private List<FrameEvent> events;
    private Context context;
    private List<FavoriteEvent> favoriteEvents ;
    private FavoriteListener favoriteListener;
    private String matchId;

    public interface FavoriteListener {
        void onFavoritesUpdated();
    }

    public TimelineAdapter(List<FrameEvent> events, Context context, String matchId) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.events = events;
        this.context = context;
        this.matchId = matchId;
        this.favoriteEvents = LocalStorageUtil.loadFavoriteEvents(context);
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView eventTime;
        TextView eventType;
        TextView eventDetails;
        ImageView eventIcon;
        EditText noteEditText;
        Button favoriteButton;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventType = itemView.findViewById(R.id.eventType);
            eventDetails = itemView.findViewById(R.id.eventDetails);
            eventIcon = itemView.findViewById(R.id.event_icon);
            noteEditText = itemView.findViewById(R.id.noteEditText);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        FrameEvent event = events.get(position);
        holder.eventTime.setText(formatTimestamp(event.getTimestamp()));
        String rawEventType = event.getType(); // 原始事件类型（英文）
        String displayEventType = EventTypeMapper.getDisplayText(rawEventType);
        holder.eventType.setText(displayEventType);
        // 在 onBindViewHolder 开头添加日志
        Log.d("EventDebug", "事件数据 - " +
                "Killer: " + event.getKillerName() + " (" + event.getKillerId() + "), " +
                "Victim: " + event.getVictimName() + " (" + event.getVictimId() + "), " +
                "Assists: " + event.getAssistingParticipantNames());
        // 根据事件类型设置不同的图标
        int iconResId = R.drawable.ic_launcher_background; // 默认图标
        String iconPath = "default_icon.svg";
        switch (displayEventType) {
            case "野怪击杀":
                if ("ELITE_MONSTER_KILL".equals(event.getType())) {
                    String monsterType = event.getMonsterType();
                    String monsterSubtype = event.getMonsterSubType();
                    if ("RIFTHERALD".equals(monsterType)) {
                        iconResId = R.drawable.icon_riftherald; // 先锋图标（VectorDrawable）
                    } else if ("BARON_NASHOR".equals(monsterType)) {
                        iconResId = R.drawable.icon_baron_nashor; // 大龙图标（VectorDrawable）
                    } else if ("DRAGON".equals(monsterType)) {
                        if ("WATER_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_water_dragon; // 水龙图标（VectorDrawable）
                        } else if ("FIRE_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_fire_dragon; // 火龙图标（VectorDrawable）
                        } else if ("HEXTECH_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_hextech_dragon;
                        } else if ("ELECTRIC_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_earth_dragon;
                        } else if ("CHEMTECH_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_chemtech_dragon;
                        } else if ("AIR_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_air_dragon;
                        } else if ("ELDER_DRAGON".equals(monsterSubtype)) {
                            iconResId = R.drawable.icon_elder_dragon;
                        }
                    }
                }
                break;
            case "防御塔击杀":
            case "镀层获取":
                iconResId = R.drawable.ic_building_kill;
                break;
            case "英雄击杀":

                iconResId = R.drawable.ic_kills;
                break;
            case "龙魂获取时间点":
                iconResId = R.drawable.ic_monster_kill;
                break;
            default:
                iconResId = R.drawable.ic_launcher_foreground;
                break;
        }
        holder.eventIcon.setImageResource(iconResId);

        // 构建更清晰的事件详情字符串
        // 替换原有的StringBuilder逻辑
        SpannableStringBuilder details = new SpannableStringBuilder();

        // 击杀者信息
        if (event.getKillerName() != null || event.getKillerId() != null) {
            details.append("击杀者: ")
                    .append(event.getKillerName() != null ? event.getKillerName() : "")
                    .append(event.getKillerId() != null ? "(ID:" + event.getKillerId() + ")" : "");
        }

        // 受害者信息
        if (event.getVictimName() != null || event.getVictimId() != null) {
            if (details.length() > 0) details.append("\n");
            details.append("击败: ")
                    .append(event.getVictimName() != null ? event.getVictimName() : "")
                    .append(event.getVictimId() != null ? "(ID:" + event.getVictimId() + ")" : "");
        }

        // 助攻信息
        if (event.getAssistingParticipantNames() != null && !event.getAssistingParticipantNames().isEmpty()) {
            if (details.length() > 0) details.append("\n");
            details.append("助攻: ").append(TextUtils.join(", ", event.getAssistingParticipantNames()));
        }

        // 设置文本（确保非空）
        holder.eventDetails.setText(details.length() > 0 ? details : "无详细信息");

        holder.itemView.setOnClickListener(v -> {
            if (context != null) {
                Log.d("TimelineAdapter", "点击事件触发，实际事件类型: " + event.getType());
                // 根据事件类型跳转到不同的详情页
                if ("ELITE_MONSTER_KILL".equals(event.getType())) {
                    // 野怪击杀事件跳转到野怪详情页
                    Intent monsterIntent = new Intent(context, MonsterDetailsActivity.class);
                    monsterIntent.putExtra(MonsterDetailsActivity.EXTRA_MONSTER_TYPE, event.getMonsterType());
                    monsterIntent.putExtra(MonsterDetailsActivity.EXTRA_MONSTER_SUBTYPE, event.getMonsterSubType());
                    // 确保位置信息被传递
                    if (event.getPosition() != null) {
                        monsterIntent.putExtra(MonsterDetailsActivity.EXTRA_POSITION, event.getPosition());
                    }
                    context.startActivity(monsterIntent);
                } else if ("BUILDING_KILL".equals(event.getType()) ||
                        "TURRET_PLATE_DESTROYED".equals(event.getType())) {
                    Log.d("TimelineAdapter", "进入建筑相关事件跳转逻辑");
                    Intent buildingIntent = new Intent(context, BuildingDetailsActivity.class);
                    buildingIntent.putExtra(BuildingDetailsActivity.EXTRA_EVENT, event); // event 是 FrameEvent 对象
                    context.startActivity(buildingIntent);
                } else {
                    // 假设英雄伤害相关事件类型为 "CHAMPION_DAMAGE"
                    if ("CHAMPION_KILL".equals(event.getType())) {
                        Log.d("TimelineAdapter", "进入英雄伤害事件跳转逻辑");
                        if (event.getVictimDamageDealt() != null && !event.getVictimDamageDealt().isEmpty()) {
                            Log.d("TimelineAdapter", "英雄伤害数据大小: " + event.getVictimDamageDealt().size());
                            ArrayList<DamageData> damageList = new ArrayList<>(event.getVictimDamageDealt());
                            Intent intent = new Intent(context, DamageDetailsActivity.class);
                            intent.putExtra(DamageDetailsActivity.EXTRA_DAMAGE_DATA_LIST, damageList);
                            // 传递位置信息
                            if (event.getPosition() != null) {
                                intent.putExtra(DamageDetailsActivity.EXTRA_POSITION, event.getPosition());
                            }
                            context.startActivity(intent);
                        } else {
                            Log.w("TimelineAdapter", "英雄伤害事件无伤害数据可传递");
                            Toast.makeText(context, "该英雄伤害事件没有伤害数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("TimelineAdapter", "未知事件类型，未进行跳转: " + event.getType());
                    }
                }
            } else {
                Log.e("TimelineAdapter", "Context 为空！");
            }

        });
        holder.eventDetails.setText(details.length() > 0 ? details : "无详细信息");
        holder.favoriteButton.setOnClickListener(v -> {
            String note = holder.noteEditText.getText().toString();
            FavoriteEvent favoriteEvent = new FavoriteEvent(event, note);
            favoriteEvent.setMatchId(matchId);
            favoriteEvents.add(favoriteEvent);
            saveFavoriteEventsToFile();
            if (favoriteListener != null) {
                favoriteListener.onFavoritesUpdated();
            }
            Toast.makeText(context, "事件已收藏", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private String formatTimestamp(long timestamp) {
        long seconds = timestamp / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void saveFavoriteEventsToFile() {
        LocalStorageUtil.saveFavoriteEvents(context, favoriteEvents);
    }

    public void setFavoriteListener(FavoriteListener listener) {
        this.favoriteListener = listener;
    }
}