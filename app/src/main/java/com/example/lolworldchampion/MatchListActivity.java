package com.example.lolworldchampion;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lolworldchampion.MatchAdapter;
import com.example.lolworldchampion.MatchSummary;
import com.example.lolworldchampion.CSVParser;
import com.example.lolworldchampion.JsonParser;
import com.example.lolworldchampion.ParticipantCSVParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchListActivity extends AppCompatActivity implements MatchAdapter.OnMatchClickListener {
    private List<MatchSummary> matchSummaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        // 解析 participantname.csv 文件
        Map<String, Map<Integer, String>> participantMap = null;
        try {
            // 直接传递 Context 对象给 parseParticipantCSV 方法
            participantMap = ParticipantCSVParser.parseParticipantCSV(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error reading participant CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // 加载CSV数据
        loadCSVData(participantMap);

        // 设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.matchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MatchAdapter(matchSummaries, this));
    }

    private void loadCSVData(Map<String, Map<Integer, String>> participantMap) {
        try (InputStream inputStream = getResources().openRawResource(R.raw.match_info)) {
            matchSummaries = CSVParser.parseMatchSummaries(inputStream);
            if (matchSummaries == null || matchSummaries.isEmpty()) {
                Toast.makeText(this, "Failed to load match data", Toast.LENGTH_SHORT).show();
            } else {
                // 不需要在这里加载JSON，只在用户点击时加载
                Log.d("MatchListActivity", "Loaded " + matchSummaries.size() + " matches from CSV");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading match data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            matchSummaries = new ArrayList<>(); // prevent NPE
        }
    }

    @Override
    public void onMatchClick(String jsonFileName) {
        // 直接传递文件名（不含扩展名）
        TimelineActivity.startActivity(this, jsonFileName);
    }
}