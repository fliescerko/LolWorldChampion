package com.example.lolworldchampion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChoiceActivity extends AppCompatActivity {
    private String matchId;
    private ProgressBar progressBar;
    private OkHttpClient httpClient;
    private Map<String, Map<Integer, String>> participantMap;
    private MatchSummary matchSummary;
    private Button btnPlayerStatus;
    private Button btnTimelineEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        // 初始化视图
        initViews();

        // 初始化HTTP客户端
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // 获取基本比赛信息
        matchId = getIntent().getStringExtra("match_id");
        if (matchId == null) {
            showErrorAndFinish("比赛ID无效");
            return;
        }

        // 显示加载指示器并加载数据
        progressBar.setVisibility(View.VISIBLE);
        loadMatchData();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        btnPlayerStatus = findViewById(R.id.btn_player_status);
        btnTimelineEvent = findViewById(R.id.btn_timeline_event);

        // 初始禁用按钮
        setButtonsEnabled(false);

        // 设置点击监听器
        btnPlayerStatus.setOnClickListener(v -> {
            if (matchSummary != null) {
                Log.d("ChoiceActivity", "跳转到PlayerStatusActivity");
                Intent intent = new Intent(this, PlayerStatusActivity.class);
                intent.putExtra("match_summary", matchSummary);
                startActivity(intent);
            } else {
                Toast.makeText(this, "数据尚未加载完成", Toast.LENGTH_SHORT).show();
            }
        });

        btnTimelineEvent.setOnClickListener(v -> {
            if (matchSummary != null) {
                Log.d("ChoiceActivity", "跳转到TimelineActivity");
                Intent intent = new Intent(this, TimelineActivity.class);
                intent.putExtra("match_summary", matchSummary);
                startActivity(intent);
            } else {
                Toast.makeText(this, "数据尚未加载完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnPlayerStatus.setEnabled(enabled);
        btnTimelineEvent.setEnabled(enabled);
    }

    private void loadMatchData() {
        new Thread(() -> {
            try {
                // 1. 加载参与者数据
                participantMap = ParticipantCSVParser.parseParticipantCSV(this);

                // 2. 从网络获取时间线数据
                String url = "https://lol.fandom.com/wiki/V5_data:" + matchId + "/Timeline?action=edit";
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "Mozilla/5.0")
                        .build();

                Response response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String html = response.body().string();
                String jsonData = extractJsonFromHtml(html);
                if (jsonData == null) {
                    throw new IOException("No JSON data found");
                }

                // 3. 解析数据
                matchSummary = JsonParser.parseMatchJson(jsonData, participantMap, matchId);
                if (matchSummary != null) {
                    matchSummary.setMatchId(matchId);  // 明确设置matchId
                }
                // 设置从Intent获取的基本信息
                runOnUiThread(() -> {
                    if (getIntent() != null) {
                        matchSummary.setBlueTeamFullName(getIntent().getStringExtra("blue_team"));
                        matchSummary.setRedTeamFullName(getIntent().getStringExtra("red_team"));
                        matchSummary.setGame(getIntent().getStringExtra("game"));
                        matchSummary.setStartTime(getIntent().getStringExtra("start_time"));
                    }

                    progressBar.setVisibility(View.GONE);
                    setButtonsEnabled(true);
                    Toast.makeText(ChoiceActivity.this, "数据加载完成", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("ChoiceActivity", "加载数据失败", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChoiceActivity.this, "加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
    private String extractJsonFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }

        // 查找 <textarea> 标签
        int start = html.indexOf("<textarea");
        if (start == -1) {
            Log.e("ChoiceActivity", "No textarea tag found");
            return null;
        }

        // 找到 textarea 内容开始位置
        start = html.indexOf(">", start);
        if (start == -1) {
            Log.e("ChoiceActivity", "Invalid textarea tag");
            return null;
        }
        start++; // 跳过 '>' 字符

        // 找到 textarea 结束标签
        int end = html.indexOf("</textarea>", start);
        if (end == -1) {
            Log.e("ChoiceActivity", "No closing textarea tag found");
            return null;
        }

        // 提取并清理内容
        String content = html.substring(start, end).trim();

        // 去除可能的HTML实体编码
        content = content.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");

        // 验证内容是否是有效的JSON
        try {
            // 简单的JSON验证 - 检查是否以 { 开头
            if (!content.startsWith("{") && !content.startsWith("[")) {
                Log.e("ChoiceActivity", "Extracted content is not JSON");
                return null;
            }
        } catch (Exception e) {
            Log.e("ChoiceActivity", "JSON validation failed", e);
            return null;
        }

        return content;

    }

    private void initButtons() {
        Button btnPlayerStatus = findViewById(R.id.btn_player_status);
        btnPlayerStatus.setOnClickListener(v -> {
            if (matchSummary != null) {
                Log.d("ChoiceActivity", "跳转到PlayerStatusActivity");
                Intent intent = new Intent(this, PlayerStatusActivity.class);
                intent.putExtra("match_summary", matchSummary);
                intent.putExtra("match_id", matchId);  // 额外传递matchId
                startActivity(intent);
            } else {
                Toast.makeText(this, "数据尚未加载完成", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnTimelineEvent = findViewById(R.id.btn_timeline_event);
        btnTimelineEvent.setOnClickListener(v -> {
            if (matchSummary != null) {
                Intent intent = new Intent(ChoiceActivity.this, TimelineActivity.class);
                intent.putExtra("match_summary", matchSummary);
                startActivity(intent);
            }
        });
    }
}