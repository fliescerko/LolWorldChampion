package com.example.lolworldchampion;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MatchListActivity extends AppCompatActivity implements MatchAdapter.OnMatchClickListener {
    private static final String TAG = "MatchListActivity";
    private static final String WIKI_BASE_URL = "https://lol.fandom.com/wiki/V5_data:";
    private static final String URL_SUFFIX = "/Timeline?action=edit";
    private static final int MAX_RETRIES = 3;
    private static final int REQUEST_DELAY_MS = 1500;
    private final Gson gson = new Gson();
    private List<MatchSummary> matchSummaries;
    private MatchAdapter adapter;
    private ExecutorService executorService;
    private Map<String, Map<Integer, String>> participantMap;
    private ProgressBar progressBar;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        initViews();
        initHttpClient();
        checkNetworkAndLoadData();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.matchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchAdapter(new ArrayList<>(), this); // 初始化为空列表
        recyclerView.setAdapter(adapter);
    }

    private void initHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

    private void checkNetworkAndLoadData() {
        if (!isNetworkAvailable()) {
            showToast("请检查网络连接");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        executorService = Executors.newFixedThreadPool(4);
        loadInitialData();
    }

    private void loadInitialData() {
        executorService.execute(() -> {
            try {
                // 1. 加载参与者数据
                participantMap = ParticipantCSVParser.parseParticipantCSV(this);

                // 2. 加载比赛基础信息
                try (InputStream inputStream = getResources().openRawResource(R.raw.match_info)) {
                    List<MatchSummary> summaries = CSVParser.parseMatchSummaries(inputStream);

                    runOnUiThread(() -> {
                        if (summaries == null || summaries.isEmpty()) {
                            showToast("未找到比赛数据");
                            return;
                        }

                        matchSummaries = summaries;
                        adapter.updateData(matchSummaries);
                        fetchTimelineData();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "初始化数据加载失败", e);
                showToast("数据加载失败: " + e.getMessage());
            }
        });
    }

    private void fetchTimelineData() {
        for (int i = 0; i < matchSummaries.size(); i++) {
            final int position = i;
            mainHandler.postDelayed(() ->
                            fetchMatchWithRetry(matchSummaries.get(position), position, MAX_RETRIES),
                    i * REQUEST_DELAY_MS
            );
        }
    }

    private void fetchMatchWithRetry(MatchSummary summary, int position, int retriesLeft) {
        String url = WIKI_BASE_URL + summary.getMatchId() + URL_SUFFIX;
        Log.d(TAG, "请求URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "text/html")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "请求失败: " + summary.getMatchId(), e);
                if (retriesLeft > 0) {
                    mainHandler.postDelayed(() ->
                                    fetchMatchWithRetry(summary, position, retriesLeft - 1),
                            2000
                    );
                } else {
                    showToast(summary.getMatchId() + " 数据加载失败");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP错误: " + response.code() + " - " + summary.getMatchId());
                    if (retriesLeft > 0) {
                        mainHandler.postDelayed(() ->
                                        fetchMatchWithRetry(summary, position, retriesLeft - 1),
                                2000
                        );
                    }
                    return;
                }

                try {
                    String html = response.body().string();
                    processResponse(html, summary, position);
                } catch (Exception e) {
                    Log.e(TAG, "数据处理错误: " + summary.getMatchId(), e);
                }
            }
        });
    }

    private void processResponse(String html, MatchSummary summary, int position) {
        try {
            String jsonData = extractJsonFromHtml(html);
            if (jsonData == null) {
                Log.e(TAG, "未找到JSON数据: " + summary.getMatchId());
                return;
            }

            MatchSummary detailedSummary = JsonParser.parseMatchJson(
                    jsonData, participantMap, summary.getMatchId());

            // 保留基础信息
            detailedSummary.setMatchId(summary.getMatchId());
            detailedSummary.setGame(summary.getGame());
            detailedSummary.setStartTime(summary.getStartTime());
            detailedSummary.setBlueTeamFullName(summary.getBlueTeamFullName());
            detailedSummary.setRedTeamFullName(summary.getRedTeamFullName());
            detailedSummary.setWinningTeam(summary.getWinningTeam());

            updateUI(position, detailedSummary);
        } catch (Exception e) {
            Log.e(TAG, "JSON解析错误: " + summary.getMatchId(), e);
        }
    }

    private String extractJsonFromHtml(String html) {
        int start = html.indexOf("<textarea");
        if (start == -1) return null;

        start = html.indexOf(">", start);
        if (start == -1) return null;
        start++;

        int end = html.indexOf("</textarea>", start);
        if (end == -1) return null;

        return html.substring(start, end).trim();
    }

    private void updateUI(int position, MatchSummary summary) {
        runOnUiThread(() -> {
            matchSummaries.set(position, summary);
            adapter.notifyItemChanged(position);

            // 检查是否全部加载完成
            if (position == matchSummaries.size() - 1) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(
                MatchListActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    @Override
    public void onMatchClick(String matchId) {
        if (matchId == null) {
            Toast.makeText(this, "无效的比赛ID", Toast.LENGTH_SHORT).show();
            return;
        }

        for (MatchSummary summary : matchSummaries) {
            // 双重空值检查
            if (summary != null &&
                    summary.getMatchId() != null &&
                    summary.getMatchId().equals(matchId)) {

                // 转换为JSON字符串（添加空检查）
                if (summary.getBlueTeamFullName() == null) {
                    summary.setBlueTeamFullName("未知队伍");
                }
                if (summary.getRedTeamFullName() == null) {
                    summary.setRedTeamFullName("未知队伍");
                }

                String json = new Gson().toJson(summary);
                Intent intent = new Intent(this, TimelineActivity.class);
                intent.putExtra("match_json", json);
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(this, "未找到比赛数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
        mainHandler.removeCallbacksAndMessages(null);
    }
}