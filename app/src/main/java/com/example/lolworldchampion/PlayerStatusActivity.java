package com.example.lolworldchampion;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStatusActivity extends AppCompatActivity {

    private static final int SAMPLING_INTERVAL = 5; // 每5帧采样一次
    private Map<String, Map<Integer, String>> participantMap;
    private MatchSummary matchSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_status);
        participantMap = ParticipantCSVParser.parseParticipantCSV(this);
        // 1. 获取MatchSummary对象
        matchSummary = (MatchSummary) getIntent().getSerializableExtra("match_summary");
        if (matchSummary == null) {
            Log.e("PlayerStatus", "MatchSummary为空");
            Toast.makeText(this, "数据错误: MatchSummary为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("PlayerStatus", "收到比赛: " + matchSummary.getMatchId());
        if (matchSummary.getMatchId() == null) {
            Log.e("PlayerStatus", "MatchId为空，尝试从Intent获取");
            // 尝试从Intent直接获取matchId
            String matchId = getIntent().getStringExtra("match_id");
            if (matchId != null) {
                matchSummary.setMatchId(matchId);
            } else {
                Toast.makeText(this, "数据错误: 比赛ID为空", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        // 2. 检查frames数据
        List<Frame> frames = matchSummary.getFrames();
        if (frames == null || frames.isEmpty()) {
            Log.e("PlayerStatus", "无时间线数据");
            Toast.makeText(this, "无时间线数据", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("PlayerStatus", "总帧数: " + frames.size());

        // 3. 采样关键帧的participantFrames
        List<Map<Integer, ParticipantFrame>> sampledFrames = sampleFrames(frames);
        if (sampledFrames.isEmpty()) {
            Log.e("PlayerStatus", "无法获取任何帧的选手数据");
            Toast.makeText(this, "无选手数据", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PlayerStatus", "成功采样 " + sampledFrames.size() + " 个时间点的数据");
        showPlayerStatuses(sampledFrames);
    }

    /**
     * 采样关键帧数据
     */
    private List<Map<Integer, ParticipantFrame>> sampleFrames(List<Frame> allFrames) {
        List<Map<Integer, ParticipantFrame>> sampledFrames = new ArrayList<>();

        // 确定采样间隔
        int interval = Math.max(1, SAMPLING_INTERVAL); // 确保至少每1帧采样一次

        // 采样每5帧的数据
        for (int i = 0; i < allFrames.size(); i += interval) {
            Frame frame = allFrames.get(i);
            Map<Integer, ParticipantFrame> participantFrames = frame.getParticipantFrames();

            if (participantFrames != null && !participantFrames.isEmpty()) {
                // 添加时间戳信息
                Map<Integer, ParticipantFrame> timestampedFrames = new HashMap<>(participantFrames);
                sampledFrames.add(timestampedFrames);
                Log.d("PlayerStatus", "采样第 " + i + " 帧的数据，包含 " + participantFrames.size() + " 个选手");
            } else {
                Log.w("PlayerStatus", "第 " + i + " 帧没有有效的participantFrames数据");
            }
        }

        // 确保包含最后一帧
        if (!allFrames.isEmpty() && (sampledFrames.isEmpty() || !allFrames.get(allFrames.size() - 1).equals(allFrames.get(sampledFrames.size() - 1)))) {
            Frame lastFrame = allFrames.get(allFrames.size() - 1);
            Map<Integer, ParticipantFrame> lastParticipantFrames = lastFrame.getParticipantFrames();
            if (lastParticipantFrames != null && !lastParticipantFrames.isEmpty()) {
                sampledFrames.add(lastParticipantFrames);
                Log.d("PlayerStatus", "添加最后一帧(" + (allFrames.size() - 1) + ")的数据");
            }
        }

        return sampledFrames;
    }

    private void showPlayerStatuses(List<Map<Integer, ParticipantFrame>> allSampledFrames) {
        LinearLayout container = findViewById(R.id.player_status_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        String matchId = matchSummary.getMatchId();
        Map<Integer, String> currentMatchParticipants = participantMap.get(matchId);

        if (currentMatchParticipants == null) {
            Log.w("PlayerStatus", "找不到该比赛的选手映射数据");
        } else {
            Log.d("PlayerStatus",   matchSummary.getMatchId() + " 比赛的选手映射: " + currentMatchParticipants);
        }
        // 为每个采样帧创建一个时间点视图
        for (int frameIndex = 0; frameIndex < allSampledFrames.size(); frameIndex++) {
            Map<Integer, ParticipantFrame> participantFrames = allSampledFrames.get(frameIndex);

            // 添加时间点标题
            View timePointHeader = inflater.inflate(R.layout.item_time_point_header, container, false);
            TextView timePointText = timePointHeader.findViewById(R.id.time_point_text);
            timePointText.setText("时间点:" + ((frameIndex + 1)*5 -5)+"分钟");
            container.addView(timePointHeader);

            // 显示该时间点的所有选手数据
            for (Map.Entry<Integer, ParticipantFrame> entry : participantFrames.entrySet()) {
                ParticipantFrame frame = entry.getValue();
                View playerStatusView = inflater.inflate(R.layout.item_player_status, container, false);

                TextView playerIdText = playerStatusView.findViewById(R.id.player_id_text);
                TextView levelText = playerStatusView.findViewById(R.id.level_text);
                TextView goldText = playerStatusView.findViewById(R.id.gold_text);
                TextView healthText = playerStatusView.findViewById(R.id.health_text);
                TextView attackDamageText = playerStatusView.findViewById(R.id.attack_damage_text);
                TextView abilityPowerText = playerStatusView.findViewById(R.id.ability_power_text);
                TextView armorText = playerStatusView.findViewById(R.id.armor_text);
                TextView magicResistText = playerStatusView.findViewById(R.id.magic_resist_text);

                // 在 showPlayerStatuses() 中修改选手名称获取逻辑
                String playerName = "未知选手";

                if (currentMatchParticipants != null) {
                    // 确保 participantId 是 Integer（如果是 String 则用 Integer.parseInt() 转换）
                    Integer participantId = frame.getParticipantId();
                    playerName = currentMatchParticipants.getOrDefault(participantId, "未知选手");
                    Log.d("PlayerStatus", "选手 ID: " + participantId + ", 名称: " + playerName);
                }

                playerIdText.setText("选手: " + playerName + " (ID:" + frame.getParticipantId() + ")");
                levelText.setText("等级: " + frame.getLevel());
                goldText.setText("当前金币: " + frame.getCurrentGold());

                // 显示冠军属性
                ChampionStats stats = frame.getChampionStats();
                if (stats != null) {
                    // 确保将数值转换为字符串
                    healthText.setText("生命值: " + stats.getHealth());
                    attackDamageText.setText("攻击力: " + stats.getAttackDamage());
                    abilityPowerText.setText("法术强度: " + stats.getAbilityPower());
                    armorText.setText("护甲: " + stats.getArmor());
                    magicResistText.setText("魔抗: " + stats.getMagicResist());
                } else {
                    healthText.setText("生命值: N/A");
                    attackDamageText.setText("攻击力: N/A");
                    abilityPowerText.setText("法术强度: N/A");
                    armorText.setText("护甲: N/A");
                    magicResistText.setText("魔抗: N/A");
                }

                container.addView(playerStatusView);
            }
        }
    }
    }
