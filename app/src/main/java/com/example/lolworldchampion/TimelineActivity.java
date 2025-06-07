package com.example.lolworldchampion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private static final String MATCH_SUMMARY = "match_summary";

    public static void startActivity(Context context, String matchJson) {
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.putExtra("match_json", matchJson);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        String json = getIntent().getStringExtra("match_json");
        MatchSummary matchSummary = new Gson().fromJson(json, MatchSummary.class);
        if (matchSummary == null) {
            showErrorAndFinish("No match summary specified");
            return;
        }

        setupRecyclerView(matchSummary);
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
        String matchId = matchSummary.getMatchId(); // 获取 matchId
        recyclerView.setAdapter(new TimelineAdapter(allEvents, this, matchId)); // 传递 matchId
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}