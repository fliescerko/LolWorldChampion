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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // 1. 检查Intent
        if (getIntent() == null) {
            showErrorAndFinish("No intent provided");
            return;
        }

        // 2. 获取MatchSummary对象
        MatchSummary matchSummary;
        try {
            matchSummary = (MatchSummary) getIntent().getSerializableExtra("match_summary");
        } catch (ClassCastException e) {
            showErrorAndFinish("Invalid data format");
            return;
        }

        // 3. 验证数据
        if (matchSummary == null) {
            showErrorAndFinish("No match summary provided");
            return;
        }

        Log.d(TAG, "Received match ID: " + matchSummary.getMatchId());
        Log.d(TAG, "Frame count: " + (matchSummary.getFrames() != null ? matchSummary.getFrames().size() : "null"));

        setupRecyclerView(matchSummary);
    }

    private void setupRecyclerView(MatchSummary matchSummary) {
        // 验证frames数据
        if (matchSummary.getFrames() == null) {
            showErrorAndFinish("Frames data is null");
            return;
        }

        if (matchSummary.getFrames().isEmpty()) {
            showErrorAndFinish("Frames data is empty");
            return;
        }

        // 打印详细帧信息
        for (int i = 0; i < Math.min(5, matchSummary.getFrames().size()); i++) {
            Frame frame = matchSummary.getFrames().get(i);
            Log.d(TAG, String.format("Frame %d: %d events", i,
                    frame.getEvents() != null ? frame.getEvents().size() : 0));
        }

        // 合并所有事件
        List<FrameEvent> allEvents = new ArrayList<>();
        for (Frame frame : matchSummary.getFrames()) {
            if (frame.getEvents() != null) {
                allEvents.addAll(frame.getEvents());
            }
        }

        Log.d(TAG, "Total events before filtering: " + allEvents.size());

        // 过滤事件
        List<String> allowedTypes = Arrays.asList(
                "CHAMPION_KILL",
                "ELITE_MONSTER_KILL",
                "TURRET_PLATE_DESTROYED",
                "BUILDING_KILL",
                "DRAGON_SOUL_GIVEN"
        );

        List<FrameEvent> filteredEvents = new ArrayList<>();
        for (FrameEvent event : allEvents) {
            if (event != null && event.getType() != null &&
                    allowedTypes.contains(event.getType())) {
                filteredEvents.add(event);
            }
        }

        Log.d(TAG, "Total events after filtering: " + filteredEvents.size());

        if (filteredEvents.isEmpty()) {
            showErrorAndFinish("No events after filtering");
            return;
        }

        // 设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.timelineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TimelineAdapter(filteredEvents, this, matchSummary.getMatchId()));
    }

    private void showErrorAndFinish(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}