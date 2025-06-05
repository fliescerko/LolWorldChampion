package com.example.lolworldchampion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private static final String JSON_FILE_NAME = "json_file_name";
    private static final String MATCHES_DIR = "matches/"; // 资产子目录
    private static final String FILE_SUFFIX = ".json";    // 统一文件扩展名

    public static void startActivity(AppCompatActivity activity, String jsonFileName) {
        Intent intent = new Intent(activity, TimelineActivity.class);
        intent.putExtra(JSON_FILE_NAME, jsonFileName); // 不包含.json后缀
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        String jsonFileName = getIntent().getStringExtra(JSON_FILE_NAME);
        if (jsonFileName == null || jsonFileName.isEmpty()) {
            showErrorAndFinish("No JSON file specified");
            return;
        }

        // 解析参与者 CSV 文件
        Map<String, Map<Integer, String>> participantMap = ParticipantCSVParser.parseParticipantCSV(this);

        // 加载并验证JSON数据
        MatchSummary matchSummary = loadMatchSummaryFromJson(jsonFileName, participantMap);
        if (matchSummary == null) {
            showErrorAndFinish("Failed to load match data");
            return;
        }

        setupRecyclerView(matchSummary);
    }

    private MatchSummary loadMatchSummaryFromJson(String baseFileName, Map<String, Map<Integer, String>> participantMap) {
        // 构建完整资产路径：matches/ + 小写文件名 + .json
        String assetPath = MATCHES_DIR + baseFileName.toLowerCase() + FILE_SUFFIX;

        // 提取 matchId (与baseFileName相同，已移除路径和扩展名)
        String matchId = baseFileName.toLowerCase();
        Log.d(TAG, "Extracted matchId: " + matchId + " from baseFileName: " + baseFileName);

        // 打印调试信息
        Log.d(TAG, "Looking for participant names in matchId: " + matchId);
        if (participantMap.containsKey(matchId)) {
            Log.d(TAG, "Found participants: " + participantMap.get(matchId));
        } else {
            Log.d(TAG, "No participants found for this matchId!");
        }

        try (InputStream is = getAssets().open(assetPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            Log.d(TAG, "Loading JSON file: " + assetPath);

            // 读取文件内容
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            String jsonString = jsonBuilder.toString();
            Log.d(TAG, "JSON content: " + jsonString);

            // 解析JSON，传递matchId
            MatchSummary matchSummary = JsonParser.parseMatchJson(jsonString, participantMap,matchId );

            // 验证matchId是否正确设置
            Log.d(TAG, "Parsed matchId: " + matchSummary.getMatchId());

            return matchSummary;
        } catch (IOException e) {
            Log.e(TAG, "File not found: " + assetPath, e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
            return null;
        }


    }
    private String readJsonFile(String assetPath) throws IOException {
        InputStream is = getAssets().open(assetPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        reader.close();
        return jsonBuilder.toString();
    }

    private void setupRecyclerView(MatchSummary matchSummary) {
        if (matchSummary.getFrames() == null || matchSummary.getFrames().isEmpty()) {
            showErrorAndFinish("No timeline data available");
            return;
        }
        List<String> allowedTypes = Arrays.asList(
                "CHAMPION_KILL",
                "CHAMPION_SPECIAL_KILL",
                "ELITE_MONSTER_KILL",
                "TURRET_PLATE_DESTROYED",
                "BUILDING_KILL",
                "DRAGON_SOUL_GIVEN"
        );
        // 合并所有帧的事件
        List<FrameEvent> allEvents = new ArrayList<>();
        for (Frame frame : matchSummary.getFrames()) {
            if (frame.getEvents() != null) {
                for (FrameEvent event : frame.getEvents()) {
                    if (allowedTypes.contains(event.getType())) {
                        allEvents.add(event);
                    }
                }
            }
        }

        // 初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.timelineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TimelineAdapter(allEvents, this));
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}